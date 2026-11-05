package co.edu.unicauca.question.application.port;

import co.edu.unicauca.question.application.command.SubmitQuestionForReview;

/** Puerto de salida para publicar comandos de forma asincrona (la implementacion usa RabbitMQ). */
public interface CommandPublisher {

    void publish(SubmitQuestionForReview command);
}
