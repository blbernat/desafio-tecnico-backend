package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.dto.TransacaoDTO;
import br.com.desafio.pesagem.entities.TransacaoTransporte;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class TransacaoRepositoryImp implements TransacaoRepository {

    private final JdbcClient jdbcClient;
    public TransacaoRepositoryImp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Integer save(TransacaoDTO t) {
        return this.jdbcClient.sql("INSERT INTO transacao_transporte " +
                " (caminhao_id, tipo_grao_id, balanca_id, peso_bruto, peso_liquido, custo_carga, inicio, fim) " +
                " values(:caminhao_id, :tipo_grao_id, :balanca_id, :peso_bruto, :peso_liquido, :custo_carga, :inicio, :fim)")
                .param("caminhao_id", t.caminhao().getId())
                .param("tipo_grao_id",t.tipoGrao().getId())
                .param("balanca_id", t.balanca().getId())
                .param("peso_bruto", t.pesoBruto())
                .param("peso_liquido", t.pesoLiquido())
                .param("custo_carga", t.custoCarga())
                .param("inicio", t.inicio())
                .param("fim", LocalDate.now())
                .update();
    }

    @Override
    public Optional<TransacaoTransporte> findTransacao(Long balandaId, Long caminhaoId, Long tipoGraoId) {
        JdbcClient.StatementSpec statement;
        var sql = new StringBuilder("SELECT * FROM transacao_transporte");
        sql.append(" WHERE 1=1 ");

        if (balandaId != null) {
            sql.append(" AND balanca_id = :balandaId ");
        }
        if (caminhaoId != null) {
            sql.append(" AND caminhao_id = :caminhaoId ");
        }
        if (tipoGraoId != null) {
            sql.append(" AND tipo_grao_id = :tipoGraoId");
        }

        statement = jdbcClient.sql(sql.toString());

        if (balandaId != null) {
            statement.param("balandaId", balandaId);
        }
        if (caminhaoId != null) {
            statement.param("caminhaoId", caminhaoId);
        }
        if (tipoGraoId != null) {
            statement.param("tipoGraoId", tipoGraoId);
        }

        return statement.query(TransacaoTransporte.class).optional();
    }
}
