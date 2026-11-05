package co.edu.unicauca.question.application;

public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(Long id) {
        super("No existe la pregunta con id " + id + ".");
    }
}
