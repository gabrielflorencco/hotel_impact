package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.dto.HospedeForm;
import com.gabrielflorencco.hotel_impact.model.Hospede;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.HospedeRepository;
import com.gabrielflorencco.hotel_impact.repository.ReservaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HospedeService {

    private final HospedeRepository hospedes;
    private final ReservaRepository reservas;

    public HospedeService(HospedeRepository hospedes, ReservaRepository reservas) {
        this.hospedes = hospedes;
        this.reservas = reservas;
    }

    @Transactional(readOnly = true)
    public List<Hospede> listarTodos() {
        return hospedes.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Page<Hospede> buscar(String termo, String termoCpf, Pageable pageable) {
        return hospedes.buscar(termo, termoCpf, pageable);
    }

    @Transactional(readOnly = true)
    public Hospede buscarPorId(Long id) {
        return hospedes.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Hóspede não encontrado."));
    }

    @Transactional
    public Hospede salvar(HospedeForm form) {
        String cpf = apenasDigitos(form.getCpf());
        if (cpf != null && cpf.isBlank()) {
            cpf = null;
        }
        validarCpfUnico(cpf, form.getId());

        Hospede hospede = form.getId() == null
                ? new Hospede()
                : buscarPorId(form.getId());
        hospede.setNome(form.getNome().trim());
        hospede.setCpf(cpf);
        hospede.setEmail(form.getEmail() == null || form.getEmail().isBlank() ? null : form.getEmail().trim().toLowerCase());
        hospede.setTelefone(form.getTelefone() == null || form.getTelefone().isBlank() ? null : form.getTelefone().trim());
        hospede.setDataNascimento(form.getDataNascimento());
        hospede.setObservacoes(form.getObservacoes() == null || form.getObservacoes().isBlank()
                ? null : form.getObservacoes().trim());
        if (hospede.getId() == null) {
            hospede.setDataCadastro(LocalDateTime.now());
        }
        return hospedes.save(hospede);
    }

    @Transactional
    public void excluir(Long id) {
        Hospede hospede = buscarPorId(id);
        if (reservas.existsByHospedeId(id)) {
            throw new NegocioException(
                    "Não é possível excluir o hóspede: existem reservas associadas, que preservam o histórico.");
        }
        hospedes.delete(hospede);
    }

    private void validarCpfUnico(String cpf, Long id) {
        if (cpf == null) {
            return;
        }
        boolean duplicado = id == null
                ? hospedes.existsByCpf(cpf)
                : hospedes.existsByCpfAndIdNot(cpf, id);
        if (duplicado) {
            throw new NegocioException("Já existe um hóspede cadastrado com este CPF.");
        }
    }

    private String apenasDigitos(String valor) {
        if (valor == null) {
            return null;
        }
        String digitos = valor.replaceAll("\\D", "");
        return digitos.isBlank() ? null : digitos;
    }
}