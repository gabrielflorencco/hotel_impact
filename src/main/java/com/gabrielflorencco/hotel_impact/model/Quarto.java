package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.time.LocalDateTime;

public class Quarto {

    private Long id;
    private String numero;
    private CategoriaQuarto categoria;
    private Integer andar;
    private Integer capacidadeOperacional;
    private EstadoQuarto estado = EstadoQuarto.DISPONIVEL;
    private String observacoes;
    private Boolean ativo = true;
    private Long versao;

    public Quarto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public CategoriaQuarto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaQuarto categoria) {
        this.categoria = categoria;
    }

    public Integer getAndar() {
        return andar;
    }

    public void setAndar(Integer andar) {
        this.andar = andar;
    }

    /** Capacidade operacional opcional; quando ausente, usa-se a capacidade da categoria. */
    public Integer getCapacidadeOperacional() {
        return capacidadeOperacional;
    }

    public void setCapacidadeOperacional(Integer capacidadeOperacional) {
        this.capacidadeOperacional = capacidadeOperacional;
    }

    public EstadoQuarto getEstado() {
        return estado;
    }

    public void setEstado(EstadoQuarto estado) {
        this.estado = estado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public int getCapacidadeEfetiva() {
        return capacidadeOperacional != null ? capacidadeOperacional : categoria.getCapacidadeMaxima();
    }

    public boolean isOcupavel() {
        return estado == EstadoQuarto.DISPONIVEL;
    }

    public void ocupar() {
        if (estado != EstadoQuarto.DISPONIVEL) {
            throw new NegocioException("Somente quartos disponíveis podem ser ocupados.");
        }
        estado = EstadoQuarto.OCUPADO;
    }

    public void enviarParaLimpeza() {
        if (estado != EstadoQuarto.OCUPADO) {
            throw new NegocioException("Somente quartos ocupados podem ser enviados para limpeza.");
        }
        estado = EstadoQuarto.LIMPEZA;
    }

    public void concluirLimpeza() {
        if (estado != EstadoQuarto.LIMPEZA && estado != EstadoQuarto.OCUPADO) {
            throw new NegocioException("Somente quartos aguardando limpeza podem ser liberados.");
        }
        estado = EstadoQuarto.DISPONIVEL;
    }

    public void interditar() {
        estado = EstadoQuarto.INTERDITADO;
    }

    public void liberarInterdicao() {
        if (estado != EstadoQuarto.INTERDITADO) {
            throw new NegocioException("Somente quartos interditados podem ser liberados.");
        }
        estado = EstadoQuarto.DISPONIVEL;
    }

    public void iniciarManutencao() {
        if (estado != EstadoQuarto.DISPONIVEL && estado != EstadoQuarto.LIMPEZA) {
            throw new NegocioException("Somente quartos disponíveis ou em limpeza podem entrar em manutenção.");
        }
        estado = EstadoQuarto.MANUTENCAO;
    }

    public void disponibilizar() {
        estado = EstadoQuarto.DISPONIVEL;
    }
}