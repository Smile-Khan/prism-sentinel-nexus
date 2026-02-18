package com.smile.prism.search.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the OpenAPI 3.0 specification.
 * Provides the metadata for the Swagger UI discovery portal.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prism Discovery Hub API")
                        .version("1.0.0")
                        .description("High-performance Search Engine for the Sentinel-Prism Nexus Ecosystem. " +
                                "Supports fuzzy matching, match highlighting, and CDC-based projections.")
                        .contact(new Contact()
                                .name("Pathan Ismailkhan (Smile-Khan)")
                                .email("specialist@nexus.com")
                                .url("https://github.com/Smile-Khan"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}