package co.edu.unicauca.lifecycle.api;

import co.edu.unicauca.lifecycle.application.LifecycleService;
import co.edu.unicauca.lifecycle.domain.QuestionState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Consultas para verificar que el comando asincrono ya fue procesado. */
@RestController
@RequestMapping("/lifecycle")
public class LifecycleController {

    private final LifecycleService service;

    public LifecycleController(LifecycleService service) {
        this.service = service;
    }

    @GetMapping("/questions/{questionId}")
    public ResponseEntity<Map<String, Object>> stateOf(@PathVariable Long questionId) {
        return service.stateOf(questionId)
                .map(state -> ResponseEntity.ok(Map.<String, Object>of("questionId", questionId, "state", state.name())))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.<String, Object>of(
                        "questionId", questionId,
                        "message", "Lifecycle Service aun no tiene estado para esta pregunta (no se ha enviado a revision).")));
    }

    @GetMapping("/operations/{operationId}")
    public Map<String, Object> operation(@PathVariable String operationId) {
        boolean processed = service.isOperationProcessed(operationId);
        return Map.of("operationId", operationId, "status", processed ? "COMPLETED" : "PROCESSING");
    }
}
