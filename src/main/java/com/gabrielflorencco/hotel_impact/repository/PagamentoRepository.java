package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.Pagamento;
import com.gabrielflorencco.hotel_impact.enums.EstadoPagamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository de Pagamento com SQL explícito via JdbcTemplate.
 */
@Repository
public class PagamentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PagamentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Pagamento> MAPPER = new RowMapper<>() {
        @Override
        public Pagamento mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pagamento p = new Pagamento();
            p.setId(rs.getLong("id"));
            p.setValor(rs.getBigDecimal("valor"));
            p.setForma(com.gabrielflorencco.hotel_impact.enums.FormaPagamento.valueOf(rs.getString("forma")));
            p.setDataHora(rs.getTimestamp("data_hora") != null ? rs.getTimestamp("data_hora").toLocalDateTime() : null);
            p.setEstado(EstadoPagamento.valueOf(rs.getString("estado")));
            p.setObservacao(rs.getString("observacao"));
            p.setVersao(rs.getLong("versao"));
            return p;
        }
    };

    public List<Pagamento> findByHospedagemIdOrderByDataHoraAsc(Long hospedagemId) {
        String sql = "SELECT id, hospedagem_id, valor, forma, data_hora, estado, observacao, versao FROM pagamento WHERE hospedagem_id = ? ORDER BY data_hora ASC";
        return jdbcTemplate.query(sql, MAPPER, hospedagemId);
    }

    public List<Pagamento> findByEstadoAndDataHoraBetween(EstadoPagamento estado, LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT id, hospedagem_id, valor, forma, data_hora, estado, observacao, versao FROM pagamento WHERE estado = ? AND data_hora >= ? AND data_hora < ? ORDER BY data_hora ASC";
        return jdbcTemplate.query(sql, MAPPER, estado.name(), inicio, fim);
    }

    public List<Pagamento> findByHospedagemIdAndEstado(Long hospedagemId, EstadoPagamento estado) {
        String sql = "SELECT id, hospedagem_id, valor, forma, data_hora, estado, observacao, versao FROM pagamento WHERE hospedagem_id = ? AND estado = ? ORDER BY data_hora ASC";
        return jdbcTemplate.query(sql, MAPPER, hospedagemId, estado.name());
    }

    public Optional<Pagamento> findByIdAndHospedagemId(Long id, Long hospedagemId) {
        String sql = "SELECT id, hospedagem_id, valor, forma, data_hora, estado, observacao, versao FROM pagamento WHERE id = ? AND hospedagem_id = ?";
        return jdbcTemplate.query(sql, MAPPER, id, hospedagemId).stream().findFirst();
    }

    public BigDecimal somarPorEstadoEntre(EstadoPagamento estado, LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COALESCE(SUM(valor), 0) FROM pagamento WHERE estado = ? AND data_hora >= ? AND data_hora < ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, estado.name(), inicio, fim);
    }

    public Pagamento save(Pagamento pagamento) {
        if (pagamento.getId() == null) {
            String sql = """
                INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    pagamento.getHospedagem().getId(), pagamento.getValor(),
                    pagamento.getForma().name(), pagamento.getDataHora(),
                    pagamento.getEstado().name(), pagamento.getObservacao());
            pagamento.setId(id);
        } else {
            String sql = """
                UPDATE pagamento
                SET valor = ?, forma = ?, estado = ?, observacao = ?
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    pagamento.getValor(), pagamento.getForma().name(),
                    pagamento.getEstado().name(), pagamento.getObservacao(),
                    pagamento.getId(), pagamento.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Pagamento modificado por outra transação");
            }
        }
        return pagamento;
    }
}