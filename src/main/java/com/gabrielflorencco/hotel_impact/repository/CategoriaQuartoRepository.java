package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.CategoriaQuarto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository de CategoriaQuarto com SQL explícito via JdbcTemplate.
 */
@Repository
public class CategoriaQuartoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaQuartoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<CategoriaQuarto> MAPPER = new RowMapper<>() {
        @Override
        public CategoriaQuarto mapRow(ResultSet rs, int rowNum) throws SQLException {
            CategoriaQuarto c = new CategoriaQuarto();
            c.setId(rs.getLong("id"));
            c.setNome(rs.getString("nome"));
            c.setDescricao(rs.getString("descricao"));
            c.setCapacidadeMaxima(rs.getInt("capacidade_maxima"));
            c.setQuantidadeCamas(rs.getInt("quantidade_camas"));
            c.setPrecoBase(rs.getBigDecimal("preco_base"));
            c.setAtivo(rs.getBoolean("ativo"));
            return c;
        }
    };

    public List<CategoriaQuarto> findAllByOrderByNomeAsc() {
        String sql = """
            SELECT id, nome, descricao, capacidade_maxima, quantidade_camas, preco_base, ativo
            FROM categoria_quarto
            ORDER BY nome
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public Optional<CategoriaQuarto> findById(Long id) {
        String sql = """
            SELECT id, nome, descricao, capacidade_maxima, quantidade_camas, preco_base, ativo
            FROM categoria_quarto
            WHERE id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public Optional<CategoriaQuarto> findByNomeIgnoreCase(String nome) {
        String sql = """
            SELECT id, nome, descricao, capacidade_maxima, quantidade_camas, preco_base, ativo
            FROM categoria_quarto
            WHERE LOWER(nome) = LOWER(?)
            """;
        return jdbcTemplate.query(sql, MAPPER, nome).stream().findFirst();
    }

    public boolean existsByNomeIgnoreCase(String nome) {
        String sql = "SELECT 1 FROM categoria_quarto WHERE LOWER(nome) = LOWER(?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, nome));
    }

    public boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id) {
        String sql = "SELECT 1 FROM categoria_quarto WHERE LOWER(nome) = LOWER(?) AND id <> ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, nome, id));
    }

    public CategoriaQuarto save(CategoriaQuarto categoria) {
        if (categoria.getId() == null) {
            String sql = """
                INSERT INTO categoria_quarto (nome, descricao, capacidade_maxima, quantidade_camas, preco_base, ativo)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    categoria.getNome(), categoria.getDescricao(),
                    categoria.getCapacidadeMaxima(), categoria.getQuantidadeCamas(),
                    categoria.getPrecoBase(), categoria.getAtivo());
            categoria.setId(id);
        } else {
            String sql = """
                UPDATE categoria_quarto
                SET nome = ?, descricao = ?, capacidade_maxima = ?, quantidade_camas = ?, preco_base = ?, ativo = ?
                WHERE id = ?
                """;
            jdbcTemplate.update(sql,
                    categoria.getNome(), categoria.getDescricao(),
                    categoria.getCapacidadeMaxima(), categoria.getQuantidadeCamas(),
                    categoria.getPrecoBase(), categoria.getAtivo(), categoria.getId());
        }
        return categoria;
    }

    public void delete(CategoriaQuarto categoria) {
        String sql = "DELETE FROM categoria_quarto WHERE id = ?";
        jdbcTemplate.update(sql, categoria.getId());
    }
}