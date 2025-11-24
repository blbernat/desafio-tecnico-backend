package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Filial;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FilialRepositoryImp implements FilialRepository {
    private final JdbcClient jdbcClient;

    public FilialRepositoryImp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Filial> findByName(String nomeFilial) {
        return this.jdbcClient.sql("SELECT * FROM filial where lower(nome) LIKE :nomeFilial")
                .param("nomeFilial", nomeFilial)
                .query(Filial.class)
                .optional();
    }

    @Override
    public Optional<Filial> findById(Long id) {
        return this.jdbcClient.sql("SELECT * FROM filial where id = :id")
                .param("id", id)
                .query(Filial.class)
                .optional();
    }
}
