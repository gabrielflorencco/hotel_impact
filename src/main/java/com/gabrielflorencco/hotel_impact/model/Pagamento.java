package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.domain.Lancamento;
import com.gabrielflorencco.hotel_impact.enums.EstadoPagamento;
import com.gabrielflorencco.hotel_impact.enums.FormaPagamento;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento implements Lancamento {

    private Long id;
    private Hospedagem hospedagem;
    private BigDecimal valor;
    private FormaPagamento forma;
    private LocalDateTime dataHora;
    private EstadoPagamento estado = EstadoPagamento.CONFIRMADO;
    private String observacao;
    private Long versao;

    public Pagamento() {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public EstadoPagamento getEstado() {
        return estado;
    }

    public void setEstado(EstadoPagamento estado) {
        this.estado = estado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    @Override
    public BigDecimal valorContabil() {
        return getValor();
    }

    @Override
    public boolean contaNoFechamento() {
        return estado == EstadoPagamento.CONFIRMADO;
    }

    public void estornar() {
        if (estado == EstadoPagamento.ESTORNADO) {
            throw new NegocioException("O pagamento já está estornado.");
        }
        estado = EstadoPagamento.ESTORNADO;
    }
}