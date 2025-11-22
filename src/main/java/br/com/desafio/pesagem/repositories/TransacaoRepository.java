package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.dto.TransacaoDTO;

import java.util.Optional;

public interface TransacaoRepository {

    Optional<TransacaoDTO> findOpenTransaction(String placa);
    Integer save(TransacaoDTO transacaoTransporte);
}
