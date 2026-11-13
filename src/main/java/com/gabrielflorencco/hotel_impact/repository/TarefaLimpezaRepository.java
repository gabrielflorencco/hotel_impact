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
            t.setId(rs.getLong("t_id"));
            t.setEstado(EstadoTarefaLimpeza.valueOf(rs.getString("t_estado")));
            t.setCriadaEm(rs.getTimestamp("t_criada_em") != null ? rs.getTimestamp("t_criada_em").toLocalDateTime() : null);
            t.setConcluidaEm(rs.getTimestamp("t_concluida_em") != null ? rs.getTimestamp("t_concluida_em").toLocalDateTime() : null);
            t.setObservacoes(rs.getString("t_observacoes"));
            t.setVersao(rs.getLong("t_versao"));

            com.gabrielflorencco.hotel_impact.model.Quarto q = new com.gabrielflorencco.hotel_impact.model.Quarto();
            q.setId(rs.getLong("q_id"));
            q.setNumero(rs.getString("q_numero"));
            q.setAndar(rs.getInt("q_andar"));
            q.setCapacidadeOperacional(rs.getObject("q_capacidade_operacional") != null ? rs.getInt("q_capacidade_operacional") : null);
            q.setEstado(com.gabrielflorencco.hotel_impact.enums.EstadoQuarto.valueOf(rs.getString("q_estado")));
            q.setObservacoes(rs.getString("q_observacoes"));
            q.setAtivo(rs.getBoolean("q_ativo"));
            q.setVersao(rs.getLong("q_versao"));

            com.gabrielflorencco.hotel_impact.model.CategoriaQuarto cat = new com.gabrielflorencco.hotel_impact.model.CategoriaQuarto();
            cat.setId(rs.getLong("cat_id"));
            cat.setNome(rs.getString("cat_nome"));
            cat.setDescricao(rs.getString("cat_descricao"));
            cat.setCapacidadeMaxima(rs.getInt("cat_capacidade_maxima"));
            cat.setQuantidadeCamas(rs.getInt("cat_quantidade_camas"));
            cat.setPrecoBase(rs.getBigDecimal("cat_preco_base"));
            cat.setAtivo(rs.getBoolean("cat_ativo"));
            q.setCategoria(cat);

            t.setQuarto(q);
            return t;
        }
    };

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM tarefa_limpeza WHERE quarto_id = ?";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, quartoId);
        return !result.isEmpty();
    }

    public List<TarefaLimpeza> findByEstadoOrderByCriadaEmAsc(EstadoTarefaLimpeza estado) {
        String sql = """
            SELECT t.id AS t_id, t.quarto_id AS t_quarto_id, t.estado AS t_estado, t.criada_em AS t_criada_em, t.concluida_em AS t_concluida_em, t.observacoes AS t_observacoes, t.versao AS t_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
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
            SELECT t.id AS t_id, t.quarto_id AS t_quarto_id, t.estado AS t_estado, t.criada_em AS t_criada_em, t.concluida_em AS t_concluida_em, t.observacoes AS t_observacoes, t.versao AS t_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
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
            SELECT t.id AS t_id, t.quarto_id AS t_quarto_id, t.estado AS t_estado, t.criada_em AS t_criada_em, t.concluida_em AS t_concluida_em, t.observacoes AS t_observacoes, t.versao AS t_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
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
            SELECT t.id AS t_id, t.quarto_id AS t_quarto_id, t.estado AS t_estado, t.criada_em AS t_criada_em, t.concluida_em AS t_concluida_em, t.observacoes AS t_observacoes, t.versao AS t_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
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
                SET estado = ?, concluida_em = ?, observacoes = ?, versao = versao + 1
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
