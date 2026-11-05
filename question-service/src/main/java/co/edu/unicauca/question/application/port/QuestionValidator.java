package co.edu.unicauca.question.application.port;

import co.edu.unicauca.question.application.command.CreateQuestionCommand;

/** Puerto hacia el Validation Service. Lanza QuestionValidationException si la pregunta no es valida. */
public interface QuestionValidator {

    void validate(CreateQuestionCommand command);
}
