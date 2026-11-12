package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.model.Hospede;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hóspede exibido em listagens: o CPF aparece mascarado por privacidade
 * (somente os três dígitos finais).
 */
public final class HospedeResumoView {

    private final Long id;
    private final String nome;
    private final String cpfMascarado;
    private final String email;
    private final String telefone;
    private final LocalDate dataNascimento;
    private final LocalDateTime dataCadastro;

    private HospedeResumoView(Hospede hospede) {
        this.id = hospede.getId();
        this.nome = hospede.getNome();
        this.cpfMascarado = mascararCpf(hospede.getCpf());
        this.email = hospede.getEmail();
        this.telefone = hospede.getTelefone();
        this.dataNascimento = hospede.getDataNascimento();
        this.dataCadastro = hospede.getDataCadastro();
    }

    public static HospedeResumoView de(Hospede hospede) {
        return new HospedeResumoView(hospede);
    }

    /** •••.•••.•••-789 para preservar privacidade em listagens. */
    static String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return null;
        }
        return "•••.•••.•••-" + cpf.substring(8);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpfMascarado() {
        return cpfMascarado;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}