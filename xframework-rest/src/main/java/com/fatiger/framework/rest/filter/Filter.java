package com.fatiger.framework.rest.filter;

import com.fatiger.framework.common.utils.IPUtil;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.fatiger.framework.constant.General.*;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Slf4j
@ManagedBean
public class Filter implements javax.servlet.Filter {

    private static final String UNKNOWN_STRING = "unknown";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Stopwatch sp = Stopwatch.createStarted();

        // 多数据源复位设置

        if (!(servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse)) {
            throw new ServletException("HttpFilter can't handle an non-http request");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;


        try {

            buildRequestId(httpRequest);

            filterChain.doFilter(httpRequest, httpResponse);

        } catch (Exception e) {
            log.error(String.format("!!! ExceptionLogHttp method:%s, url:%s, reqHeader:%s, case:%s", httpRequest.getMethod(),
                    fullPath(httpRequest), getHeader(httpRequest), e.getMessage()), e);
        } finally {
            log.debug("=== Access method [" + httpRequest.getMethod() + "]、url [{}], cost time [{}] ms )", fullPath(httpRequest), sp.stop().elapsed(TimeUnit.MILLISECONDS));
            MDC.clear();
        }


    }

    @Override
    public void destroy() {

    }

    private String getHeader(HttpServletRequest req) {
        Enumeration<String> headerNames = req.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, req.getHeader(headerName));
        }
        return headers.toString();
    }

    private String fullPath(HttpServletRequest req) {
        StringBuilder path = new StringBuilder(req.getScheme().trim().concat("://").concat(req.getServerName()).concat(":")
                .concat(Integer.toString(req.getServerPort())).concat(req.getRequestURI()));
        String queryStr = req.getQueryString();
        if (queryStr != null) {
            path.append("?").append(req.getQueryString());
        }
        return path.toString();
    }

    private void buildRequestId(HttpServletRequest httpRequest) {
        String requestId = MDC.get(TRACE_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = httpRequest.getHeader(REQUEST_ID);
            if (StringUtils.isEmpty(requestId)) {
                requestId = IPUtil.getOneLocalIP();
            }

        }
        MDC.put(REQUEST_ID, requestId);
        MDC.put(SERVER_IP, IPUtil.getOneLocalIP());
        MDC.put(CLIENT_IP, getRemoteAddr(httpRequest));
    }

    public String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!StringUtils.isEmpty(ip)) {
            String[] newIp = ip.split(",");
            return newIp[0].trim();
        }

        return ip;
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {

    }
}
