package com.gabrielflorencco.hotel_impact.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservaForm {

    /** Presente apenas na edição (action do formulário); nulo na criação. */
    private Long id;

    @NotNull
    private Long hospedeId;

    @NotNull
    private Long quartoId;

    /** ISO no input date (yyyy-MM-dd): qualquer outro formato é esvaziado pelo navegador no re-render. */
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCheckIn;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCheckOut;

    @NotNull
    @Min(1)
    @Max(20)
    private Integer quantidadeHospedes;

    /** Diária acordada no momento da reserva (valor histórico). */
    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal valorDiaria;

    /** Desconto opcional; nunca pode exceder o valor bruto (validado no serviço). */
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal desconto;

    /** Obrigatório quando a reserva abrange datas já passadas (exceção administrativa). */
    @Size(max = 300)
    private String motivoExcecao;

    @Size(max = 500)
    private String observacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHospedeId() {
        return hospedeId;
    }

    public void setHospedeId(Long hospedeId) {
        this.hospedeId = hospedeId;
    }

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }

    public LocalDate getDataCheckIn() {
        return dataCheckIn;
    }

    public void setDataCheckIn(LocalDate dataCheckIn) {
        this.dataCheckIn = dataCheckIn;
    }

    public LocalDate getDataCheckOut() {
        return dataCheckOut;
    }

    public void setDataCheckOut(LocalDate dataCheckOut) {
        this.dataCheckOut = dataCheckOut;
    }

    public Integer getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public void setQuantidadeHospedes(Integer quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(BigDecimal valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getMotivoExcecao() {
        return motivoExcecao;
    }

    public void setMotivoExcecao(String motivoExcecao) {
        this.motivoExcecao = motivoExcecao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}