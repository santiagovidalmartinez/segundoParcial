package co.edu.unicauca.lifecycle.infrastructure.messaging;

import co.edu.unicauca.lifecycle.application.LifecycleService;
import co.edu.unicauca.lifecycle.application.SubmitQuestionForReview;
import co.edu.unicauca.lifecycle.domain.QuestionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consumo en segundo plano del comando SubmitQuestionForReview desde la cola question.submit.queue. */
@Component
public class SubmitQuestionListener {

    private static final Logger log = LoggerFactory.getLogger(SubmitQuestionListener.class);

    private final LifecycleService service;

    public SubmitQuestionListener(LifecycleService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SUBMIT)
    public void onSubmitQuestion(SubmitQuestionForReview command) {
        log.info("Comando recibido: operationId={} questionId={}", command.operationId(), command.questionId());
        QuestionState state = service.handle(command);
        log.info("Pregunta {} -> estado {}", command.questionId(), state);
    }
}
