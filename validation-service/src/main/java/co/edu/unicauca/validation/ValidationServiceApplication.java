package co.edu.unicauca.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Validation Service (:8082): aplica las reglas de validacion estructural de las preguntas (HU03). */
@SpringBootApplication
public class ValidationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidationServiceApplication.class, args);
    }
}
