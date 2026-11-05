package co.edu.unicauca.question.api;

import co.edu.unicauca.question.api.dto.ErrorResponse;
import co.edu.unicauca.question.application.QuestionNotFoundException;
import co.edu.unicauca.question.application.QuestionValidationException;
import co.edu.unicauca.question.application.ValidationServiceUnavailableException;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(QuestionValidationException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(QuestionValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), e.getErrors()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("El cuerpo de la solicitud no es un JSON valido.", List.of()));
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(QuestionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), List.of()));
    }

    @ExceptionHandler(ValidationServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleValidationDown(ValidationServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorResponse(e.getMessage(), List.of()));
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<ErrorResponse> handleBroker(AmqpException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("No se pudo publicar el comando en RabbitMQ.", List.of()));
    }
}
