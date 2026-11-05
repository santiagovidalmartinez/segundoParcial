package co.edu.unicauca.question.infrastructure.jpa.record;

import co.edu.unicauca.question.domain.model.QuestionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad relacional de la pregunta (tabla questions). Es la unica clase que lleva anotaciones de JPA;
 * el modelo de dominio {@code Question} permanece puro. La relacion 1 a muchos con las opciones usa
 * cascade = ALL: al guardar la pregunta se guardan sus opciones.
 */
@Entity
@Table(name = "questions")
public class QuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String statement;

    @Column(name = "correct_key", nullable = false, length = 1)
    private String correctKey;

    @Column(nullable = false, length = 2000)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionStatus status;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "option_order")
    private List<AnswerOptionRecord> options = new ArrayList<>();

    public QuestionRecord() {
        // requerido por JPA
    }

    public void addOption(AnswerOptionRecord option) {
        options.add(option);
        option.setQuestion(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getCorrectKey() {
        return correctKey;
    }

    public void setCorrectKey(String correctKey) {
        this.correctKey = correctKey;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public List<AnswerOptionRecord> getOptions() {
        return options;
    }
}
