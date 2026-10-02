package com.microserviceslab.pedidoservice;

import java.io.Serializable;

public class PedidoEvento implements Serializable {

    private Long pedidoId;
    private Long produtoId;
    private int quantidade;
    private String correlationId;

    public PedidoEvento() {}

    public PedidoEvento(Long pedidoId, Long produtoId, int quantidade, String correlationId) {
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.correlationId = correlationId;
    }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
