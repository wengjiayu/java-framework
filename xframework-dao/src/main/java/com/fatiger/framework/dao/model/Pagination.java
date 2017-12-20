package com.fatiger.framework.dao.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Data
public class Pagination implements Serializable {
    private static final long serialVersionUID = 345894055251695515L;
    private long totalCount = 0L;
    private int pageSize = 20;
    private String sortColumn;
    private int currentPageIndex;
    private int sortEnum;
}
