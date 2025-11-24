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

    @Override
    public Optional<TipoGrao> findByName(String nome) {
        return this.jdbc.sql("SELECT * FROM tipo_grao where lower(nome) LIKE :nome")
                .param("nome", nome)
                .query(TipoGrao.class)
                .optional();
    }

    @Override
    public Optional<TipoGrao> findById(Long id) {
        return this.jdbc.sql("SELECT * FROM tipo_grao where id = :id")
                .param("id", id)
                .query(TipoGrao.class)
                .optional();
    }
}
