package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.EstadoManutencao;
import com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.time.LocalDateTime;

/**
 * Ordem de manutenção de um quarto. Ordens com interdição (ex.: CRÍTICA) tiram
 * o quarto de operação; a abertura valida a consistência com hospedagens ativas.
 */
public class OrdemManutencao {

    private Long id;
    private Quarto quarto;
    private String titulo;
    private String descricao;
    private PrioridadeManutencao prioridade = PrioridadeManutencao.MEDIA;
    private EstadoManutencao estado = EstadoManutencao.ABERTA;
    private LocalDateTime abertaEm;
    private LocalDateTime concluidaEm;
    private Boolean interditouQuarto = false;
    private String observacoes;
    private Long versao;

    public OrdemManutencao() {
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PrioridadeManutencao getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeManutencao prioridade) {
        this.prioridade = prioridade;
    }

    public EstadoManutencao getEstado() {
        return estado;
    }

    public void setEstado(EstadoManutencao estado) {
        this.estado = estado;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public void setAbertaEm(LocalDateTime abertaEm) {
        this.abertaEm = abertaEm;
    }

    public LocalDateTime getConcluidaEm() {
        return concluidaEm;
    }

    public void setConcluidaEm(LocalDateTime concluidaEm) {
        this.concluidaEm = concluidaEm;
    }

    public Boolean getInterditouQuarto() {
        return interditouQuarto;
    }

    public void setInterditouQuarto(Boolean interditouQuarto) {
        this.interditouQuarto = interditouQuarto;
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

    /*
     * Máquina de estados da ordem: a ordem valida suas transições; o serviço
     * orquestra o quarto e a persistência.
     */

    /** Inicia a execução da ordem. */
    public void iniciarExecucao() {
        if (estado != EstadoManutencao.ABERTA) {
            throw new NegocioException("Somente ordens abertas podem ser iniciadas.");
        }
        estado = EstadoManutencao.EM_ANDAMENTO;
    }

    /** Conclui a ordem (a restauração do quarto é orquestrada pelo serviço). */
    public void concluirOrdem() {
        exigirAtiva();
        estado = EstadoManutencao.CONCLUIDA;
        concluidaEm = LocalDateTime.now();
    }

    /** Cancela a ordem (remoção lógica). */
    public void cancelarOrdem() {
        exigirAtiva();
        estado = EstadoManutencao.CANCELADA;
    }

    private void exigirAtiva() {
        if (estado != EstadoManutencao.ABERTA && estado != EstadoManutencao.EM_ANDAMENTO) {
            throw new NegocioException("Somente ordens abertas ou em andamento permitem esta operação.");
        }
    }
}