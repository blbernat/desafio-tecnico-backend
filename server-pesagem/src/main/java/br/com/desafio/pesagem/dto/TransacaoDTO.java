package br.com.desafio.pesagem.dto;

import br.com.desafio.pesagem.entities.Balanca;
import br.com.desafio.pesagem.entities.Caminhao;
import br.com.desafio.pesagem.entities.Filial;
import br.com.desafio.pesagem.entities.TipoGrao;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TransacaoDTO(@NotBlank(message = "É obrigatório informar o caminhão!")
                           Caminhao caminhao,
                           @NotBlank(message = "É obrigatório informar o tipo de grão!")
                           TipoGrao tipoGrao,
                           @NotBlank(message = "É obrigatório informar a balança utilizada!")
                           Balanca balanca,
                           Filial filial,
                           @NotBlank(message = "É obrigatório informar o peso bruto informado na pesagem!")
                           Double pesoBruto,
                           Double pesoLiquido,
                           Double custoCarga,
                           LocalDateTime inicio) {
}
