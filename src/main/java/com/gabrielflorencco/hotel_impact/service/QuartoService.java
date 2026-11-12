package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.dto.QuartoForm;
import com.gabrielflorencco.hotel_impact.model.CategoriaQuarto;
import com.gabrielflorencco.hotel_impact.model.Quarto;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.CategoriaQuartoRepository;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.OrdemManutencaoRepository;
import com.gabrielflorencco.hotel_impact.repository.QuartoRepository;
import com.gabrielflorencco.hotel_impact.repository.ReservaRepository;
import com.gabrielflorencco.hotel_impact.repository.TarefaLimpezaRepository;
import com.gabrielflorencco.hotel_impact.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartos;
    private final CategoriaQuartoRepository categorias;
    private final ReservaRepository reservas;
    private final HospedagemRepository hospedagens;
    private final TarefaLimpezaRepository tarefasLimpeza;
    private final OrdemManutencaoRepository ordensManutencao;
    private final AuditoriaService auditoria;

    public QuartoService(QuartoRepository quartos, CategoriaQuartoRepository categorias,
                         ReservaRepository reservas, HospedagemRepository hospedagens,
                         TarefaLimpezaRepository tarefasLimpeza, OrdemManutencaoRepository ordensManutencao,
                         AuditoriaService auditoria) {
        this.quartos = quartos;
        this.categorias = categorias;
        this.reservas = reservas;
        this.hospedagens = hospedagens;
        this.tarefasLimpeza = tarefasLimpeza;
        this.ordensManutencao = ordensManutencao;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<Quarto> buscar(String numero, EstadoQuarto estado, Long categoriaId) {
        return quartos.buscar(numero, estado, categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Quarto> listarReservaveis() {
        return quartos.findReservaveis();
    }

    @Transactional(readOnly = true)
    public Quarto buscarPorId(Long id) {
        return quartos.findByIdComCategoria(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Quarto não encontrado."));
    }

    @Transactional
    public Quarto salvar(QuartoForm form) {
        validarNumeroUnico(form.getNumero().trim(), form.getId());
        CategoriaQuarto categoria = categorias.findById(form.getCategoriaId())
                .orElseThrow(() -> new NegocioException("Categoria informada não existe."));

        Quarto quarto;
        if (form.getId() == null) {
            exigirCategoriaAtiva(categoria);
            quarto = new Quarto();
            quarto.setEstado(EstadoQuarto.DISPONIVEL);
        } else {
            quarto = buscarPorId(form.getId());
            if (!quarto.getCategoria().getId().equals(categoria.getId())) {
                exigirCategoriaAtiva(categoria);
            }
            exigirSemHospedagemAtiva(quarto, "alterar o quarto");
        }

        quarto.setNumero(form.getNumero().trim());
        quarto.setCategoria(categoria);
        quarto.setAndar(form.getAndar());
        quarto.setCapacidadeOperacional(form.getCapacidadeOperacional());
        quarto.setObservacoes(form.getObservacoes() == null ? null : form.getObservacoes().trim());
        quarto.setAtivo(form.getAtivo() != null && form.getAtivo());
        return quartos.save(quarto);
    }

    @Transactional
    public void excluir(Long id) {
        Quarto quarto = buscarPorId(id);
        if (reservas.existsByQuartoId(id) || hospedagens.existsByQuartoId(id)
                || tarefasLimpeza.existsByQuartoId(id) || ordensManutencao.existsByQuartoId(id)) {
            throw new NegocioException(
                    "Não é possível excluir o quarto: ele possui histórico de reservas, hospedagens, limpeza ou manutenção. Desative-o.");
        }
        exigirSemHospedagemAtiva(quarto, "excluir o quarto");
        quartos.delete(quarto);
    }

    @Transactional
    public boolean alternarAtivo(Long id) {
        Quarto quarto = buscarPorId(id);
        if (Boolean.TRUE.equals(quarto.getAtivo())) {
            exigirSemHospedagemAtiva(quarto, "desativar o quarto");
            quarto.setAtivo(false);
        } else {
            quarto.setAtivo(true);
        }
        quartos.save(quarto);
        return quarto.getAtivo();
    }

    @Transactional
    public void interditar(Long id) {
        Quarto quarto = buscarPorId(id);
        exigirSemHospedagemAtiva(quarto, "interditar o quarto");
        quarto.interditar();
        quartos.save(quarto);
    }

    @Transactional
    public void liberar(Long id) {
        Quarto quarto = buscarPorId(id);
        quarto.liberarInterdicao();
        quartos.save(quarto);
    }

    @Transactional
    public void concluirLimpeza(Long id) {
        Quarto quarto = buscarPorId(id);
        tarefasLimpeza.findFirstByQuartoIdAndEstadoNotOrderByCriadaEmDesc(
                        id, com.gabrielflorencco.hotel_impact.enums.EstadoTarefaLimpeza.CONCLUIDA)
                .ifPresent(tarefa -> {
                    tarefa.concluir();
                    tarefasLimpeza.save(tarefa);
                });
        quarto.concluirLimpeza();
        quartos.save(quarto);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.LIMPEZA_CONCLUIDA,
                "Quarto", quarto.getId(), "Quarto " + quarto.getNumero());
    }

    @Transactional
    public void iniciarLimpeza(Long id) {
        Quarto quarto = buscarPorId(id);
        if (quarto.getEstado() != EstadoQuarto.LIMPEZA) {
            throw new NegocioException("Somente quartos aguardando limpeza têm tarefa de limpeza.");
        }
        com.gabrielflorencco.hotel_impact.model.TarefaLimpeza tarefa =
                tarefasLimpeza.findFirstByQuartoIdAndEstadoNotOrderByCriadaEmDesc(
                                id, com.gabrielflorencco.hotel_impact.enums.EstadoTarefaLimpeza.CONCLUIDA)
                        .orElseThrow(() -> new NegocioException(
                                "Não há tarefa de limpeza aberta para o quarto " + quarto.getNumero() + "."));
        tarefa.iniciar();
        tarefasLimpeza.save(tarefa);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.LIMPEZA_INICIADA,
                "TarefaLimpeza", tarefa.getId(), "Quarto " + quarto.getNumero());
    }

    private void validarNumeroUnico(String numero, Long id) {
        boolean duplicado = id == null
                ? quartos.existsByNumero(numero)
                : quartos.existsByNumeroAndIdNot(numero, id);
        if (duplicado) {
            throw new NegocioException("Já existe um quarto com o número informado.");
        }
    }

    private void exigirCategoriaAtiva(CategoriaQuarto categoria) {
        if (!Boolean.TRUE.equals(categoria.getAtivo())) {
            throw new NegocioException(
                    "A categoria '" + categoria.getNome() + "' está inativa. Reative-a para utilizá-la em quartos.");
        }
    }

    private void exigirSemHospedagemAtiva(Quarto quarto, String operacao) {
        if (hospedagens.existsByQuartoIdAndEstado(quarto.getId(), EstadoHospedagem.EM_ANDAMENTO)) {
            throw new NegocioException("Não é possível " + operacao + ": existe uma hospedagem em andamento nele.");
        }
    }
}