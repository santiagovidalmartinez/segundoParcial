package co.edu.unicauca.lifecycle.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Declara la misma topologia que Question Service (idempotente) y configura la conversion JSON. */
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

    /**
     * El publicador y el consumidor son proyectos distintos: la clase del comando no tiene el mismo paquete.
     * Con TypePrecedence.INFERRED se usa el tipo del parametro del @RabbitListener y se ignora el
     * encabezado __TypeId__ del mensaje.
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
