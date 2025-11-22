package br.com.desafio.pesagem.service;

import br.com.desafio.pesagem.dto.LeituraPesagemDTO;
import br.com.desafio.pesagem.dto.TransacaoDTO;
import br.com.desafio.pesagem.entities.Balanca;
import br.com.desafio.pesagem.entities.Caminhao;
import br.com.desafio.pesagem.entities.TipoGrao;
import br.com.desafio.pesagem.repositories.BalancaRepository;
import br.com.desafio.pesagem.repositories.CaminhaoRepository;
import br.com.desafio.pesagem.repositories.TipoGraoRepository;
import br.com.desafio.pesagem.repositories.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PesagemService {

    private final CaminhaoRepository caminhaoRepo;
    private final BalancaRepository balancaRepo;
    private final TipoGraoRepository tipoGraoRepo;
    private final TransacaoRepository transacaoRepo;

    public PesagemService(CaminhaoRepository caminhaoRepo,
                          BalancaRepository balancaRepo,
                          TipoGraoRepository tipoGraoRepo,
                          TransacaoRepository transacaoRepo) {
        this.caminhaoRepo = caminhaoRepo;
        this.balancaRepo = balancaRepo;
        this.tipoGraoRepo = tipoGraoRepo;
        this.transacaoRepo = transacaoRepo;
    }

    private static final int BUFFER_SIZE = 10; // últimas 10 leituras = 1s
    private static final int VARIACAO_MAX = 3; // 3 kg de variação máxima
    private static final int MIN_ESTAVEIS = 8; // leituras estáveis necessárias

    private final Map<String, Deque<Double>> buffers = new ConcurrentHashMap<>();
    private final Set<String> bloqueadas = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /*
    - o ESP32 envia peso a cada 100 ms
    - o peso varia enquanto o caminhão ainda está se movendo
    - precisa detectar quando atingiu um valor estável
     */

    public void processarPeso(LeituraPesagemDTO leitura, LocalDateTime inicio) {
        Deque<Double> buffer = buffers.computeIfAbsent(leitura.plate(), k-> new ArrayDeque<>());
        if (buffer.size() == BUFFER_SIZE) {
            buffer.removeFirst();
        }
        buffer.addLast(leitura.weight());

        if (buffer.size() < BUFFER_SIZE) {
            return; //retorna false = não estável
        }

        double min = buffer.stream().mapToDouble(v -> v).min().orElse(leitura.weight());
        double max = buffer.stream().mapToDouble(v -> v).max().orElse(leitura.weight());

        if (max - min <= VARIACAO_MAX) {
            long estaveis = buffer.stream().filter(p-> Math.abs(p - leitura.weight()) <= VARIACAO_MAX).count();
            if (estaveis >= MIN_ESTAVEIS && !bloqueadas.contains(leitura.plate())) {

                // evita gravação repetida da balança
                bloqueadas.add(leitura.plate());

                // limpar o buffer
                this.reset(leitura.idBalanca());

                LeituraPesagemDTO leituraEstavel = new LeituraPesagemDTO(leitura.idBalanca(), leitura.plate(), leitura.weight(), inicio);
                this.salvarPesagemEstabilizada(leituraEstavel);
            }
        }
    }

    // reset manual quando caminhão sair
    public void reset(String balancaId) {
        buffers.remove(balancaId);
        bloqueadas.remove(balancaId);
    }

    public void salvarPesagemEstabilizada(LeituraPesagemDTO dto) {
        Caminhao caminhao = caminhaoRepo.findByPlate(dto.plate())
                .orElseThrow(()-> new IllegalStateException("Caminhão não cadastrado: " + dto.plate()));

        Balanca balanca = balancaRepo.findByCodigoHardware(dto.idBalanca())
                .orElseThrow(()-> new IllegalStateException("Balança não cadastrada: " + dto.idBalanca()));

        // aqui se busca o primeiro tipo de grão cadastrado
        TipoGrao tipoGrao = tipoGraoRepo.findAny()
                .orElseThrow(()-> new IllegalStateException("Tipo de grão não cadastrado"));

        double tara = caminhao.getTara();
        double pesoBruto = dto.weight();
        double pesoLiquido = pesoBruto- tara;
        double custo = pesoLiquido * tipoGrao.getPrecoTon();
        TransacaoDTO transacaoDTO = new TransacaoDTO(caminhao, tipoGrao, balanca, pesoBruto, pesoLiquido, custo, dto.inicio());
        transacaoRepo.save(transacaoDTO);

    }
}
