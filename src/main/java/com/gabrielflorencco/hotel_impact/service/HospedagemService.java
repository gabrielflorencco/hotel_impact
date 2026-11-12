package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.model.Quarto;
import com.gabrielflorencco.hotel_impact.model.Reserva;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.QuartoRepository;
import com.gabrielflorencco.hotel_impact.repository.ReservaRepository;
import com.gabrielflorencco.hotel_impact.repository.TarefaLimpezaRepository;
import com.gabrielflorencco.hotel_impact.service.AuditoriaService;
import com.gabrielflorencco.hotel_impact.service.PagamentoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Regras de negócio das hospedagens: a ocupação real do quarto, originada de
 * uma reserva (1:1). O check-in exige reserva ativa (pendente ou confirmada)
 * na data prevista, sem hospedagem anterior, e quarto ativo e disponível; o
 * check-out encerra a hospedagem, conclui a reserva e envia o quarto para
 * limpeza.
 */
@Service
public class HospedagemService {

    private final HospedagemRepository hospedagens;
    private final ReservaRepository reservas;
    private final QuartoRepository quartos;
    private final TarefaLimpezaRepository tarefasLimpeza;
    private final AuditoriaService auditoria;
    private final PagamentoService pagamentoService;

    public HospedagemService(HospedagemRepository hospedagens, ReservaRepository reservas,
                             QuartoRepository quartos, TarefaLimpezaRepository tarefasLimpeza,
                             AuditoriaService auditoria, PagamentoService pagamentoService) {
        this.hospedagens = hospedagens;
        this.reservas = reservas;
        this.quartos = quartos;
        this.tarefasLimpeza = tarefasLimpeza;
        this.auditoria = auditoria;
        this.pagamentoService = pagamentoService;
    }

    @Transactional(readOnly = true)
    public Page<Hospedagem> buscar(EstadoHospedagem estado, Pageable pageable) {
        return estado == null
                ? hospedagens.buscarTodos(pageable)
                : hospedagens.buscarPorEstado(estado, pageable);
    }

    @Transactional(readOnly = true)
    public Hospedagem buscarPorId(Long id) {
        return hospedagens.findByIdComDetalhes(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Hospedagem não encontrada."));
    }

    @Transactional(readOnly = true)
    public Optional<Hospedagem> buscarPorReserva(Long reservaId) {
        return hospedagens.findByReservaId(reservaId);
    }

    @Transactional
    public Hospedagem checkIn(Long reservaId) {
        Reserva reserva = reservas.findByIdComDetalhes(reservaId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Reserva não encontrada."));
        validarElegibilidadeCheckIn(reserva);

        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setReserva(reserva);
        hospedagem.setQuarto(reserva.getQuarto());
        hospedagem.abrir(LocalDateTime.now());
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.CHECK_IN,
                "Hospedagem", reserva.getId(), "Reserva " + reserva.getCodigo());

        Quarto quarto = reserva.getQuarto();
        quarto.ocupar();
        quartos.save(quarto);

        return hospedagens.save(hospedagem);
    }

    @Transactional
    public void checkOut(Long hospedagemId) {
        checkOut(hospedagemId, null);
    }

    @Transactional
    public void checkOut(Long hospedagemId, String motivoExcecao) {
        Hospedagem hospedagem = buscarPorId(hospedagemId);
        java.math.BigDecimal saldo = pagamentoService.saldoDevedor(hospedagemId);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.CHECK_OUT,
                "Hospedagem", hospedagemId, "Reserva " + hospedagem.getReserva().getCodigo());

        hospedagem.encerrar(saldo, motivoExcecao);

        Reserva reserva = hospedagem.getReserva();
        if (reserva.isAtiva()) {
            reserva.concluir();
            reservas.save(reserva);
        }

        Quarto quarto = hospedagem.getQuarto();
        quarto.enviarParaLimpeza();
        quartos.save(quarto);

        com.gabrielflorencco.hotel_impact.model.TarefaLimpeza tarefa =
                new com.gabrielflorencco.hotel_impact.model.TarefaLimpeza();
        tarefa.setQuarto(quarto);
        tarefasLimpeza.save(tarefa);
    }

    @Transactional(readOnly = true)
    public boolean podeFazerCheckIn(Reserva reserva) {
        try {
            validarElegibilidadeCheckIn(reserva);
            return true;
        } catch (NegocioException ex) {
            return false;
        }
    }

    private void validarElegibilidadeCheckIn(Reserva reserva) {
        if (!reserva.isAtiva()) {
            throw new NegocioException("Somente reservas pendentes ou confirmadas permitem check-in.");
        }
        if (hospedagens.existsByReservaId(reserva.getId())) {
            throw new NegocioException("A reserva já possui uma hospedagem.");
        }
        Quarto quarto = reserva.getQuarto();
        if (!Boolean.TRUE.equals(quarto.getAtivo())) {
            throw new NegocioException(
                    "O quarto " + quarto.getNumero() + " está desativado. Reative-o para recebê-la.");
        }
        quarto.getEstado().exigirRecepcao(quarto);
        if (hospedagens.existsByQuartoIdAndEstado(quarto.getId(), EstadoHospedagem.EM_ANDAMENTO)) {
            throw new NegocioException(
                    "Não é possível fazer check-in: já existe uma hospedagem em andamento no quarto "
                            + quarto.getNumero() + ".");
        }
        if (LocalDate.now().isBefore(reserva.getDataCheckIn())) {
            throw new NegocioException("Check-in só é permitido a partir da data prevista na reserva ("
                    + reserva.getDataCheckIn() + ").");
        }
    }
}