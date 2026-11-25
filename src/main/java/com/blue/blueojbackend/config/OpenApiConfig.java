package com.blue.blueojbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenAPI 文档元数据配置。
 */
@Configuration(proxyBeanMethods = false)
@Profile({"dev", "test"})
@OpenAPIDefinition(
        info = @Info(
                title = "Blue OJ 后端接口文档",
                description = "Blue OJ 单体架构阶段后端接口",
                version = "1.0.0",
                contact = @Contact(name = "Blue OJ")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
