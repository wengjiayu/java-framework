package com.fatiger.framework.rest.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.fatiger.framework.common.beans.RestInfo;
import com.fatiger.framework.common.utils.HttpClientUtil;
import com.fatiger.framework.common.utils.JsonUtil;
import com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.ExceptionErrorCode;
import com.fatiger.framework.core.context.BaseProperties;
import com.fatiger.framework.core.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wengjiayu
 * @date 11/10/2017
 * contact E-mail wengjiayu521@163.com
 */
@Slf4j
public final class RestClientWrapper {


    private Map<String, RestInfo> restConfigs = new ConcurrentHashMap<>();
    private static volatile RestClientWrapper restWrapperConfig = null;

    private RestClientWrapper() {
    }

    public static RestClientWrapper getInstance() {
        if (restWrapperConfig == null) {
            synchronized (RestClientWrapper.class) {
                if (restWrapperConfig == null) {
                    restWrapperConfig = new RestClientWrapper();
                }
            }
        }
        return restWrapperConfig;
    }

    private void loadData() {
        if (this.restConfigs.isEmpty()) {
            String restConfigFileName = BaseProperties.getString("rest.config.filename");
            if (restConfigFileName == null) {
                throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE, "${rest.config.filename} is null, check your properties");
            }

            try {
                InputStream input = (new ClassPathResource(restConfigFileName)).getInputStream();
                Throwable var3 = null;

                try {
                    Map data = JSON.parseObject(input, Map.class, new Feature[0]);
                    Iterator iterator = data.entrySet().iterator();

                    while (iterator.hasNext()) {
                        Map.Entry<String, JSONObject> entry = (Map.Entry<String, JSONObject>) iterator.next();
                        this.restConfigs.put(entry.getKey(), (entry.getValue()).toJavaObject(RestInfo.class));
                    }
                } catch (Throwable var15) {
                    var3 = var15;
                    throw var15;
                } finally {
                    if (var3 != null) {
                        try {
                            input.close();
                        } catch (Throwable var14) {
                            var3.addSuppressed(var14);
                        }
                    } else {
                        input.close();
                    }
                }
            } catch (Exception var17) {
                throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE, var17.getMessage(), var17);
            }
        }

    }

    public <R> R call(String configId, Object[] configParam, Header[] headers, String paramData, TypeReference<R> returnType) {
        String result = this.call(configId, configParam, headers, paramData);
        return JsonUtil.json2Reference(result, returnType);
    }

    public String call(String configId, Object[] configParam, Header[] headers, String paramData) {
        if (StringUtils.isEmpty(configId)) {
            throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE, "configId is null or empty");
        } else {
            this.loadData();
            RestInfo restInfo = this.restConfigs.get(configId);
            String serviceHost = restInfo.getServiceHost();
            String relativePath = restInfo.getRelativePath();
            String httpMethodInfo = restInfo.getHttpMethod();
            if (!StringUtils.isEmpty(serviceHost) && !StringUtils.isEmpty(relativePath) && !StringUtils.isEmpty(httpMethodInfo)) {
                String urlByParam = "http://" + serviceHost + relativePath;
                String url = MessageFormat.format(urlByParam, configParam);
                StringEntity stringEntity = null;
                if (paramData != null) {
                    stringEntity = new StringEntity(paramData, StandardCharsets.UTF_8);
                    stringEntity.setContentType("application/json");
                }

                HttpRequestBase requestBase = null;
                HttpMethod httpMethod = Enum.valueOf(HttpMethod.class, httpMethodInfo.toUpperCase());
                switch (httpMethod) {
                    case GET:
                        requestBase = new HttpGet(url);
                        break;
                    case POST:
                        requestBase = new HttpPost(url);
                        ((HttpPost) requestBase).setEntity(stringEntity);
                        break;
                    case PUT:
                        requestBase = new HttpPut(url);
                        ((HttpPut) requestBase).setEntity(stringEntity);
                        break;
                    case DELETE:
                        requestBase = new HttpDelete(url);
                }

                log.debug("=== Request URI : {}", restInfo.getServiceHost() + restInfo.getRelativePath());

                if (requestBase == null) {
                    throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE, "Specified HTTP Method [" + httpMethod.toString() + "] is not invalid");
                } else {
                    (requestBase).setHeaders(headers);
                    return HttpClientUtil.sendHttpRequest(requestBase, (long) restInfo.getTimeout(), TimeUnit.SECONDS);
                }

            } else {
                throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE, "configId: " + configId + ", [serviceHost] or [relativePath] or [httpMethod] is null or empty,please check rest-config-*.json");
            }
        }
    }

    RestInfo getRestInfo(String configId) {
        return this.restConfigs.get(configId);
    }
}
