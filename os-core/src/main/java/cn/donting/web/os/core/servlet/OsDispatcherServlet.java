package cn.donting.web.os.core.servlet;

import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.api.wap.WapWindowType;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.OsSetting;
import cn.donting.web.os.core.api.OsApi;
import cn.donting.web.os.core.db.entity.User;
import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.db.repository.IUserRepository;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.domain.DigestAuthInfo;
import cn.donting.web.os.core.exception.ResponseException;
import cn.donting.web.os.core.exception.WapLoadException;
import cn.donting.web.os.core.exception.WapNotFoundException;
import cn.donting.web.os.core.properties.DevOsProperties;
import cn.donting.web.os.core.service.OsService;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.service.WapRuntimeService;
import cn.donting.web.os.core.util.DigestUtil;
import cn.donting.web.os.core.util.UrlUtil;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import cn.donting.web.os.core.wap.Wap;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.springframework.http.HttpStatus;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 重写service。根据wapId,走wap 自己容器的 DispatcherServlet。
 * 需要设置线程 的类加载器。spring容器许多的 操作需要使用到
 * <p>
 * RequestContextHolder 用于    @Autowired 的方式获取  request 等...
 * 不一样的事在  wap 没有注入 tocmcat
 * 所以 RequestContextHolder 绑定 request 放生在 {@link FrameworkServlet#processRequest} {@link FrameworkServlet#buildRequestAttributes}
 * <p>
 * 请求在进入tomcat是会走  RequestContextFilter， 此时也会绑定{@link RequestContextFilter#initContextHolders(HttpServletRequest, ServletRequestAttributes)}
 * request。 所以在  service 期间也能通过 @Autowired 的方式获取  request 等...
 *
 * @see DispatcherServlet
 * @see RequestContextHolder
 * @see FrameworkServlet
 * @see RequestContextFilter
 */
@Slf4j
public class OsDispatcherServlet extends DispatcherServlet {

    /**
     * 设置线程 wapId。用于errorPage
     *
     * @see ErrorPage#getLocation()
     */
    public static final ThreadLocal<String> wapIdThreadLocal = new ThreadLocal<>();
    private static final Set<String> loginIgnoreUrl = new HashSet<>(Arrays.asList("/os/user/login", "/os/user/login/force"));
    private final OsApi osApi;
    private final WapRuntimeService wapRuntimeService;
    private final IWapInfoRepository wapInfoRepository;
    private final UserService userService;
    private final OsService osService;
    private final ObjectMapper objectMapper;
    private final DevOsProperties devOsProperties;
    private final IUserRepository userRepository;

    private final Map<String,String> urlWapMap=new ConcurrentHashMap<>();


    public OsDispatcherServlet(DevOsProperties devOsProperties, OsService osService, IWapInfoRepository wapInfoRepository, UserService userService, OsApi osApi, WapRuntimeService wapRuntimeService, IUserRepository userRepository) {
        this.osApi = osApi;
        this.userService = userService;
        this.osService = osService;
        this.devOsProperties = devOsProperties;
        this.wapRuntimeService = wapRuntimeService;
        this.wapInfoRepository = wapInfoRepository;
        this.userRepository = userRepository;
        objectMapper = new ObjectMapper();
    }

    /**
     * 分发 http 请求
     *
     * @param request  the {@link HttpServletRequest} object that contains the request the client made of the servlet
     * @param response the {@link HttpServletResponse} object that contains the response the servlet returns to the client
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //需要设置线程上下文 线程
        //spring 某些操作需要用到
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            String queryString = "";
            if (request.getQueryString() != null) {
                queryString = "?" + URLDecoder.decode(queryString, "utf-8");
            }
            if (!checkLogin(request, response)) {
                return;
            }
            if (checkIndex(request, response)) {
                return;
            }
            log.debug("http请求:{} {}{}", request.getMethod(), request.getRequestURI(), queryString);
            String wapId = getWapId(request);
            if (wapId == null) {
                log.error("wap is null:{}", request.getRequestURI());
                response.setStatus(500);
                return;
            }
            String requestURI = request.getRequestURI();
            if (!requestURI.startsWith("/" + wapId)) {
                request.getRequestDispatcher("/" + wapId + requestURI).forward(request, response);
                return;
            }
            //404 转发到dev 服务器
            if (request.getAttribute("request_origin_url") == null) {
                request.setAttribute("request_origin_url", request.getRequestURI());
            }

            wapIdThreadLocal.set(wapId);
            if (OsCoreApplication.OS_ID.equals(wapId)) {
                Thread.currentThread().setContextClassLoader(OsDispatcherServlet.class.getClassLoader());
                super.service(request, response);
                return;
            }
            Wap wap = wapRuntimeService.getAndLoadWap(wapId);
            Thread.currentThread().setContextClassLoader(wap.getWapClassLoader());
            wap.doService(request, response);
            return;
        } catch (WapNotFoundException e) {
            log.warn(e.getMessage());
            sendResponseBody(200, response, ResponseBody.failData(ResponseBodyCodeEnum.NOT_FOUND, e.getMessage()));
        } catch (WapLoadException e) {
            log.warn(e.getMessage());
            sendResponseBody(200, response, ResponseBody.failData(ResponseBodyCodeEnum.UNKNOWN_ERROR, e.getMessage()));
        } catch (ResponseException e) {
            log.debug(e.getMessage(), e);
            sendResponseBody(e.getHttpStatus(), response, e.getResponseEntity());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendResponseBody(200, response, ResponseBody.fail(ResponseBodyCodeEnum.UNKNOWN_ERROR));
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private String getWapId(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURL = httpServletRequest.getRequestURI();
        String wapId = urlWapMap.get(requestURL);
        if (wapId!=null) {
            return wapId;
        }
        wapId = getWapId(requestURL);
        if (OsCoreApplication.OS_ID.equals(wapId)) {
            return wapId;
        }
        if (wapId != null && wapInfoRepository.findById(wapId).isPresent()) {
            urlWapMap.put(requestURL,wapId);
            return wapId;
        }
        String referer = httpServletRequest.getHeader("Referer");
        if(referer==null){
            return null;
        }
        String refererUrl = getRefererPath(referer);
        wapId = urlWapMap.get(refererUrl);
        if (wapId!=null) {
            urlWapMap.put(requestURL,wapId);
            return wapId;
        }
        return null;
    }

    public static String getRefererPath(String url) {
        if (url.contains("?")) {
            url=url.split("\\?")[0];
        }
        String regex = "https?://[^/]+(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    private String getWapId(String url) {
        String[] split = url.split("/");
        if (split.length >= 1) {
            return split[1];
        } else {
            return null;
        }
    }

    /**
     * 检查登陆状态
     *
     * @param request
     * @return
     */
    private boolean checkLogin(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
        String requestURI = request.getRequestURI();
        if (loginIgnoreUrl.contains(requestURI)) {
            return true;
        }
        User loginUser = userService.getLoginUser();
        if (loginUser != null) {
            return true;
        }
        // 检查请求头中是否有Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Digest")) {
            DigestAuthInfo authInfo = getAuthInfoObject(authHeader);
            Optional<User> userOp = userRepository.findById(authInfo.getUsername());
            if (!userOp.isPresent()) {
                sendWWAuthenticate(request, httpServletResponse);
                return false;
            }
            User user = userOp.get();
            String nonce = (String) request.getSession().getAttribute("nonce");
            if (nonce != null) {
                /*
                 * 生成 response 的算法：
                 *  response = MD5(MD5(username:realm:password):nonce:nc:cnonce:qop:MD5(<request-method>:url))
                 */
                // 这里密码固定为 123456, 实际应用需要根据用户名查询数据库或缓存获得
                String HA1 = DigestUtil.MD5(authInfo.getUsername() + ":" + authInfo.getRealm() + ":" + user.getPassword());
                String HD = String.format(authInfo.getNonce() + ":" + authInfo.getNc() + ":" + authInfo.getCnonce() + ":"
                        + authInfo.getQop());
                String HA2 = DigestUtil.MD5(request.getMethod() + ":" + authInfo.getUri());
                String responseValid = DigestUtil.MD5(HA1 + ":" + HD + ":" + HA2);
                if (!responseValid.equals(authInfo.getResponse())) {
                    sendWWAuthenticate(request, httpServletResponse);
                    return false;
                }
                //login
                user.setNonce(nonce);
                user.setNonceExpiredTime(System.currentTimeMillis() + User.NONCE_EXPIRED_TIME);
                request.getSession().removeAttribute("nonce");
                userRepository.save(user);
                return true;
            } else {
                nonce = user.getNonce();
            }
            //nonce 不相等错误请求/其他地方请求登陆了
            if (!authInfo.getNonce().equals(nonce)) {
                sendWWAuthenticate(request, httpServletResponse);
                return false;
            }
            //过期
            if (user.getNonceExpiredTime() < System.currentTimeMillis()) {
                user.setNonce(null);
                user.setNonceExpiredTime(0);
                userRepository.save(user);
                sendWWAuthenticate(request, httpServletResponse);
                return false;
            }
            user.setNonce(null);
            user.setPassword(null);
            user.setNonceExpiredTime(0);
            request.getSession().setAttribute("user", user);
            return true;
        }
        sendWWAuthenticate(request, httpServletResponse);
        return false;
    }

    private void sendWWAuthenticate(HttpServletRequest request, HttpServletResponse response) {
        String nonce = UUID.randomUUID().toString();
        response.setHeader("WWW-Authenticate", "Digest qop=\"auth\",nonce=\"" + nonce + "\"");
        response.setStatus(401);
        request.getSession().setAttribute("nonce", nonce);
    }

    /**
     * 该方法用于将 Authorization 请求头的内容封装成一个对象。
     * <p>
     * Authorization 请求头的内容为：
     * Digest username="aaa", realm="no auth", nonce="b2b74be03ff44e1884ba0645bb961b53",
     * uri="/BootDemo/login", response="90aff948e6f2207d69ecedc5d39f6192", qop=auth,
     * nc=00000002, cnonce="eb73c2c68543faaa"
     */
    public static DigestAuthInfo getAuthInfoObject(String authStr) {
        if (authStr == null || authStr.length() <= 7)
            return null;

        if (authStr.toLowerCase().indexOf("digest") >= 0) {
            // 截掉前缀 Digest
            authStr = authStr.substring(6);
        }
        // 将双引号去掉
        authStr = authStr.replaceAll("\"", "");
        DigestAuthInfo digestAuthObject = new DigestAuthInfo();
        String[] authArray = new String[8];
        authArray = authStr.split(",");
        // System.out.println(java.util.Arrays.toString(authArray));
        for (int i = 0, len = authArray.length; i < len; i++) {
            String auth = authArray[i];
            String key = auth.substring(0, auth.indexOf("=")).trim();
            String value = auth.substring(auth.indexOf("=") + 1).trim();
            switch (key) {
                case "username":
                    digestAuthObject.setUsername(value);
                    break;
                case "realm":
                    digestAuthObject.setRealm(value);
                    break;
                case "nonce":
                    digestAuthObject.setNonce(value);
                    break;
                case "uri":
                    digestAuthObject.setUri(value);
                    break;
                case "response":
                    digestAuthObject.setResponse(value);
                    break;
                case "qop":
                    digestAuthObject.setQop(value);
                    break;
                case "nc":
                    digestAuthObject.setNc(value);
                    break;
                case "cnonce":
                    digestAuthObject.setCnonce(value);
                    break;
            }
        }
        return digestAuthObject;
    }

    private boolean checkIndex(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
        String requestURI = request.getRequestURI();
        OsSetting osSetting = osService.getOsSetting();
        //访问 http://127.0.0.1 跳转到 桌面
        String accept = request.getHeader("Accept");
        if (requestURI.equals("/") && request.getMethod().equalsIgnoreCase("GET") && accept.contains("text/html")) {
            String desktopWapId = osSetting.getDesktopWapId();
            if (desktopWapId == null) {
                throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.WAP_DESKTOP_UNINSTALLED));
            }
            WapInfo wapInfo = wapInfoRepository.findById(desktopWapId).get();
            for (WapWindow wapWindow : wapInfo.getWapWindows()) {
                if (wapWindow.getType().equals(WapWindowType.Desktop)) {
                    String url = UrlUtil.urlToWapUrl(wapInfo.getId(), wapWindow.getUrl());
                    httpServletResponse.sendRedirect(url);
                    return true;
//                    throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.LOGIN_NONE));
                }
            }
        }
        return false;
    }

    private void sendResponseBody(int status, HttpServletResponse response, ResponseBody responseEntity) {
        response.setCharacterEncoding("UTF-8"); // 设置字符编码
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        try {
            String body = objectMapper.writeValueAsString(responseEntity);
            response.getOutputStream().write(body.getBytes());
        } catch (Exception ex) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

}
