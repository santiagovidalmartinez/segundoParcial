package co.edu.unicauca.lifecycle.domain;

/**
 * Estado del ciclo de vida de una pregunta. Regla: solo una pregunta en DRAFT puede pasar a
 * PENDING_REVIEW. Una pregunta que Lifecycle todavia no conoce se considera nueva (DRAFT).
 */
public class QuestionLifecycle {

    private final Long questionId;
    private QuestionState state;

    public QuestionLifecycle(Long questionId, QuestionState state) {
        this.questionId = questionId;
        this.state = state;
    }

    public static QuestionLifecycle newDraft(Long questionId) {
        return new QuestionLifecycle(questionId, QuestionState.DRAFT);
    }

    public boolean canSubmitForReview() {
        return state == QuestionState.DRAFT;
    }

    /** DRAFT -> PENDING_REVIEW. Lanza IllegalStateException si el estado actual no lo permite. */
    public void submitForReview() {
        if (!canSubmitForReview()) {
            throw new IllegalStateException("No se puede enviar a revision una pregunta en estado " + state + ".");
        }
        state = QuestionState.PENDING_REVIEW;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public QuestionState getState() {
        return state;
    }
}
