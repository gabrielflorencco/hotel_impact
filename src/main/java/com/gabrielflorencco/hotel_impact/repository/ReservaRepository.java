package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.Reserva;
import com.gabrielflorencco.hotel_impact.enums.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository de Reserva com SQL explícito via JdbcTemplate.
 */
@Repository
public class ReservaRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Reserva> MAPPER = new RowMapper<>() {
        @Override
        public Reserva mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reserva r = new Reserva();
            r.setId(rs.getLong("r.id"));
            r.setCodigo(rs.getString("r.codigo"));
            r.setDataCheckIn(rs.getDate("r.data_check_in").toLocalDate());
            r.setDataCheckOut(rs.getDate("r.data_check_out").toLocalDate());
            r.setQuantidadeHospedes(rs.getInt("r.quantidade_hospedes"));
            r.setValorDiaria(rs.getBigDecimal("r.valor_diaria"));
            r.setDesconto(rs.getBigDecimal("r.desconto"));
            r.setValorTotal(rs.getBigDecimal("r.valor_total"));
            r.setEstado(EstadoReserva.valueOf(rs.getString("r.estado")));
            r.setMotivoExcecao(rs.getString("r.motivo_excecao"));
            r.setObservacoes(rs.getString("r.observacoes"));
            r.setDataCriacao(rs.getTimestamp("r.data_criacao") != null ? rs.getTimestamp("r.data_criacao").toLocalDateTime() : null);
            r.setVersao(rs.getLong("r.versao"));

            com.gabrielflorencco.hotel_impact.model.Hospede h = new com.gabrielflorencco.hotel_impact.model.Hospede();
            h.setId(rs.getLong("h.id"));
            h.setNome(rs.getString("h.nome"));
            h.setCpf(rs.getString("h.cpf"));
            h.setTelefone(rs.getString("h.telefone"));
            h.setEmail(rs.getString("h.email"));
            h.setDataNascimento(rs.getDate("h.data_nascimento") != null ? rs.getDate("h.data_nascimento").toLocalDate() : null);
            h.setObservacoes(rs.getString("h.observacoes"));
            h.setDataCadastro(rs.getTimestamp("h.data_cadastro") != null ? rs.getTimestamp("h.data_cadastro").toLocalDateTime() : null);
            r.setHospede(h);

            com.gabrielflorencco.hotel_impact.model.Quarto q = new com.gabrielflorencco.hotel_impact.model.Quarto();
            q.setId(rs.getLong("q.id"));
            q.setNumero(rs.getString("q.numero"));
            q.setAndar(rs.getInt("q.andar"));
            q.setCapacidadeOperacional(rs.getObject("q.capacidade_operacional") != null ? rs.getInt("q.capacidade_operacional") : null);
            q.setEstado(com.gabrielflorencco.hotel_impact.enums.EstadoQuarto.valueOf(rs.getString("q.estado")));
            q.setObservacoes(rs.getString("q.observacoes"));
            q.setAtivo(rs.getBoolean("q.ativo"));
            q.setVersao(rs.getLong("q.versao"));

            com.gabrielflorencco.hotel_impact.model.CategoriaQuarto c = new com.gabrielflorencco.hotel_impact.model.CategoriaQuarto();
            c.setId(rs.getLong("cat.id"));
            c.setNome(rs.getString("cat.nome"));
            c.setDescricao(rs.getString("cat.descricao"));
            c.setCapacidadeMaxima(rs.getInt("cat.capacidade_maxima"));
            c.setQuantidadeCamas(rs.getInt("cat.quantidade_camas"));
            c.setPrecoBase(rs.getBigDecimal("cat.preco_base"));
            c.setAtivo(rs.getBoolean("cat.ativo"));
            q.setCategoria(c);

            r.setQuarto(q);

            return r;
        }
    };

    public Page<Reserva> buscarTodos(Pageable pageable) {
        String sql = baseQuery() + " ORDER BY r.data_check_in DESC, r.id DESC";
        long total = countAll();
        List<Reserva> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER
        );
        return new PageImpl<>(content, pageable, total);
    }

    public Page<Reserva> buscarPorEstado(EstadoReserva estado, Pageable pageable) {
        String sql = baseQuery() + " WHERE r.estado = ? ORDER BY r.data_check_in DESC, r.id DESC";
        long total = countByEstado(estado);
        List<Reserva> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER,
                estado.name()
        );
        return new PageImpl<>(content, pageable, total);
    }

    public Optional<Reserva> findByIdComDetalhes(Long id) {
        String sql = baseQuery() + " WHERE r.id = ?";
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public Optional<Reserva> findByCodigoComDetalhes(String codigo) {
        String sql = baseQuery() + " WHERE r.codigo = ?";
        return jdbcTemplate.query(sql, MAPPER, codigo).stream().findFirst();
    }

    public Optional<Reserva> findByCodigo(String codigo) {
        String sql = "SELECT id, codigo, hospede_id, quarto_id, data_check_in, data_check_out, " +
                "quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, " +
                "motivo_excecao, observacoes, versao FROM reserva WHERE codigo = ?";
        return jdbcTemplate.query(sql, MAPPER, codigo).stream().findFirst();
    }

    public boolean existsByCodigo(String codigo) {
        String sql = "SELECT 1 FROM reserva WHERE codigo = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, codigo));
    }

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM reserva WHERE quarto_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, quartoId));
    }

    public boolean existsByHospedeId(Long hospedeId) {
        String sql = "SELECT 1 FROM reserva WHERE hospede_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, hospedeId));
    }

    public List<Reserva> findConflitantes(Long quartoId, LocalDate inicio, LocalDate fim, List<EstadoReserva> estadosAtivos) {
        String placeholders = estadosAtivos.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT r.id, r.codigo, r.hospede_id, r.quarto_id, r.data_check_in, r.data_check_out, " +
                "r.quantidade_hospedes, r.valor_diaria, r.desconto, r.valor_total, r.data_criacao, " +
                "r.estado, r.motivo_excecao, r.observacoes, r.versao " +
                "FROM reserva r " +
                "WHERE r.quarto_id = ? AND r.estado IN (" + placeholders + ") " +
                "AND r.data_check_in < ? AND r.data_check_out > ?";
        List<Object> params = new java.util.ArrayList<>();
        params.add(quartoId);
        params.addAll(estadosAtivos.stream().map(Enum::name).toList());
        params.add(fim);
        params.add(inicio);
        return jdbcTemplate.query(sql, MAPPER, params.toArray());
    }

    public List<Reserva> findConflitantesExceto(Long quartoId, Long reservaId, LocalDate inicio, LocalDate fim, List<EstadoReserva> estadosAtivos) {
        String placeholders = estadosAtivos.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT r.id, r.codigo, r.hospede_id, r.quarto_id, r.data_check_in, r.data_check_out, " +
                "r.quantidade_hospedes, r.valor_diaria, r.desconto, r.valor_total, r.data_criacao, " +
                "r.estado, r.motivo_excecao, r.observacoes, r.versao " +
                "FROM reserva r " +
                "WHERE r.quarto_id = ? AND r.id <> ? AND r.estado IN (" + placeholders + ") " +
                "AND r.data_check_in < ? AND r.data_check_out > ?";
        List<Object> params = new java.util.ArrayList<>();
        params.add(quartoId);
        params.add(reservaId);
        params.addAll(estadosAtivos.stream().map(Enum::name).toList());
        params.add(fim);
        params.add(inicio);
        return jdbcTemplate.query(sql, MAPPER, params.toArray());
    }

    public long countByEstadoInAndDataCheckInEquals(List<EstadoReserva> estados, LocalDate dataCheckIn) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT COUNT(*) FROM reserva WHERE estado IN (" + placeholders + ") AND data_check_in = ?";
        List<Object> params = estados.stream().map(Enum::name).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        params.add(dataCheckIn);
        return jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
    }

    public long countByEstadoInAndDataCheckOutEquals(List<EstadoReserva> estados, LocalDate dataCheckOut) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT COUNT(*) FROM reserva WHERE estado IN (" + placeholders + ") AND data_check_out = ?";
        List<Object> params = estados.stream().map(Enum::name).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        params.add(dataCheckOut);
        return jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
    }

    public long countByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE data_criacao >= ? AND data_criacao < ?";
        return jdbcTemplate.queryForObject(sql, Long.class, inicio, fim);
    }

    public long countByEstadoAndDataCriacaoBetween(EstadoReserva estado, LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE estado = ? AND data_criacao >= ? AND data_criacao < ?";
        return jdbcTemplate.queryForObject(sql, Long.class, estado.name(), inicio, fim);
    }

    public List<Reserva> buscarAtivasPorDataCheckInComDetalhes(List<EstadoReserva> estados, LocalDate dia) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = baseQuery() + " WHERE r.estado IN (" + placeholders + ") AND r.data_check_in = ? ORDER BY r.id DESC";
        List<Object> params = estados.stream().map(Enum::name).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        params.add(dia);
        return jdbcTemplate.query(sql, MAPPER, params.toArray());
    }

    public List<Reserva> buscarAtivasPorDataCheckOutComDetalhes(List<EstadoReserva> estados, LocalDate dia) {
        String placeholders = estados.stream().map(e -> "?").collect(java.util.stream.Collectors.joining(","));
        String sql = baseQuery() + " WHERE r.estado IN (" + placeholders + ") AND r.data_check_out = ? ORDER BY r.id DESC";
        List<Object> params = estados.stream().map(Enum::name).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        params.add(dia);
        return jdbcTemplate.query(sql, MAPPER, params.toArray());
    }

    public long countByCodigoStartingWith(String prefixo) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE codigo LIKE ?";
        return jdbcTemplate.queryForObject(sql, Long.class, prefixo + "%");
    }

    public Reserva save(Reserva reserva) {
        if (reserva.getId() == null) {
            String sql = """
                INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out,
                                     quantidade_hospedes, valor_diaria, desconto, valor_total,
                                     data_criacao, estado, motivo_excecao, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    reserva.getCodigo(), reserva.getHospede().getId(), reserva.getQuarto().getId(),
                    reserva.getDataCheckIn(), reserva.getDataCheckOut(),
                    reserva.getQuantidadeHospedes(), reserva.getValorDiaria(),
                    reserva.getDesconto(), reserva.getValorTotal(),
                    LocalDateTime.now(), reserva.getEstado().name(),
                    reserva.getMotivoExcecao(), reserva.getObservacoes());
            reserva.setId(id);
            reserva.setDataCriacao(LocalDateTime.now());
        } else {
            String sql = """
                UPDATE reserva
                SET codigo = ?, hospede_id = ?, quarto_id = ?, data_check_in = ?, data_check_out = ?,
                    quantidade_hospedes = ?, valor_diaria = ?, desconto = ?, valor_total = ?,
                    estado = ?, motivo_excecao = ?, observacoes = ?
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    reserva.getCodigo(), reserva.getHospede().getId(), reserva.getQuarto().getId(),
                    reserva.getDataCheckIn(), reserva.getDataCheckOut(),
                    reserva.getQuantidadeHospedes(), reserva.getValorDiaria(),
                    reserva.getDesconto(), reserva.getValorTotal(),
                    reserva.getEstado().name(), reserva.getMotivoExcecao(),
                    reserva.getObservacoes(), reserva.getId(), reserva.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Reserva modificada por outra transação");
            }
        }
        return reserva;
    }

    private String baseQuery() {
        return """
            SELECT r.id, r.codigo, r.hospede_id, r.quarto_id, r.data_check_in, r.data_check_out,
                   r.quantidade_hospedes, r.valor_diaria, r.desconto, r.valor_total, r.data_criacao,
                   r.estado, r.motivo_excecao, r.observacoes, r.versao,
                   h.id, h.cpf, h.nome, h.telefone, h.email, h.data_nascimento, h.observacoes, h.data_cadastro,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM reserva r
            JOIN hospede h ON r.hospede_id = h.id
            JOIN quarto q ON r.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            """;
    }

    private long countAll() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reserva", Long.class);
    }

    private long countByEstado(EstadoReserva estado) {
        String sql = "SELECT COUNT(*) FROM reserva WHERE estado = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, estado.name());
    }

    private String addPagination(String baseSql, Pageable pageable) {
        return baseSql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
    }
}