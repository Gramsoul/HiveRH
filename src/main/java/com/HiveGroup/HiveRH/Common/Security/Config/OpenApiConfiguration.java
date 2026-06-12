package com.HiveGroup.HiveRH.Common.Security.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI hiveRhOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HiveRH API")
                        .description("API para la gestion de recursos humanos de HiveRH.")
                        .version("v1")
                        .contact(new Contact()
                                .name("HiveRH Team"))
                        .license(new License()
                                .name("Uso interno")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtenido desde /api/auth/login")));
    }
}