package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.TipoGrao;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TipoGraoRepositoryImp implements TipoGraoRepository{

    private final JdbcClient jdbc;

    public TipoGraoRepositoryImp(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<TipoGrao> findAny() {
        return this.jdbc.sql("SELECT id, nome, preco_ton FROM tipo_grao LIMIT 1")
                .query(TipoGrao.class)
                .optional();
    }
}
