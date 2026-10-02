package com.microserviceslab.pedidoservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoRepository repository;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${ESTOQUE_SERVICE_URL:http://localhost:8081}")
    private String estoqueUrl;

    public PedidoController(PedidoRepository repository, RestTemplate restTemplate, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.info("[correlationId={}] Criando pedido: produtoId={}, quantidade={}", correlationId, request.getProdutoId(), request.getQuantidade());

        // 1. Reservar estoque via REST síncrono
        try {
            String url = estoqueUrl + "/produtos/" + request.getProdutoId() + "/reservar";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Correlation-Id", correlationId);
            HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(Map.of("quantidade", request.getQuantidade()), headers);
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);            log.info("[correlationId={}] Estoque reservado com sucesso", correlationId);
        } catch (HttpClientErrorException e) {
            log.warn("[correlationId={}] Falha ao reservar estoque: {}", correlationId, e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("mensagem", "Falha ao reservar estoque", "correlationId", correlationId));
        } catch (Exception e) {
            log.error("[correlationId={}] Erro ao comunicar com estoque-service: {}", correlationId, e.getMessage());
            return ResponseEntity.status(503)
                    .body(Map.of("mensagem", "Servico de estoque indisponivel", "correlationId", correlationId));
        }

        // 2. Salvar pedido
        Pedido pedido = new Pedido();
        pedido.setProdutoId(request.getProdutoId());
        pedido.setQuantidade(request.getQuantidade());
        pedido.setStatus("AGUARDANDO_PAGAMENTO");
        pedido.setCorrelationId(correlationId);
        repository.save(pedido);
        log.info("[correlationId={}] Pedido {} criado", correlationId, pedido.getId());

        // 3. Publicar evento no RabbitMQ para pagamento-service
        PedidoEvento evento = new PedidoEvento(pedido.getId(), pedido.getProdutoId(), pedido.getQuantidade(), correlationId);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY_CRIADO, evento);
        log.info("[correlationId={}] Evento pedido.criado publicado para pedido {}", correlationId, pedido.getId());

        return ResponseEntity.ok(pedido);
    }

    @GetMapping
    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
