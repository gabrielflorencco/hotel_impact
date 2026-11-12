package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.model.Quarto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Formulário de quarto. O estado operacional não é editável aqui:
 * ele muda apenas por operações de negócio explícitas (check-in, limpeza,
 * manutenção, interdição).
 */
public class QuartoForm {

    private Long id;

    @NotBlank
    @Size(max = 10)
    private String numero;

    @NotNull
    private Long categoriaId;

    @NotNull
    @PositiveOrZero
    @Max(200)
    private Integer andar;

    /** Capacidade operacional opcional; vazia usa a capacidade da categoria. */
    @Min(1)
    @Max(20)
    private Integer capacidadeOperacional;

    @Size(max = 500)
    private String observacoes;

    private Boolean ativo = true;

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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getAndar() {
        return andar;
    }

    public void setAndar(Integer andar) {
        this.andar = andar;
    }

    public Integer getCapacidadeOperacional() {
        return capacidadeOperacional;
    }

    public void setCapacidadeOperacional(Integer capacidadeOperacional) {
        this.capacidadeOperacional = capacidadeOperacional;
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

    public static QuartoForm de(Quarto quarto) {
        QuartoForm form = new QuartoForm();
        form.setId(quarto.getId());
        form.setNumero(quarto.getNumero());
        form.setCategoriaId(quarto.getCategoria() != null ? quarto.getCategoria().getId() : null);
        form.setAndar(quarto.getAndar());
        form.setCapacidadeOperacional(quarto.getCapacidadeOperacional());
        form.setObservacoes(quarto.getObservacoes());
        form.setAtivo(quarto.getAtivo());
        return form;
    }
}