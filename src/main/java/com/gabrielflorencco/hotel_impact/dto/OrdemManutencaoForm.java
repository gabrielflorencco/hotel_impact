package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Formulário de abertura de ordem de manutenção. O estado e o flag de
 * interdição são definidos pelo serviço conforme a prioridade e a situação
 * atual do quarto; ordens não são editadas — correções usam cancelamento.
 */
public class OrdemManutencaoForm {

    @NotNull
    private Long quartoId;

    @NotBlank
    @Size(min = 3, max = 120)
    private String titulo;

    @Size(max = 500)
    private String descricao;

    @NotNull
    private PrioridadeManutencao prioridade;

    @Size(max = 300)
    private String observacoes;

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PrioridadeManutencao getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeManutencao prioridade) {
        this.prioridade = prioridade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}