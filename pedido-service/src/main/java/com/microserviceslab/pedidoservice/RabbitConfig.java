package com.microserviceslab.pedidoservice;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "pedidos.exchange";
    public static final String ROUTING_KEY_CRIADO = "pedido.criado";
    public static final String QUEUE_PAGAMENTO_PROCESSADO = "pagamento.processado";
    public static final String ROUTING_KEY_PROCESSADO = "pagamento.processado";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue filaPagamentoProcessado() {
        return new Queue(QUEUE_PAGAMENTO_PROCESSADO, true);
    }

    @Bean
    public Binding bindingPagamentoProcessado(Queue filaPagamentoProcessado, TopicExchange exchange) {
        return BindingBuilder.bind(filaPagamentoProcessado).to(exchange).with(ROUTING_KEY_PROCESSADO);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
