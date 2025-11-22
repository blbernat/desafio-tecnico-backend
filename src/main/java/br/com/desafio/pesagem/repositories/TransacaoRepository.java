package br.com.desafio.pesagem.repositories;

import br.com.desafio.pesagem.dto.TransacaoDTO;
import br.com.desafio.pesagem.entities.TransacaoTransporte;

import java.util.Optional;

public interface TransacaoRepository {

    Integer save(TransacaoDTO transacaoTransporte);
    Optional<TransacaoTransporte> findTransacao(Long balandaId, Long caminhaoId, Long tipoGraoId);
}
