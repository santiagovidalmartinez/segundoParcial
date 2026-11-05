package co.edu.unicauca.question.infrastructure.jpa.mapper;

import co.edu.unicauca.question.domain.model.AnswerOption;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.infrastructure.jpa.record.AnswerOptionRecord;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

import java.util.List;

/**
 * Mapeador bidireccional entre el modelo de dominio (Question) y la entidad JPA (QuestionRecord).
 * Es la unica clase que conoce los dos modelos.
 */
public final class QuestionDomainMapper {

    private QuestionDomainMapper() {
    }

    public static QuestionRecord toRecord(Question question) {
        QuestionRecord record = new QuestionRecord();
        record.setId(question.getId());
        record.setStatement(question.getStatement());
        record.setCorrectKey(question.getCorrectKey());
        record.setJustification(question.getJustification());
        record.setStatus(question.getStatus());
        for (AnswerOption option : question.getOptions()) {
            record.addOption(new AnswerOptionRecord(option.label(), option.text()));
        }
        return record;
    }

    public static Question toDomain(QuestionRecord record) {
        List<AnswerOption> options = record.getOptions().stream()
                .map(o -> new AnswerOption(o.getLabel(), o.getText()))
                .toList();
        return Question.rehydrate(record.getId(), record.getStatement(), options,
                record.getCorrectKey(), record.getJustification(), record.getStatus());
    }
}
