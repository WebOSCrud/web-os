package cn.donting.web.os.launch;

import cn.donting.web.os.api.OsApi;
import cn.donting.web.os.launch.loader.OsClassLoader;

/**
 * os core APi
 * @see OsApi
 * @author donting
 */
public class CoreOsApi {
    private static OsApi osApi;

    /**
     * 由 os core 设置，
     * os core 实现OsApi， 供 wap 使用
     *
     * @param osApi
     */
    public static void setOsApi(OsApi osApi) {
        if(!osApi.getClass().getClassLoader().equals(OsClassLoader.class)){
            return;
        }
        if(CoreOsApi.osApi!=null){
            return;
        }
        CoreOsApi.osApi = osApi;
    }

    public static OsApi getOsApi() {
        return osApi;
    }
}
