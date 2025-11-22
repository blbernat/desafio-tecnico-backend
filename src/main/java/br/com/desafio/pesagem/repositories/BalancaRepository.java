package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.entities.Balanca;
import java.util.Optional;

public interface BalancaRepository {
    Optional<Balanca> findByCodigoHardware(String idBalanca);

}
