package com.gabrielflorencco.hotel_impact.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CategoriaQuarto {

    private Long id;
    private String nome;
    private String descricao;
    private Integer capacidadeMaxima;
    private Integer quantidadeCamas;
    private BigDecimal precoBase;
    private Boolean ativo = true;

    public CategoriaQuarto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Integer getQuantidadeCamas() {
        return quantidadeCamas;
    }

    public void setQuantidadeCamas(Integer quantidadeCamas) {
        this.quantidadeCamas = quantidadeCamas;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public void alternarAtivo() {
        this.ativo = !Boolean.TRUE.equals(this.ativo);
    }
}