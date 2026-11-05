package co.edu.unicauca.question.infrastructure.client;

import co.edu.unicauca.question.application.QuestionValidationException;
import co.edu.unicauca.question.application.ValidationServiceUnavailableException;
import co.edu.unicauca.question.application.command.CreateQuestionCommand;
import co.edu.unicauca.question.application.port.QuestionValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Comunicacion SINCRONA con el Validation Service usando RestTemplate: POST {url}/validate.
 * 200 OK -> la pregunta es valida; 400 Bad Request -> se propaga la lista de errores.
 */
@Component
public class RestQuestionValidator implements QuestionValidator {

    public record Payload(String statement, List<String> options, String correctKey, String justification) {
    }

    public record Reply(boolean valid, List<String> errors) {
    }

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String validateUrl;

    public RestQuestionValidator(RestTemplate restTemplate, ObjectMapper objectMapper,
                                 @Value("${validation.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.validateUrl = baseUrl + "/validate";
    }

    @Override
    public void validate(CreateQuestionCommand command) {
        Payload payload = new Payload(command.statement(), command.options(), command.correctKey(), command.justification());
        try {
            restTemplate.postForEntity(validateUrl, payload, Reply.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new QuestionValidationException(extractErrors(e.getResponseBodyAsString()));
        } catch (ResourceAccessException e) {
            throw new ValidationServiceUnavailableException(
                    "No se pudo contactar al Validation Service en " + validateUrl + ".", e);
        }
    }

    private List<String> extractErrors(String body) {
        try {
            Reply reply = objectMapper.readValue(body, Reply.class);
            if (reply.errors() != null && !reply.errors().isEmpty()) {
                return reply.errors();
            }
        } catch (Exception ignored) {
            // cae al mensaje generico
        }
        return List.of("El Validation Service rechazo la pregunta (400) sin detalle de errores.");
    }
}
