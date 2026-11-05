package co.edu.unicauca.lifecycle;

import co.edu.unicauca.lifecycle.domain.QuestionLifecycle;
import co.edu.unicauca.lifecycle.domain.QuestionState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionLifecycleTest {

    @Test
    void unaPreguntaNuevaEstaEnDraft() {
        assertEquals(QuestionState.DRAFT, QuestionLifecycle.newDraft(1L).getState());
    }

    @Test
    void draftPasaAPendingReview() {
        QuestionLifecycle lifecycle = QuestionLifecycle.newDraft(1L);
        lifecycle.submitForReview();
        assertEquals(QuestionState.PENDING_REVIEW, lifecycle.getState());
    }

    @Test
    void noSePuedeEnviarDosVeces() {
        QuestionLifecycle lifecycle = QuestionLifecycle.newDraft(1L);
        lifecycle.submitForReview();
        assertFalse(lifecycle.canSubmitForReview());
        assertThrows(IllegalStateException.class, lifecycle::submitForReview);
    }
}
