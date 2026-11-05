package co.edu.unicauca.question.api.dto;

/** Cuerpo de la respuesta 202 Accepted: identificador de la operacion y estado PROCESSING. */
public record SubmitResponse(String operationId, String status) {
}
