package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import com.gabrielflorencco.hotel_impact.model.Reserva;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Reserva exibida em listagens: dados já buscados com JOIN no
 * repositório (hóspede e quarto), sem consultas adicionais por linha.
 */
public final class ReservaResumoView {

    private final Long id;
    private final String codigo;
    private final String hospedeNome;
    private final String quartoNumero;
    private final LocalDate dataCheckIn;
    private final LocalDate dataCheckOut;
    private final long noites;
    private final BigDecimal valorTotal;
    private final EstadoReserva estado;

    private ReservaResumoView(Reserva reserva) {
        this.id = reserva.getId();
        this.codigo = reserva.getCodigo();
        this.hospedeNome = reserva.getHospede().getNome();
        this.quartoNumero = reserva.getQuarto().getNumero();
        this.dataCheckIn = reserva.getDataCheckIn();
        this.dataCheckOut = reserva.getDataCheckOut();
        this.noites = reserva.getQuantidadeNoites();
        this.valorTotal = reserva.getValorTotal();
        this.estado = reserva.getEstado();
    }

    public static ReservaResumoView de(Reserva reserva) {
        return new ReservaResumoView(reserva);
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getHospedeNome() {
        return hospedeNome;
    }

    public String getQuartoNumero() {
        return quartoNumero;
    }

    public LocalDate getDataCheckIn() {
        return dataCheckIn;
    }

    public LocalDate getDataCheckOut() {
        return dataCheckOut;
    }

    public long getNoites() {
        return noites;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}