package co.edu.unicauca.question.api.dto;

import java.util.List;

/** Cuerpo de POST /questions. La validacion estructural la hace el Validation Service, no este DTO. */
public record CreateQuestionRequest(String statement, List<String> options, String correctKey, String justification) {
}
