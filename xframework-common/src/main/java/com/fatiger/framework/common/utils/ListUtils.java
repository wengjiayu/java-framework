package com.fatiger.framework.common.utils;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wengjiayu on 11/10/2017.
 * contact E-mail wengjiayu521@163.com
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ListUtils {

    private static final String LS_NOT_NULL = " 'ls' must not be Null!";
    private static final String LS2_NOT_NULL = " 'ls2' must not be Null!";

    private ListUtils() {
    }

    /**
     * 求差集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static <T> List<T> diff(List<T> ls, List<T> ls2) {
        Assert.notNull(ls, LS_NOT_NULL);
        Assert.notNull(ls2, LS2_NOT_NULL);
        List<T> result = new ArrayList(ls.size());
        result.addAll(ls);
        result.removeAll(ls2);
        return result;
    }

    /**
     * 求交集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static <T> List<T> intersect(List<T> ls, List<T> ls2) {
        Assert.notNull(ls, LS_NOT_NULL);
        Assert.notNull(ls2, LS2_NOT_NULL);
        List result = new ArrayList(ls.size());
        result.addAll(ls);
        result.retainAll(ls2);
        return result;
    }

    /**
     * 求并集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static <T> List<T> union(List<T> ls, List<T> ls2) {
        Assert.notNull(ls, LS_NOT_NULL);
        Assert.notNull(ls2, LS2_NOT_NULL);
        List<T> result = new ArrayList(ls.size());
        result.addAll(ls);
        result.removeAll(ls2);
        result.addAll(ls2);
        return result;
    }


}
