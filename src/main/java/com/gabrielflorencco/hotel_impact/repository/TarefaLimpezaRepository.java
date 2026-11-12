package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.TarefaLimpeza;
import com.gabrielflorencco.hotel_impact.enums.EstadoTarefaLimpeza;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository de TarefaLimpeza com SQL explícito via JdbcTemplate.
 */
@Repository
public class TarefaLimpezaRepository {

    private final JdbcTemplate jdbcTemplate;

    public TarefaLimpezaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<TarefaLimpeza> MAPPER = new RowMapper<>() {
        @Override
        public TarefaLimpeza mapRow(ResultSet rs, int rowNum) throws SQLException {
            TarefaLimpeza t = new TarefaLimpeza();
            t.setId(rs.getLong("id"));
            t.setEstado(EstadoTarefaLimpeza.valueOf(rs.getString("estado")));
            t.setCriadaEm(rs.getTimestamp("criada_em") != null ? rs.getTimestamp("criada_em").toLocalDateTime() : null);
            t.setConcluidaEm(rs.getTimestamp("concluida_em") != null ? rs.getTimestamp("concluida_em").toLocalDateTime() : null);
            t.setObservacoes(rs.getString("observacoes"));
            t.setVersao(rs.getLong("versao"));

            com.gabrielflorencco.hotel_impact.model.Quarto q = new com.gabrielflorencco.hotel_impact.model.Quarto();
            q.setId(rs.getLong("q.id"));
            q.setNumero(rs.getString("q.numero"));
            q.setAndar(rs.getInt("q.andar"));
            q.setCapacidadeOperacional(rs.getObject("q.capacidade_operacional") != null ? rs.getInt("q.capacidade_operacional") : null);
            q.setEstado(com.gabrielflorencco.hotel_impact.enums.EstadoQuarto.valueOf(rs.getString("q.estado")));
            q.setObservacoes(rs.getString("q.observacoes"));
            q.setAtivo(rs.getBoolean("q.ativo"));
            q.setVersao(rs.getLong("q.versao"));

            com.gabrielflorencco.hotel_impact.model.CategoriaQuarto cat = new com.gabrielflorencco.hotel_impact.model.CategoriaQuarto();
            cat.setId(rs.getLong("cat.id"));
            cat.setNome(rs.getString("cat.nome"));
            cat.setDescricao(rs.getString("cat.descricao"));
            cat.setCapacidadeMaxima(rs.getInt("cat.capacidade_maxima"));
            cat.setQuantidadeCamas(rs.getInt("cat.quantidade_camas"));
            cat.setPrecoBase(rs.getBigDecimal("cat.preco_base"));
            cat.setAtivo(rs.getBoolean("cat.ativo"));
            q.setCategoria(cat);

            t.setQuarto(q);
            return t;
        }
    };

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM tarefa_limpeza WHERE quarto_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, quartoId));
    }

    public List<TarefaLimpeza> findByEstadoOrderByCriadaEmAsc(EstadoTarefaLimpeza estado) {
        String sql = """
            SELECT t.id, t.quarto_id, t.estado, t.criada_em, t.concluida_em, t.observacoes, t.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM tarefa_limpeza t
            JOIN quarto q ON t.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE t.estado = ?
            ORDER BY t.criada_em ASC
            """;
        return jdbcTemplate.query(sql, MAPPER, estado.name());
    }

    public List<TarefaLimpeza> findByQuartoIdOrderByCriadaEmDesc(Long quartoId) {
        String sql = """
            SELECT t.id, t.quarto_id, t.estado, t.criada_em, t.concluida_em, t.observacoes, t.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM tarefa_limpeza t
            JOIN quarto q ON t.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE t.quarto_id = ?
            ORDER BY t.criada_em DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, quartoId);
    }

    public long countByEstado(EstadoTarefaLimpeza estado) {
        String sql = "SELECT COUNT(*) FROM tarefa_limpeza WHERE estado = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, estado.name());
    }

    public Optional<TarefaLimpeza> findFirstByQuartoIdAndEstadoNotOrderByCriadaEmDesc(Long quartoId, EstadoTarefaLimpeza estado) {
        String sql = """
            SELECT t.id, t.quarto_id, t.estado, t.criada_em, t.concluida_em, t.observacoes, t.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM tarefa_limpeza t
            JOIN quarto q ON t.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE t.quarto_id = ? AND t.estado <> ?
            ORDER BY t.criada_em DESC
            LIMIT 1
            """;
        return jdbcTemplate.query(sql, MAPPER, quartoId, estado.name()).stream().findFirst();
    }

    public List<TarefaLimpeza> findByEstadoNotOrderByCriadaEmDesc(EstadoTarefaLimpeza estado) {
        String sql = """
            SELECT t.id, t.quarto_id, t.estado, t.criada_em, t.concluida_em, t.observacoes, t.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM tarefa_limpeza t
            JOIN quarto q ON t.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE t.estado <> ?
            ORDER BY t.criada_em DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, estado.name());
    }

    public long countByConcluidaEmBetween(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) FROM tarefa_limpeza WHERE concluida_em >= ? AND concluida_em < ?";
        return jdbcTemplate.queryForObject(sql, Long.class, inicio, fim);
    }

    public TarefaLimpeza save(TarefaLimpeza tarefa) {
        if (tarefa.getId() == null) {
            String sql = """
                INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    tarefa.getQuarto().getId(), tarefa.getEstado().name(),
                    tarefa.getCriadaEm(), tarefa.getConcluidaEm(), tarefa.getObservacoes());
            tarefa.setId(id);
        } else {
            String sql = """
                UPDATE tarefa_limpeza
                SET estado = ?, concluida_em = ?, observacoes = ?
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    tarefa.getEstado().name(), tarefa.getConcluidaEm(),
                    tarefa.getObservacoes(), tarefa.getId(), tarefa.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Tarefa modificada por outra transação");
            }
        }
        return tarefa;
    }
}