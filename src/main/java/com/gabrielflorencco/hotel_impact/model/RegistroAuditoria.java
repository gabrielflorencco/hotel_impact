package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria;

import java.time.LocalDateTime;

/**
 * Evento de auditoria de operação de negócio: registro histórico imutável —
 * sem setters, criado no momento da operação e jamais alterado. A mesma
 * transação da operação audita garante consistência (a operação e o
 * registro persistem juntos ou nada persiste).
 *
 * Nenhum dado sensível é gravado: apenas o identificador do usuário
 * autenticado, a operação e a referência ao registro afetado.
 */
public class RegistroAuditoria {

    private Long id;
    private OperacaoAuditoria operacao;
    private String usuario;
    private String entidadeTipo;
    private Long entidadeId;
    private LocalDateTime dataHora;
    private String detalhe;

    public RegistroAuditoria() {
    }

    public RegistroAuditoria(OperacaoAuditoria operacao, String usuario,
                             String entidadeTipo, Long entidadeId,
                             LocalDateTime dataHora, String detalhe) {
        this.operacao = operacao;
        this.usuario = usuario;
        this.entidadeTipo = entidadeTipo;
        this.entidadeId = entidadeId;
        this.dataHora = dataHora;
        this.detalhe = detalhe == null || detalhe.isBlank() ? null : detalhe.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperacaoAuditoria getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoAuditoria operacao) {
        this.operacao = operacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEntidadeTipo() {
        return entidadeTipo;
    }

    public void setEntidadeTipo(String entidadeTipo) {
        this.entidadeTipo = entidadeTipo;
    }

    public Long getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(Long entidadeId) {
        this.entidadeId = entidadeId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }
}