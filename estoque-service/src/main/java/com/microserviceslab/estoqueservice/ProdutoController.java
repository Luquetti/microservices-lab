package com.microserviceslab.estoqueservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reservar")
    public ResponseEntity<?> reservar(@PathVariable Long id,
                                      @RequestBody Map<String, Integer> body,
                                      @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {
        {
            int quantidade = body.get("quantidade");
            log.info("[correlationId={}] Reservando {} unidades do produto {}", correlationId, quantidade, id);

            return repository.findById(id)
                    .map(produto -> {
                        if (produto.getQuantidade() < quantidade) {
                            log.warn("[correlationId={}] Estoque insuficiente para produto {}: disponivel={}, solicitado={}", correlationId, id, produto.getQuantidade(), quantidade);
                            return ResponseEntity.status(409)
                                    .body(Map.of("mensagem", "Estoque insuficiente"));
                        }
                        produto.setQuantidade(produto.getQuantidade() - quantidade);
                        repository.save(produto);
                        log.info("[correlationId={}] Produto {} reservado: restam {} unidades", correlationId, id, produto.getQuantidade());
                        return ResponseEntity.ok(produto);
                    })
                    .orElse(ResponseEntity.status(404)
                            .body(Map.of("mensagem", "Produto inexistente?")));
        }
    }
}