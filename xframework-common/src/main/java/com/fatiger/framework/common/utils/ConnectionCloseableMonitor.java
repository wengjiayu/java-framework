package com.fatiger.framework.common.utils;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * @author wengjiayu
 * @date 03/01/2018
 * @E-mail wengjiayu521@163.com
 */
public class ConnectionCloseableMonitor extends Thread {

    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;


    public ConnectionCloseableMonitor(PoolingHttpClientConnectionManager connManager) {
        connMgr = connManager;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(500);
                    // 关闭失效的连接
                    connMgr.closeExpiredConnections();
                    // 可选的, 关闭30秒内不活动的连接
                    connMgr.closeIdleConnections(10, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }


}
