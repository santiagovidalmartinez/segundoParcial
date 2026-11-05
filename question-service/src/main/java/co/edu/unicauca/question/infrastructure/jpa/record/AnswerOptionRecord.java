package co.edu.unicauca.question.infrastructure.jpa.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Representacion relacional de una opcion de respuesta (lado propietario de la relacion con la pregunta). */
@Entity
@Table(name = "answer_options")
public class AnswerOptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1)
    private String label;

    @Column(nullable = false, length = 500)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionRecord question;

    protected AnswerOptionRecord() {
        // requerido por JPA
    }

    public AnswerOptionRecord(String label, String text) {
        this.label = label;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public QuestionRecord getQuestion() {
        return question;
    }

    void setQuestion(QuestionRecord question) {
        this.question = question;
    }
}
