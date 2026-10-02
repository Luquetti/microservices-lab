package com.microserviceslab.pagamentoservice;

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
    public static final String QUEUE_PEDIDO_CRIADO = "pedido.criado";
    public static final String ROUTING_KEY_CRIADO = "pedido.criado";
    public static final String ROUTING_KEY_PROCESSADO = "pagamento.processado";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue filaPedidoCriado() {
        return new Queue(QUEUE_PEDIDO_CRIADO, true);
    }

    @Bean
    public Binding bindingPedidoCriado(Queue filaPedidoCriado, TopicExchange exchange) {
        return BindingBuilder.bind(filaPedidoCriado).to(exchange).with(ROUTING_KEY_CRIADO);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
