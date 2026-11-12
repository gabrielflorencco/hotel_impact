package com.gabrielflorencco.hotel_impact.repository;

import com.gabrielflorencco.hotel_impact.model.Hospedagem;
import com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem;
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
 * Repository de Hospedagem com SQL explícito via JdbcTemplate.
 */
@Repository
public class HospedagemRepository {

    private final JdbcTemplate jdbcTemplate;

    public HospedagemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Hospedagem> MAPPER = new RowMapper<>() {
        @Override
        public Hospedagem mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hospedagem h = new Hospedagem();
            h.setId(rs.getLong("h.id"));
            h.setCheckInReal(rs.getTimestamp("h.check_in_real") != null ? rs.getTimestamp("h.check_in_real").toLocalDateTime() : null);
            h.setCheckOutReal(rs.getTimestamp("h.check_out_real") != null ? rs.getTimestamp("h.check_out_real").toLocalDateTime() : null);
            h.setEstado(EstadoHospedagem.valueOf(rs.getString("h.estado")));
            h.setMotivoExcecaoEncerramento(rs.getString("h.motivo_excecao_encerramento"));
            h.setObservacoes(rs.getString("h.observacoes"));
            h.setVersao(rs.getLong("h.versao"));

            com.gabrielflorencco.hotel_impact.model.Reserva r = new com.gabrielflorencco.hotel_impact.model.Reserva();
            r.setId(rs.getLong("r.id"));
            r.setCodigo(rs.getString("r.codigo"));
            r.setDataCheckIn(rs.getDate("r.data_check_in").toLocalDate());
            r.setDataCheckOut(rs.getDate("r.data_check_out").toLocalDate());
            r.setQuantidadeHospedes(rs.getInt("r.quantidade_hospedes"));
            r.setValorDiaria(rs.getBigDecimal("r.valor_diaria"));
            r.setDesconto(rs.getBigDecimal("r.desconto"));
            r.setValorTotal(rs.getBigDecimal("r.valor_total"));
            r.setEstado(com.gabrielflorencco.hotel_impact.enums.EstadoReserva.valueOf(rs.getString("r.estado")));
            r.setMotivoExcecao(rs.getString("r.motivo_excecao"));
            r.setObservacoes(rs.getString("r.observacoes"));
            r.setDataCriacao(rs.getTimestamp("r.data_criacao") != null ? rs.getTimestamp("r.data_criacao").toLocalDateTime() : null);
            r.setVersao(rs.getLong("r.versao"));

            com.gabrielflorencco.hotel_impact.model.Hospede hosp = new com.gabrielflorencco.hotel_impact.model.Hospede();
            hosp.setId(rs.getLong("hsp.id"));
            hosp.setNome(rs.getString("hsp.nome"));
            hosp.setCpf(rs.getString("hsp.cpf"));
            hosp.setTelefone(rs.getString("hsp.telefone"));
            hosp.setEmail(rs.getString("hsp.email"));
            hosp.setDataNascimento(rs.getDate("hsp.data_nascimento") != null ? rs.getDate("hsp.data_nascimento").toLocalDate() : null);
            hosp.setObservacoes(rs.getString("hsp.observacoes"));
            hosp.setDataCadastro(rs.getTimestamp("hsp.data_cadastro") != null ? rs.getTimestamp("hsp.data_cadastro").toLocalDateTime() : null);
            r.setHospede(hosp);

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

            r.setQuarto(q);
            h.setReserva(r);
            h.setQuarto(q);

            return h;
        }
    };

    public Optional<Hospedagem> findByIdComDetalhes(Long id) {
        String sql = baseQuery() + " WHERE h.id = ?";
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public Optional<Hospedagem> findById(Long id) {
        String sql = "SELECT id, reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes, versao FROM hospedagem WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Hospedagem h = new Hospedagem();
            h.setId(rs.getLong("id"));
            h.setCheckInReal(rs.getTimestamp("check_in_real") != null ? rs.getTimestamp("check_in_real").toLocalDateTime() : null);
            h.setCheckOutReal(rs.getTimestamp("check_out_real") != null ? rs.getTimestamp("check_out_real").toLocalDateTime() : null);
            h.setEstado(com.gabrielflorencco.hotel_impact.enums.EstadoHospedagem.valueOf(rs.getString("estado")));
            h.setMotivoExcecaoEncerramento(rs.getString("motivo_excecao_encerramento"));
            h.setObservacoes(rs.getString("observacoes"));
            h.setVersao(rs.getLong("versao"));
            return h;
        }, id).stream().findFirst();
    }

    public Optional<Hospedagem> findByReservaId(Long reservaId) {
        String sql = baseQuery() + " WHERE h.reserva_id = ?";
        return jdbcTemplate.query(sql, MAPPER, reservaId).stream().findFirst();
    }

    public Optional<Hospedagem> findByQuartoIdAndEstado(Long quartoId, EstadoHospedagem estado) {
        String sql = baseQuery() + " WHERE h.quarto_id = ? AND h.estado = ?";
        return jdbcTemplate.query(sql, MAPPER, quartoId, estado.name()).stream().findFirst();
    }

    public boolean existsByReservaId(Long reservaId) {
        String sql = "SELECT 1 FROM hospedagem WHERE reserva_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, reservaId));
    }

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM hospedagem WHERE quarto_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, quartoId));
    }

    public boolean existsByQuartoIdAndEstado(Long quartoId, EstadoHospedagem estado) {
        String sql = "SELECT 1 FROM hospedagem WHERE quarto_id = ? AND estado = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, quartoId, estado.name()));
    }

    public long countByEstado(EstadoHospedagem estado) {
        String sql = "SELECT COUNT(*) FROM hospedagem WHERE estado = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, estado.name());
    }

    public Page<Hospedagem> buscarTodos(Pageable pageable) {
        String sql = baseQuery() + " ORDER BY h.check_in_real DESC, h.id DESC";
        long total = countAll();
        List<Hospedagem> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER
        );
        return new PageImpl<>(content, pageable, total);
    }

    public Page<Hospedagem> buscarPorEstado(EstadoHospedagem estado, Pageable pageable) {
        String sql = baseQuery() + " WHERE h.estado = ? ORDER BY h.check_in_real DESC, h.id DESC";
        long total = countByEstado(estado);
        List<Hospedagem> content = jdbcTemplate.query(
                addPagination(sql, pageable),
                MAPPER,
                estado.name()
        );
        return new PageImpl<>(content, pageable, total);
    }

    public long countByCheckInRealBetween(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) FROM hospedagem WHERE check_in_real >= ? AND check_in_real < ?";
        return jdbcTemplate.queryForObject(sql, Long.class, inicio, fim);
    }

    public long countByCheckOutRealBetween(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) FROM hospedagem WHERE check_out_real >= ? AND check_out_real < ?";
        return jdbcTemplate.queryForObject(sql, Long.class, inicio, fim);
    }

    public List<Hospedagem> buscarIniciadasEntre(LocalDateTime inicio, LocalDateTime fim) {
        String sql = baseQuery() + " WHERE h.check_in_real >= ? AND h.check_in_real < ? ORDER BY h.check_in_real DESC";
        return jdbcTemplate.query(sql, MAPPER, inicio, fim);
    }

    public Hospedagem save(Hospedagem hospedagem) {
        if (hospedagem.getId() == null) {
            String sql = """
                INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    hospedagem.getReserva().getId(), hospedagem.getQuarto().getId(),
                    hospedagem.getCheckInReal(), hospedagem.getCheckOutReal(),
                    hospedagem.getEstado().name(),
                    hospedagem.getMotivoExcecaoEncerramento(),
                    hospedagem.getObservacoes());
            hospedagem.setId(id);
        } else {
            String sql = """
                UPDATE hospedagem
                SET check_out_real = ?, estado = ?, motivo_excecao_encerramento = ?, observacoes = ?
                WHERE id = ? AND versao = ?
                """;
            int rows = jdbcTemplate.update(sql,
                    hospedagem.getCheckOutReal(), hospedagem.getEstado().name(),
                    hospedagem.getMotivoExcecaoEncerramento(), hospedagem.getObservacoes(),
                    hospedagem.getId(), hospedagem.getVersao());
            if (rows == 0) {
                throw new org.springframework.dao.OptimisticLockingFailureException(
                        "Hospedagem modificada por outra transação");
            }
        }
        return hospedagem;
    }

    private String baseQuery() {
        return """
            SELECT h.id, h.reserva_id, h.quarto_id, h.check_in_real, h.check_out_real, h.estado, h.motivo_excecao_encerramento, h.observacoes, h.versao,
                   r.id, r.codigo, r.hospede_id, r.quarto_id, r.data_check_in, r.data_check_out,
                   r.quantidade_hospedes, r.valor_diaria, r.desconto, r.valor_total, r.data_criacao,
                   r.estado, r.motivo_excecao, r.observacoes, r.versao,
                   hsp.id, hsp.cpf, hsp.nome, hsp.telefone, hsp.email, hsp.data_nascimento, hsp.observacoes, hsp.data_cadastro,
                   q.id, q.numero, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo, q.versao,
                   cat.id, cat.nome, cat.descricao, cat.capacidade_maxima, cat.quantidade_camas, cat.preco_base, cat.ativo
            FROM hospedagem h
            JOIN reserva r ON h.reserva_id = r.id
            JOIN hospede hsp ON r.hospede_id = hsp.id
            JOIN quarto q ON h.quarto_id = q.id
            JOIN categoria_quarto cat ON q.categoria_id = cat.id
            """;
    }

    private long countAll() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM hospedagem", Long.class);
    }

    private String addPagination(String baseSql, Pageable pageable) {
        return baseSql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
    }
}