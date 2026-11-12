package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.EstadoTarefaLimpeza;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.time.LocalDateTime;

public class TarefaLimpeza {

    private Long id;
    private Quarto quarto;
    private EstadoTarefaLimpeza estado = EstadoTarefaLimpeza.PENDENTE;
    private LocalDateTime criadaEm;
    private LocalDateTime concluidaEm;
    private String observacoes;
    private Long versao;

    public TarefaLimpeza() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public EstadoTarefaLimpeza getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarefaLimpeza estado) {
        this.estado = estado;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    public LocalDateTime getConcluidaEm() {
        return concluidaEm;
    }

    public void setConcluidaEm(LocalDateTime concluidaEm) {
        this.concluidaEm = concluidaEm;
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

    public void iniciar() {
        if (estado != EstadoTarefaLimpeza.PENDENTE) {
            throw new NegocioException("Somente tarefas de limpeza pendentes podem ser iniciadas.");
        }
        estado = EstadoTarefaLimpeza.EM_EXECUCAO;
    }

    public void concluir() {
        if (estado == EstadoTarefaLimpeza.CONCLUIDA) {
            throw new NegocioException("A tarefa de limpeza já foi concluída.");
        }
        estado = EstadoTarefaLimpeza.CONCLUIDA;
        concluidaEm = LocalDateTime.now();
    }

    public boolean aberta() {
        return estado != EstadoTarefaLimpeza.CONCLUIDA;
    }
}