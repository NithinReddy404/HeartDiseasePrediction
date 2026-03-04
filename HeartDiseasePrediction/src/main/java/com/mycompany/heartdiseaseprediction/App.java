package com.mycompany.heartdiseaseprediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        // Setup DB tables (fast - skips if already exists)
        DBConnection.createDatabase();
        DBConnection.createHeartDiseaseData();

        // Start Spring Boot FIRST — this is a blocking call that keeps the JVM alive
        // ModelCache will be triggered on the first API request instead
        SpringApplication.run(App.class, args);
    }
}
