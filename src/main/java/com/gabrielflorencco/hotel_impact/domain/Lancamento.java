package com.gabrielflorencco.hotel_impact.domain;

import java.math.BigDecimal;

public interface Lancamento {

    /** Valor monetário do lançamento. */
    BigDecimal valorContabil();

    /** Elegibilidade para o fechamento — regra própria de cada tipo real. */
    boolean contaNoFechamento();
}