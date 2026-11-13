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
            h.setId(rs.getLong("h_id"));
            h.setCheckInReal(rs.getTimestamp("h_check_in_real") != null ? rs.getTimestamp("h_check_in_real").toLocalDateTime() : null);
            h.setCheckOutReal(rs.getTimestamp("h_check_out_real") != null ? rs.getTimestamp("h_check_out_real").toLocalDateTime() : null);
            h.setEstado(EstadoHospedagem.valueOf(rs.getString("h_estado")));
            h.setMotivoExcecaoEncerramento(rs.getString("h_motivo_excecao_encerramento"));
            h.setObservacoes(rs.getString("h_observacoes"));
            h.setVersao(rs.getLong("h_versao"));

            com.gabrielflorencco.hotel_impact.model.Reserva r = new com.gabrielflorencco.hotel_impact.model.Reserva();
            r.setId(rs.getLong("r_id"));
            r.setCodigo(rs.getString("r_codigo"));
            r.setDataCheckIn(rs.getDate("r_data_check_in").toLocalDate());
            r.setDataCheckOut(rs.getDate("r_data_check_out").toLocalDate());
            r.setQuantidadeHospedes(rs.getInt("r_quantidade_hospedes"));
            r.setValorDiaria(rs.getBigDecimal("r_valor_diaria"));
            r.setDesconto(rs.getBigDecimal("r_desconto"));
            r.setValorTotal(rs.getBigDecimal("r_valor_total"));
            r.setEstado(com.gabrielflorencco.hotel_impact.enums.EstadoReserva.valueOf(rs.getString("r_estado")));
            r.setMotivoExcecao(rs.getString("r_motivo_excecao"));
            r.setObservacoes(rs.getString("r_observacoes"));
            r.setDataCriacao(rs.getTimestamp("r_data_criacao") != null ? rs.getTimestamp("r_data_criacao").toLocalDateTime() : null);
            r.setVersao(rs.getLong("r_versao"));

            com.gabrielflorencco.hotel_impact.model.Hospede hosp = new com.gabrielflorencco.hotel_impact.model.Hospede();
            hosp.setId(rs.getLong("hsp_id"));
            hosp.setNome(rs.getString("hsp_nome"));
            hosp.setCpf(rs.getString("hsp_cpf"));
            hosp.setTelefone(rs.getString("hsp_telefone"));
            hosp.setEmail(rs.getString("hsp_email"));
            hosp.setDataNascimento(rs.getDate("hsp_data_nascimento") != null ? rs.getDate("hsp_data_nascimento").toLocalDate() : null);
            hosp.setObservacoes(rs.getString("hsp_observacoes"));
            hosp.setDataCadastro(rs.getTimestamp("hsp_data_cadastro") != null ? rs.getTimestamp("hsp_data_cadastro").toLocalDateTime() : null);
            r.setHospede(hosp);

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
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, reservaId);
        return !result.isEmpty();
    }

    public boolean existsByQuartoId(Long quartoId) {
        String sql = "SELECT 1 FROM hospedagem WHERE quarto_id = ?";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, quartoId);
        return !result.isEmpty();
    }

    public boolean existsByQuartoIdAndEstado(Long quartoId, EstadoHospedagem estado) {
        String sql = "SELECT 1 FROM hospedagem WHERE quarto_id = ? AND estado = ?";
        List<Boolean> result = jdbcTemplate.query(sql, (rs, rowNum) -> true, quartoId, estado.name());
        return !result.isEmpty();
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
                SET check_out_real = ?, estado = ?, motivo_excecao_encerramento = ?, observacoes = ?, versao = versao + 1
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
            SELECT h.id AS h_id, h.reserva_id AS h_reserva_id, h.quarto_id AS h_quarto_id, h.check_in_real AS h_check_in_real,
                   h.check_out_real AS h_check_out_real, h.estado AS h_estado, h.motivo_excecao_encerramento AS h_motivo_excecao_encerramento,
                   h.observacoes AS h_observacoes, h.versao AS h_versao,
                   r.id AS r_id, r.codigo AS r_codigo, r.hospede_id AS r_hospede_id, r.quarto_id AS r_quarto_id,
                   r.data_check_in AS r_data_check_in, r.data_check_out AS r_data_check_out,
                   r.quantidade_hospedes AS r_quantidade_hospedes, r.valor_diaria AS r_valor_diaria, r.desconto AS r_desconto,
                   r.valor_total AS r_valor_total, r.data_criacao AS r_data_criacao,
                   r.estado AS r_estado, r.motivo_excecao AS r_motivo_excecao, r.observacoes AS r_observacoes, r.versao AS r_versao,
                   hsp.id AS hsp_id, hsp.cpf AS hsp_cpf, hsp.nome AS hsp_nome, hsp.telefone AS hsp_telefone,
                   hsp.email AS hsp_email, hsp.data_nascimento AS hsp_data_nascimento, hsp.observacoes AS hsp_observacoes,
                   hsp.data_cadastro AS hsp_data_cadastro,
                   q.id AS q_id, q.numero AS q_numero, q.andar AS q_andar, q.capacidade_operacional AS q_capacidade_operacional,
                   q.estado AS q_estado, q.observacoes AS q_observacoes, q.ativo AS q_ativo, q.versao AS q_versao,
                   cat.id AS cat_id, cat.nome AS cat_nome, cat.descricao AS cat_descricao, cat.capacidade_maxima AS cat_capacidade_maxima,
                   cat.quantidade_camas AS cat_quantidade_camas, cat.preco_base AS cat_preco_base, cat.ativo AS cat_ativo
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