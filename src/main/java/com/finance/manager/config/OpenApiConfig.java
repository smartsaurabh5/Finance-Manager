package com.finance.manager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI financeManagerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Manager API")
                        .version("1.0.0")
                        .description("Personal finance manager backend APIs using session-cookie authentication"));
    }
}
