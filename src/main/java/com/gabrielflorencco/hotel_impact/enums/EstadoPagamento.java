package com.gabrielflorencco.hotel_impact.enums;

/**
 * Estados do pagamento. Um pagamento CONFIRMADO é imutável quanto ao valor
 * original: correções usam estorno e novo lançamento. Estornados não contam
 * como recebidos.
 */
public enum EstadoPagamento {
    CONFIRMADO,
    ESTORNADO
}
