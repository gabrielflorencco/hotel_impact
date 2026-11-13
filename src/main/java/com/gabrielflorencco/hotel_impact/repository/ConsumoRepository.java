package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.Consumo;
import com.gabrielflorencco.hotel_impact.enums.EstadoConsumo;
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
 * Repository de Consumo com SQL explícito via JdbcTemplate.
 */
@Repository
public class ConsumoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ConsumoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Consumo> MAPPER = new RowMapper<>() {
        @Override
        public Consumo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Consumo c = new Consumo();
            c.setId(rs.getLong("c_id"));
            c.setQuantidade(rs.getInt("c_quantidade"));
            c.setPrecoUnitario(rs.getBigDecimal("c_preco_unitario"));
            c.setValorTotal(rs.getBigDecimal("c_valor_total"));
            c.setDataHora(rs.getTimestamp("c_data_hora") != null ? rs.getTimestamp("c_data_hora").toLocalDateTime() : null);
            c.setEstado(EstadoConsumo.valueOf(rs.getString("c_estado")));
            c.setObservacoes(rs.getString("c_observacoes"));
            c.setVersao(rs.getLong("c_versao"));

            com.gabrielflorencco.hotel_impact.model.ItemCatalogo item = new com.gabrielflorencco.hotel_impact.model.ItemCatalogo();
            item.setId(rs.getLong("i_id"));
            item.setNome(rs.getString("i_nome"));
            item.setDescricao(rs.getString("i_descricao"));
            item.setTipo(com.gabrielflorencco.hotel_impact.enums.TipoItem.valueOf(rs.getString("i_tipo")));
            item.setPrecoUnitario(rs.getBigDecimal("i_preco_unitario"));
            item.setControlaEstoque(rs.getBoolean("i_controla_estoque"));
            item.setEstoqueAtual(rs.getInt("i_estoque_atual"));
            item.setEstoqueMinimo(rs.getInt("i_estoque_minimo"));
            item.setAtivo(rs.getBoolean("i_ativo"));
            item.setVersao(rs.getLong("i_versao"));
            c.setItem(item);

            return c;
        }
    };

    public List<Consumo> findByHospedagemIdComItem(Long hospedagemId) {
        String sql = """
            SELECT c.id AS c_id, c.hospedagem_id AS c_hospedagem_id, c.item_id AS c_item_id, c.quantidade AS c_quantidade, c.preco_unitario AS c_preco_unitario, c.valor_total AS c_valor_total, c.data_hora AS c_data_hora, c.estado AS c_estado, c.observacoes AS c_observacoes, c.versao AS c_versao,
                   i.id AS i_id, i.nome AS i_nome, i.descricao AS i_descricao, i.tipo AS i_tipo, i.preco_unitario AS i_preco_unitario, i.controla_estoque AS i_controla_estoque, i.estoque_atual AS i_estoque_atual, i.estoque_minimo AS i_estoque_minimo, i.ativo AS i_ativo, i.versao AS i_versao
            FROM consumo c
            JOIN item_catalogo i ON c.item_id = i.id
            WHERE c.hospedagem_id = ?
            ORDER BY c.data_hora DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, hospedagemId);
    }

    public Optional<Consumo> findByIdAndHospedagemId(Long id, Long hospedagemId) {
        String sql = """
            SELECT c.id AS c_id, c.hospedagem_id AS c_hospedagem_id, c.item_id AS c_item_id, c.quantidade AS c_quantidade, c.preco_unitario AS c_preco_unitario, c.valor_total AS c_valor_total, c.data_hora AS c_data_hora, c.estado AS c_estado, c.observacoes AS c_observacoes, c.versao AS c_versao,
                   i.id AS i_id, i.nome AS i_nome, i.descricao AS i_descricao, i.tipo AS i_tipo, i.preco_unitario AS i_preco_unitario, i.controla_estoque AS i_controla_estoque, i.estoque_atual AS i_estoque_atual, i.estoque_minimo AS i_estoque_minimo, i.ativo AS i_ativo, i.versao AS i_versao
            FROM consumo c
            JOIN item_catalogo i ON c.item_id = i.id
            WHERE c.id = ? AND c.hospedagem_id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id, hospedagemId).stream().findFirst();
    }

    public List<Consumo> findByHospedagemIdAndEstado(Long hospedagemId, EstadoConsumo estado) {
        String sql = """
            SELECT c.id AS c_id, c.hospedagem_id AS c_hospedagem_id, c.item_id AS c_item_id, c.quantidade AS c_quantidade, c.preco_unitario AS c_preco_unitario, c.valor_total AS c_valor_total, c.data_hora AS c_data_hora, c.estado AS c_estado, c.observacoes AS c_observacoes, c.versao AS c_versao,
                   i.id AS i_id, i.nome AS i_nome, i.descricao AS i_descricao, i.tipo AS i_tipo, i.preco_unitario AS i_preco_unitario, i.controla_estoque AS i_controla_estoque, i.estoque_atual AS i_estoque_atual, i.estoque_minimo AS i_estoque_minimo, i.ativo AS i_ativo, i.versao AS i_versao
            FROM consumo c
            JOIN item_catalogo i ON c.item_id = i.id
            WHERE c.hospedagem_id = ? AND c.estado = ?
            ORDER BY c.data_hora DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, hospedagemId, estado.name());
    }

    public boolean existsByItemId(Long itemId) {
        String sql = "SELECT 1 FROM consumo WHERE item_id = ?";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, itemId);
        return !result.isEmpty();
    }

    public Consumo save(Consumo consumo) {
        if (consumo.getId() == null) {
            String sql = """
                INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes, versao)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    consumo.getHospedagem().getId(), consumo.getItem().getId(),
                    consumo.getQuantidade(), consumo.getPrecoUnitario(),
                    consumo.getValorTotal(), consumo.getDataHora(),
                    consumo.getEstado().name(), consumo.getObservacoes());
            consumo.setId(id);
        } else {
            String sql = """
                UPDATE consumo
                SET quantidade = ?, preco_unitario = ?, valor_total = ?, estado = ?, observacoes = ?, versao = versao + 1
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    consumo.getQuantidade(), consumo.getPrecoUnitario(),
                    consumo.getValorTotal(), consumo.getEstado().name(),
                    consumo.getObservacoes(), consumo.getId(), consumo.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Consumo modificado por outra transação");
            }
        }
        return consumo;
    }
}