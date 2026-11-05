package co.edu.unicauca.validation.api;

import java.util.List;

/** Cuerpo de POST /validate: los datos de la pregunta a validar. */
public record QuestionToValidate(String statement, List<String> options, String correctKey, String justification) {
}
