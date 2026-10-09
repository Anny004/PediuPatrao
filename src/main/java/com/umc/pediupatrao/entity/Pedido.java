package com.umc.pediupatrao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;
    private String clienteId;
    private String atendente;
    private String status;
    private List<ItemPedido> itens = new ArrayList<>();
    private Double desconto = 0.0;
    private Double valorTotal = 0.0;

    private String responsavelEntrada;
    private LocalDateTime dataHoraEntrada;
    private String responsavelSaida;
    private LocalDateTime dataHoraSaida;

    private String justificativaCancelamento;
    private String usuarioCancelamento;
    private LocalDateTime dataHoraCancelamento;
    private String statusAnteriorCancelamento;

    private String usuarioDesconto;
    private LocalDateTime dataHoraDesconto;
    private Double descontoAnterior = 0.0;
    private Double valorTotalAnteriorDesconto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getAtendente() {
        return atendente;
    }

    public void setAtendente(String atendente) {
        this.atendente = atendente;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getResponsavelEntrada() {
        return responsavelEntrada;
    }

    public void setResponsavelEntrada(String responsavelEntrada) {
        this.responsavelEntrada = responsavelEntrada;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
    }

    public String getResponsavelSaida() {
        return responsavelSaida;
    }

    public void setResponsavelSaida(String responsavelSaida) {
        this.responsavelSaida = responsavelSaida;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }

    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    public String getJustificativaCancelamento() {
        return justificativaCancelamento;
    }

    public void setJustificativaCancelamento(String justificativaCancelamento) {
        this.justificativaCancelamento = justificativaCancelamento;
    }

    public String getUsuarioCancelamento() {
        return usuarioCancelamento;
    }

    public void setUsuarioCancelamento(String usuarioCancelamento) {
        this.usuarioCancelamento = usuarioCancelamento;
    }

    public LocalDateTime getDataHoraCancelamento() {
        return dataHoraCancelamento;
    }

    public void setDataHoraCancelamento(LocalDateTime dataHoraCancelamento) {
        this.dataHoraCancelamento = dataHoraCancelamento;
    }

    public String getStatusAnteriorCancelamento() {
        return statusAnteriorCancelamento;
    }

    public void setStatusAnteriorCancelamento(String statusAnteriorCancelamento) {
        this.statusAnteriorCancelamento = statusAnteriorCancelamento;
    }

    public String getUsuarioDesconto() {
        return usuarioDesconto;
    }

    public void setUsuarioDesconto(String usuarioDesconto) {
        this.usuarioDesconto = usuarioDesconto;
    }

    public LocalDateTime getDataHoraDesconto() {
        return dataHoraDesconto;
    }

    public void setDataHoraDesconto(LocalDateTime dataHoraDesconto) {
        this.dataHoraDesconto = dataHoraDesconto;
    }

    public Double getDescontoAnterior() {
        return descontoAnterior;
    }

    public void setDescontoAnterior(Double descontoAnterior) {
        this.descontoAnterior = descontoAnterior;
    }

    public Double getValorTotalAnteriorDesconto() {
        return valorTotalAnteriorDesconto;
    }

    public void setValorTotalAnteriorDesconto(Double valorTotalAnteriorDesconto) {
        this.valorTotalAnteriorDesconto = valorTotalAnteriorDesconto;
    }
}