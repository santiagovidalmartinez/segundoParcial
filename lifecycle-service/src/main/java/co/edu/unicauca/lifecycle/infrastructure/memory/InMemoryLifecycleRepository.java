package co.edu.unicauca.lifecycle.infrastructure.memory;

import co.edu.unicauca.lifecycle.domain.LifecycleRepository;
import co.edu.unicauca.lifecycle.domain.QuestionLifecycle;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Almacenamiento en memoria del estado (suficiente para la prueba; se pierde al reiniciar el servicio). */
@Repository
public class InMemoryLifecycleRepository implements LifecycleRepository {

    private final Map<Long, QuestionLifecycle> states = new ConcurrentHashMap<>();
    private final Set<String> operations = ConcurrentHashMap.newKeySet();

    @Override
    public Optional<QuestionLifecycle> findByQuestionId(Long questionId) {
        return Optional.ofNullable(states.get(questionId));
    }

    @Override
    public void save(QuestionLifecycle lifecycle) {
        states.put(lifecycle.getQuestionId(), lifecycle);
    }

    @Override
    public boolean isOperationProcessed(String operationId) {
        return operations.contains(operationId);
    }

    @Override
    public void markOperationProcessed(String operationId) {
        operations.add(operationId);
    }
}
