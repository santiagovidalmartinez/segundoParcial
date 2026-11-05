package co.edu.unicauca.lifecycle.application;

/** Comando recibido desde RabbitMQ (mismo contenido JSON que publica Question Service). */
public record SubmitQuestionForReview(String operationId, Long questionId, String requestedAt) {
}
