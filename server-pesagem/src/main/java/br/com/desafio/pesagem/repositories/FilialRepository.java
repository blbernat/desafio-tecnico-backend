package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Filial;

import java.util.Optional;

public interface FilialRepository {
    Optional<Filial> findById(Long id);
    Optional<Filial> findByName(String nomeFilial);
}
