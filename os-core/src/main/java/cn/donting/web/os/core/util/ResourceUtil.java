package cn.donting.web.os.core.util;

import cn.donting.web.os.core.OsCoreApplication;
import cn.donting.web.os.core.config.WapWebMvcConfigurer;
import cn.donting.web.os.core.db.entity.WapResource;
import cn.donting.web.os.core.file.OSFileSpaces;
import cn.donting.web.os.core.wap.Wap;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class ResourceUtil {

    public static String getWapResourceId(String wapId, String resource) {
        return wapId + "." + resource;
    }

    /**
     * 把 wap 中的 resource 资源转换为 http 访问的方式
     * 一般是访问 图片资源
     *
     * @return
     */
    public static String getWapResourceHttpURL(WapResource wapResource) {
        if (wapResource == null) {
            return null;
        }
        return WapWebMvcConfigurer.WAP_RESOURCES_PATH + "/" + wapResource.getWapId() + "/" + wapResource.getFileName();

    }

    /**
     * 去掉 开头的 /
     *
     * @param resource 基于  resources 目录的 路径 static/icon.png
     * @return 不以 / 开头的 path
     */
    public static String getResourcePath(String resource) {
        if (resource == null) {
            return null;
        }
        if (resource.startsWith("/")) {
            return resource.substring(1, resource.length() - 1);
        }
        return resource;
    }
}
