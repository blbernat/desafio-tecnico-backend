package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.TipoGrao;

import java.util.Optional;

public interface TipoGraoRepository {

    Optional<TipoGrao> findAny();
    Optional<TipoGrao> findByName(String nome);
    Optional<TipoGrao> findById(Long id);
}
