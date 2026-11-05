package co.edu.unicauca.question.domain;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void laPreguntaNuevaNaceEnDraftSinId() {
        Question q = Question.draft("Enunciado", List.of("a", "b", "c", "d"), "b", "Porque si");
        assertEquals(QuestionStatus.DRAFT, q.getStatus());
        assertNull(q.getId());
    }

    @Test
    void lasOpcionesRecibenEtiquetasAaD() {
        Question q = Question.draft("Enunciado", List.of("uno", "dos", "tres", "cuatro"), "A", "J");
        assertEquals(List.of("A", "B", "C", "D"), q.getOptions().stream().map(o -> o.label()).toList());
        assertEquals("tres", q.getOptions().get(2).text());
    }

    @Test
    void laClaveSeNormalizaAMayusculas() {
        assertEquals("C", Question.draft("E", List.of("a", "b", "c", "d"), " c ", "J").getCorrectKey());
    }

    @Test
    void rehydrateConservaIdYEstado() {
        Question q = Question.rehydrate(7L, "E", List.of(), "A", "J", QuestionStatus.PENDING_REVIEW);
        assertEquals(7L, q.getId());
        assertEquals(QuestionStatus.PENDING_REVIEW, q.getStatus());
    }

    @Test
    void laListaDeOpcionesEsInmutable() {
        Question q = Question.draft("E", List.of("a", "b", "c", "d"), "A", "J");
        assertThrows(UnsupportedOperationException.class, () -> q.getOptions().clear());
    }
}
