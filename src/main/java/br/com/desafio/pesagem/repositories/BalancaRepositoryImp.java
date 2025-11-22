package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Balanca;
import br.com.desafio.pesagem.entities.Filial;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class BalancaRepositoryImp implements BalancaRepository {
    private final JdbcClient jdbcClient;

    public BalancaRepositoryImp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Balanca> findByCodigoHardware(String idBalanca) {
        return this.jdbcClient.sql("SELECT max(id) FROM balanca where codigo_hardware = :idBalanca")
                .param("idBalanca", idBalanca)
                .query(Balanca.class)
                .optional();
    }

    @Override
    public Optional<Filial> findFilial(String nomeFilial) {
        return this.jdbcClient.sql("SELECT max(id) FROM filial where lower(nome) LIKE :nomeFilial")
                .param("nomeFilial", nomeFilial)
                .query(Filial.class)
                .optional();
    }
    @Override
    public Optional<Balanca> findByFilial(Long idFilial) {
        return this.jdbcClient.sql("SELECT max(id) FROM balanca where filial_id = :idFilial")
                .param("idFilial", idFilial)
                .query(Balanca.class)
                .optional();
    }
}
