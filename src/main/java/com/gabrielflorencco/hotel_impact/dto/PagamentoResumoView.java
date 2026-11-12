package com.gabrielflorencco.hotel_impact.dto;

import com.gabrielflorencco.hotel_impact.enums.EstadoPagamento;
import com.gabrielflorencco.hotel_impact.enums.FormaPagamento;
import com.gabrielflorencco.hotel_impact.model.Pagamento;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Pagamento exibido na conta da hospedagem. Estornados permanecem na lista
 * para preservar o histórico, mas não contam como recebidos.
 */
public final class PagamentoResumoView {

    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Long id;
    private final FormaPagamento forma;
    private final BigDecimal valor;
    private final String dataHora;
    private final EstadoPagamento estado;
    private final String observacao;

    private PagamentoResumoView(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.forma = pagamento.getForma();
        this.valor = pagamento.getValor();
        this.dataHora = pagamento.getDataHora() == null ? null : pagamento.getDataHora().format(DATA_HORA);
        this.estado = pagamento.getEstado();
        this.observacao = pagamento.getObservacao();
    }

    public static PagamentoResumoView de(Pagamento pagamento) {
        return new PagamentoResumoView(pagamento);
    }

    public Long getId() {
        return id;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getDataHora() {
        return dataHora;
    }

    public EstadoPagamento getEstado() {
        return estado;
    }

    public String getObservacao() {
        return observacao;
    }
}