package co.edu.unicauca.question.infrastructure.messaging;

import co.edu.unicauca.question.application.command.SubmitQuestionForReview;
import co.edu.unicauca.question.application.port.CommandPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Publica el comando SubmitQuestionForReview en la exchange saberpro.direct.exchange con la routing key question.submit. */
@Component
public class RabbitCommandPublisher implements CommandPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitCommandPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(SubmitQuestionForReview command) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY_SUBMIT, command);
        log.info("Comando SubmitQuestionForReview publicado: operationId={} questionId={}",
                command.operationId(), command.questionId());
    }
}
