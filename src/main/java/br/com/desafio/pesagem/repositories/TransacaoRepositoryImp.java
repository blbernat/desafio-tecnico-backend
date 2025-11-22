package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.dto.TransacaoDTO;
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
    public Optional<TransacaoDTO> findOpenTransaction(String placa) {
        String sql = "SELECT max(id) FROM transacao_transporte where caminhao_id = " +
                    "( select max(id) from caminhao where placa = :placa ";
        return this.jdbcClient.sql(sql)
                .param("placa", placa)
                .query(TransacaoDTO.class)
                .optional();
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
}
