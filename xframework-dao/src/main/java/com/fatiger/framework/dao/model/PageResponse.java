package com.fatiger.framework.dao.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wengjiayu
 * @date 16/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Data
public class PageResponse<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 8840010985438523038L;
    private List<T> resultData;
    private Pagination pagination = new Pagination();

}
