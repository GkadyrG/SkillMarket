package com.example.dotalink.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dotalinkOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("DotaLink API")
                        .version("1.0")
                        .description("OpenAPI documentation for DotaLink endpoints")
                        .contact(new Contact().name("DotaLink team")))
                .servers(List.of(new Server().url("/").description("Current server")));
    }
}
