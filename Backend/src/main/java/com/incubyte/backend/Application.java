package com.incubyte.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(Application.class, args);
    }

    
    private static void loadDotEnv() {
        try {
            var envPath = Paths.get("/Backend/.env");
            if (Files.exists(envPath)) {
                List<String> lines = Files.readAllLines(envPath);
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int equalsIdx = line.indexOf('=');
                    if (equalsIdx > 0) {
                        String key = line.substring(0, equalsIdx).trim();
                        String value = line.substring(equalsIdx + 1).trim();
                        
                        if ((value.startsWith("\"") && value.endsWith("\"")) || 
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        
                        System.setProperty(key, value);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to read .env file: " + e.getMessage());
        }
    }
}

