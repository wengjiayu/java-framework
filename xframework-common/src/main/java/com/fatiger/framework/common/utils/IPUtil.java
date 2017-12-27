package com.fatiger.framework.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

/**
 * @author wengjiayu
 * @date 25/12/2017
 * @E-mail wengjiayu521@163.com
 */
@Slf4j
public final class IPUtil {
    private static final String DEFAULT_LOCAL_IP = "127.0.0.1";

    private static final String LOCAL_IP = getLocalIp();

    public static String getOneLocalIP() {
        return LOCAL_IP;
    }

    private IPUtil() {
    }

    private static String getLocalIp() {
        Enumeration<?> allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
            return DEFAULT_LOCAL_IP;
        }
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();

            Optional<String> possible = Collections.list(addresses).stream().filter(x -> x instanceof Inet4Address)
                    .map(InetAddress::getHostAddress).filter(x -> !x.equals(DEFAULT_LOCAL_IP)).findFirst();

            if (possible.isPresent()) {
                return possible.get();
            }
        }
        return DEFAULT_LOCAL_IP;
    }
}
