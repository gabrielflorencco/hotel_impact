package com.gabrielflorencco.hotel_impact.service;

import com.gabrielflorencco.hotel_impact.dto.PagamentoForm;
import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.model.Pagamento;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoConsumo;
import com.gabrielflorencco.hotel_impact.enums.EstadoPagamento;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.exception.RegistroNaoEncontradoException;
import com.gabrielflorencco.hotel_impact.repository.ConsumoRepository;
import com.gabrielflorencco.hotel_impact.repository.HospedagemRepository;
import com.gabrielflorencco.hotel_impact.repository.PagamentoRepository;
import com.gabrielflorencco.hotel_impact.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentos;
    private final HospedagemRepository hospedagens;
    private final ConsumoRepository consumos;
    private final AuditoriaService auditoria;

    public PagamentoService(PagamentoRepository pagamentos, HospedagemRepository hospedagens,
                            ConsumoRepository consumos, AuditoriaService auditoria) {
        this.pagamentos = pagamentos;
        this.hospedagens = hospedagens;
        this.consumos = consumos;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listarPorHospedagem(Long hospedagemId) {
        return pagamentos.findByHospedagemIdOrderByDataHoraAsc(hospedagemId);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalConfirmados(Long hospedagemId) {
        return contaDe(hospedagemId).pago();
    }

    @Transactional(readOnly = true)
    public com.gabrielflorencco.hotel_impact.domain.Conta contaDe(Long hospedagemId) {
        Hospedagem hospedagem = hospedagens.findByIdComDetalhes(hospedagemId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Hospedagem não encontrada."));
        return new com.gabrielflorencco.hotel_impact.domain.Conta(
                hospedagem.getReserva().getValorTotal(),
                consumos.findByHospedagemIdComItem(hospedagemId),
                pagamentos.findByHospedagemIdOrderByDataHoraAsc(hospedagemId));
    }

    @Transactional(readOnly = true)
    public BigDecimal totalConta(Long hospedagemId) {
        return contaDe(hospedagemId).total();
    }

    @Transactional(readOnly = true)
    public BigDecimal saldoDevedor(Long hospedagemId) {
        return contaDe(hospedagemId).saldoDevedor();
    }

    @Transactional
    public Pagamento registrar(Long hospedagemId, PagamentoForm form) {
        Hospedagem hospedagem = hospedagens.findByIdComDetalhes(hospedagemId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Hospedagem não encontrada."));
        if (hospedagem.getEstado() == EstadoHospedagem.CANCELADA) {
            throw new NegocioException(
                    "A hospedagem foi encerrada excepcionalmente e não aceita novos pagamentos.");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setHospedagem(hospedagem);
        pagamento.setDataHora(java.time.LocalDateTime.now());
        pagamento.setValor(form.getValor());
        pagamento.setForma(form.getForma());
        pagamento.setEstado(EstadoPagamento.CONFIRMADO);
        pagamento.setObservacao(nuloSeVazio(form.getObservacao()));
        Pagamento salvo = pagamentos.save(pagamento);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.PAGAMENTO_REGISTRADO,
                "Pagamento", salvo.getId(),
                "Hospedagem " + hospedagemId + " · " + form.getForma());
        return salvo;
    }

    @Transactional
    public void estornar(Long hospedagemId, Long pagamentoId) {
        Pagamento pagamento = pagamentos.findByIdAndHospedagemId(pagamentoId, hospedagemId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Pagamento não encontrado nesta hospedagem."));
        pagamento.estornar();
        pagamentos.save(pagamento);
        auditoria.registrar(com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria.PAGAMENTO_ESTORNADO,
                "Pagamento", pagamentoId, "Hospedagem " + hospedagemId);
    }

    private String nuloSeVazio(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}