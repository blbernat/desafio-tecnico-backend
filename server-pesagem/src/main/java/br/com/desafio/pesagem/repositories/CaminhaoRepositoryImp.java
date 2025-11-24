package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Caminhao;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class CaminhaoRepositoryImp implements CaminhaoRepository {
    private final JdbcClient jdbcClient;

    public CaminhaoRepositoryImp(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Caminhao> findByPlate(String placa) {
        return this.jdbcClient.sql("SELECT * FROM caminhao where placa = :placa")
                .param("placa", placa)
                .query(Caminhao.class)
                .optional();
    }

    @Override
    public Optional<Caminhao> findById(Long id) {
        return this.jdbcClient.sql("SELECT * FROM caminhao where id = :id")
                .param("id", id)
                .query(Caminhao.class)
                .optional();
    }

}
