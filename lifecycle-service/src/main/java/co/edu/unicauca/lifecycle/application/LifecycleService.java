package co.edu.unicauca.lifecycle.application;

import co.edu.unicauca.lifecycle.domain.LifecycleRepository;
import co.edu.unicauca.lifecycle.domain.QuestionLifecycle;
import co.edu.unicauca.lifecycle.domain.QuestionState;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LifecycleService {

    private final LifecycleRepository repository;

    public LifecycleService(LifecycleRepository repository) {
        this.repository = repository;
    }

    /**
     * Procesa el comando SubmitQuestionForReview: DRAFT -> PENDING_REVIEW. Es idempotente (si RabbitMQ
     * entrega dos veces el mismo mensaje no pasa nada) y NO lanza excepciones por estados invalidos, para
     * que el mensaje no vuelva a la cola en un ciclo infinito.
     *
     * @return el estado resultante de la pregunta
     */
    public QuestionState handle(SubmitQuestionForReview command) {
        if (repository.isOperationProcessed(command.operationId())) {
            return repository.findByQuestionId(command.questionId()).map(QuestionLifecycle::getState)
                    .orElse(QuestionState.DRAFT);
        }
        QuestionLifecycle lifecycle = repository.findByQuestionId(command.questionId())
                .orElseGet(() -> QuestionLifecycle.newDraft(command.questionId()));
        if (lifecycle.canSubmitForReview()) {
            lifecycle.submitForReview();
            repository.save(lifecycle);
        }
        repository.markOperationProcessed(command.operationId());
        return lifecycle.getState();
    }

    public Optional<QuestionState> stateOf(Long questionId) {
        return repository.findByQuestionId(questionId).map(QuestionLifecycle::getState);
    }

    public boolean isOperationProcessed(String operationId) {
        return repository.isOperationProcessed(operationId);
    }
}
