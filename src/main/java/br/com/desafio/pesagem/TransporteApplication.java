package br.com.desafio.pesagem;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

@SpringBootApplication
public class TransporteApplication {

    private static final int INTERVALO_MS = 100;
    private static final int OSCILACAO = 20;
    private static final String URL = "http://localhost:8080/pesagens";

    public static void main(String[] args) throws Exception {

        // rodar no bash: mvn clean package ou ./mvnw clean package
        // java -jar target/transporte-0.0.1-SNAPSHOT.jar BAL001 ABC1D23 8000
        // comando para rodar as requisições com os argumentos: balanca, placa e peso

        if (args.length < 3) {
            System.out.println("Erro ao receber args <balancaId> <placa> <peso>");
            System.out.println("Ex: BAL001 ABC1D23 8000");
            return;
        }

        String balancaId = args[0];
        String placa = args[1];
        String pesoBase = args[2];

        HttpClient client = HttpClient.newHttpClient();
        Random random = new Random();

        System.out.println("=== ESP32 SIMULATOR ===");
        System.out.println("Balança: " + balancaId);
        System.out.println("Placa  : " + placa);
        System.out.println("Peso  : " + pesoBase);
        System.out.println("Enviando para: " + URL);
        System.out.println("=======================\n");

        int i = 0;

        while (i <= 10) {
            i++;

            int pesoSimulado = Integer.parseInt(pesoBase) + random.nextInt(OSCILACAO * 2) - OSCILACAO;

            String json = String.format(
                    "{\"id\":\"%s\",\"plate\":\"%s\",\"weight\":%d}",
                    balancaId, placa, pesoSimulado
            );

            System.out.println("json: " + json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            System.out.println("Request: " + request.toString());

            try {
                System.out.println("Entrei no try");
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Enviado: " + json + " | Status: " + response.statusCode());
            } catch (Exception e) {
                System.out.println("Erro ao enviar: " + e.getMessage());
            }

            Thread.sleep(INTERVALO_MS);
        }
    }

}
