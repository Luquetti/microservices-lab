package com.microserviceslab.pagamentoservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pedidoId;
    private String status; // APROVADO ou REJEITADO

    public Pagamento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
