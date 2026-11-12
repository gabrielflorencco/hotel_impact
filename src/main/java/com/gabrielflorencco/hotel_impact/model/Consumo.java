package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.domain.Lancamento;
import com.gabrielflorencco.hotel_impact.enums.EstadoConsumo;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Lançamento de consumo em uma hospedagem. O preço unitário é capturado no
 * lançamento para preservar o histórico. Lançamentos nunca são excluídos:
 * correções usam cancelamento (estorno lógico) que restitui o estoque uma
 * única vez.
 */
public class Consumo implements Lancamento {

    private Long id;
    private Hospedagem hospedagem;
    private ItemCatalogo item;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal valorTotal;
    private LocalDateTime dataHora;
    private EstadoConsumo estado = EstadoConsumo.ATIVO;
    private String observacoes;
    private Long versao;

    public Consumo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hospedagem getHospedagem() {
        return hospedagem;
    }

    public void setHospedagem(Hospedagem hospedagem) {
        this.hospedagem = hospedagem;
    }

    public ItemCatalogo getItem() {
        return item;
    }

    public void setItem(ItemCatalogo item) {
        this.item = item;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public EstadoConsumo getEstado() {
        return estado;
    }

    public void setEstado(EstadoConsumo estado) {
        this.estado = estado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    @Override
    public BigDecimal valorContabil() {
        return getValorTotal();
    }

    @Override
    public boolean contaNoFechamento() {
        return estado == EstadoConsumo.ATIVO;
    }

    /**
     * Cancela o lançamento (estorno lógico único); a restituição do estoque
     * é orquestrada pelo serviço.
     */
    public void cancelar() {
        if (estado == EstadoConsumo.CANCELADO) {
            throw new NegocioException("O lançamento já está cancelado.");
        }
        estado = EstadoConsumo.CANCELADO;
    }
}