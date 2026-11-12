package com.gabrielflorencco.hotel_impact.domain;

import com.gabrielflorencco.hotel_impact.model.Consumo;
import com.gabrielflorencco.hotel_impact.model.Pagamento;

import java.math.BigDecimal;
import java.util.List;

public record Conta(BigDecimal valorReserva, List<Consumo> consumos, List<Pagamento> pagamentos) {

    public Conta {
        consumos = List.copyOf(consumos);
        pagamentos = List.copyOf(pagamentos);
    }

    /** Total dos consumos elegíveis — parcela gerada durante a estadia. */
    public BigDecimal totalConsumos() {
        return Fechamento.totalElegivel(consumos);
    }

    /** Total pago: pagamentos confirmados (estornos saem). */
    public BigDecimal pago() {
        return Fechamento.totalElegivel(pagamentos);
    }

    /** Total da conta: reserva + consumos que contam. */
    public BigDecimal total() {
        return valorReserva.add(totalConsumos());
    }

    /** Saldo devedor: total − pago; negativo significa crédito. */
    public BigDecimal saldoDevedor() {
        return total().subtract(pago());
    }

    /** Conta quitada: saldo zero ou crédito a favor do hóspede. */
    public boolean quitada() {
        return saldoDevedor().signum() <= 0;
    }
}