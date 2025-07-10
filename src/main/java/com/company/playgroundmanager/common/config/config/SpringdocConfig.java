package com.company.playgroundmanager.common.config.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@OpenAPIDefinition
@Configuration
public class SpringdocConfig {
    private static final String VERSION = "0.0.1";

    @Bean
    public OpenAPI baseOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Playgrounds")
                        .version(VERSION).description("Playgrounds API Documentation"));
    }
}

