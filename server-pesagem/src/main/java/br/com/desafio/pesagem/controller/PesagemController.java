package br.com.desafio.pesagem.controller;

import br.com.desafio.pesagem.dto.CustoDTO;
import br.com.desafio.pesagem.dto.EnvioPesagemDTO;
import br.com.desafio.pesagem.dto.LeituraPesagemDTO;
import br.com.desafio.pesagem.entities.TransacaoTransporte;
import br.com.desafio.pesagem.service.PesagemService;
import br.com.desafio.pesagem.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/pesagens")
@Tag(name = "Pesagem", description = "Endpoints para pesagem de caminhões")
public class PesagemController {

    private final PesagemService pesagemService;
    private final TransacaoService transacaoService;

    public PesagemController(PesagemService pesagemService, TransacaoService transacaoService) {
        this.pesagemService = pesagemService;
        this.transacaoService = transacaoService;
    }

    @Operation(
            summary = "Endpoint pesagem",
            description = "As requisições do simulador ESP32 são recebidas e validadas. Ao estabilizar o peso é salvo o registro de transação do transporte.",
            responses = { @ApiResponse(description = "Ok", responseCode = "200") })
    @PostMapping
    public ResponseEntity<Void> receberLeituraPesagem(@RequestBody EnvioPesagemDTO envio) {
        //this.pesagemService.processarPeso(envio, LocalDateTime.now());
        LeituraPesagemDTO leituraEstavel = new LeituraPesagemDTO(envio.id(), envio.plate(), envio.weight(), LocalDateTime.now());
        this.pesagemService.salvarPesagemEstabilizada(leituraEstavel);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Busca as transações cadastradas",
            description = "Busca transações cadastradas. Pode-se buscar todas ou por filial, caminhão e tipo de grão. Retorna uma lista de JSON. Exemplo (todos): http://localhost:8080/v1/pesagens. Exemplo (com filtro): http://localhost:8080/v1/pesagens?caminhao=ABC1D23",
            responses = { @ApiResponse(description = "Ok", responseCode = "200")})
    @GetMapping
    public ResponseEntity<List<TransacaoTransporte>> findTransacao (@RequestParam(value = "filial", required = false) String filial,
                                                                    @RequestParam(value = "caminhao", required = false) String caminhao,
                                                                    @RequestParam(value = "tipoGrao", required = false) String tipoGrao) {
        List<TransacaoTransporte> transacao = this.transacaoService.findTransacao(filial, caminhao, tipoGrao);
        return ResponseEntity.ok(transacao);
    }

    @Operation(
            summary = "Busca os custos das transações",
            description = "Busca os custos das transações. Pode-se buscar por filial, caminhão e tipo de grão. Retorna uma lista de JSON. Exemplo (todos): http://localhost:8080/v1/pesagens. Exemplo (com filtro): http://localhost:8080/v1/pesagens?caminhao=ABC1D23",
            responses = { @ApiResponse(description = "Ok", responseCode = "200"), @ApiResponse(description = "Not found", responseCode = "404")})
    @GetMapping("/custo")
    public ResponseEntity<CustoDTO> findCustos (@RequestParam(value = "entidade", required = false) String entidade,
                                                      @RequestParam(value = "nome", required = false) String nome) {
        CustoDTO custo = this.transacaoService.findCusto(entidade, nome);
        return ResponseEntity.ok(custo);
    }
}
