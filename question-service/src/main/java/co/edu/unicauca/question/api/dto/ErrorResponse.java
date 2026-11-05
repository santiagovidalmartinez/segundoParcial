package co.edu.unicauca.question.api.dto;

import java.util.List;

public record ErrorResponse(String message, List<String> errors) {
}
