package com.fatiger.framework.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.DateCodec;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import static com.fatiger.framework.common.utils.DateUtil.DEFAULT_DATEDETAIL_PATTERN;

/**
 * Created by wengjiayu on 11/10/2017.
 * contact E-mail wengjiayu521@163.com
 */
public class JsonUtil implements java.io.Serializable {


    private static final long serialVersionUID = 2208654349131970447L;

    static {
        ParserConfig.getGlobalInstance().putDeserializer(Date.class, DateCodec.instance);
        JSON.DEFFAULT_DATE_FORMAT = DEFAULT_DATEDETAIL_PATTERN;
    }

    public static String object2JSON(Object obj, SerializerFeature... serializerFeature) {
        if (obj == null) {
            return "{}";
        }
        return JSON.toJSONString(obj, serializerFeature);
    }


    public static String object2JSON(Object obj, SerializeConfig serializeConfig,
                                     SerializerFeature... serializerFeature) {
        if (obj == null) {
            return "{}";
        }
        return JSON.toJSONString(obj, serializeConfig, serializerFeature);
    }

    public static String object2JSON(Object obj) {
        if (obj == null) {
            return "{}";
        }
        return JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
    }


    public static <T> T json2Object(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    public static <T> T json2Reference(String json, TypeReference<T> reference) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, reference);
    }

    public static <T> T json2Type(String json, Type type) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, type);
    }

    public static Map<String, Object> json2Map(String json, Feature... features) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        }, features);
    }

    public static Map<String, Object> json2Map(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> T json2Reference(String json, TypeReference<T> reference, Feature... features) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, reference, features);
    }

    @Deprecated
    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return JSON.parseObject(bytes, new TypeReference<T>() {
        }.getType());
    }

    @Deprecated
    public static <T> byte[] serialize(T t) {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONBytes(t, SerializerFeature.WriteClassName);
    }
}
