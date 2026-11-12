package com.gabrielflorencco.hotel_impact.model;

import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import com.gabrielflorencco.hotel_impact.exception.NegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Reserva: o compromisso futuro de hospedagem. Períodos são intervalos
 * semiabertos [check-in, check-out). O valor da diária e o total são
 * históricos — mudanças no preço-base da categoria não afetam reservas
 * existentes. O total e o bruto são sempre calculados no backend.
 */
public class Reserva {

    private Long id;
    private String codigo;
    private Hospede hospede;
    private Quarto quarto;
    private LocalDate dataCheckIn;
    private LocalDate dataCheckOut;
    private Integer quantidadeHospedes;
    private BigDecimal valorDiaria;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal valorTotal;
    private EstadoReserva estado = EstadoReserva.PENDENTE;
    private String motivoExcecao;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private Long versao;

    public Reserva() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Hospede getHospede() {
        return hospede;
    }

    public void setHospede(Hospede hospede) {
        this.hospede = hospede;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDate getDataCheckIn() {
        return dataCheckIn;
    }

    public void setDataCheckIn(LocalDate dataCheckIn) {
        this.dataCheckIn = dataCheckIn;
    }

    public LocalDate getDataCheckOut() {
        return dataCheckOut;
    }

    public void setDataCheckOut(LocalDate dataCheckOut) {
        this.dataCheckOut = dataCheckOut;
    }

    public Integer getQuantidadeHospedes() {
        return quantidadeHospedes;
    }

    public void setQuantidadeHospedes(Integer quantidadeHospedes) {
        this.quantidadeHospedes = quantidadeHospedes;
    }

    public BigDecimal getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(BigDecimal valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getMotivoExcecao() {
        return motivoExcecao;
    }

    public void setMotivoExcecao(String motivoExcecao) {
        this.motivoExcecao = motivoExcecao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    /** Número de noites do período semiaberto [check-in, check-out). */
    public long getQuantidadeNoites() {
        return ChronoUnit.DAYS.between(dataCheckIn, dataCheckOut);
    }

    /** Valor bruto da hospedagem sem desconto. */
    public BigDecimal getValorBruto() {
        return valorDiaria.multiply(BigDecimal.valueOf(getQuantidadeNoites()));
    }

    /** Reservas em estado que bloqueia a disponibilidade do quarto. */
    public boolean isAtiva() {
        return estado == EstadoReserva.PENDENTE || estado == EstadoReserva.CONFIRMADA;
    }

    /*
     * Máquina de estados da reserva: a própria reserva é a única capaz de
     * transitar seu estado. Os serviços orquestram as verificações que
     * dependem do mundo externo (check-in existente, data passada,
     * persistência) e chamam estas operações de domínio.
     */

    /** Confirma uma reserva pendente. */
    public void confirmar() {
        if (estado != EstadoReserva.PENDENTE) {
            throw new NegocioException("Somente reservas pendentes podem ser confirmadas.");
        }
        estado = EstadoReserva.CONFIRMADA;
    }

    /** Cancela uma reserva ativa — o serviço garante que ainda não houve check-in. */
    public void cancelar() {
        if (!isAtiva()) {
            throw new NegocioException("Somente reservas pendentes ou confirmadas podem ser canceladas.");
        }
        estado = EstadoReserva.CANCELADA;
    }

    /** Registra o não-comparecimento — o serviço garante data de chegada passada e ausência de check-in. */
    public void registrarNoShow() {
        if (!isAtiva()) {
            throw new NegocioException("Somente reservas pendentes ou confirmadas podem ser registradas como no-show.");
        }
        estado = EstadoReserva.NO_SHOW;
    }

    /**
     * Reprograma a reserva (edição controlada): período e desconto são
     * validados localmente e o total é recalculado no servidor —
     * diária × noites − desconto. Verificações externas (disponibilidade,
     * capacidade, quarto em operação) pertencem ao serviço.
     */
    public void reprogramar(Hospede novoHospede, Quarto novoQuarto,
                            LocalDate novoCheckIn, LocalDate novoCheckOut,
                            Integer novaQuantidade, BigDecimal novaDiaria,
                            BigDecimal novoDesconto, String motivoExcecao, String observacoes) {
        if (!novoCheckOut.isAfter(novoCheckIn)) {
            throw new NegocioException("A data de check-out deve ser posterior à data de check-in.");
        }
        BigDecimal descontos = novoDesconto == null ? BigDecimal.ZERO : novoDesconto;
        BigDecimal bruto = novaDiaria.multiply(
                BigDecimal.valueOf(novoCheckOut.toEpochDay() - novoCheckIn.toEpochDay()));
        if (descontos.compareTo(bruto) > 0) {
            throw new NegocioException(
                    "O desconto não pode exceder o valor bruto da reserva (R$ " + bruto.toPlainString() + ").");
        }
        this.hospede = novoHospede;
        this.quarto = novoQuarto;
        this.dataCheckIn = novoCheckIn;
        this.dataCheckOut = novoCheckOut;
        this.quantidadeHospedes = novaQuantidade;
        this.valorDiaria = novaDiaria;
        this.desconto = descontos;
        this.valorTotal = bruto.subtract(descontos);
        this.motivoExcecao = motivoExcecao == null || motivoExcecao.isBlank() ? null : motivoExcecao.trim();
        this.observacoes = observacoes == null || observacoes.isBlank() ? null : observacoes.trim();
    }

    /** Conclui a reserva no check-out da hospedagem. */
    public void concluir() {
        if (!isAtiva()) {
            throw new NegocioException("Somente reservas ativas podem ser concluídas.");
        }
        estado = EstadoReserva.CONCLUIDA;
    }
}