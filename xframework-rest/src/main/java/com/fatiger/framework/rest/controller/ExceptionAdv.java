package com.fatiger.framework.rest.controller;

import com.fatiger.framework.common.beans.DataResult;
import com.fatiger.framework.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wengjiayu on 11/10/2017.
 * contact E-mail wengjiayu521@163.com
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdv {

    @ExceptionHandler(AppException.class)
    @ResponseBody
    public DataResult appExceptionHandler(AppException appException ) {
        log.error("catch  AppException ---> ", String.valueOf(appException.getCode())+ appException.getMessage());
        if(appException.getReturnObj()!=null)
        {
            return DataResult.ok(appException.getCode(), appException.getMessage(),appException.getReturnObj());
        }
        else {
            return DataResult.fail(appException.getCode(), appException.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @Profile({"dev", "qa"})
    public DataResult exceptionHandler( Exception e ) {
        log.error("exception service didn't catch", e);
        return DataResult.fail(50000, "exception that service didn't catch" + e.toString());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @Profile({"dev", "qa"})
    public DataResult exceptionHandler( MethodArgumentNotValidException e ) {
        log.error("Argument may be error", e);
        return DataResult.fail(50000, "Argument may be error" + e.toString());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }



}
