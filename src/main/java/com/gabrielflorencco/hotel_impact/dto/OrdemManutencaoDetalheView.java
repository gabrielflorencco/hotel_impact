package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.EstadoManutencao;
import com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao;
import com.gabrielflorencco.hotel_impact.model.OrdemManutencao;
import java.time.format.DateTimeFormatter;

/**
 * Ordem de manutenção exibida na tela de detalhes, com datas formatadas e
 * as operações de estado possíveis (iniciar/concluir/cancelar).
 */
public final class OrdemManutencaoDetalheView {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Long id;
    private final Long quartoId;
    private final String quartoNumero;
    private final String titulo;
    private final String descricao;
    private final PrioridadeManutencao prioridade;
    private final EstadoManutencao estado;
    private final boolean interditouQuarto;
    private final String abertaEm;
    private final String concluidaEm;
    private final String observacoes;
    private final boolean podeIniciar;
    private final boolean podeConcluirOuCancelar;

    private OrdemManutencaoDetalheView(OrdemManutencao ordem) {
        this.id = ordem.getId();
        this.quartoId = ordem.getQuarto().getId();
        this.quartoNumero = ordem.getQuarto().getNumero();
        this.titulo = ordem.getTitulo();
        this.descricao = ordem.getDescricao();
        this.prioridade = ordem.getPrioridade();
        this.estado = ordem.getEstado();
        this.interditouQuarto = Boolean.TRUE.equals(ordem.getInterditouQuarto());
        this.abertaEm = ordem.getAbertaEm() == null ? null : ordem.getAbertaEm().format(DATA_HORA);
        this.concluidaEm = ordem.getConcluidaEm() == null ? null : ordem.getConcluidaEm().format(DATA_HORA);
        this.observacoes = ordem.getObservacoes();
        this.podeIniciar = ordem.getEstado() == EstadoManutencao.ABERTA;
        this.podeConcluirOuCancelar = ordem.getEstado() == EstadoManutencao.ABERTA
                || ordem.getEstado() == EstadoManutencao.EM_ANDAMENTO;
    }

    public static OrdemManutencaoDetalheView de(OrdemManutencao ordem) {
        return new OrdemManutencaoDetalheView(ordem);
    }

    public Long getId() {
        return id;
    }

    public Long getQuartoId() {
        return quartoId;
    }

    public String getQuartoNumero() {
        return quartoNumero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
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

    public String getAbertaEm() {
        return abertaEm;
    }

    public String getConcluidaEm() {
        return concluidaEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public boolean isPodeIniciar() {
        return podeIniciar;
    }

    public boolean isPodeConcluirOuCancelar() {
        return podeConcluirOuCancelar;
    }
}