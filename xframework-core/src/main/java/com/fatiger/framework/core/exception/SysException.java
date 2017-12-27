package com.fatiger.framework.core.exception;

import java.util.Arrays;

/**
 * Created by wengjiayu on 01/11/2017.
 * E-mail wengjiayu521@163.com
 */
public class SysException extends RuntimeException {


    private static final long serialVersionUID = -8909393577265277982L;
    private transient Object[] args;
    private transient Object returnObj;
    private int errorCode;

    public SysException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public SysException(int errorCode, String msg, Object returnObj) {
        super(msg);
        this.errorCode = errorCode;
        this.returnObj = returnObj;
    }

    public SysException(int errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public SysException(int errorCode, String msg, Throwable cause, Object returnObj) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.returnObj = returnObj;
    }

    public SysException(int errorCode, String msg, Object[] args) {
        super(msg);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length).clone();
    }

    public SysException(int errorCode, String msg, Object[] args, Object returnObj) {
        super(msg);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        this.returnObj = returnObj;
    }

    public SysException(int errorCode, String msg, Object[] args, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
    }

    public SysException(int errorCode, String msg, Object[] args, Throwable cause, Object returnObj) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        this.returnObj = returnObj;
    }

    public SysException(Throwable cause) {
        super(cause);
    }

    public SysException(Throwable cause, Object returnObj) {
        super(cause);
        this.returnObj = returnObj;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setArgs(Object[] args) {
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
    }

    public Object getReturnObj() {
        return this.returnObj;
    }

    public int getCode() {
        return errorCode;
    }

    public void setCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
