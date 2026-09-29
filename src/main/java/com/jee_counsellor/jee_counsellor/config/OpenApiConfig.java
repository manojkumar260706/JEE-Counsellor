package com.jee_counsellor.jee_counsellor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JEE Counsellor Backend API")
                        .version("1.0.0")
                        .description("RESTful API documentation for JEE Counsellor candidate onboarding, authentication, and seat counselling prediction.")
                        .contact(new Contact().name("JEE Counsellor Team"))
                        .license(new License().name("Apache 2.0")));
    }
}
