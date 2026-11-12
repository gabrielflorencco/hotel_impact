package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.Quarto;
import com.gabrielflorencco.hotel_impact.enums.EstadoQuarto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository de Quarto com SQL explícito via JdbcTemplate.
 */
@Repository
public class QuartoRepository {

    private final JdbcTemplate jdbcTemplate;

    public QuartoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Quarto> MAPPER = new RowMapper<>() {
        @Override
        public Quarto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Quarto q = new Quarto();
            q.setId(rs.getLong("q.id"));
            q.setNumero(rs.getString("q.numero"));
            q.setAndar(rs.getInt("q.andar"));
            q.setCapacidadeOperacional(rs.getObject("q.capacidade_operacional") != null ? rs.getInt("q.capacidade_operacional") : null);
            q.setEstado(EstadoQuarto.valueOf(rs.getString("q.estado")));
            q.setObservacoes(rs.getString("q.observacoes"));
            q.setAtivo(rs.getBoolean("q.ativo"));
            q.setVersao(rs.getLong("q.versao"));

            com.gabrielflorencco.hotel_impact.model.CategoriaQuarto cat = new com.gabrielflorencco.hotel_impact.model.CategoriaQuarto();
            cat.setId(rs.getLong("c.id"));
            cat.setNome(rs.getString("c.nome"));
            cat.setDescricao(rs.getString("c.descricao"));
            cat.setCapacidadeMaxima(rs.getInt("c.capacidade_maxima"));
            cat.setQuantidadeCamas(rs.getInt("c.quantidade_camas"));
            cat.setPrecoBase(rs.getBigDecimal("c.preco_base"));
            cat.setAtivo(rs.getBoolean("c.ativo"));
            q.setCategoria(cat);

            return q;
        }
    };

    public List<Quarto> buscar(String numero, EstadoQuarto estado, Long categoriaId) {
        StringBuilder sql = new StringBuilder("""
            SELECT q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   c.id, c.nome, c.descricao, c.capacidade_maxima, c.quantidade_camas, c.preco_base, c.ativo
            FROM quarto q
            JOIN categoria_quarto c ON q.categoria_id = c.id
            WHERE 1=1
            """);
        List<Object> params = new java.util.ArrayList<>();

        if (numero != null && !numero.isBlank()) {
            sql.append(" AND LOWER(q.numero) LIKE LOWER(?)");
            params.add("%" + numero + "%");
        }
        if (estado != null) {
            sql.append(" AND q.estado = ?");
            params.add(estado.name());
        }
        if (categoriaId != null) {
            sql.append(" AND q.categoria_id = ?");
            params.add(categoriaId);
        }

        sql.append(" ORDER BY q.numero");

        return jdbcTemplate.query(sql.toString(), MAPPER, params.toArray());
    }

    public List<Quarto> findAllComCategoria() {
        String sql = """
            SELECT q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   c.id, c.nome, c.descricao, c.capacidade_maxima, c.quantidade_camas, c.preco_base, c.ativo
            FROM quarto q
            JOIN categoria_quarto c ON q.categoria_id = c.id
            ORDER BY q.numero
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public Optional<Quarto> findByIdComCategoria(Long id) {
        String sql = """
            SELECT q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   c.id, c.nome, c.descricao, c.capacidade_maxima, c.quantidade_camas, c.preco_base, c.ativo
            FROM quarto q
            JOIN categoria_quarto c ON q.categoria_id = c.id
            WHERE q.id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public List<Quarto> findAllByAtivoTrueOrderByNumeroAsc() {
        String sql = """
            SELECT q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   c.id, c.nome, c.descricao, c.capacidade_maxima, c.quantidade_camas, c.preco_base, c.ativo
            FROM quarto q
            JOIN categoria_quarto c ON q.categoria_id = c.id
            WHERE q.ativo = true
            ORDER BY q.numero
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public List<Quarto> findReservaveis() {
        String sql = """
            SELECT q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   c.id, c.nome, c.descricao, c.capacidade_maxima, c.quantidade_camas, c.preco_base, c.ativo
            FROM quarto q
            JOIN categoria_quarto c ON q.categoria_id = c.id
            WHERE q.ativo = true
              AND q.estado NOT IN ('INTERDITADO', 'MANUTENCAO')
            ORDER BY q.numero
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public Optional<Quarto> findByNumero(String numero) {
        String sql = """
            SELECT q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   c.id, c.nome, c.descricao, c.capacidade_maxima, c.quantidade_camas, c.preco_base, c.ativo
            FROM quarto q
            JOIN categoria_quarto c ON q.categoria_id = c.id
            WHERE q.numero = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, numero).stream().findFirst();
    }

    public boolean existsByNumero(String numero) {
        String sql = "SELECT 1 FROM quarto WHERE numero = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, numero));
    }

    public boolean existsByNumeroAndIdNot(String numero, Long id) {
        String sql = "SELECT 1 FROM quarto WHERE numero = ? AND id <> ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, numero, id));
    }

    public long countByCategoriaId(Long categoriaId) {
        String sql = "SELECT COUNT(*) FROM quarto WHERE categoria_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, categoriaId);
    }

    public long countByEstadoAndAtivoTrue(EstadoQuarto estado) {
        String sql = "SELECT COUNT(*) FROM quarto WHERE estado = ? AND ativo = true";
        return jdbcTemplate.queryForObject(sql, Long.class, estado.name());
    }

    public long countByAtivoTrue() {
        String sql = "SELECT COUNT(*) FROM quarto WHERE ativo = true";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Quarto save(Quarto quarto) {
        if (quarto.getId() == null) {
            String sql = """
                INSERT INTO quarto (numero, categoria_id, andar, capacidade_operacional, estado, observacoes, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    quarto.getNumero(), quarto.getCategoria().getId(),
                    quarto.getAndar(), quarto.getCapacidadeOperacional(),
                    quarto.getEstado().name(), quarto.getObservacoes(), quarto.getAtivo());
            quarto.setId(id);
        } else {
            String sql = """
                UPDATE quarto
                SET numero = ?, categoria_id = ?, andar = ?, capacidade_operacional = ?, estado = ?, observacoes = ?, ativo = ?
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    quarto.getNumero(), quarto.getCategoria().getId(),
                    quarto.getAndar(), quarto.getCapacidadeOperacional(),
                    quarto.getEstado().name(), quarto.getObservacoes(),
                    quarto.getAtivo(), quarto.getId(), quarto.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Quarto modificado por outra transação");
            }
        }
        return quarto;
    }

    public void delete(Quarto quarto) {
        String sql = "DELETE FROM quarto WHERE id = ?";
        jdbcTemplate.update(sql, quarto.getId());
    }
}