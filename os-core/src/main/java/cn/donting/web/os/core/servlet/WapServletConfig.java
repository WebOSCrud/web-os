package cn.donting.web.os.core.servlet;

import cn.donting.web.os.core.wap.Wap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

/**
 * 一些 wap 的 ServletConfig
 * wap ServletConfig 配置。 几乎没有实现。 理论上应该需要 实现某些接口，但是还没返现有什么异常。
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see WapServletContext
 * @author donting
 */
public class WapServletConfig implements ServletConfig {
    private ServletContext servletContext;
    private Wap wap;

    public WapServletConfig(Wap wap,ServletContext servletContext) {
        this.servletContext = servletContext;
        this.wap = wap;
    }


    @Override
    public String getServletName() {
        return wap.getWapInfo().getId()+"-DispatcherServlet";
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return servletContext.getInitParameterNames();
    }
}
