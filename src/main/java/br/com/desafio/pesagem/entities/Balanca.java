package br.com.desafio.pesagem.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Balanca {
    private Long id;
    private String codigoHardware; // id enviado pelo ESP32
    private Filial filial;
}

