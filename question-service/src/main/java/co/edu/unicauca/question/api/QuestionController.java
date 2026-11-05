package co.edu.unicauca.question.api;

import co.edu.unicauca.question.api.dto.CreateQuestionRequest;
import co.edu.unicauca.question.api.dto.QuestionResponse;
import co.edu.unicauca.question.api.dto.SubmitResponse;
import co.edu.unicauca.question.application.QuestionService;
import co.edu.unicauca.question.application.command.CreateQuestionCommand;
import co.edu.unicauca.question.application.command.SubmitReceipt;
import co.edu.unicauca.question.domain.model.Question;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    /** 201 Created si la validacion es exitosa; 400 Bad Request con la lista de errores si no lo es. */
    @PostMapping
    public ResponseEntity<QuestionResponse> create(@RequestBody CreateQuestionRequest request) {
        Question created = service.create(new CreateQuestionCommand(
                request.statement(), request.options(), request.correctKey(), request.justification()));
        return ResponseEntity.created(URI.create("/questions/" + created.getId())).body(QuestionResponse.from(created));
    }

    @GetMapping("/{id}")
    public QuestionResponse get(@PathVariable Long id) {
        return QuestionResponse.from(service.get(id));
    }

    @GetMapping
    public List<QuestionResponse> list() {
        return service.list().stream().map(QuestionResponse::from).toList();
    }

    /** 202 Accepted inmediato con operationId y estado PROCESSING; el cambio de estado ocurre en segundo plano. */
    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmitResponse> submit(@PathVariable Long id) {
        SubmitReceipt receipt = service.submitForReview(id);
        return ResponseEntity.accepted().body(new SubmitResponse(receipt.operationId(), receipt.status()));
    }
}
