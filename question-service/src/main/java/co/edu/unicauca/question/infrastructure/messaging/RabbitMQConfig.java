package co.edu.unicauca.question.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia de mensajeria: exchange directo saberpro.direct.exchange, cola question.submit.queue
 * y routing key question.submit. Se declara igual en Lifecycle Service (declaracion idempotente),
 * asi no importa cual servicio arranque primero.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "saberpro.direct.exchange";
    public static final String QUEUE_SUBMIT = "question.submit.queue";
    public static final String ROUTING_KEY_SUBMIT = "question.submit";

    @Bean
    public DirectExchange saberproExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue submitQueue() {
        return QueueBuilder.durable(QUEUE_SUBMIT).build();
    }

    @Bean
    public Binding submitBinding(Queue submitQueue, DirectExchange saberproExchange) {
        return BindingBuilder.bind(submitQueue).to(saberproExchange).with(ROUTING_KEY_SUBMIT);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
