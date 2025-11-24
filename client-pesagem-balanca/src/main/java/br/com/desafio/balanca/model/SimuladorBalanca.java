package main.java.br.com.desafio.balanca.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class SimuladorBalanca {
    
    private final Random random = new Random();
    private final BigDecimal targetWeight;
    private final String scaleId;
    private BigDecimal currentWeight;
    private int stabilizationCount = 0;
    private static final int STABILIZATION_THRESHOLD = 3;
    private static final BigDecimal VARIATION_FACTOR = new BigDecimal("50.0");
    
    public SimuladorBalanca(String scaleId, BigDecimal targetWeight) {
        this.scaleId = scaleId;
        this.targetWeight = targetWeight;
        // Inicia com um peso aleatório próximo ao alvo
        this.currentWeight = generateInitialWeight();
    }
    
    private BigDecimal generateInitialWeight() {
        // Gera um peso entre 70% e 130% do peso alvo
        double factor = 0.7 + (random.nextDouble() * 0.6);
        return targetWeight.multiply(BigDecimal.valueOf(factor))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    public WeightReading simulateNextReading() {
        if (isStabilized()) {
            return new WeightReading(scaleId, currentWeight, true);
        }
        
        // Calcula a diferença entre o peso atual e o alvo
        BigDecimal difference = targetWeight.subtract(currentWeight);
        
        // Reduz gradualmente a diferença com alguma variação
        BigDecimal adjustment = difference.multiply(BigDecimal.valueOf(0.3 + random.nextDouble() * 0.4));
        
        // Adiciona pequena variação aleatória
        BigDecimal variation = BigDecimal.valueOf(random.nextDouble() * VARIATION_FACTOR.doubleValue() - VARIATION_FACTOR.doubleValue() / 2);
        
        currentWeight = currentWeight.add(adjustment).add(variation)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Verifica se está próximo ao peso alvo (dentro de 1%)
        BigDecimal tolerance = targetWeight.multiply(BigDecimal.valueOf(0.01));
        if (currentWeight.subtract(targetWeight).abs().compareTo(tolerance) <= 0) {
            stabilizationCount++;
        } else {
            stabilizationCount = 0;
        }
        
        return new WeightReading(scaleId, currentWeight, false);
    }
    
    public boolean isStabilized() {
        return stabilizationCount >= STABILIZATION_THRESHOLD;
    }
    
    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }
    
    public String getScaleId() {
        return scaleId;
    }
    
    public void reset(BigDecimal newTargetWeight) {
        this.currentWeight = generateInitialWeight();
        this.stabilizationCount = 0;
    }
    
    public static class WeightReading {
        private final String scaleId;
        private final BigDecimal weight;
        private final boolean stabilized;
        
        public WeightReading(String scaleId, BigDecimal weight, boolean stabilized) {
            this.scaleId = scaleId;
            this.weight = weight;
            this.stabilized = stabilized;
        }
        
        public String getScaleId() {
            return scaleId;
        }
        
        public BigDecimal getWeight() {
            return weight;
        }
        
        public boolean isStabilized() {
            return stabilized;
        }
    }
}
