package co.edu.unicauca.question.api.dto;

import co.edu.unicauca.question.domain.model.Question;

import java.util.List;

public record QuestionResponse(Long id, String statement, List<OptionDto> options, String correctKey,
                               String justification, String status) {

    public record OptionDto(String label, String text) {
    }

    public static QuestionResponse from(Question q) {
        return new QuestionResponse(q.getId(), q.getStatement(),
                q.getOptions().stream().map(o -> new OptionDto(o.label(), o.text())).toList(),
                q.getCorrectKey(), q.getJustification(), q.getStatus().name());
    }
}
