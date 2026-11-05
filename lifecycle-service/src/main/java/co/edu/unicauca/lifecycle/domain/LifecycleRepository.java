package co.edu.unicauca.lifecycle.domain;

import java.util.Optional;

/** Contrato de almacenamiento del estado y de las operaciones procesadas. */
public interface LifecycleRepository {

    Optional<QuestionLifecycle> findByQuestionId(Long questionId);

    void save(QuestionLifecycle lifecycle);

    boolean isOperationProcessed(String operationId);

    void markOperationProcessed(String operationId);
}
