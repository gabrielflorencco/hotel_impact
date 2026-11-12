package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.model.Reserva;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoPagamento;
import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import com.gabrielflorencco.hotel_impact.enums.EstadoTarefaLimpeza;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.ItemCatalogoRepository;
import com.gabrielflorencco.hotel_impact.repository.PagamentoRepository;
import com.gabrielflorencco.hotel_impact.repository.QuartoRepository;
import com.gabrielflorencco.hotel_impact.repository.ReservaRepository;
import com.gabrielflorencco.hotel_impact.repository.TarefaLimpezaRepository;
import com.gabrielflorencco.hotel_impact.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private static final List<EstadoReserva> RESERVAS_ATIVAS =
            List.of(EstadoReserva.PENDENTE, EstadoReserva.CONFIRMADA);

    private final QuartoRepository quartos;
    private final HospedagemRepository hospedagens;
    private final ReservaRepository reservas;
    private final PagamentoRepository pagamentos;
    private final ItemCatalogoRepository itens;
    private final TarefaLimpezaRepository tarefas;
    private final AuditoriaService auditoria;

    public DashboardService(QuartoRepository quartos, HospedagemRepository hospedagens,
                            ReservaRepository reservas, PagamentoRepository pagamentos,
                            ItemCatalogoRepository itens, TarefaLimpezaRepository tarefas,
                            AuditoriaService auditoria) {
        this.quartos = quartos;
        this.hospedagens = hospedagens;
        this.reservas = reservas;
        this.pagamentos = pagamentos;
        this.itens = itens;
        this.tarefas = tarefas;
        this.auditoria = auditoria;
    }

    public record Indicadores(
            long quartosAtivos,
            long quartosDisponiveis,
            long quartosOcupados,
            long quartosEmLimpeza,
            long quartosEmManutencao,
            long quartosInterditados,
            long hospedagensEmAndamento,
            long chegadasPrevistasHoje,
            long saidasPrevistasHoje,
            BigDecimal receitaConfirmadaHoje,
            long itensComEstoqueBaixo,
            long tarefasLimpezaPendentes,
            long tarefasLimpezaEmAndamento) {
    }

    @Transactional(readOnly = true)
    public Indicadores indicadores() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = hoje.atStartOfDay();
        LocalDateTime inicioDoDiaSeguinte = hoje.plusDays(1).atStartOfDay();

        return new Indicadores(
                quartos.countByAtivoTrue(),
                quartos.countByEstadoAndAtivoTrue(EstadoQuarto.DISPONIVEL),
                quartos.countByEstadoAndAtivoTrue(EstadoQuarto.OCUPADO),
                quartos.countByEstadoAndAtivoTrue(EstadoQuarto.LIMPEZA),
                quartos.countByEstadoAndAtivoTrue(EstadoQuarto.MANUTENCAO),
                quartos.countByEstadoAndAtivoTrue(EstadoQuarto.INTERDITADO),
                hospedagens.countByEstado(EstadoHospedagem.EM_ANDAMENTO),
                reservas.countByEstadoInAndDataCheckInEquals(RESERVAS_ATIVAS, hoje),
                reservas.countByEstadoInAndDataCheckOutEquals(RESERVAS_ATIVAS, hoje),
                pagamentos.somarPorEstadoEntre(EstadoPagamento.CONFIRMADO, inicioDoDia, inicioDoDiaSeguinte),
                itens.countComEstoqueBaixo(),
                tarefas.countByEstado(EstadoTarefaLimpeza.PENDENTE),
                tarefas.countByEstado(EstadoTarefaLimpeza.EM_EXECUCAO));
    }

    @Transactional(readOnly = true)
    public List<com.gabrielflorencco.hotel_impact.model.RegistroAuditoria> atividadesRecentes() {
        return auditoria.listarRecentes(
                org.springframework.data.domain.PageRequest.of(0, 6)).getContent();
    }

    @Transactional(readOnly = true)
    public List<Reserva> chegadasDeHoje() {
        return reservas.buscarAtivasPorDataCheckInComDetalhes(RESERVAS_ATIVAS, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Reserva> saidasDeHoje() {
        return reservas.buscarAtivasPorDataCheckOutComDetalhes(RESERVAS_ATIVAS, LocalDate.now());
    }
}