package com.fatiger.framework.core.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wengjiayu on 01/11/2017.
 * E-mail wengjiayu521@163.com
 */
@Slf4j
@SuppressWarnings("NullableProblems")
public class BaseProperties extends PropertyPlaceholderConfigurer {

    private static Map<String, Object> ctxPropertiesMap = new ConcurrentHashMap<>();

    private static ConfigurableConversionService conversionService = new DefaultConversionService();

    private static final AtomicBoolean LOADED_FLAG = new AtomicBoolean(true);

    private static final String APP_PROPERTIES = "applicationConfigurationProperties";

    private static final String ENV = "spring.profiles.active";

    private static final String CLASS_PATH_RESOURCE = "class path resource";

    private static final String CHARACTER = "[";

    private static final String CHARACTERTWO = "]";

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
        log.info("==========================PropertyPlaceholderConfigurer==============================");
        log.info("========================================================");
        log.info("========================================================");
        super.processProperties(beanFactoryToProcess, props);
        if (LOADED_FLAG.get()) {
            loadData(props);
        }
    }

    protected static void setConfig(String key, String value) {
        ctxPropertiesMap.put(key, value);
    }

    public static void loadData(Properties props) {
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            ctxPropertiesMap.put(keyStr, value);
        }

    }

    public static Object getProperty(String key) {
        return ctxPropertiesMap.get(key);
    }

    public static String getString(String key) {
        return (String) ctxPropertiesMap.get(key);
    }

    public static Map<String, Object> getAll() {
        return Collections.unmodifiableMap(ctxPropertiesMap);
    }

    public static boolean containsProperty(String key) {
        return ctxPropertiesMap.containsKey(key);
    }

    public static boolean setProperty(String key, String value) {
        if (key == null || value == null) {
            return false;
        }

        ctxPropertiesMap.put(key, value);

        return true;

    }

    public static String getProperty(String key, String defaultValue) {
        Object value = ctxPropertiesMap.get(key);
        return value == null ? defaultValue : (String) value;
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        Object value = ctxPropertiesMap.get(key);
        if (value == null) {
            return null;
        }
        return conversionService.convert(value, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        Object value = ctxPropertiesMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return conversionService.convert(value, targetType);
    }

    public static void loadData(Environment event) {
        log.info("==========================Init BaseProperties==============================");

        if (LOADED_FLAG.get()) {
            ConfigurableEnvironment environment = (ConfigurableEnvironment) event;

            Iterator<PropertySource<?>> iter = environment.getPropertySources().iterator();
            while (iter.hasNext()) {
                propertyHandler(iter);
            }

            PropertySource<?> appPS = environment.getPropertySources().get(APP_PROPERTIES);// TODO: 02/11/2017 不同阶段内容不一样
            if (appPS != null) {
                BaseProperties.setPropertySource(appPS);
            }

            String env = event.getProperty(ENV);
            if (env != null) {
                ctxPropertiesMap.put(ENV, env);
            }
            LOADED_FLAG.set(false);
        }
    }

    private static void setPropertySource(PropertySource<?> ps) {
        EnumerablePropertySource<?> eps = (EnumerablePropertySource<?>) ps;
        for (String key : eps.getPropertyNames()) {
            ctxPropertiesMap.put(key, eps.getProperty(key));
        }
    }

    private static void propertyHandler(Iterator<PropertySource<?>> iter) {
        PropertySource<?> ps = iter.next();
        String name = ps.getName();
        if (name.startsWith(CLASS_PATH_RESOURCE)) {
            try {
                String propertiesName = name.substring(name.indexOf(CHARACTER) + 1, name.lastIndexOf(CHARACTERTWO));
                loadData(PropertiesLoaderUtils.loadAllProperties(propertiesName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}