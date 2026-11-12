package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.Hospede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository de Hospede com SQL explícito via JdbcTemplate.
 */
@Repository
public class HospedeRepository {

    private final JdbcTemplate jdbcTemplate;

    public HospedeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Hospede> MAPPER = new RowMapper<>() {
        @Override
        public Hospede mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hospede h = new Hospede();
            h.setId(rs.getLong("id"));
            h.setCpf(rs.getString("cpf"));
            h.setNome(rs.getString("nome"));
            h.setTelefone(rs.getString("telefone"));
            h.setEmail(rs.getString("email"));
            h.setDataNascimento(rs.getDate("data_nascimento") != null ? rs.getDate("data_nascimento").toLocalDate() : null);
            h.setObservacoes(rs.getString("observacoes"));
            h.setDataCadastro(rs.getTimestamp("data_cadastro") != null ? rs.getTimestamp("data_cadastro").toLocalDateTime() : null);
            return h;
        }
    };

    public List<Hospede> findAllByOrderByNomeAsc() {
        String sql = """
            SELECT id, cpf, nome, telefone, email, data_nascimento, observacoes, data_cadastro
            FROM hospede
            ORDER BY nome
            """;
        return jdbcTemplate.query(sql, MAPPER);
    }

    public Optional<Hospede> findById(Long id) {
        String sql = """
            SELECT id, cpf, nome, telefone, email, data_nascimento, observacoes, data_cadastro
            FROM hospede
            WHERE id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public boolean existsByCpf(String cpf) {
        String sql = "SELECT 1 FROM hospede WHERE cpf = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, cpf));
    }

    public boolean existsByCpfAndIdNot(String cpf, Long id) {
        String sql = "SELECT 1 FROM hospede WHERE cpf = ? AND id <> ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, cpf, id));
    }

    public Page<Hospede> buscar(String termo, String termoCpf, Pageable pageable) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, cpf, nome, telefone, email, data_nascimento, observacoes, data_cadastro
            FROM hospede
            WHERE 1=1
            """);
        List<Object> params = new java.util.ArrayList<>();

        if (termo != null && !termo.isBlank()) {
            sql.append(" AND (LOWER(nome) LIKE LOWER(?) OR telefone LIKE ? OR LOWER(email) LIKE LOWER(?))");
            String likeTermo = "%" + termo + "%";
            params.add(likeTermo);
            params.add(likeTermo);
            params.add(likeTermo);
        }
        if (termoCpf != null && !termoCpf.isBlank()) {
            sql.append(" AND cpf LIKE ?");
            params.add("%" + termoCpf + "%");
        }

        sql.append(" ORDER BY nome ASC");

        long total = count(termo, termoCpf);
        List<Hospede> content = jdbcTemplate.query(
                addPagination(sql.toString(), pageable),
                MAPPER,
                params.toArray()
        );
        return new PageImpl<>(content, pageable, total);
    }

    private long count(String termo, String termoCpf) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM hospede WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();

        if (termo != null && !termo.isBlank()) {
            sql.append(" AND (LOWER(nome) LIKE LOWER(?) OR telefone LIKE ? OR LOWER(email) LIKE LOWER(?))");
            String likeTermo = "%" + termo + "%";
            params.add(likeTermo);
            params.add(likeTermo);
            params.add(likeTermo);
        }
        if (termoCpf != null && !termoCpf.isBlank()) {
            sql.append(" AND cpf LIKE ?");
            params.add("%" + termoCpf + "%");
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    private String addPagination(String baseSql, Pageable pageable) {
        return baseSql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
    }

    public Hospede save(Hospede hospede) {
        if (hospede.getId() == null) {
            String sql = """
                INSERT INTO hospede (cpf, nome, telefone, email, data_nascimento, observacoes, data_cadastro)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    hospede.getCpf(), hospede.getNome(), hospede.getTelefone(),
                    hospede.getEmail(), hospede.getDataNascimento(),
                    hospede.getObservacoes(), LocalDateTime.now());
            hospede.setId(id);
            hospede.setDataCadastro(LocalDateTime.now());
        } else {
            String sql = """
                UPDATE hospede
                SET cpf = ?, nome = ?, telefone = ?, email = ?, data_nascimento = ?, observacoes = ?
                WHERE id = ?
                """;
            jdbcTemplate.update(sql,
                    hospede.getCpf(), hospede.getNome(), hospede.getTelefone(),
                    hospede.getEmail(), hospede.getDataNascimento(),
                    hospede.getObservacoes(), hospede.getId());
        }
        return hospede;
    }

    public void delete(Hospede hospede) {
        String sql = "DELETE FROM hospede WHERE id = ?";
        jdbcTemplate.update(sql, hospede.getId());
    }
}