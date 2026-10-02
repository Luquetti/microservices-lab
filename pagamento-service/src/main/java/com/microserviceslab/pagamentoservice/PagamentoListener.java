package com.microserviceslab.pagamentoservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PagamentoListener {

    private static final Logger log = LoggerFactory.getLogger(PagamentoListener.class);
    private final PagamentoRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public PagamentoListener(PagamentoRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PEDIDO_CRIADO)
    public void processarPedido(PedidoEvento evento) {
        String correlationId = evento.getCorrelationId();
        log.info("[correlationId={}] Recebido pedido {} para pagamento", correlationId, evento.getPedidoId());

        // 80% aprovado, 20% rejeitado
        boolean aprovado = random.nextInt(100) < 80;

        Pagamento pagamento = new Pagamento();
        pagamento.setPedidoId(evento.getPedidoId());
        pagamento.setStatus(aprovado ? "APROVADO" : "REJEITADO");
        repository.save(pagamento);

        log.info("[correlationId={}] Pagamento do pedido {}: {}", correlationId, evento.getPedidoId(), pagamento.getStatus());

        // Etapa 12: avisa o pedido-service do resultado
        PagamentoEvento processado = new PagamentoEvento(evento.getPedidoId(), pagamento.getStatus(), correlationId);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY_PROCESSADO, processado);
        log.info("[correlationId={}] Evento pagamento.processado publicado para pedido {}", correlationId, evento.getPedidoId());
    }
}
