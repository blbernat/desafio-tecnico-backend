package br.com.desafio.pesagem.dto;

import java.time.LocalDateTime;

public record LeituraPesagemDTO(
        String idBalanca,
        String plate,
        Double weight,
        LocalDateTime inicio
) {}
