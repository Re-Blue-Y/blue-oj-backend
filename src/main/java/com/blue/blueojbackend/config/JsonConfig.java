package com.blue.blueojbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Spring MVC JSON 配置。
 */
@Configuration(proxyBeanMethods = false)
public class JsonConfig {

    /**
     * 将 Long 统一序列化为字符串，避免 JavaScript 大整数精度丢失。
     */
    @Bean
    public SimpleModule longToStringModule() {
        SimpleModule module = new SimpleModule("blue-oj-long-to-string");
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        return module;
    }
}
