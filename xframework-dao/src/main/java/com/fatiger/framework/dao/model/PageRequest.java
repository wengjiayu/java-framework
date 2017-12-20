package com.fatiger.framework.dao.model;

import java.io.Serializable;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */
public class PageRequest<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -8080195755865389569L;
    private int pageNum = 1;
    private int pageSize = 20;
    private T paramData;

    public PageRequest() {
    }

    public PageRequest(int pageNum, int pageSize) {
        int pageNumTemp = pageNum <= 0 ? 1 : pageNum;
        int pageSizeTemp = pageSize <= 0 ? 1 : pageSize;
        this.pageNum = pageNumTemp;
        this.pageSize = pageSizeTemp;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        int pageNumTemp = pageNum <= 0 ? 1 : pageNum;
        this.pageNum = pageNumTemp;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        int pageSizeTemp = pageSize <= 0 ? 1 : pageSize;
        this.pageSize = pageSizeTemp;
    }

    public T getParamData() {
        return this.paramData;
    }

    public void setParamData(T paramData) {
        this.paramData = paramData;
    }
}
