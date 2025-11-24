package main.java.br.com.desafio.balanca.controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.java.br.com.desafio.balanca.model.SimuladorBalanca;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {
    
    @FXML private TextField scaleIdField;
    @FXML private TextField plateField;
    @FXML private TextField targetWeightField;
    @FXML private TextField serverUrlField;
    
    @FXML private Text currentWeightText;
    @FXML private Text statusText;
    @FXML private ProgressIndicator progressIndicator;
    
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    @FXML private Button retryFailedButton;
    
    @FXML private TextArea logArea;
    @FXML private Label failedRecordsLabel;
    
    private SimuladorBalanca simulator;
    private WeighHubClient client;
    private FallbackStorage fallbackStorage;
    private ScheduledExecutorService scheduler;
    private boolean isRunning = false;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    @FXML
    public void initialize() {
        fallbackStorage = new FallbackStorage();
        updateFailedRecordsCount();
        
        // Set default values
        scaleIdField.setText("SCALE-001");
        serverUrlField.setText("http://localhost:8080");
        targetWeightField.setText("15000");
        
        stopButton.setDisable(true);
        progressIndicator.setVisible(false);
        
        log("Sistema iniciado. Aguardando dados...");
    }
    
    @FXML
    private void handleStart() {
        if (isRunning) return;
        
        String scaleId = scaleIdField.getText().trim();
        String plate = plateField.getText().trim();
        String targetWeightStr = targetWeightField.getText().trim();
        String serverUrl = serverUrlField.getText().trim();
        
        if (scaleId.isEmpty() || plate.isEmpty() || targetWeightStr.isEmpty()) {
            showError("Preencha todos os campos obrigatórios!");
            return;
        }
        
        try {
            BigDecimal targetWeight = new BigDecimal(targetWeightStr);
            
            simulator = new SimuladorBalanca(scaleId, targetWeight);
            client = new WeighHubClient(serverUrl);
            
            isRunning = true;
            startButton.setDisable(true);
            stopButton.setDisable(false);
            resetButton.setDisable(true);
            progressIndicator.setVisible(true);
            
            log("Iniciando simulação de pesagem...");
            log("Balança: " + scaleId + " | Placa: " + plate + " | Peso alvo: " + targetWeight + " kg");
            
            startSimulation(plate);
            
        } catch (NumberFormatException e) {
            showError("Peso inválido! Use apenas números.");
        }
    }
    
    private void startSimulation(String plate) {
        scheduler = Executors.newScheduledThreadPool(1);
        
        scheduler.scheduleAtFixedRate(() -> {
            if (!isRunning) return;
            
            SimuladorBalanca.WeightReading reading = simulator.simulateNextReading();
            
            Platform.runLater(() -> {
                currentWeightText.setText(String.format("%.2f kg", reading.getWeight()));
                
                if (reading.isStabilized()) {
                    statusText.setText("✓ PESO ESTABILIZADO");
                    statusText.setFill(Color.GREEN);
                    
                    // Send to server
                    sendWeightToServer(reading.getScaleId(), plate, reading.getWeight());
                    
                    // Stop simulation
                    stopSimulation();
                } else {
                    statusText.setText("⚖ ESTABILIZANDO...");
                    statusText.setFill(Color.ORANGE);
                    log(String.format("Peso atual: %.2f kg", reading.getWeight()));
                }
            });
            
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
    
    private void sendWeightToServer(String scaleId, String plate, BigDecimal weight) {
        new Thread(() -> {
            log("Enviando dados ao servidor...");
            WeighHubClient.SendResult result = client.sendWeighingRecord(scaleId, plate, weight);
            
            Platform.runLater(() -> {
                if (result.isSuccess()) {
                    log("✓ Dados enviados com sucesso!");
                    statusText.setText("✓ ENVIADO AO SERVIDOR");
                    statusText.setFill(Color.GREEN);
                } else {
                    log("✗ Falha ao enviar: " + result.getMessage());
                    log("Salvando localmente...");
                    fallbackStorage.saveFailedWeighing(scaleId, plate, weight, result.getMessage());
                    updateFailedRecordsCount();
                    
                    statusText.setText("✗ ERRO - SALVO LOCALMENTE");
                    statusText.setFill(Color.RED);
                }
            });
        }).start();
    }
    
    @FXML
    private void handleStop() {
        stopSimulation();
    }
    
    private void stopSimulation() {
        isRunning = false;
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        
        Platform.runLater(() -> {
            startButton.setDisable(false);
            stopButton.setDisable(true);
            resetButton.setDisable(false);
            progressIndicator.setVisible(false);
            log("Simulação parada.");
        });
    }
    
    @FXML
    private void handleReset() {
        currentWeightText.setText("0.00 kg");
        statusText.setText("AGUARDANDO");
        statusText.setFill(Color.GRAY);
        plateField.clear();
        log("Sistema resetado.");
    }
    
    @FXML
    private void handleClearLog() {
        logArea.clear();
    }
    
    @FXML
    private void handleOpenFallbackFolder() {
        try {
            java.awt.Desktop.getDesktop().open(fallbackStorage.getFallbackDirectory());
        } catch (Exception e) {
            showError("Erro ao abrir pasta: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRetryFailed() {
        String serverUrl = serverUrlField.getText().trim();
        
        if (serverUrl.isEmpty()) {
            showError("Configure a URL do servidor antes de reenviar!");
            return;
        }
        
        retryFailedButton.setDisable(true);
        log("Iniciando reenvio de registros falhados...");
        
        new Thread(() -> {
            try {
                WeighHubClient retryClient = new WeighHubClient(serverUrl);
                List<FallbackStorage.FailedWeighing> failedWeighings = fallbackStorage.getAllFailedWeighings();
                
                if (failedWeighings.isEmpty()) {
                    Platform.runLater(() -> {
                        log("Nenhum registro falhado encontrado.");
                        retryFailedButton.setDisable(false);
                    });
                    return;
                }
                
                int total = failedWeighings.size();
                int success = 0;
                int failed = 0;
                
                log(String.format("Encontrados %d registros falhados. Reenviando...", total));
                
                for (FallbackStorage.FailedWeighing weighing : failedWeighings) {
                    String scaleId = weighing.getScaleId();
                    String plate = weighing.getPlate();
                    BigDecimal weight = weighing.getWeight();
                    
                    Platform.runLater(() -> 
                        log(String.format("Reenviando: %s | Placa: %s | Peso: %.2f kg", scaleId, plate, weight))
                    );
                    
                    WeighHubClient.SendResult result = retryClient.sendWeighingRecord(scaleId, plate, weight);
                    
                    if (result.isSuccess()) {
                        success++;
                        // Delete the file on success
                        if (fallbackStorage.deleteFile(weighing.getFile())) {
                            Platform.runLater(() -> 
                                log("✓ Enviado com sucesso e arquivo excluído!")
                            );
                        } else {
                            Platform.runLater(() -> 
                                log("✓ Enviado com sucesso, mas falha ao excluir arquivo")
                            );
                        }
                    } else {
                        failed++;
                        Platform.runLater(() -> 
                            log("✗ Falha ao reenviar: " + result.getMessage())
                        );
                    }
                    
                    // Small delay between retries
                    Thread.sleep(500);
                }
                
                final int finalSuccess = success;
                final int finalFailed = failed;
                
                Platform.runLater(() -> {
                    log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                    log(String.format("Reenvio concluído: %d sucesso, %d falha(s)", finalSuccess, finalFailed));
                    updateFailedRecordsCount();
                    retryFailedButton.setDisable(false);
                    
                    if (finalSuccess > 0) {
                        showInfo(String.format(
                            "Reenvio concluído!\n\nSucesso: %d\nFalha: %d\n\nArquivos enviados foram excluídos.",
                            finalSuccess, finalFailed
                        ));
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    log("✗ Erro ao reenviar registros: " + e.getMessage());
                    retryFailedButton.setDisable(false);
                    showError("Erro ao reenviar registros: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void log(String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        Platform.runLater(() -> {
            logArea.appendText("[" + timestamp + "] " + message + "\n");
        });
    }
    
    private void updateFailedRecordsCount() {
        int count = fallbackStorage.getFailedRecordsCount();
        Platform.runLater(() -> {
            failedRecordsLabel.setText("Registros não enviados: " + count);
        });
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
