package co.edu.unicauca.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Question Service (:8083): crea preguntas, las persiste con JPA y publica comandos en RabbitMQ. */
@SpringBootApplication
public class QuestionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuestionServiceApplication.class, args);
    }
}
