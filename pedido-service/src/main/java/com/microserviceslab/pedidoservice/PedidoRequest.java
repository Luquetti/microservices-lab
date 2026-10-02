package com.microserviceslab.pedidoservice;

public class PedidoRequest {
    private Long produtoId;
    private int quantidade;

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
