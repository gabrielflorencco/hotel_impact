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
            c.setId(rs.getLong("c.id"));
            c.setQuantidade(rs.getInt("c.quantidade"));
            c.setPrecoUnitario(rs.getBigDecimal("c.preco_unitario"));
            c.setValorTotal(rs.getBigDecimal("c.valor_total"));
            c.setDataHora(rs.getTimestamp("c.data_hora") != null ? rs.getTimestamp("c.data_hora").toLocalDateTime() : null);
            c.setEstado(EstadoConsumo.valueOf(rs.getString("c.estado")));
            c.setObservacoes(rs.getString("c.observacoes"));
            c.setVersao(rs.getLong("c.versao"));

            com.gabrielflorencco.hotel_impact.model.ItemCatalogo item = new com.gabrielflorencco.hotel_impact.model.ItemCatalogo();
            item.setId(rs.getLong("i.id"));
            item.setNome(rs.getString("i.nome"));
            item.setDescricao(rs.getString("i.descricao"));
            item.setTipo(com.gabrielflorencco.hotel_impact.enums.TipoItem.valueOf(rs.getString("i.tipo")));
            item.setPrecoUnitario(rs.getBigDecimal("i.preco_unitario"));
            item.setControlaEstoque(rs.getBoolean("i.controla_estoque"));
            item.setEstoqueAtual(rs.getInt("i.estoque_atual"));
            item.setEstoqueMinimo(rs.getInt("i.estoque_minimo"));
            item.setAtivo(rs.getBoolean("i.ativo"));
            item.setVersao(rs.getLong("i.versao"));
            c.setItem(item);

            return c;
        }
    };

    public List<Consumo> findByHospedagemIdComItem(Long hospedagemId) {
        String sql = """
            SELECT c.id, c.hospedagem_id, c.item_id, c.quantidade, c.preco_unitario, c.valor_total, c.data_hora, c.estado, c.observacoes, c.versao,
                   i.id, i.nome, i.descricao, i.tipo, i.preco_unitario, i.controla_estoque, i.estoque_atual, i.estoque_minimo, i.ativo, i.versao
            FROM consumo c
            JOIN item_catalogo i ON c.item_id = i.id
            WHERE c.hospedagem_id = ?
            ORDER BY c.data_hora DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, hospedagemId);
    }

    public Optional<Consumo> findByIdAndHospedagemId(Long id, Long hospedagemId) {
        String sql = """
            SELECT c.id, c.hospedagem_id, c.item_id, c.quantidade, c.preco_unitario, c.valor_total, c.data_hora, c.estado, c.observacoes, c.versao,
                   i.id, i.nome, i.descricao, i.tipo, i.preco_unitario, i.controla_estoque, i.estoque_atual, i.estoque_minimo, i.ativo, i.versao
            FROM consumo c
            JOIN item_catalogo i ON c.item_id = i.id
            WHERE c.id = ? AND c.hospedagem_id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id, hospedagemId).stream().findFirst();
    }

    public List<Consumo> findByHospedagemIdAndEstado(Long hospedagemId, EstadoConsumo estado) {
        String sql = """
            SELECT c.id, c.hospedagem_id, c.item_id, c.quantidade, c.preco_unitario, c.valor_total, c.data_hora, c.estado, c.observacoes, c.versao,
                   i.id, i.nome, i.descricao, i.tipo, i.preco_unitario, i.controla_estoque, i.estoque_atual, i.estoque_minimo, i.ativo, i.versao
            FROM consumo c
            JOIN item_catalogo i ON c.item_id = i.id
            WHERE c.hospedagem_id = ? AND c.estado = ?
            ORDER BY c.data_hora DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, hospedagemId, estado.name());
    }

    public boolean existsByItemId(Long itemId) {
        String sql = "SELECT 1 FROM consumo WHERE item_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, itemId));
    }

    public Consumo save(Consumo consumo) {
        if (consumo.getId() == null) {
            String sql = """
                INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
                SET quantidade = ?, preco_unitario = ?, valor_total = ?, estado = ?, observacoes = ?
                WHERE id = ?
                """;
            jdbcTemplate.update(sql,
                    consumo.getQuantidade(), consumo.getPrecoUnitario(),
                    consumo.getValorTotal(), consumo.getEstado().name(),
                    consumo.getObservacoes(), consumo.getId());
        }
        return consumo;
    }
}