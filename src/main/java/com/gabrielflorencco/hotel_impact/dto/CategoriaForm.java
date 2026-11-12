package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.model.CategoriaQuarto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Formulário de categoria de quarto. Os valores enviados pelo navegador
 * nunca são vinculados diretamente à entidade.
 */
public class CategoriaForm {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 80)
    private String nome;

    @Size(max = 500)
    private String descricao;

    @NotNull
    @Min(1)
    private Integer capacidadeMaxima;

    @NotNull
    @Min(1)
    private Integer quantidadeCamas;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precoBase;

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

    public static CategoriaForm da(CategoriaQuarto categoria) {
        CategoriaForm form = new CategoriaForm();
        form.setId(categoria.getId());
        form.setNome(categoria.getNome());
        form.setDescricao(categoria.getDescricao());
        form.setCapacidadeMaxima(categoria.getCapacidadeMaxima());
        form.setQuantidadeCamas(categoria.getQuantidadeCamas());
        form.setPrecoBase(categoria.getPrecoBase());
        form.setAtivo(categoria.getAtivo());
        return form;
    }
}