package br.com.desafio.pesagem.service;

import br.com.desafio.pesagem.dto.LeituraPesagemDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EstabilizacaoService {
    private static final int BUFFER_SIZE = 10; // últimas 10 leituras = 1s
    private static final int VARIACAO_MAX = 3; // 3 kg de variação máxima
    private static final int MIN_ESTAVEIS = 8; // leituras estáveis necessárias

    private final Map<String, Deque<Double>> buffers = new ConcurrentHashMap<>();
    private final Set<String> bloqueadas = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public Optional<LeituraPesagemDTO> processarPeso(LeituraPesagemDTO leitura, LocalDateTime inicio) {
        Deque<Double> buffer = buffers.computeIfAbsent(leitura.plate(), k-> new ArrayDeque<>());
        if (buffer.size() == BUFFER_SIZE) {
            buffer.removeFirst();
        }
        buffer.addLast(leitura.weight());

        if (buffer.size() < BUFFER_SIZE) {
            return Optional.empty(); //retorna false = não estável
        }

        double min = buffer.stream().mapToDouble(v -> v).min().orElse(leitura.weight());
        double max = buffer.stream().mapToDouble(v -> v).max().orElse(leitura.weight());

        if (max - min <= VARIACAO_MAX) {
            long estaveis = buffer.stream().filter(p-> Math.abs(p - leitura.weight()) <= VARIACAO_MAX).count();
            if (estaveis >= MIN_ESTAVEIS && !bloqueadas.contains(leitura.plate())) {
                bloqueadas.add(leitura.plate()); // evita gravar repetidas vezes
                // podemos limpar o buffer se quiser
                // buffers.remove(leitura.plate());
                return Optional.of(new LeituraPesagemDTO(leitura.idBalanca(), leitura.plate(), leitura.weight(), inicio));
            }
        }
        return Optional.empty();
    }

    // reset manual quando caminhão sair
    public void reset(String balancaId) {
        buffers.remove(balancaId);
        bloqueadas.remove(balancaId);
    }
}
