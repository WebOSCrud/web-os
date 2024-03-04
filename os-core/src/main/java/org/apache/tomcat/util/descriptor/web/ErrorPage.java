/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;

import cn.donting.web.os.core.servlet.OsDispatcherServlet;
import org.apache.catalina.Context;
import org.apache.tomcat.util.buf.UDecoder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.ServletContextInitializer;

/**
 * tomcat 内部类，tomcat会自动转发 404,500 等错误到 getLocation 配置的 错误页码
 * springboot 内部自动配置了 {@link ErrorPage#location} 到 tomcat
 *
 * 重写 getLocation，根据 线程上下文类加载器确定 wapID
 * 用于tomcat 自动转发 /error
 *
 * <p> 根据 classpath 加载优先原则， 会覆盖 tomcat ErrorPage的</p>
 *
 * <p>
 * wap 展示不支持 配置  server.error.path 只能使用 默认 /error
 * <p>
 * Representation of an error page element for a web application,
 * as represented in a <code>&lt;error-page&gt;</code> element in the
 * deployment descriptor.
 *
 * @author Craig R. McClanahan
 * @see ErrorPageRegistry
 * @see TomcatServletWebServerFactory#configureContext(Context, ServletContextInitializer[])
 */
public class ErrorPage extends XmlEncodingBase implements Serializable {

    private static final long serialVersionUID = 2L;


    private String ErrorPage = "OSErrorPage";
    // ----------------------------------------------------- Instance Variables

    /**
     * The error (status) code for which this error page is active. Note that
     * status code 0 is used for the default error page.
     */
    private int errorCode = 0;


    /**
     * The exception type for which this error page is active.
     */
    private String exceptionType = null;


    /**
     * The context-relative location to handle this error or exception.
     */
    private String location = null;


    // ------------------------------------------------------------- Properties


    /**
     * @return the error code.
     */
    public int getErrorCode() {
        return this.errorCode;
    }


    /**
     * Set the error code.
     *
     * @param errorCode The new error code
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Set the error code (hack for default XmlMapper data type).
     *
     * @param errorCode The new error code
     */
    public void setErrorCode(String errorCode) {

        try {
            this.errorCode = Integer.parseInt(errorCode);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }


    /**
     * @return the exception type.
     */
    public String getExceptionType() {
        return this.exceptionType;
    }


    /**
     * Set the exception type.
     *
     * @param exceptionType The new exception type
     */
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }


    /**
     * 重写 getLocation，根据 线程上下文类加载器确定 wapID
     * 用于tomcat 自动转发 /error
     *
     * @return the location.
     */
    public String getLocation() {
        String wapId = OsDispatcherServlet.wapIdThreadLocal.get();
        return "/" + wapId + this.location;
    }


    /**
     * Set the location.
     *
     * @param location The new location
     */
    public void setLocation(String location) {

        //        if ((location == null) || !location.startsWith("/"))
        //            throw new IllegalArgumentException
        //                ("Error Page Location must start with a '/'");
        this.location = UDecoder.URLDecode(location, getCharset());

    }


    // --------------------------------------------------------- Public Methods


    /**
     * Render a String representation of this object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ErrorPage[");
        if (exceptionType == null) {
            sb.append("errorCode=");
            sb.append(errorCode);
        } else {
            sb.append("exceptionType=");
            sb.append(exceptionType);
        }
        sb.append(", location=");
        sb.append(location);
        sb.append(']');
        return sb.toString();
    }

    public String getName() {
        if (exceptionType == null) {
            return Integer.toString(errorCode);
        } else {
            return exceptionType;
        }
    }

}
