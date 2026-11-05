package co.edu.unicauca.question.domain.model;

/** Estados de una pregunta. El Question Service solo crea en DRAFT; Lifecycle Service es quien la pasa a PENDING_REVIEW. */
public enum QuestionStatus {
    DRAFT,
    PENDING_REVIEW
}
