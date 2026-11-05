package co.edu.unicauca.validation.api;

import co.edu.unicauca.validation.domain.StructuralValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ValidationController {

    private final StructuralValidator validator = new StructuralValidator();

    /** 200 OK si la pregunta es estructuralmente valida; 400 Bad Request con la lista de errores si no. */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@RequestBody QuestionToValidate question) {
        List<String> errors = validator.validate(
                question.statement(), question.options(), question.correctKey(), question.justification());
        if (errors.isEmpty()) {
            return ResponseEntity.ok(new ValidationResponse(true, List.of()));
        }
        return ResponseEntity.badRequest().body(new ValidationResponse(false, errors));
    }
}
