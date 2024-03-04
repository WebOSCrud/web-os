package cn.donting.web.os.core.util;

public class UrlUtil {

    /**
     * url 转为wap 访问的 http url
     *
     * @param wapId
     * @param url 访问路径
     * @return
     */
    public static String urlToWapUrl(String wapId, String url) {
        if (url == null) {
            return null;
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        if (url.startsWith("/" + wapId)) {
            return url;
        }
        return "/" + wapId + url;
    }
}
