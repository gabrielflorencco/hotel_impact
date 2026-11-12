package com.gabrielflorencco.hotel_impact.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Formulário de lançamento de consumo em uma hospedagem. O preço unitário é
 * sempre capturado do catálogo no momento do lançamento (valor histórico);
 * o total é calculado no servidor.
 */
public class ConsumoForm {

    @NotNull
    private Long itemId;

    @NotNull
    @Min(1)
    @Max(99)
    private Integer quantidade;

    @Size(max = 300)
    private String observacoes;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}