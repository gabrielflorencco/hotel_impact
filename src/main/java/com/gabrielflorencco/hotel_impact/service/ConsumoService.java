package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.dto.ConsumoForm;
import com.gabrielflorencco.hotel_impact.model.Consumo;
import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.model.ItemCatalogo;
import com.gabrielflorencco.hotel_impact.enums.EstadoConsumo;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.ConsumoRepository;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.ItemCatalogoRepository;
import com.gabrielflorencco.hotel_impact.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ConsumoService {

    private final ConsumoRepository consumos;
    private final HospedagemRepository hospedagens;
    private final ItemCatalogoRepository itens;
    private final AuditoriaService auditoria;

    public ConsumoService(ConsumoRepository consumos, HospedagemRepository hospedagens,
                          ItemCatalogoRepository itens, AuditoriaService auditoria) {
        this.consumos = consumos;
        this.hospedagens = hospedagens;
        this.itens = itens;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<Consumo> listarPorHospedagem(Long hospedagemId) {
        return consumos.findByHospedagemIdComItem(hospedagemId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalAtivos(Long hospedagemId) {
        return com.gabrielflorencco.hotel_impact.domain.Fechamento
                .totalElegivel(consumos.findByHospedagemIdComItem(hospedagemId));
    }

    @Transactional
    public Consumo lancar(Long hospedagemId, ConsumoForm form) {
        Hospedagem hospedagem = hospedagens.findByIdComDetalhes(hospedagemId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Hospedagem não encontrada."));
        if (hospedagem.getEstado() != EstadoHospedagem.EM_ANDAMENTO) {
            throw new NegocioException("Consumos só podem ser lançados em hospedagens em andamento.");
        }

        ItemCatalogo item = itens.findById(form.getItemId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Item do catálogo não encontrado."));
        if (!Boolean.TRUE.equals(item.getAtivo())) {
            throw new NegocioException("O item \"" + item.getNome() + "\" está inativo e não pode ser lançado.");
        }

        Consumo consumo = new Consumo();
        consumo.setHospedagem(hospedagem);
        consumo.setDataHora(java.time.LocalDateTime.now());
        consumo.setItem(item);
        consumo.setQuantidade(form.getQuantidade());
        consumo.setPrecoUnitario(item.getPrecoUnitario());
        consumo.setValorTotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(form.getQuantidade())));
        consumo.setEstado(EstadoConsumo.ATIVO);
        consumo.setObservacoes(nuloSeVazio(form.getObservacoes()));

        if (item.isEstoqueControladoEfetivo()) {
            if (item.getEstoqueAtual() < form.getQuantidade()) {
                throw new NegocioException("Estoque insuficiente de \"" + item.getNome() + "\": restam "
                        + item.getEstoqueAtual() + " unidade(s).");
            }
            item.setEstoqueAtual(item.getEstoqueAtual() - form.getQuantidade());
            itens.save(item);
        }
        Consumo salvo = consumos.save(consumo);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.CONSUMO_LANCADO,
                "Consumo", salvo.getId(), "Hospedagem " + hospedagemId + " · " + item.getNome());
        return salvo;
    }

    @Transactional
    public void cancelar(Long hospedagemId, Long consumoId) {
        Hospedagem hospedagem = hospedagens.findByIdComDetalhes(hospedagemId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Hospedagem não encontrada."));
        if (hospedagem.getEstado() != EstadoHospedagem.EM_ANDAMENTO) {
            throw new NegocioException("Consumos só podem ser cancelados em hospedagens em andamento.");
        }

        Consumo consumo = consumos.findByIdAndHospedagemId(consumoId, hospedagemId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Consumo não encontrado nesta hospedagem."));
        consumo.cancelar();
        consumos.save(consumo);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.CONSUMO_CANCELADO,
                "Consumo", consumoId, "Hospedagem " + hospedagemId);

        ItemCatalogo item = consumo.getItem();
        if (item.isEstoqueControladoEfetivo()) {
            item.setEstoqueAtual(item.getEstoqueAtual() + consumo.getQuantidade());
            itens.save(item);
        }
    }

    private String nuloSeVazio(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}