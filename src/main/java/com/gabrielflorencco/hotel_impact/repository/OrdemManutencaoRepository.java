package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.OrdemManutencao;
import com.gabrielflorencco.hotel_impact.enums.EstadoManutencao;
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
 * Repository de OrdemManutencao com SQL explícito via JdbcTemplate.
 */
@Repository
public class OrdemManutencaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrdemManutencaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<OrdemManutencao> MAPPER = new RowMapper<>() {
        @Override
        public OrdemManutencao mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrdemManutencao o = new OrdemManutencao();
            o.setId(rs.getLong("o_id"));
            o.setTitulo(rs.getString("o_titulo"));
            o.setDescricao(rs.getString("o_descricao"));
            o.setPrioridade(com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao.valueOf(rs.getString("o_prioridade")));
            o.setEstado(EstadoManutencao.valueOf(rs.getString("o_estado")));
            o.setAbertaEm(rs.getTimestamp("o_aberta_em") != null ? rs.getTimestamp("o_aberta_em").toLocalDateTime() : null);
            o.setConcluidaEm(rs.getTimestamp("o_concluida_em") != null ? rs.getTimestamp("o_concluida_em").toLocalDateTime() : null);
            o.setInterditouQuarto(rs.getBoolean("o_interditou_quarto"));
            o.setObservacoes(rs.getString("o_observacoes"));
            o.setVersao(rs.getLong("o_versao"));

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

            o.setQuarto(q);

            return o;
        }
    };

    public Optional<OrdemManutencao> findByIdComQuarto(Long id) {
        String sql = """
            SELECT o.id AS o_id, o.quarto_id, o.titulo AS o_titulo, o.descricao AS o_descricao, o.prioridade AS o_prioridade, o.estado AS o_estado, o.aberta_em AS o_aberta_em, o.concluida_em AS o_concluida_em, o.interditou_quarto AS o_interditou_quarto, o.observacoes AS o_observacoes, o.versao AS o_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE o.id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM ordem_manutencao WHERE quarto_id = ?";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, quartoId);
        return !result.isEmpty();
    }

    public boolean existsByQuartoIdAndEstadoIn(Long quartoId, List<EstadoManutencao> estados) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT 1 FROM ordem_manutencao WHERE quarto_id = ? AND estado IN (" + placeholders + ")";
        List<Object> params = new java.util.ArrayList<>();
        params.add(quartoId);
        params.addAll(estados.stream().map(Enum::name).toList());
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, params.toArray());
        return !result.isEmpty();
    }

    public List<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> findByEstadoOrderByAbertaEmDesc(EstadoManutencao estado) {
        String sql = """
            SELECT o.id AS o_id, o.quarto_id, o.titulo AS o_titulo, o.descricao AS o_descricao, o.prioridade AS o_prioridade, o.estado AS o_estado, o.aberta_em AS o_aberta_em, o.concluida_em AS o_concluida_em, o.interditou_quarto AS o_interditou_quarto, o.observacoes AS o_observacoes, o.versao AS o_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE o.estado = ?
            ORDER BY o.aberta_em DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, estado.name());
    }

    public List<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> findByQuartoIdOrderByAbertaEmDesc(Long quartoId) {
        String sql = """
            SELECT o.id AS o_id, o.quarto_id, o.titulo AS o_titulo, o.descricao AS o_descricao, o.prioridade AS o_prioridade, o.estado AS o_estado, o.aberta_em AS o_aberta_em, o.concluida_em AS o_concluida_em, o.interditou_quarto AS o_interditou_quarto, o.observacoes AS o_observacoes, o.versao AS o_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE o.quarto_id = ?
            ORDER BY o.aberta_em DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, quartoId);
    }

    public List<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> findByEstadoIn(List<EstadoManutencao> estados) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT o.id AS o_id, o.quarto_id, o.titulo AS o_titulo, o.descricao AS o_descricao, o.prioridade AS o_prioridade, o.estado AS o_estado, o.aberta_em AS o_aberta_em, o.concluida_em AS o_concluida_em, o.interditou_quarto AS o_interditou_quarto, o.observacoes AS o_observacoes, o.versao AS o_versao, " +
                "q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao, " +
                "cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo " +
                "FROM ordem_manutencao o " +
                "JOIN quarto q ON o.quarto_id = q.id " +
                "JOIN categoria_quarto cat ON q.categoria_id = cat.id " +
                "WHERE o.estado IN (" + placeholders + ") " +
                "ORDER BY o.aberta_em DESC";
        List<Object> params = new java.util.ArrayList<>();
        for (EstadoManutencao e : estados) {
            params.add(e.name());
        }
        return jdbcTemplate.query(sql, MAPPER, params.toArray());
    }

    public Page<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> buscarTodos(Pageable pageable) {
        String sql = baseQuery() + " ORDER BY o.aberta_em DESC, o.id DESC";
        long total = countAll();
        List<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER
        );
        return new PageImpl<>(content, pageable, total);
    }

    public Page<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> buscarPorEstado(EstadoManutencao estado, Pageable pageable) {
        String sql = baseQuery() + " WHERE o.estado = ? ORDER BY o.aberta_em DESC, o.id DESC";
        long total = countByEstado(estado);
        List<com.gabrielflorencco.hotel_impact.model.OrdemManutencao> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER,
                estado.name()
        );
        return new PageImpl<>(content, pageable, total);
    }

    public com.gabrielflorencco.hotel_impact.model.OrdemManutencao save(com.gabrielflorencco.hotel_impact.model.OrdemManutencao ordem) {
        if (ordem.getId() == null) {
            String sql = """
                INSERT INTO ordem_manutencao (quarto_id, titulo, descricao, prioridade, estado, aberta_em, concluida_em, interditou_quarto, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    ordem.getQuarto().getId(), ordem.getTitulo(), ordem.getDescricao(),
                    ordem.getPrioridade().name(), ordem.getEstado().name(),
                    ordem.getAbertaEm(), ordem.getConcluidaEm(),
                    ordem.getInterditouQuarto(), ordem.getObservacoes());
            ordem.setId(id);
        } else {
            String sql = """
                UPDATE ordem_manutencao
                SET titulo = ?, descricao = ?, prioridade = ?, estado = ?, aberta_em = ?, concluida_em = ?, interditou_quarto = ?, observacoes = ?, versao = versao + 1
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    ordem.getTitulo(), ordem.getDescricao(), ordem.getPrioridade().name(),
                    ordem.getEstado().name(), ordem.getAbertaEm(), ordem.getConcluidaEm(),
                    ordem.getInterditouQuarto(), ordem.getObservacoes(),
                    ordem.getId(), ordem.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Ordem modificada por outra transação");
            }
        }
        return ordem;
    }

    private String baseQuery() {
        return """
            SELECT o.id AS o_id, o.quarto_id, o.titulo AS o_titulo, o.descricao AS o_descricao, o.prioridade AS o_prioridade, o.estado AS o_estado, o.aberta_em AS o_aberta_em, o.concluida_em AS o_concluida_em, o.interditou_quarto AS o_interditou_quarto, o.observacoes AS o_observacoes, o.versao AS o_versao,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional, q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima, cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            """;
    }

    private long countAll() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordem_manutencao", Long.class);
    }

    private long countByEstado(EstadoManutencao estado) {
        String sql = "SELECT COUNT(*) FROM ordem_manutencao WHERE estado = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, estado.name());
    }

    private String addPagination(String baseSql, Pageable pageable) {
        return baseSql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
    }
}