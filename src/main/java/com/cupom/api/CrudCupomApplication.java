package com.cupom.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação CRUD Cupom
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "CRUD Cupom API",
        version = "1.0.0",
        description = "API REST para gerenciamento de cupons de desconto",
        contact = @Contact(
            name = "Samuel Dantas",
            email = "samueldantasbarbosa@hotmail.com"
        )
    )
)
public class CrudCupomApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudCupomApplication.class, args);
    }
}
