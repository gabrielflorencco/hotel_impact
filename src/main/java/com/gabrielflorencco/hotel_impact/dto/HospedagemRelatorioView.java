package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Hospedagem exibida no relatório por período: dados já buscados com
 * JOIN no repositório, sem consultas adicionais por linha.
 */
public final class HospedagemRelatorioView {

    private final Long id;
    private final String reservaCodigo;
    private final String hospedeNome;
    private final String quartoNumero;
    private final LocalDateTime checkInReal;
    private final LocalDateTime checkOutReal;
    private final EstadoHospedagem estado;
    private final BigDecimal valorTotalReserva;

    private HospedagemRelatorioView(Hospedagem hospedagem) {
        this.id = hospedagem.getId();
        this.reservaCodigo = hospedagem.getReserva().getCodigo();
        this.hospedeNome = hospedagem.getReserva().getHospede().getNome();
        this.quartoNumero = hospedagem.getQuarto().getNumero();
        this.checkInReal = hospedagem.getCheckInReal();
        this.checkOutReal = hospedagem.getCheckOutReal();
        this.estado = hospedagem.getEstado();
        this.valorTotalReserva = hospedagem.getReserva().getValorTotal();
    }

    public static HospedagemRelatorioView de(Hospedagem hospedagem) {
        return new HospedagemRelatorioView(hospedagem);
    }

    public Long getId() {
        return id;
    }

    public String getReservaCodigo() {
        return reservaCodigo;
    }

    public String getHospedeNome() {
        return hospedeNome;
    }

    public String getQuartoNumero() {
        return quartoNumero;
    }

    public LocalDateTime getCheckInReal() {
        return checkInReal;
    }

    public LocalDateTime getCheckOutReal() {
        return checkOutReal;
    }

    public EstadoHospedagem getEstado() {
        return estado;
    }

    public BigDecimal getValorTotalReserva() {
        return valorTotalReserva;
    }
}