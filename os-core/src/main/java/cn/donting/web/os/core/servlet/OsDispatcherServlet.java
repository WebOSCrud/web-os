package cn.donting.web.os.core.servlet;

import cn.donting.web.os.api.user.User;
import cn.donting.web.os.api.wap.WapWindow;
import cn.donting.web.os.api.wap.WapWindowType;
import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.OsSetting;
import cn.donting.web.os.core.api.OsApi;
import cn.donting.web.os.core.db.entity.WapInfo;
import cn.donting.web.os.core.db.repository.IWapInfoRepository;
import cn.donting.web.os.core.exception.ResponseException;
import cn.donting.web.os.core.exception.WapLoadException;
import cn.donting.web.os.core.exception.WapNotFoundException;
import cn.donting.web.os.core.properties.DevOsProperties;
import cn.donting.web.os.core.service.OsService;
import cn.donting.web.os.core.service.UserService;
import cn.donting.web.os.core.service.WapRuntimeService;
import cn.donting.web.os.core.util.UrlUtil;
import cn.donting.web.os.core.vo.ResponseBody;
import cn.donting.web.os.core.vo.ResponseBodyCodeEnum;
import cn.donting.web.os.core.wap.Wap;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.springframework.http.HttpStatus;
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

    public OsDispatcherServlet(DevOsProperties devOsProperties, OsService osService, IWapInfoRepository wapInfoRepository, UserService userService, OsApi osApi, WapRuntimeService wapRuntimeService) {
        this.osApi = osApi;
        this.userService = userService;
        this.osService = osService;
        this.devOsProperties = devOsProperties;
        this.wapRuntimeService = wapRuntimeService;
        this.wapInfoRepository = wapInfoRepository;
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
        if (request.getAttribute("request_origin_url") == null) {
            request.setAttribute("request_origin_url", request.getRequestURI());
        }
        //需要设置线程上下文 线程
        //spring 某些操作需要用到
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            String queryString="";
            if(request.getQueryString()!=null){
                queryString="?"+URLDecoder.decode(queryString, "utf-8");
            }
            log.debug("http请求:{} {}{}", request.getMethod(),
                    request.getRequestURI(),
                    queryString);

            checkLogin(request, response);

            String wapId = getWapId(request);
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
            sendResponseBody(200, response, e.getResponseEntity());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendResponseBody(200, response, ResponseBody.fail(ResponseBodyCodeEnum.UNKNOWN_ERROR));
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private String getWapId(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        String[] split = requestURI.split("/");
        if (split.length > 1) {
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
    private void checkLogin(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if (loginIgnoreUrl.contains(requestURI)) {
            return;
        }
        //忽略桌面的登陆资源
        OsSetting osSetting = osService.getOsSetting();
        if (osSetting.getDesktopWapId() != null && request.getMethod().equalsIgnoreCase("GET")) {
            if (requestURI.startsWith("/" + osSetting.getDesktopWapId())) {
                List<String> loginIgnoreResources = osSetting.getLoginIgnoreRURL();
                String url = requestURI.replaceFirst("/" + osSetting.getDesktopWapId(), "");
                if (loginIgnoreResources.contains(url)) {
                    return;
                }
            }
        }
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
                    httpServletResponse.sendRedirect("/" + desktopWapId + url);
                    throw new ResponseException(ResponseBody.fail(ResponseBodyCodeEnum.LOGIN_NONE));
                }
            }
        }
        userService.checkLogin();
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
