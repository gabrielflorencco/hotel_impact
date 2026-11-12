package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoPagamento;
import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.PagamentoRepository;
import com.gabrielflorencco.hotel_impact.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Relatórios por período: números calculados com consultas de
 * contagem e soma no banco — check-ins/check-outs reais, receita confirmada,
 * reservas criadas (por estado, incluindo cancelamentos e no-shows) e a
 * lista das hospedagens iniciadas no período. Períodos são semiabertos
 * [inicio, fim), medidos à meia-noite.
 */
@Service
public class RelatorioService {

    private final HospedagemRepository hospedagens;
    private final ReservaRepository reservas;
    private final PagamentoRepository pagamentos;

    public RelatorioService(HospedagemRepository hospedagens, ReservaRepository reservas,
                            PagamentoRepository pagamentos) {
        this.hospedagens = hospedagens;
        this.reservas = reservas;
        this.pagamentos = pagamentos;
    }

    public record ResumoPeriodo(
            LocalDate inicio,
            LocalDate fim,
            BigDecimal receitaConfirmada,
            long checkIns,
            long checkOuts,
            long reservasCriadas,
            long reservasCanceladas,
            long noShows,
            List<Hospedagem> hospedagensIniciadas) {
    }

    @Transactional(readOnly = true)
    public ResumoPeriodo resumoPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDateTime inicioDoDia = inicio.atStartOfDay();
        LocalDateTime inicioDoDiaSeguinte = fim.plusDays(1).atStartOfDay();

        return new ResumoPeriodo(
                inicio,
                fim,
                pagamentos.somarPorEstadoEntre(EstadoPagamento.CONFIRMADO, inicioDoDia, inicioDoDiaSeguinte),
                hospedagens.countByCheckInRealBetween(inicioDoDia, inicioDoDiaSeguinte),
                hospedagens.countByCheckOutRealBetween(inicioDoDia, inicioDoDiaSeguinte),
                reservas.countByDataCriacaoBetween(inicioDoDia, inicioDoDiaSeguinte),
                reservas.countByEstadoAndDataCriacaoBetween(EstadoReserva.CANCELADA, inicioDoDia, inicioDoDiaSeguinte),
                reservas.countByEstadoAndDataCriacaoBetween(EstadoReserva.NO_SHOW, inicioDoDia, inicioDoDiaSeguinte),
                hospedagens.buscarIniciadasEntre(inicioDoDia, inicioDoDiaSeguinte));
    }
}