package com.fatiger.framework.rest.handler;

import com.fatiger.framework.constant.com.fatiger.framework.constant.annotation.PermissionLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wengjiayu
 * @date 18/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Slf4j
public class GlobalInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("=============== 拦截器前置 =============");

        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        HandlerMethod method = (HandlerMethod) handler;
        PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
        if (permission != null && permission.limit()) {

            // TODO: 10/01/2018 校验登录
//                throw new SysException(APP_ERROR_CODE, "登陆失效");
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.debug("=============== 拦截器后置 =============");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        log.debug("=============== 执行完成时 =============");
    }

}
