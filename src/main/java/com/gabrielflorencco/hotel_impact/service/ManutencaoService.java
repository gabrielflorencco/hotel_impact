package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.dto.OrdemManutencaoForm;
import com.gabrielflorencco.hotel_impact.model.OrdemManutencao;
import com.gabrielflorencco.hotel_impact.model.Quarto;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoManutencao;
import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.OrdemManutencaoRepository;
import com.gabrielflorencco.hotel_impact.repository.QuartoRepository;
import com.gabrielflorencco.hotel_impact.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ManutencaoService {

    private static final List<EstadoManutencao> ESTADOS_ATIVOS =
            List.of(EstadoManutencao.ABERTA, EstadoManutencao.EM_ANDAMENTO);

    private final OrdemManutencaoRepository ordens;
    private final AuditoriaService auditoria;
    private final QuartoRepository quartos;
    private final HospedagemRepository hospedagens;

    public ManutencaoService(OrdemManutencaoRepository ordens, AuditoriaService auditoria,
                             QuartoRepository quartos, HospedagemRepository hospedagens) {
        this.ordens = ordens;
        this.auditoria = auditoria;
        this.quartos = quartos;
        this.hospedagens = hospedagens;
    }

    @Transactional(readOnly = true)
    public Page<OrdemManutencao> buscar(EstadoManutencao estado, Pageable pageable) {
        return estado == null
                ? ordens.buscarTodos(pageable)
                : ordens.buscarPorEstado(estado, pageable);
    }

    @Transactional(readOnly = true)
    public OrdemManutencao buscarPorId(Long id) {
        return ordens.findByIdComQuarto(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Ordem de manutenção não encontrada."));
    }

    @Transactional
    public OrdemManutencao abrir(OrdemManutencaoForm form) {
        Quarto quarto = quartos.findByIdComCategoria(form.getQuartoId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Quarto não encontrado."));

        if (hospedagens.existsByQuartoIdAndEstado(quarto.getId(), EstadoHospedagem.EM_ANDAMENTO)) {
            throw new NegocioException(
                    "Não é possível abrir a manutenção: existe uma hospedagem em andamento no quarto "
                            + quarto.getNumero() + ".");
        }
        if (ordens.existsByQuartoIdAndEstadoIn(quarto.getId(), ESTADOS_ATIVOS)) {
            throw new NegocioException(
                    "Já existe uma manutenção ativa para o quarto " + quarto.getNumero() + ".");
        }

        OrdemManutencao ordem = new OrdemManutencao();
        ordem.setAbertaEm(LocalDateTime.now());
        ordem.setQuarto(quarto);
        ordem.setTitulo(form.getTitulo().trim());
        ordem.setDescricao(nuloSeVazio(form.getDescricao()));
        ordem.setPrioridade(form.getPrioridade());
        ordem.setObservacoes(nuloSeVazio(form.getObservacoes()));
        ordem.setEstado(EstadoManutencao.ABERTA);

        boolean criticaInterditando = form.getPrioridade() == PrioridadeManutencao.CRITICA
                && quarto.getEstado() != EstadoQuarto.INTERDITADO;
        ordem.setInterditouQuarto(criticaInterditando);
        if (criticaInterditando) {
            quarto.interditar();
            quartos.save(quarto);
        }
        OrdemManutencao salva = ordens.save(ordem);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.MANUTENCAO_ABERTA,
                "OrdemManutencao", salva.getId(),
                "Quarto " + quarto.getNumero() + " · " + salva.getTitulo());
        return salva;
    }

    @Transactional
    public void iniciar(Long id) {
        OrdemManutencao ordem = buscarPorId(id);
        ordem.iniciarExecucao();
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.MANUTENCAO_INICIADA,
                "OrdemManutencao", id, "Quarto " + ordem.getQuarto().getNumero());

        Quarto quarto = ordem.getQuarto();
        if (quarto.getEstado() == EstadoQuarto.DISPONIVEL || quarto.getEstado() == EstadoQuarto.LIMPEZA) {
            quarto.iniciarManutencao();
            quartos.save(quarto);
        }
    }

    @Transactional
    public void concluir(Long id) {
        OrdemManutencao ordem = buscarPorId(id);
        ordem.concluirOrdem();
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.MANUTENCAO_CONCLUIDA,
                "OrdemManutencao", id, "Quarto " + ordem.getQuarto().getNumero());
        liberarQuartoSeCausadoPelaOrdem(ordem);
    }

    @Transactional
    public void cancelar(Long id) {
        OrdemManutencao ordem = buscarPorId(id);
        ordem.cancelarOrdem();
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.MANUTENCAO_CANCELADA,
                "OrdemManutencao", id, "Quarto " + ordem.getQuarto().getNumero());
        liberarQuartoSeCausadoPelaOrdem(ordem);
    }

    private void liberarQuartoSeCausadoPelaOrdem(OrdemManutencao ordem) {
        Quarto quarto = ordem.getQuarto();
        if (Boolean.TRUE.equals(ordem.getInterditouQuarto())
                || quarto.getEstado() == EstadoQuarto.MANUTENCAO) {
            quarto.disponibilizar();
            quartos.save(quarto);
        }
    }

    private String nuloSeVazio(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}