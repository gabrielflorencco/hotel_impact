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
            o.setId(rs.getLong("o.id"));
            o.setTitulo(rs.getString("o.titulo"));
            o.setDescricao(rs.getString("o.descricao"));
            o.setPrioridade(com.gabrielflorencco.hotel_impact.enums.PrioridadeManutencao.valueOf(rs.getString("o.prioridade")));
            o.setEstado(EstadoManutencao.valueOf(rs.getString("o.estado")));
            o.setAbertaEm(rs.getTimestamp("o.aberta_em") != null ? rs.getTimestamp("o.aberta_em").toLocalDateTime() : null);
            o.setConcluidaEm(rs.getTimestamp("o.concluida_em") != null ? rs.getTimestamp("o.concluida_em").toLocalDateTime() : null);
            o.setInterditouQuarto(rs.getBoolean("o.interditou_quarto"));
            o.setObservacoes(rs.getString("o.observacoes"));
            o.setVersao(rs.getLong("o.versao"));

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

            o.setQuarto(q);

            return o;
        }
    };

    public Optional<OrdemManutencao> findByIdComQuarto(Long id) {
        String sql = """
            SELECT o.id, o.quarto_id, o.titulo, o.descricao, o.prioridade, o.estado, o.aberta_em, o.concluida_em, o.interditou_quarto, o.observacoes, o.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE o.id = ?
            """;
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM ordem_manutencao WHERE quarto_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, quartoId));
    }

    public boolean existsByQuartoIdAndEstadoIn(Long quartoId, List<EstadoManutencao> estados) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT 1 FROM ordem_manutencao WHERE quarto_id = ? AND estado IN (" + placeholders + ")";
        List<Object> params = new java.util.ArrayList<>();
        params.add(quartoId);
        params.addAll(estados.stream().map(Enum::name).toList());
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, params.toArray()));
    }

    public List<OrdemManutencao> findByEstadoOrderByAbertaEmDesc(EstadoManutencao estado) {
        String sql = """
            SELECT o.id, o.quarto_id, o.titulo, o.descricao, o.prioridade, o.estado, o.aberta_em, o.concluida_em, o.interditou_quarto, o.observacoes, o.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE o.estado = ?
            ORDER BY o.aberta_em DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, estado.name());
    }

    public List<OrdemManutencao> findByQuartoIdOrderByAbertaEmDesc(Long quartoId) {
        String sql = """
            SELECT o.id, o.quarto_id, o.titulo, o.descricao, o.prioridade, o.estado, o.aberta_em, o.concluida_em, o.interditou_quarto, o.observacoes, o.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM ordem_manutencao o
            JOIN quarto q ON o.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            WHERE o.quarto_id = ?
            ORDER BY o.aberta_em DESC
            """;
        return jdbcTemplate.query(sql, MAPPER, quartoId);
    }

    public List<OrdemManutencao> findByEstadoIn(List<EstadoManutencao> estados) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT o.id, o.quarto_id, o.titulo, o.descricao, o.prioridade, o.estado, o.aberta_em, o.concluida_em, o.interditou_quarto, o.observacoes, o.versao, " +
                "q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao, " +
                "cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo " +
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

    public Page<OrdemManutencao> buscarTodos(Pageable pageable) {
        String sql = baseQuery() + " ORDER BY o.aberta_em DESC, o.id DESC";
        long total = countAll();
        List<OrdemManutencao> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER
        );
        return new PageImpl<>(content, pageable, total);
    }

    public Page<OrdemManutencao> buscarPorEstado(EstadoManutencao estado, Pageable pageable) {
        String sql = baseQuery() + " WHERE o.estado = ? ORDER BY o.aberta_em DESC, o.id DESC";
        long total = countByEstado(estado);
        List<OrdemManutencao> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER,
                estado.name()
        );
        return new PageImpl<>(content, pageable, total);
    }

    public OrdemManutencao save(OrdemManutencao ordem) {
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
                SET titulo = ?, descricao = ?, prioridade = ?, estado = ?, aberta_em = ?, concluida_em = ?, interditou_quarto = ?, observacoes = ?
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
            SELECT o.id, o.quarto_id, o.titulo, o.descricao, o.prioridade, o.estado, o.aberta_em, o.concluida_em, o.interditou_quarto, o.observacoes, o.versao,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
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