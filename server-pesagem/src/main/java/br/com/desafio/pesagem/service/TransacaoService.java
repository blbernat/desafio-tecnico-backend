package br.com.desafio.pesagem.service;

import br.com.desafio.pesagem.dto.CustoDTO;
import br.com.desafio.pesagem.dto.TransacaoDTO;
import br.com.desafio.pesagem.entities.*;
import br.com.desafio.pesagem.repositories.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransacaoService {
    private final TransacaoRepository transacaoRepo;
    private final CaminhaoRepository caminhaoRepo;
    private final BalancaRepository balancaRepo;
    private final TipoGraoRepository tipoGraoRepo;
    private final FilialRepository filialRepo;

    public TransacaoService(TransacaoRepository transacaoRepo, CaminhaoRepository caminhaoRepo, BalancaRepository balancaRepo, TipoGraoRepository tipoGraoRepo, FilialRepository filialRepo) {
        this.transacaoRepo = transacaoRepo;
        this.caminhaoRepo = caminhaoRepo;
        this.balancaRepo = balancaRepo;
        this.tipoGraoRepo = tipoGraoRepo;
        this.filialRepo = filialRepo;
    }

    public List<TransacaoTransporte> findTransacao(String nomeFilial, String placaCaminhao, String nomeGrao) {
        Caminhao caminhao = new Caminhao();
        Filial filial = new Filial();
        //Balanca balanca = new Balanca();
        TipoGrao tipoGrao = new TipoGrao();

        if (placaCaminhao != null) {
            caminhao = caminhaoRepo.findByPlate(placaCaminhao)
                    .orElseThrow(()-> new IllegalStateException("Caminhão não cadastrado: " + placaCaminhao));
        }
        if (nomeFilial != null) {
            filial = filialRepo.findByName(nomeFilial)
                    .orElseThrow(()-> new IllegalStateException("Filial não cadastrada: " + nomeFilial));
        }
        if (nomeGrao != null) {
            tipoGrao = tipoGraoRepo.findByName(nomeGrao)
                    .orElseThrow(()-> new IllegalStateException("Tipo de grão não cadastrado: " + nomeGrao));
        }

        return this.transacaoRepo.findTransacao(filial.getId(), caminhao.getId(), tipoGrao.getId());
    }

    public CustoDTO findCusto(String entidade, String nome) {
        Caminhao caminhao = new Caminhao();
        Filial filial = new Filial();
        Balanca balanca = new Balanca();
        TipoGrao tipoGrao = new TipoGrao();

        Long idEntidade = 0L;
        if ("filial".equalsIgnoreCase(entidade)) {
            filial = filialRepo.findByName(nome)
                    .orElseThrow(()-> new IllegalStateException("Filial não encontrada: " + nome));

            idEntidade = filial.getId();
        } else if ("caminhao".equalsIgnoreCase(entidade)) {
            caminhao = caminhaoRepo.findByPlate(nome)
                    .orElseThrow(()-> new IllegalStateException("Caminhão não encontrado: " + nome));

            idEntidade = caminhao.getId();
        } else if ("grao".equalsIgnoreCase(entidade)) {
            tipoGrao = tipoGraoRepo.findByName(nome)
                    .orElseThrow(()-> new IllegalStateException("Tipo de grão não encontrado: " + nome));

            idEntidade = tipoGrao.getId();
        }

        List<TransacaoDTO> transacoes = this.transacaoRepo.findCusto(entidade, idEntidade);

        return this.calcularValorCusto(transacoes, entidade, nome);
    }

    public CustoDTO calcularValorCusto(List<TransacaoDTO> transacoes, String entidade, String nome) {
        double valor = 0D;

        for (TransacaoDTO t : transacoes) {
            valor += t.custoCarga();
        }

        return new CustoDTO(entidade, nome, valor);
    }
}
