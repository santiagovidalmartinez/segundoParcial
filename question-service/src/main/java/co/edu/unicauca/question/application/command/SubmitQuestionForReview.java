package co.edu.unicauca.question.application.command;

/**
 * Comando SubmitQuestionForReview: se publica en RabbitMQ para que Lifecycle Service cambie
 * el estado de la pregunta a PENDING_REVIEW. {@code requestedAt} va en formato ISO-8601.
 */
public record SubmitQuestionForReview(String operationId, Long questionId, String requestedAt) {
}
