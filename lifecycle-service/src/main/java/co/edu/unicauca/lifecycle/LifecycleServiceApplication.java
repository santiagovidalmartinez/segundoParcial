package co.edu.unicauca.lifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Lifecycle Service (:8084): unica autoridad sobre el estado de las preguntas. Consume comandos de RabbitMQ. */
@SpringBootApplication
public class LifecycleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifecycleServiceApplication.class, args);
    }
}
