package main.java.br.com.desafio.balanca.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FallbackStorage {
    
    private static final Logger logger = LoggerFactory.getLogger(FallbackStorage.class);
    private static final String FALLBACK_DIR = "failed_weighings";
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    
    private final Gson gson;
    private final File fallbackDirectory;
    
    public FallbackStorage() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.fallbackDirectory = new File(FALLBACK_DIR);
        
        if (!fallbackDirectory.exists()) {
            boolean created = fallbackDirectory.mkdirs();
            if (created) {
                logger.info("Created fallback directory: {}", fallbackDirectory.getAbsolutePath());
            }
        }
    }
    
    public void saveFailedWeighing(String scaleId, String plate, BigDecimal weight, String errorMessage) {
        try {
            Map<String, Object> record = new HashMap<>();
            record.put("scaleId", scaleId);
            record.put("plate", plate);
            record.put("weight", weight);
            record.put("timestamp", LocalDateTime.now().toString());
            record.put("error", errorMessage);
            record.put("status", "FAILED");
            
            String filename = String.format("weighing_%s_%s.json", 
                    scaleId, 
                    LocalDateTime.now().format(FILENAME_FORMATTER));
            File file = new File(fallbackDirectory, filename);
            
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(record, writer);
                logger.info("Saved failed weighing to: {}", file.getAbsolutePath());
            }
            
        } catch (IOException e) {
            logger.error("Failed to save weighing record to fallback storage", e);
        }
    }
    
    public int getFailedRecordsCount() {
        File[] files = fallbackDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        return files != null ? files.length : 0;
    }
    
    public File getFallbackDirectory() {
        return fallbackDirectory;
    }
    
    public List<FailedWeighing> getAllFailedWeighings() {
        List<FailedWeighing> failedWeighings = new ArrayList<>();
        File[] files = fallbackDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    Map<String, Object> data = gson.fromJson(reader, Map.class);
                    
                    String scaleId = (String) data.get("scaleId");
                    String plate = (String) data.get("plate");
                    
                    // Handle weight as either Double or String
                    BigDecimal weight;
                    Object weightObj = data.get("weight");
                    if (weightObj instanceof Double) {
                        weight = BigDecimal.valueOf((Double) weightObj);
                    } else if (weightObj instanceof String) {
                        weight = new BigDecimal((String) weightObj);
                    } else {
                        weight = new BigDecimal(weightObj.toString());
                    }
                    
                    String error = (String) data.get("error");
                    
                    failedWeighings.add(new FailedWeighing(file, scaleId, plate, weight, error));
                    
                } catch (Exception e) {
                    logger.error("Failed to read weighing file: {}", file.getName(), e);
                }
            }
        }
        
        return failedWeighings;
    }
    
    public boolean deleteFile(File file) {
        if (file.delete()) {
            logger.info("Deleted file: {}", file.getName());
            return true;
        } else {
            logger.error("Failed to delete file: {}", file.getName());
            return false;
        }
    }
    
    public static class FailedWeighing {
        private final File file;
        private final String scaleId;
        private final String plate;
        private final BigDecimal weight;
        private final String error;
        
        public FailedWeighing(File file, String scaleId, String plate, BigDecimal weight, String error) {
            this.file = file;
            this.scaleId = scaleId;
            this.plate = plate;
            this.weight = weight;
            this.error = error;
        }
        
        public File getFile() {
            return file;
        }
        
        public String getScaleId() {
            return scaleId;
        }
        
        public String getPlate() {
            return plate;
        }
        
        public BigDecimal getWeight() {
            return weight;
        }
        
        public String getError() {
            return error;
        }
    }
}
