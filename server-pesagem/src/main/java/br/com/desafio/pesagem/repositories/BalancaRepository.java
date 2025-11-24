package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Balanca;
import br.com.desafio.pesagem.entities.Filial;

import java.util.Optional;

public interface BalancaRepository {
    Optional<Balanca> findByCodigoHardware(String idBalanca);
    Optional<Balanca> findByFilial(Long idFilial);
    Optional<Balanca> findById(Long id);
}
