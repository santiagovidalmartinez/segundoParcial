package co.edu.unicauca.question.application.command;

/** Recibo inmediato del envio a revision: la operacion queda PROCESSING mientras Lifecycle la atiende. */
public record SubmitReceipt(String operationId, String status) {
}
