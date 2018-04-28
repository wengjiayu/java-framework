package com.fatiger.framework.common.utils;

import com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.ExceptionErrorCode;
import com.fatiger.framework.core.awares.SpringContextWrapper;
import com.fatiger.framework.core.context.BaseProperties;
import com.fatiger.framework.core.exception.SysException;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.MDC;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.ExceptionErrorCode.*;
import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.GeneralKey.*;
import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.PropertiesCons.HTTP_RECEIVE_TIMEOUT_THRESHOLD;
import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.PropertiesCons.HTTP_RECEIVE_TIMEOUT_THRESHOLD_DEFAULT;

/**
 * HttpClient4.0封装
 *
 * @author wengjiayu
 */
@Slf4j
public class HttpClientUtil {

    private static final String USER_AGENT = "user_agent";
    private static final String IBJ = "internal";
    private static final String TIMER_PREFIX = "timer.http.client";


    private static volatile CloseableHttpClient httpClient;

    private static volatile boolean isMetricsEnable = true;

    private static long receiveTimeoutMs = BaseProperties.getProperty(HTTP_RECEIVE_TIMEOUT_THRESHOLD, Long.class, HTTP_RECEIVE_TIMEOUT_THRESHOLD_DEFAULT);

    private HttpClientUtil() {
    }

    private static RequestConfig requestConfig = null;

    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (CloseableHttpClient.class) {
                if (httpClient == null) {
                    ConnectionSocketFactory connectionSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
                    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", connectionSocketFactory)
                            .register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();
                    HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
                            DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);
                    DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory,
                            dnsResolver);

                    SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
                    connManager.setDefaultSocketConfig(defaultSocketConfig);

                    connManager.setMaxTotal(BaseProperties.getProperty("httpclient.conn.maxTotal", Integer.class, 300));
                    connManager.setDefaultMaxPerRoute(
                            BaseProperties.getProperty("httpclient.conn.maxPerRoute", Integer.class, 200));
                    connManager.setValidateAfterInactivity(1000);

                    requestConfig = RequestConfig.custom()
                            .setSocketTimeout(BaseProperties.getProperty("httpclient.socketTimeout", Integer.class,
                                    HTTP_TIMEOUT_IN_MS))
                            .setConnectTimeout(BaseProperties.getProperty("httpclient.connectTimeout", Integer.class,
                                    CONNECT_TIMEOUT_IN_MS))
                            .setConnectionRequestTimeout(BaseProperties.getProperty("httpclient.connectionRequestTimeout",
                                    Integer.class, REQUEST_TIMEOUT_IN_MS))
                            .build();

                    Object httpRequestInterceptor = getInterceptorBean("TraceHttpClientRequestInterceptor");

                    HttpClientBuilder httpClientBuilder = HttpClients.custom()
                            .setConnectionManager(connManager)
                            .setConnectionManagerShared(false)
                            .evictExpiredConnections()
                            .evictIdleConnections(10, TimeUnit.SECONDS)
                            .setDefaultRequestConfig(requestConfig)
                            .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                            .setKeepAliveStrategy((r, c) -> Duration.ofMinutes(25).toMillis()) //keep alive duration
                            .setRetryHandler(null);

                    if (httpRequestInterceptor != null) {
                        httpClientBuilder.addInterceptorFirst((HttpRequestInterceptor) httpRequestInterceptor);
                    }

                    httpClient = httpClientBuilder.build();

                    ConnectionCloseableMonitor closeThread = new ConnectionCloseableMonitor(connManager);
                    closeThread.setDaemon(true);
                    closeThread.start();
                }
            }
        }

        return httpClient;
    }


    private static Object getInterceptorBean(String beanName) {
        Object bean;
        try {
            bean = SpringContextWrapper.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
        return bean;
    }


    /**
     * 关闭 Trace 切面，并记录 Trace 事件动作
     *
     * @param httpResponse
     */
    private static void tryCloseTrace(HttpResponse httpResponse) {
        Object httpResponseInterceptor = getInterceptorBean("TraceHttpClientResponseInterceptor");

        if (httpResponseInterceptor != null) {
            try {
                ((HttpResponseInterceptor) httpResponseInterceptor).process(httpResponse, null);
            } catch (HttpException | IOException e) {
                log.debug("Exception occurred while trying to send response span ", e);
            }

        }
    }


    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数(格式:json)
     */
    public static String sendHttpPost(String httpUrl, String jsonStr, Header... headers) {
        return sendHttpPost(httpUrl, jsonStr, 0l, null, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数(格式:json)
     */
    public static String sendHttpPostByRetry(String httpUrl, String jsonStr, int retryCount, Header... headers) {
        return sendHttpPostByRetry(httpUrl, jsonStr, 0l, null, retryCount, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpPost(String httpUrl, long timeout, TimeUnit timeUnit, Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        if (header != null) {
            httpPost.setHeaders(header);
        }
        return sendHttpPost(httpPost, timeout, timeUnit);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpPostByRetry(String httpUrl, long timeout, TimeUnit timeUnit, int retryCount,
                                             Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        if (header != null) {
            httpPost.setHeaders(header);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpPost(String httpUrl, Header... headers) {
        return sendHttpPost(httpUrl, 0l, null, headers);
    }

    /**
     * 发送 post请求undertow
     *
     * @param httpUrl 地址`13
     */
    public static String sendHttpPostByRetry(String httpUrl, int retyCount, Header... headers) {
        return sendHttpPostByRetry(httpUrl, 0l, null, retyCount, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数(格式:json)
     */
    public static String sendHttpPost(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                      Header... header) {
        return sendHttpPostByRetry(httpUrl, jsonStr, timeout, timeUnit, 0, header);
    }


    /**
     * post请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数(格式:json)
     */
    public static String sendHttpPostByRetry(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                             int retryCount,
                                             Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        try {
            // 设置参数
            StringEntity stringEntity = new StringEntity(jsonStr, DEFAULT_CHARSET);
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setEntity(stringEntity);
            if (header != null) {
                httpPost.setHeaders(header);
            }
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }


    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param maps    参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps, Header... headers) {
        return sendHttpPost(httpUrl, maps, 0l, null, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param maps    参数
     */
    public static String sendHttpPostByRetry(String httpUrl, Map<String, String> maps, int retryCount,
                                             Header... headers) {
        return sendHttpPostByRetry(httpUrl, maps, 0l, null, retryCount, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param parameters 参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> parameters, long timeout, TimeUnit timeUnit,
                                      Header... header) {
        return sendHttpPostByRetry(httpUrl, parameters, timeout, timeUnit, 0, header);
    }


    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param parameters 参数
     */
    public static String sendHttpPostByRetry(String httpUrl, Map<String, String> parameters, long timeout,
                                             TimeUnit timeUnit, int retryCount,
                                             Header... header) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), parameters.get(entry.getKey())));
        }
        try {
            if (header != null) {
                httpPost.setHeaders(header);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param parameters 参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> parameters) {
        return sendHttpPost(httpUrl, parameters, 0l, null);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param parameters 参数
     */
    public static String sendHttpPostByRetry(String httpUrl, Map<String, String> parameters, int retryCount) {
        return sendHttpPostByRetry(httpUrl, parameters, 0l, null, retryCount);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param maps    参数
     */
    public static String sendHttpPut(String httpUrl, Map<String, String> maps, Header... headers) {
        return sendHttpPut(httpUrl, maps, 0l, null, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param maps    参数
     */
    public static String sendHttpPutByRetry(String httpUrl, Map<String, String> maps, int retryCount,
                                            Header... headers) {
        return sendHttpPutByRetry(httpUrl, maps, 0l, null, retryCount, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param parameters 参数
     */
    public static String sendHttpPut(String httpUrl, Map<String, String> parameters, long timeout, TimeUnit timeUnit,
                                     Header... headers) {
        return sendHttpPutByRetry(httpUrl, parameters, timeout, timeUnit, 0, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl    地址
     * @param parameters 参数
     */
    public static String sendHttpPutByRetry(String httpUrl, Map<String, String> parameters, long timeout,
                                            TimeUnit timeUnit, int retryCount,
                                            Header... headers) {
        HttpPut httpPut = new HttpPut(httpUrl);// 创建httpPost
        if (headers != null) {
            httpPut.setHeaders(headers);
        }
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), parameters.get(entry.getKey())));
        }
        try {
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET));
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpRequestByRetry(httpPut, timeout, timeUnit, retryCount);
    }

    /**
     * 发送 put请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数(格式:json)
     */
    public static String sendHttpPut(String httpUrl, String jsonStr, Header... header) {
        return sendHttpPut(httpUrl, jsonStr, 0l, null, header);
    }

    /**
     * 发送 put请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数(格式:json)
     */
    public static String sendHttpPutByRetry(String httpUrl, String jsonStr, int retryCount, Header... header) {
        return sendHttpPutByRetry(httpUrl, jsonStr, 0l, null, retryCount, header);
    }

    /**
     * 发送 put请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数 json
     */
    public static String sendHttpPut(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                     Header... header) {
        return sendHttpPutByRetry(httpUrl, jsonStr, timeout, timeUnit, 0, header);
    }

    /**
     * 发送 put请求
     *
     * @param httpUrl 地址
     * @param jsonStr 参数 json
     */
    public static String sendHttpPutByRetry(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                            int retryCount,
                                            Header... header) {
        HttpPut httpPut = new HttpPut(httpUrl);
        try {
            // 设置参数
            StringEntity stringEntity = new StringEntity(jsonStr, DEFAULT_CHARSET);
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpPut.setEntity(stringEntity);
            if (header != null) {
                httpPut.setHeaders(header);
            }
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpRequestByRetry(httpPut, timeout, timeUnit, retryCount);
    }


    /**
     * 发送delete请求，带body
     *
     * @param jsonStr json
     */
    public static String sendHttpDelete(String httpUrl, String jsonStr, Header... headers) {
        return sendHttpDelete(httpUrl, jsonStr, 0l, null, headers);
    }

    /**
     * 发送delete请求，带body
     *
     * @param jsonStr json
     */
    public static String sendHttpDeleteByRetry(String httpUrl, String jsonStr, int retryCount, Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, jsonStr, 0l, null, retryCount, headers);
    }

    /**
     * 发送delete请求，带body
     *
     * @param jsonStr json
     */
    public static String sendHttpDelete(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                        Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, jsonStr, timeout, timeUnit, 0, headers);
    }

    /**
     * 发送delete请求，带body
     *
     * @param jsonStr json
     */
    public static String sendHttpDeleteByRetry(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit, int retryCount, Header... headers) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);// 创建httpDete
        try {
            // 设置参数
            StringEntity stringEntity = new StringEntity(jsonStr, DEFAULT_CHARSET.displayName());
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            if (headers != null) {
                httpDelete.setHeaders(headers);
            }
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpRequestByRetry(httpDelete, timeout, timeUnit, retryCount);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param headers 参数
     */
    public static String sendHttpDelete(String httpUrl, Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, 0, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param headers 参数
     */
    public static String sendHttpDeleteByRetry(String httpUrl, int retryCount, Header... headers) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);// 创建httpDete
        if (headers != null) {
            httpDelete.setHeaders(headers);
        }
        return sendHttpRequestByRetry(httpDelete, 0l, null, retryCount);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param timeout timeout
     * @param headers 参数
     */
    public static String sendHttpDelete(String httpUrl, long timeout, TimeUnit timeUnit, Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, timeout, timeUnit, 0, headers);
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl 地址
     * @param timeout timeout
     * @param headers 参数
     */
    public static String sendHttpDeleteByRetry(String httpUrl, long timeout, TimeUnit timeUnit, int retryCount,
                                               Header... headers) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);// 创建httpDete
        if (headers != null) {
            httpDelete.setHeaders(headers);
        }
        return sendHttpRequestByRetry(httpDelete, timeout, timeUnit, retryCount);
    }

    /**
     * 发送Post请求
     */
    private static String sendHttpPost(HttpPost httpPost, long timeout, TimeUnit timeUnit) {
        return HttpClientUtil.sendHttpRequest(httpPost, timeout, timeUnit);
    }


    /**
     * 发送Post请求
     */
    private static String sendHttpPostByRetry(HttpPost httpPost, long timeout, TimeUnit timeUnit, int retryCount) {
        return HttpClientUtil.sendHttpRequestByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * 发送 get请求
     */
    public static String sendHttpGet(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        return sendHttpGet(httpGet, 0l, null);
    }

    /**
     * 发送 get请求
     */
    public static String sendHttpGet(String httpUrl, long timeout, TimeUnit timeUnit) {
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        return sendHttpGet(httpGet, timeout, timeUnit);
    }

    /**
     * 发送 get请求
     */
    public static String sendHttpGet(String httpUrl, Header... headers) {
        return sendHttpGet(httpUrl, 0l, null, headers);
    }

    /**
     * 发送 get请求
     */
    public static String sendHttpGet(String httpUrl, long timeout, TimeUnit timeUnit, Header... headers) {
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        if (headers != null) {
            httpGet.setHeaders(headers);
        }
        return sendHttpGet(httpGet, timeout, timeUnit);
    }

    /**
     * 发送 get请求Https
     */
    public static String sendHttpsGet(String httpUrl, Header... headers) {
        return sendHttpsGet(httpUrl, 0l, null, headers);
    }

    /**
     * 发送 get请求Https
     */
    public static String sendHttpsGet(String httpUrl, long timeout, TimeUnit timeUnit, Header... header) {
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        if (header != null) {
            httpGet.setHeaders(header);
        }
        return sendHttpsGet(httpGet, timeout, timeUnit);
    }

    /**
     * 发送Get请求
     */
    private static String sendHttpGet(HttpGet httpGet, long timeout, TimeUnit timeUnit) {
        return sendHttpRequestByRetry(httpGet, timeout, timeUnit, 3);
    }

    /**
     * 发送Get请求Https
     */
    private static String sendHttpsGet(HttpGet httpGet, long timeout, TimeUnit timeUnit) {
        return sendHttpRequestByRetry(httpGet, timeout, timeUnit, 3);
    }

    public static String sendHttpRequest(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit) {
        return sendHttpRequestByRetry(httpRequestBase, timeout, timeUnit, 0);
    }


    public static String sendHttpRequestByRetry(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit,
                                                final int retryCount) {
        int i = 0;
        String result = null;
        while (i <= retryCount) {
            try {
                result = execute(httpRequestBase, timeout, timeUnit);
                return result;
            } catch (NoHttpResponseException e) {
                if (i == retryCount) {
                    throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
                }
            } catch (Exception e) {
                log.error("ExceptionLogHttp " + e.getMessage(), e);
                throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
            }
            i++;
            log.info("HttpClient retrycount:{}", i);
        }

        return result;

    }


    public static String execute(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit) throws Exception {
        long startTimeNano = System.nanoTime();
        Stopwatch sp = Stopwatch.createStarted();
        if (httpRequestBase == null) {
            throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE, "httpRequestBase is null!");
        }
        CloseableHttpResponse response = null;
        String responseContent = null;
        String url = null;
        try {
            // 创建默认的httpClient实例.
            url = httpRequestBase.getURI().toString();
            RequestConfig config = requestConfig;
            if (timeout > 0L && timeUnit != null) {
                int timeoutInMS = Math.toIntExact(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
                config = RequestConfig.custom().setSocketTimeout(timeoutInMS).setConnectTimeout(timeoutInMS)
                        .setConnectionRequestTimeout(timeoutInMS).build();
            }
            httpRequestBase.setConfig(config);
            // 在http header中存入requestId
            setRequestId(httpRequestBase);
            // 执行请求
            response = getHttpClient().execute(httpRequestBase);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, entity.getContentEncoding() != null ? entity.getContentEncoding().getValue() : DEFAULT_CHARSET.displayName());
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 400) {
                    throw new SysException(ExceptionErrorCode.SYS_ERROR_CODE,
                            "httpStatus: " + statusCode + ", result: " + responseContent);
                }
            }
            return responseContent;
        } finally {
            closeResources(response, httpRequestBase);
            tryCloseTrace(response);
            showSlowHttpClient(sp, url);
        }
    }


    public static void execute(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit,
                               Consumer<InputStream> consumer) throws Exception {
        long startTimeNano = System.nanoTime();
        Stopwatch sp = Stopwatch.createStarted();
        if (httpRequestBase == null) {
            throw new SysException(SYS_ERROR_CODE, "httpRequestBase is null!");
        }
        CloseableHttpResponse response = null;
        String url = null;
        try {
            // 创建默认的httpClient实例.
            url = httpRequestBase.getURI().toString();
            RequestConfig config = requestConfig;
            if (timeout > 0L && timeUnit != null) {
                int timeoutInMS = Math.toIntExact(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
                config = RequestConfig.custom().setSocketTimeout(timeoutInMS).setConnectTimeout(timeoutInMS)
                        .setConnectionRequestTimeout(timeoutInMS).build();
            }
            httpRequestBase.setConfig(config);
            // 在http header中存入requestId
            setRequestId(httpRequestBase);
            // 执行请求
            response = getHttpClient().execute(httpRequestBase);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                consumer.accept(new ByteArrayInputStream(EntityUtils.toByteArray(entity)));
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 400) {
                    throw new SysException(SYS_ERROR_CODE,
                            "httpStatus: " + statusCode + ", result: " + EntityUtils.toString(entity, entity.getContentEncoding() == null ?
                                    entity.getContentEncoding().getValue() : DEFAULT_CHARSET.displayName()));
                }
            }
        } finally {
            closeResources(response, httpRequestBase);
            tryCloseTrace(response);
            showSlowHttpClient(sp, url);
        }
    }


    private static void showSlowHttpClient(Stopwatch sp, String url) {
        long useTime = sp.stop().elapsed(TimeUnit.MILLISECONDS);
        if (useTime > receiveTimeoutMs) {
            log.info("SlowLogHttp Http Client slow show: cast_time:{}, http_url:{}", useTime, url);
        }
    }

    private static void closeResources(CloseableHttpResponse httpResponse, HttpRequestBase httpRequestBase) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        try {
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private static void setRequestId(HttpMessage httpMessage) {
        httpMessage.addHeader(REQUEST_ID, MDC.get(REQUEST_ID));
        httpMessage.addHeader(USER_AGENT, IBJ);
        httpMessage.addHeader(HEADER_REQ_TIME, String.valueOf(DateUtil.getTimestampInMillis()));
    }


}
