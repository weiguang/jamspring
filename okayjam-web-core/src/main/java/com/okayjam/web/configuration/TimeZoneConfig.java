package com.okayjam.web.configuration;

import com.okayjam.web.common.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;
import org.springframework.core.annotation.Order;

/**
 * 时区配置类
 * 从配置文件读取时区设置，并在应用启动时初始化
 */
@Order(0)
@Configuration
public class TimeZoneConfig {

    private static final Logger logger = LoggerFactory.getLogger(TimeZoneConfig.class);

    /**
     * 从配置文件读取时区，默认为 UTC
     */
    @Value("${app.timezone:UTC}")
    private String timezone;

    /**
     * 从配置文件读取日期格式，默认为 yyyy-MM-dd HH:mm:ss
     */
    @Value("${app.date-format:yyyy-MM-dd HH:mm:ss}")
    private String dateFormat;

    /**
     * 应用启动时初始化时区设置
     */
    @PostConstruct
    public void init() {
        TimeZone timeZone = TimeZone.getTimeZone(timezone);
        
        // 设置 JVM 默认时区
        TimeZone.setDefault(timeZone);
        
        // 同步设置 JsonUtil 的时区和日期格式
        JsonUtil.configure(timeZone, dateFormat);
        
        logger.info("Application configured: timezone={} ({}), dateFormat={}", 
                timezone, timeZone.getDisplayName(), dateFormat);
    }

    /**
     * 获取当前配置的时区
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * 获取当前配置的 TimeZone 对象
     */
    public TimeZone getTimeZoneObject() {
        return TimeZone.getTimeZone(timezone);
    }

    /**
     * 获取当前配置的日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }
}
