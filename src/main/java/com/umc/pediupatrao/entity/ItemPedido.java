package com.umc.pediupatrao.entity;

public class ItemPedido {
    private String produtoId;
    private String nome;
    private int quantidade;
    private Double precoUnitario;

    public ItemPedido() {}

    public ItemPedido(String produtoId, String nome, int quantidade, Double precoUnitario) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public String getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(String produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Double getSubtotal() {
        if (precoUnitario == null) return 0.0;
        return precoUnitario * quantidade;
    }
}