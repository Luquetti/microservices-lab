package com.microserviceslab.pedidoservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PagamentoProcessadoListener {

    private static final Logger log = LoggerFactory.getLogger(PagamentoProcessadoListener.class);
    private final PedidoRepository repository;

    public PagamentoProcessadoListener(PedidoRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_PAGAMENTO_PROCESSADO)
    public void atualizarStatus(PagamentoEvento evento) {
        String correlationId = evento.getCorrelationId();
        log.info("[correlationId={}] Recebido pagamento.processado do pedido {}: {}", correlationId, evento.getPedidoId(), evento.getStatus());

        repository.findById(evento.getPedidoId()).ifPresentOrElse(pedido -> {
            pedido.setStatus("APROVADO".equals(evento.getStatus()) ? "PAGO" : "REJEITADO");
            repository.save(pedido);
            log.info("[correlationId={}] Pedido {} atualizado para {}", correlationId, pedido.getId(), pedido.getStatus());
        }, () -> log.warn("[correlationId={}] Pedido {} nao encontrado", correlationId, evento.getPedidoId()));
    }
}
