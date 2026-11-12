package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.EstadoManutencao;
import com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao;
import com.gabrielflorencco.hotel_impact.model.OrdemManutencao;
import java.time.LocalDateTime;

/**
 * Ordem de manutenção exibida em listagens: quarto já buscado com JOIN
 * no repositório, sem consultas adicionais por linha.
 */
public final class OrdemManutencaoResumoView {

    private final Long id;
    private final String quartoNumero;
    private final String titulo;
    private final PrioridadeManutencao prioridade;
    private final EstadoManutencao estado;
    private final boolean interditouQuarto;
    private final LocalDateTime abertaEm;
    private final LocalDateTime concluidaEm;

    private OrdemManutencaoResumoView(OrdemManutencao ordem) {
        this.id = ordem.getId();
        this.quartoNumero = ordem.getQuarto().getNumero();
        this.titulo = ordem.getTitulo();
        this.prioridade = ordem.getPrioridade();
        this.estado = ordem.getEstado();
        this.interditouQuarto = Boolean.TRUE.equals(ordem.getInterditouQuarto());
        this.abertaEm = ordem.getAbertaEm();
        this.concluidaEm = ordem.getConcluidaEm();
    }

    public static OrdemManutencaoResumoView de(OrdemManutencao ordem) {
        return new OrdemManutencaoResumoView(ordem);
    }

    public Long getId() {
        return id;
    }

    public String getQuartoNumero() {
        return quartoNumero;
    }

    public String getTitulo() {
        return titulo;
    }

    public PrioridadeManutencao getPrioridade() {
        return prioridade;
    }

    public EstadoManutencao getEstado() {
        return estado;
    }

    public boolean isInterditouQuarto() {
        return interditouQuarto;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public LocalDateTime getConcluidaEm() {
        return concluidaEm;
    }
}