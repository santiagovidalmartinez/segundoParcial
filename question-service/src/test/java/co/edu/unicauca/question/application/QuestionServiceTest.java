package co.edu.unicauca.question.application;

import co.edu.unicauca.question.application.command.CreateQuestionCommand;
import co.edu.unicauca.question.application.command.SubmitQuestionForReview;
import co.edu.unicauca.question.application.command.SubmitReceipt;
import co.edu.unicauca.question.application.port.CommandPublisher;
import co.edu.unicauca.question.application.port.QuestionValidator;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionServiceTest {

    /** Repositorio en memoria: prueba el caso de uso sin base de datos. */
    public static class InMemoryQuestionRepository implements QuestionRepository {
        public final Map<Long, Question> store = new LinkedHashMap<>();
        long next = 1;

        @Override
        public Question save(Question q) {
            Question saved = Question.rehydrate(q.getId() != null ? q.getId() : next++, q.getStatement(),
                    q.getOptions(), q.getCorrectKey(), q.getJustification(), q.getStatus());
            store.put(saved.getId(), saved);
            return saved;
        }

        @Override
        public Optional<Question> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<Question> findAll() {
            return new ArrayList<>(store.values());
        }
    }

    public static class FakeValidator implements QuestionValidator {
        public List<String> errorsToReturn = List.of();
        public int calls = 0;

        @Override
        public void validate(CreateQuestionCommand command) {
            calls++;
            if (!errorsToReturn.isEmpty()) {
                throw new QuestionValidationException(errorsToReturn);
            }
        }
    }

    public static class FakePublisher implements CommandPublisher {
        public final List<SubmitQuestionForReview> published = new ArrayList<>();

        @Override
        public void publish(SubmitQuestionForReview command) {
            published.add(command);
        }
    }

    private InMemoryQuestionRepository repository;
    private FakeValidator validator;
    private FakePublisher publisher;
    private QuestionService service;

    private final CreateQuestionCommand validCommand =
            new CreateQuestionCommand("Enunciado", List.of("a", "b", "c", "d"), "B", "Porque si");

    @BeforeEach
    void setUp() {
        repository = new InMemoryQuestionRepository();
        validator = new FakeValidator();
        publisher = new FakePublisher();
        service = new QuestionService(validator, repository, publisher);
    }

    @Test
    void crearConValidacionExitosaGuardaEnDraft() {
        Question created = service.create(validCommand);
        assertNotNull(created.getId());
        assertEquals(QuestionStatus.DRAFT, created.getStatus());
        assertEquals(1, validator.calls);
        assertEquals(1, repository.store.size());
    }

    @Test
    void crearConValidacionFallidaPropagaLosErroresYNoGuardaNada() {
        validator.errorsToReturn = List.of("Faltan opciones", "Falta justificacion");
        QuestionValidationException ex = assertThrows(QuestionValidationException.class, () -> service.create(validCommand));
        assertEquals(List.of("Faltan opciones", "Falta justificacion"), ex.getErrors());
        assertTrue(repository.store.isEmpty());
    }

    @Test
    void enviarARevisionPublicaElComandoYRespondeProcessing() {
        Question created = service.create(validCommand);
        SubmitReceipt receipt = service.submitForReview(created.getId());

        assertEquals("PROCESSING", receipt.status());
        assertNotNull(receipt.operationId());
        assertEquals(1, publisher.published.size());
        SubmitQuestionForReview command = publisher.published.get(0);
        assertEquals(created.getId(), command.questionId());
        assertEquals(receipt.operationId(), command.operationId());
    }

    @Test
    void enviarARevisionNoCambiaElEstadoLocal() {
        Question created = service.create(validCommand);
        service.submitForReview(created.getId());
        assertEquals(QuestionStatus.DRAFT, service.get(created.getId()).getStatus());
    }

    @Test
    void enviarARevisionDePreguntaInexistenteFalla() {
        assertThrows(QuestionNotFoundException.class, () -> service.submitForReview(99L));
        assertTrue(publisher.published.isEmpty());
    }

    @Test
    void cadaEnvioGeneraUnOperationIdDistinto() {
        Question created = service.create(validCommand);
        String first = service.submitForReview(created.getId()).operationId();
        String second = service.submitForReview(created.getId()).operationId();
        assertNotEquals(first, second);
    }
}
