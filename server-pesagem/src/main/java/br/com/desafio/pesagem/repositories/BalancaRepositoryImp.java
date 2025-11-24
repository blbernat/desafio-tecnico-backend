package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Balanca;
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
        return this.jdbcClient.sql("SELECT * FROM balanca where codigo_hardware = :idBalanca")
                .param("idBalanca", idBalanca)
                .query(Balanca.class)
                .optional();
    }

    @Override
    public Optional<Balanca> findByFilial(Long idFilial) {
        return this.jdbcClient.sql("SELECT * FROM balanca where filial_id = :idFilial")
                .param("idFilial", idFilial)
                .query(Balanca.class)
                .optional();
    }

    @Override
    public Optional<Balanca> findById(Long id) {
        return this.jdbcClient.sql("SELECT * FROM balanca where id = :id")
                .param("id", id)
                .query(Balanca.class)
                .optional();
    }
}
