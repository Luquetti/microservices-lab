package com.microserviceslab.pedidoservice;

import java.io.Serializable;

public class PagamentoEvento implements Serializable {

    private Long pedidoId;
    private String status;
    private String correlationId;

    public PagamentoEvento() {}

    public PagamentoEvento(Long pedidoId, String status, String correlationId) {
        this.pedidoId = pedidoId;
        this.status = status;
        this.correlationId = correlationId;
    }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
