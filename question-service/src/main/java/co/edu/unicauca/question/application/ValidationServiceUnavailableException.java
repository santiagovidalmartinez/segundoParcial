package co.edu.unicauca.question.application;

/** No se pudo contactar al Validation Service (HTTP 503 hacia el cliente). */
public class ValidationServiceUnavailableException extends RuntimeException {

    public ValidationServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
