package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.TipoItem;

import java.math.BigDecimal;

/**
 * Item do catálogo de produtos e serviços consumíveis. Produtos podem controlar
 * estoque; serviços nunca controlam. O estoque nunca pode ficar negativo
 * (também garantido por constraint de banco).
 */
public class ItemCatalogo {

    private Long id;
    private String nome;
    private String descricao;
    private TipoItem tipo;
    private BigDecimal precoUnitario;
    private Boolean controlaEstoque = false;
    private Integer estoqueAtual = 0;
    private Integer estoqueMinimo = 0;
    private Boolean ativo = true;
    private Long versao;

    public ItemCatalogo() {
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

    public TipoItem getTipo() {
        return tipo;
    }

    public void setTipo(TipoItem tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Boolean getControlaEstoque() {
        return controlaEstoque;
    }

    public void setControlaEstoque(Boolean controlaEstoque) {
        this.controlaEstoque = controlaEstoque;
    }

    public Integer getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(Integer estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public boolean isEstoqueBaixo() {
        return controlaEstoque && estoqueAtual != null && estoqueMinimo != null
                && estoqueAtual <= estoqueMinimo;
    }

    /** Serviços nunca controlam estoque, independente da flag. */
    public boolean isEstoqueControladoEfetivo() {
        return tipo == TipoItem.PRODUTO && Boolean.TRUE.equals(controlaEstoque);
    }
}