package co.edu.unicauca.question.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Modelo de dominio puro de una pregunta de seleccion multiple con unica respuesta.
 * NO tiene anotaciones de JPA ni de Spring: la representacion relacional vive en
 * {@code infrastructure.jpa.record.QuestionRecord} y se traduce con {@code QuestionDomainMapper}.
 */
public class Question {

    private static final String[] LABELS = {"A", "B", "C", "D", "E", "F"};

    private final Long id;
    private final String statement;
    private final List<AnswerOption> options;
    private final String correctKey;
    private final String justification;
    private final QuestionStatus status;

    private Question(Long id, String statement, List<AnswerOption> options, String correctKey,
                     String justification, QuestionStatus status) {
        this.id = id;
        this.statement = statement;
        this.options = List.copyOf(options);
        this.correctKey = correctKey;
        this.justification = justification;
        this.status = Objects.requireNonNull(status, "status");
    }

    /** Crea una pregunta nueva en estado DRAFT; las opciones reciben las etiquetas A, B, C, D... segun su orden. */
    public static Question draft(String statement, List<String> optionTexts, String correctKey, String justification) {
        Objects.requireNonNull(optionTexts, "optionTexts");
        if (optionTexts.size() > LABELS.length) {
            throw new IllegalArgumentException("Una pregunta admite como maximo " + LABELS.length + " opciones.");
        }
        List<AnswerOption> options = new ArrayList<>();
        for (int i = 0; i < optionTexts.size(); i++) {
            options.add(new AnswerOption(LABELS[i], optionTexts.get(i)));
        }
        String key = correctKey == null ? null : correctKey.strip().toUpperCase(Locale.ROOT);
        return new Question(null, statement, options, key, justification, QuestionStatus.DRAFT);
    }

    /** Reconstruye una pregunta ya guardada (la usa el mapeador al leer de la base de datos). */
    public static Question rehydrate(Long id, String statement, List<AnswerOption> options, String correctKey,
                                     String justification, QuestionStatus status) {
        return new Question(id, statement, options, correctKey, justification, status);
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public List<AnswerOption> getOptions() {
        return options;
    }

    public String getCorrectKey() {
        return correctKey;
    }

    public String getJustification() {
        return justification;
    }

    public QuestionStatus getStatus() {
        return status;
    }
}
