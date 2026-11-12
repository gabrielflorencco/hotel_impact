package com.gabrielflorencco.hotel_impact.enums;

import com.gabrielflorencco.hotel_impact.exception.NegocioException;
import com.gabrielflorencco.hotel_impact.model.Quarto;

/**
 * Estado operacional do quarto. Cada constante sabe o que significa receber
 * um hóspede: este é o comportamento de recepção do estado, despachado
 * dinamicamente no check-in.
 */
public enum EstadoQuarto {
    DISPONIVEL {
        @Override
        public void exigirRecepcao(Quarto quarto) {
            /* quarto pronto para receber o hóspede */
        }
    },
    OCUPADO {
        @Override
        public void exigirRecepcao(Quarto quarto) {
            throw new NegocioException(
                    "O quarto " + quarto.getNumero() + " está ocupado por outra hospedagem.");
        }
    },
    LIMPEZA {
        @Override
        public void exigirRecepcao(Quarto quarto) {
            throw new NegocioException(
                    "O quarto " + quarto.getNumero() + " está aguardando limpeza.");
        }
    },
    MANUTENCAO {
        @Override
        public void exigirRecepcao(Quarto quarto) {
            recusarForaDeOperacao(quarto);
        }
    },
    INTERDITADO {
        @Override
        public void exigirRecepcao(Quarto quarto) {
            recusarForaDeOperacao(quarto);
        }
    };

    public abstract void exigirRecepcao(Quarto quarto);

    private static void recusarForaDeOperacao(Quarto quarto) {
        throw new NegocioException(
                "O quarto " + quarto.getNumero() + " está fora de operação (" + quarto.getEstado()
                        + ") e não pode receber hospedagens.");
    }
}
