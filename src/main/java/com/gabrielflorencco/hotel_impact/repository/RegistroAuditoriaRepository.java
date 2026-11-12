package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.RegistroAuditoria;
import com.gabrielflorencco.hotel_impact.enums.OperacaoAuditoria;
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

/**
 * Repository de RegistroAuditoria com SQL explícito via JdbcTemplate.
 */
@Repository
public class RegistroAuditoriaRepository {

    private final JdbcTemplate jdbcTemplate;

    public RegistroAuditoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<RegistroAuditoria> MAPPER = new RowMapper<>() {
        @Override
        public RegistroAuditoria mapRow(ResultSet rs, int rowNum) throws SQLException {
            RegistroAuditoria r = new RegistroAuditoria();
            r.setId(rs.getLong("id"));
            r.setOperacao(OperacaoAuditoria.valueOf(rs.getString("operacao")));
            r.setUsuario(rs.getString("usuario"));
            r.setEntidadeTipo(rs.getString("entidade_tipo"));
            r.setEntidadeId(rs.getLong("entidade_id"));
            r.setDataHora(rs.getTimestamp("data_hora") != null ? rs.getTimestamp("data_hora").toLocalDateTime() : null);
            r.setDetalhe(rs.getString("detalhe"));
            return r;
        }
    };

    public Page<RegistroAuditoria> findByOrderByDataHoraDesc(Pageable pageable) {
        String sql = """
            SELECT id, operacao, usuario, entidade_tipo, entidade_id, data_hora, detalhe
            FROM registro_auditoria
            ORDER BY data_hora DESC
            """;
        long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM registro_auditoria", Long.class);
        List<RegistroAuditoria> content = jdbcTemplate.query(
                sql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset(),
                MAPPER
        );
        return new PageImpl<>(content, pageable, total);
    }

    public RegistroAuditoria save(RegistroAuditoria registro) {
        String sql = """
            INSERT INTO registro_auditoria (operacao, usuario, entidade_tipo, entidade_id, data_hora, detalhe)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                registro.getOperacao().name(), registro.getUsuario(),
                registro.getEntidadeTipo(), registro.getEntidadeId(),
                registro.getDataHora(), registro.getDetalhe());
        registro.setId(id);
        return registro;
    }
}