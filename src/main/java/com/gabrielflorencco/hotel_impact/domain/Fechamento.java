package com.gabrielflorencco.hotel_impact.domain;

import java.math.BigDecimal;
import java.util.List;

public final class Fechamento {

    private Fechamento() {
    }

    public static <T extends Lancamento> BigDecimal totalElegivel(List<T> lancamentos) {
        return lancamentos.stream()
                .filter(Lancamento::contaNoFechamento)
                .map(Lancamento::valorContabil)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}