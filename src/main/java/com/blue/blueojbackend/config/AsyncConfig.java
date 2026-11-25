package com.blue.blueojbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 本地异步任务能力配置。
 */
@Configuration(proxyBeanMethods = false)
@EnableAsync
public class AsyncConfig {
}
