package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Hospedagem {

    private Long id;
    private Reserva reserva;
    private Quarto quarto;
    private LocalDateTime checkInReal;
    private LocalDateTime checkOutReal;
    private EstadoHospedagem estado = EstadoHospedagem.EM_ANDAMENTO;
    private String motivoExcecaoEncerramento;
    private String observacoes;
    private Long versao;

    public Hospedagem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDateTime getCheckInReal() {
        return checkInReal;
    }

    public void setCheckInReal(LocalDateTime checkInReal) {
        this.checkInReal = checkInReal;
    }

    public LocalDateTime getCheckOutReal() {
        return checkOutReal;
    }

    public void setCheckOutReal(LocalDateTime checkOutReal) {
        this.checkOutReal = checkOutReal;
    }

    public EstadoHospedagem getEstado() {
        return estado;
    }

    public void setEstado(EstadoHospedagem estado) {
        this.estado = estado;
    }

    public String getMotivoExcecaoEncerramento() {
        return motivoExcecaoEncerramento;
    }

    public void setMotivoExcecaoEncerramento(String motivoExcecaoEncerramento) {
        this.motivoExcecaoEncerramento = motivoExcecaoEncerramento;
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

    public boolean isEmAndamento() {
        return estado == EstadoHospedagem.EM_ANDAMENTO;
    }

    public void abrir(LocalDateTime checkInReal) {
        this.checkInReal = checkInReal;
        this.estado = EstadoHospedagem.EM_ANDAMENTO;
    }

    public void encerrar(BigDecimal saldoDevedor) {
        if (estado != EstadoHospedagem.EM_ANDAMENTO) {
            throw new NegocioException("Somente hospedagens em andamento podem ser encerradas.");
        }
        if (saldoDevedor.signum() > 0) {
            throw new NegocioException("A conta possui saldo pendente de R$ "
                    + saldoDevedor.toPlainString() + ". Informe a justificativa do encerramento excepcional.");
        }
        estado = EstadoHospedagem.ENCERRADA;
        checkOutReal = LocalDateTime.now();
    }

    public void encerrar(BigDecimal saldoDevedor, String motivoExcecao) {
        if (estado != EstadoHospedagem.EM_ANDAMENTO) {
            throw new NegocioException("Somente hospedagens em andamento podem ser encerradas.");
        }
        if (saldoDevedor.signum() > 0) {
            if (motivoExcecao == null || motivoExcecao.isBlank()) {
                throw new NegocioException("A conta possui saldo pendente de R$ "
                        + saldoDevedor.toPlainString() + ". Informe a justificativa do encerramento excepcional.");
            }
            motivoExcecaoEncerramento = motivoExcecao.trim();
            estado = EstadoHospedagem.CANCELADA;
        } else {
            estado = EstadoHospedagem.ENCERRADA;
        }
        checkOutReal = LocalDateTime.now();
    }
}