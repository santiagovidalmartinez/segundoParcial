package co.edu.unicauca.lifecycle;

import co.edu.unicauca.lifecycle.application.LifecycleService;
import co.edu.unicauca.lifecycle.application.SubmitQuestionForReview;
import co.edu.unicauca.lifecycle.domain.QuestionState;
import co.edu.unicauca.lifecycle.infrastructure.memory.InMemoryLifecycleRepository;
import co.edu.unicauca.lifecycle.infrastructure.messaging.SubmitQuestionListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LifecycleServiceTest {

    private LifecycleService service;

    @BeforeEach
    void setUp() {
        service = new LifecycleService(new InMemoryLifecycleRepository());
    }

    private SubmitQuestionForReview command(String operationId, long questionId) {
        return new SubmitQuestionForReview(operationId, questionId, "2026-11-05T14:00:00Z");
    }

    @Test
    void elComandoPasaLaPreguntaAPendingReview() {
        assertEquals(QuestionState.PENDING_REVIEW, service.handle(command("op-1", 1L)));
        assertEquals(QuestionState.PENDING_REVIEW, service.stateOf(1L).orElseThrow());
    }

    @Test
    void marcaLaOperacionComoProcesada() {
        assertFalse(service.isOperationProcessed("op-1"));
        service.handle(command("op-1", 1L));
        assertTrue(service.isOperationProcessed("op-1"));
    }

    @Test
    void esIdempotenteSiElMismoMensajeLlegaDosVeces() {
        service.handle(command("op-1", 1L));
        assertEquals(QuestionState.PENDING_REVIEW, service.handle(command("op-1", 1L)));
    }

    @Test
    void unSegundoEnvioDeLaMismaPreguntaNoLanzaExcepcion() {
        service.handle(command("op-1", 1L));
        assertDoesNotThrow(() -> service.handle(command("op-2", 1L)));
        assertEquals(QuestionState.PENDING_REVIEW, service.stateOf(1L).orElseThrow());
    }

    @Test
    void preguntaDesconocidaNoTieneEstadoHastaQueSeEnvia() {
        assertTrue(service.stateOf(5L).isEmpty());
    }

    @Test
    void elListenerDelegaEnElServicio() {
        new SubmitQuestionListener(service).onSubmitQuestion(command("op-9", 9L));
        assertEquals(QuestionState.PENDING_REVIEW, service.stateOf(9L).orElseThrow());
    }
}
