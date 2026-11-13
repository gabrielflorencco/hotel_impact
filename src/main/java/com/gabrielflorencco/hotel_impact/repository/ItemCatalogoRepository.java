package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.ItemCatalogo;
import com.gabrielflorencco.hotel_impact.enums.TipoItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository de ItemCatalogo com SQL explícito via JdbcTemplate.
 */
@Repository
public class ItemCatalogoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ItemCatalogoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ItemCatalogo> MAPPER = new RowMapper<>() {
        @Override
        public ItemCatalogo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ItemCatalogo i = new ItemCatalogo();
            i.setId(rs.getLong("id"));
            i.setNome(rs.getString("nome"));
            i.setDescricao(rs.getString("descricao"));
            i.setTipo(TipoItem.valueOf(rs.getString("tipo")));
            i.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
            i.setControlaEstoque(rs.getBoolean("controla_estoque"));
            i.setEstoqueAtual(rs.getInt("estoque_atual"));
            i.setEstoqueMinimo(rs.getInt("estoque_minimo"));
            i.setAtivo(rs.getBoolean("ativo"));
            i.setVersao(rs.getLong("versao"));
            return i;
        }
    };

    public List<ItemCatalogo> findAllByOrderByNomeAsc() {
        String sql = """
            SELECT id, nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo, versao
            FROM item_catalogo
            ORDER BY nome
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public List<ItemCatalogo> findAllByAtivoTrueOrderByNomeAsc() {
        String sql = """
            SELECT id, nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo, versao
            FROM item_catalogo
            WHERE ativo = true
            ORDER BY nome
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public List<ItemCatalogo> findByTipoOrderByNomeAsc(TipoItem tipo) {
        String sql = """
            SELECT id, nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo, versao
            FROM item_catalogo
            WHERE tipo = ?
            ORDER BY nome
            """;
        return jdbcTemplate.query(sql, MAPPER, tipo.name());
    }

    public Optional<ItemCatalogo> findById(Long id) {
        String sql = "SELECT id, nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo, versao FROM item_catalogo WHERE id = ?";
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public Optional<ItemCatalogo> findByNome(String nome) {
        String sql = "SELECT id, nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo, versao FROM item_catalogo WHERE nome = ?";
        return jdbcTemplate.query(sql, MAPPER, nome).stream().findFirst();
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        String sql = "SELECT 1 FROM item_catalogo WHERE LOWER(nome) = LOWER(?)";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, nome);
        return !result.isEmpty();
    }

    public boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id) {
        String sql = "SELECT 1 FROM item_catalogo WHERE LOWER(nome) = LOWER(?) AND id <> ?";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, nome, id);
        return !result.isEmpty();
    }

    public long countComEstoqueBaixo() {
        String sql = """
            SELECT COUNT(*) FROM item_catalogo
            WHERE controla_estoque = true AND estoque_atual <= estoque_minimo
            """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public ItemCatalogo save(ItemCatalogo item) {
        if (item.getId() == null) {
            String sql = """
                INSERT INTO item_catalogo (nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    item.getNome(), item.getDescricao(), item.getTipo().name(),
                    item.getPrecoUnitario(), item.getControlaEstoque(),
                    item.getEstoqueAtual(), item.getEstoqueMinimo(), item.getAtivo());
            item.setId(id);
        } else {
            String sql = """
                UPDATE item_catalogo
                SET nome = ?, descricao = ?, tipo = ?, preco_unitario = ?, controla_estoque = ?, estoque_atual = ?, estoque_minimo = ?, ativo = ?
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    item.getNome(), item.getDescricao(), item.getTipo().name(),
                    item.getPrecoUnitario(), item.getControlaEstoque(),
                    item.getEstoqueAtual(), item.getEstoqueMinimo(), item.getAtivo(),
                    item.getId(), item.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Item modificado por outra transação");
            }
        }
        return item;
    }

    public void delete(ItemCatalogo item) {
        String sql = "DELETE FROM item_catalogo WHERE id = ?";
        jdbcTemplate.update(sql, item.getId());
    }
}