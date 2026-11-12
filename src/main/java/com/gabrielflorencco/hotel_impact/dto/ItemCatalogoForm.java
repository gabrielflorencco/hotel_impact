package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.TipoItem;
import com.gabrielflorencco.hotel_impact.model.ItemCatalogo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Formulário de item do catálogo. Serviços nunca controlam estoque — o
 * service ignora a flag nesses casos, preservando a invariante da entidade.
 */
public class ItemCatalogoForm {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 80)
    private String nome;

    @Size(max = 300)
    private String descricao;

    @NotNull
    private TipoItem tipo;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precoUnitario;

    /** Ignorado para serviços: eles nunca controlam estoque. */
    private Boolean controlaEstoque = false;

    @PositiveOrZero
    @Max(100_000)
    private Integer estoqueAtual = 0;

    @PositiveOrZero
    @Max(100_000)
    private Integer estoqueMinimo = 0;

    private Boolean ativo = true;

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

    public static ItemCatalogoForm de(ItemCatalogo item) {
        ItemCatalogoForm form = new ItemCatalogoForm();
        form.setId(item.getId());
        form.setNome(item.getNome());
        form.setDescricao(item.getDescricao());
        form.setTipo(item.getTipo());
        form.setPrecoUnitario(item.getPrecoUnitario());
        form.setControlaEstoque(item.getControlaEstoque());
        form.setEstoqueAtual(item.getEstoqueAtual());
        form.setEstoqueMinimo(item.getEstoqueMinimo());
        form.setAtivo(item.getAtivo());
        return form;
    }
}