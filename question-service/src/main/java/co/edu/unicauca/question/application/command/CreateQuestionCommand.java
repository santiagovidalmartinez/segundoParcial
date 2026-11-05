package co.edu.unicauca.question.application.command;

import java.util.List;

/** Datos para crear una pregunta (caso de uso 'crear pregunta'). */
public record CreateQuestionCommand(String statement, List<String> options, String correctKey, String justification) {
}
