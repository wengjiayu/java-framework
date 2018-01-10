package com.fatiger.framework.constant.com.fatiger.framework.constant.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wengjiayu
 * @date 25/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionLimit {

    boolean limit() default true;

}