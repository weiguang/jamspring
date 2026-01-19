package com.okayjam.web;

import org.apache.ibatis.logging.LogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.okayjam.web.*"})
@MapperScan(basePackages = {"com.okayjam.web.dao"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class ServiceApplication {

    public static void main(String[] args) {
        // 时区设置已移至 TimeZoneConfig 配置类，通过 application.yml 中的 app.timezone 配置
        SpringApplication.run(ServiceApplication.class, args);
    }

}