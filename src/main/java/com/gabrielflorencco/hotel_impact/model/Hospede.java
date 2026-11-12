package com.gabrielflorencco.hotel_impact.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hóspede do hotel. Campos de contato são opcionais; o CPF, quando informado,
 * é validado pelo algoritmo oficial de dígitos verificadores e armazenado
 * apenas com os 11 dígitos.
 */
public class Hospede {

    private Long id;
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;
    private String observacoes;
    private LocalDateTime dataCadastro;

    public Hospede() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}