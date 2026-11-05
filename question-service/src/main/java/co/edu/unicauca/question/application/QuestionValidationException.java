package co.edu.unicauca.question.application;

import java.util.List;

/** La pregunta no supero la validacion estructural del Validation Service (HTTP 400 hacia el cliente). */
public class QuestionValidationException extends RuntimeException {

    private final List<String> errors;

    public QuestionValidationException(List<String> errors) {
        super("La pregunta no supera la validacion estructural.");
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
