package co.edu.unicauca.question.application;

import co.edu.unicauca.question.application.command.CreateQuestionCommand;
import co.edu.unicauca.question.application.command.SubmitQuestionForReview;
import co.edu.unicauca.question.application.command.SubmitReceipt;
import co.edu.unicauca.question.application.port.CommandPublisher;
import co.edu.unicauca.question.application.port.QuestionValidator;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso del Question Service. Depende solo de contratos (puertos y repositorio de dominio):
 * no conoce JPA, RestTemplate ni RabbitMQ.
 */
@Service
public class QuestionService {

    public static final String STATUS_PROCESSING = "PROCESSING";

    private final QuestionValidator validator;
    private final QuestionRepository repository;
    private final CommandPublisher publisher;

    public QuestionService(QuestionValidator validator, QuestionRepository repository, CommandPublisher publisher) {
        this.validator = validator;
        this.repository = repository;
        this.publisher = publisher;
    }

    /**
     * Punto 1: primero invoca de forma SINCRONA al Validation Service. Si responde 400, lanza
     * QuestionValidationException y NO se guarda nada. Si responde 200, la pregunta se crea en DRAFT y se guarda.
     */
    public Question create(CreateQuestionCommand command) {
        validator.validate(command);
        Question draft = Question.draft(command.statement(), command.options(), command.correctKey(), command.justification());
        return repository.save(draft);
    }

    public Question get(Long id) {
        return repository.findById(id).orElseThrow(() -> new QuestionNotFoundException(id));
    }

    public List<Question> list() {
        return repository.findAll();
    }

    /**
     * Punto 3: publica el comando SubmitQuestionForReview y responde de inmediato (no espera a Lifecycle).
     * El cambio de estado a PENDING_REVIEW lo hace Lifecycle Service en segundo plano.
     */
    public SubmitReceipt submitForReview(Long id) {
        Question question = get(id);
        String operationId = UUID.randomUUID().toString();
        publisher.publish(new SubmitQuestionForReview(operationId, question.getId(), Instant.now().toString()));
        return new SubmitReceipt(operationId, STATUS_PROCESSING);
    }
}
