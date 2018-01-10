package com.fatiger.framework.common.utils;

import org.slf4j.Logger;
import org.slf4j.MDC;

import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.GeneralKey.CLIENT_IP;
import static com.fatiger.framework.constant.com.fatiger.framework.constant.dictionary.GeneralKey.SERVER_IP;


public class TransferSlowLogger {

    protected TransferSlowLogger() {
    }

    protected static void markSlowLog(long requestTime, long timeout, String path, Logger log) {
        try {
            if (requestTime > 0l) {
                long receiveTimestamp = DateUtil.getTimestampInMillis();
                long timeDifference = Math.abs(receiveTimestamp - requestTime);
                if (timeDifference >= timeout) {
                    log.warn("SlowLogHttp transfer_time_out(ms):{}; sent_at:{}; received_at:{}; client_ip:{}; server_ip:{}; request_url:{};",
                            timeDifference, DateUtil.dateToString(requestTime), DateUtil.dateToString(receiveTimestamp),
                            MDC.get(CLIENT_IP), MDC.get(SERVER_IP), path);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


}
