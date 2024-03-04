package cn.donting.web.os.core.security;

import cn.donting.web.os.core.loader.WapClassLoader;

import java.security.Permission;

/**
 * os 安全策略管理器，用于 wap 线程组标识
 * @see cn.donting.web.os.core.wap.Wap
 * @see WapClassLoader#threadGroup
 * @author donting
 */
public class OsSecurityManager extends SecurityManager{


    @Override
    public ThreadGroup getThreadGroup() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader instanceof WapClassLoader) {
            return ((WapClassLoader) contextClassLoader).getThreadGroup();
        }
        return super.getThreadGroup();
    }

    /**
     * 默认开放 所有权限
     * @param perm   the requested permission.
     */
    @Override
    public void checkPermission(Permission perm) {

    }
}
