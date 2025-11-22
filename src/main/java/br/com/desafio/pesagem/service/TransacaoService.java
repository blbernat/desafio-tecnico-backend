package br.com.desafio.pesagem.service;

import br.com.desafio.pesagem.dto.TransacaoDTO;
import br.com.desafio.pesagem.entities.*;
import br.com.desafio.pesagem.repositories.BalancaRepository;
import br.com.desafio.pesagem.repositories.CaminhaoRepository;
import br.com.desafio.pesagem.repositories.TipoGraoRepository;
import br.com.desafio.pesagem.repositories.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepo;
    private final CaminhaoRepository caminhaoRepo;
    private final BalancaRepository balancaRepo;
    private final TipoGraoRepository tipoGraoRepo;

    public TransacaoService(TransacaoRepository transacaoRepo, CaminhaoRepository caminhaoRepo, BalancaRepository balancaRepo, TipoGraoRepository tipoGraoRepo) {
        this.transacaoRepo = transacaoRepo;
        this.caminhaoRepo = caminhaoRepo;
        this.balancaRepo = balancaRepo;
        this.tipoGraoRepo = tipoGraoRepo;
    }

    public Optional<TransacaoTransporte> findTransacao(String nomeFilial, String placaCaminhao, String nomeGrao) {
        Caminhao caminhao = caminhaoRepo.findByPlate(placaCaminhao)
                .orElseThrow(()-> new IllegalStateException("Caminhão não cadastrado: " + placaCaminhao));

        Filial filial = balancaRepo.findFilial(nomeFilial)
                .orElseThrow(()-> new IllegalStateException("Filial não cadastrada: " + nomeFilial));

        Balanca balanca = balancaRepo.findByFilial(filial.getId())
                .orElseThrow(()-> new IllegalStateException("Balanças da filial não encontrada"));

        // aqui se busca o primeiro tipo de grão cadastrado
        TipoGrao tipoGrao = tipoGraoRepo.findByName(nomeGrao)
                .orElseThrow(()-> new IllegalStateException("Tipo de grão não cadastrado: " + nomeGrao));

        return this.transacaoRepo.findTransacao(balanca.getId(), caminhao.getId(), tipoGrao.getId());
    }
}
