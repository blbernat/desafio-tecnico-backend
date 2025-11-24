package br.com.desafio.pesagem.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransacaoTransporte {
    private Long id;
    private Caminhao caminhao;
    private TipoGrao tipoGrao;
    private Balanca balanca;
    private Filial filial;

    private Double pesoBruto;
    private Double tara;
    private Double pesoLiquido;
    private Double custoCarga;

    private LocalDateTime inicio;
    private LocalDateTime fim;
}
