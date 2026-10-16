package com.example.auxprac2.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI (Swagger UI).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI booksOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API de Libros - Practica 2")
                .description("API REST desarrollada con Spring Boot que permite administrar " +
                        "los libros de una biblioteca mediante operaciones CRUD. " +
                        "Los datos se mantienen en memoria, no se utiliza base de datos.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Nicolas Garnica")
                        .url("https://github.com/nicolas-garnica"))
                .license(new License().name("Uso educativo")));
    }
}