package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.dto.TransacaoDTO;
import br.com.desafio.pesagem.entities.TransacaoTransporte;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TransacaoRepositoryImp implements TransacaoRepository {

    private final JdbcClient jdbcClient;
    private final CaminhaoRepository caminhaoRepo;
    private final BalancaRepository balancaRepo;
    private final TipoGraoRepository tipoGraoRepo;
    private final FilialRepository filialRepo;

    public TransacaoRepositoryImp(JdbcClient jdbcClient, CaminhaoRepository caminhaoRepo, BalancaRepository balancaRepo, TipoGraoRepository tipoGraoRepo, FilialRepository filialRepo) {
        this.jdbcClient = jdbcClient;
        this.caminhaoRepo = caminhaoRepo;
        this.balancaRepo = balancaRepo;
        this.tipoGraoRepo = tipoGraoRepo;
        this.filialRepo = filialRepo;
    }

    @Override
    public Integer save(TransacaoDTO t) {
        return this.jdbcClient.sql("INSERT INTO transacao_transporte " +
                " (caminhao_id, tipo_grao_id, balanca_id, filial_id, peso_bruto, peso_liquido, custo_carga, inicio, fim) " +
                " values(:caminhao_id, :tipo_grao_id, :balanca_id, :filial_id, :peso_bruto, :peso_liquido, :custo_carga, :inicio, :fim)")
                .param("caminhao_id", t.caminhao().getId())
                .param("tipo_grao_id",t.tipoGrao().getId())
                .param("balanca_id", t.balanca().getId())
                .param("filial_id", t.filial().getId())
                .param("peso_bruto", t.pesoBruto())
                .param("peso_liquido", t.pesoLiquido())
                .param("custo_carga", t.custoCarga())
                .param("inicio", t.inicio())
                .param("fim", LocalDateTime.now())
                .update();
    }

    @Override
    public List<TransacaoTransporte> findTransacao(Long filialId, Long caminhaoId, Long tipoGraoId) {
        JdbcClient.StatementSpec statement;
        var sql = new StringBuilder("SELECT * FROM transacao_transporte");
        sql.append(" WHERE 1=1 ");

        if (filialId != null) {
            sql.append(" AND filial_id = :filialId ");
        }
        if (caminhaoId != null) {
            sql.append(" AND caminhao_id = :caminhaoId ");
        }
        if (tipoGraoId != null) {
            sql.append(" AND tipo_grao_id = :tipoGraoId");
        }

        statement = jdbcClient.sql(sql.toString());

        if (filialId != null) {
            statement.param("filialId", filialId);
        }
        if (caminhaoId != null) {
            statement.param("caminhaoId", caminhaoId);
        }
        if (tipoGraoId != null) {
            statement.param("tipoGraoId", tipoGraoId);
        }

        return statement.query(new TransacaoRowMapper(caminhaoRepo, balancaRepo, tipoGraoRepo, filialRepo)).list();
    }

    @Override
    public List<TransacaoDTO> findCusto(String entidade, Long idEntidade) {
        var sql = new StringBuilder("SELECT * FROM transacao_transporte");
        sql.append(" WHERE 1=1 ");

        if ("filial".equalsIgnoreCase(entidade)) {
            sql.append(" AND filial_id = :idEntidade ");
        }
        if ("caminhao".equalsIgnoreCase(entidade)) {
            sql.append(" AND caminhao_id = :idEntidade ");
        }
        if ("grao".equalsIgnoreCase(entidade)) {
            sql.append(" AND tipo_grao_id = :idEntidade");
        }

        return this.jdbcClient.sql(sql.toString())
                .param("idEntidade", idEntidade)
                .query(TransacaoDTO.class)
                .list();
    }
}
