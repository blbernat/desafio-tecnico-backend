package br.com.desafio.pesagem.controller;

import br.com.desafio.pesagem.dto.LeituraPesagemDTO;
import br.com.desafio.pesagem.service.PesagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/pesagens")
@Tag(name = "Pesagem", description = "Endpoints para pesagem de caminhões")
public class PesagemController {

    private final PesagemService pesagemService;

    public PesagemController(PesagemService pesagemService) {
        this.pesagemService = pesagemService;
    }

    @Operation(
            summary = "Endpoint pesagem",
            description = "As requisições do simulador ESP32 são recebidas e validadas. Ao estabilizar o peso é salvo o registro de transação do transporte.",
            responses = { @ApiResponse(description = "Ok", responseCode = "200") })
    @PostMapping
    public ResponseEntity<Void> receberLeituraPesagem(@RequestBody LeituraPesagemDTO leitura) {
        pesagemService.processarPeso(leitura, LocalDateTime.now());

        //Optional<LeituraPesagemDTO> pesoEstabilizado = estabilizacaoService.processarPeso(leitura, LocalDateTime.now());
        //pesoEstabilizado.ifPresent(p-> pesagemService.salvarPesagemEstabilizada(p));

        return ResponseEntity.ok().build();
    }
}
