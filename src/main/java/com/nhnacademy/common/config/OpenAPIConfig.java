package com.nhnacademy.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI swaggerConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("NHN Bookstore Coupon API")
                        .description("도서 쿠폰 발급 및 관리 API 문서입니다.")
                        .version("v1.0.0"));
    }
}
