package com.mycompany.heartdiseaseprediction;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PredictionController {

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody PatientInput input) {

        Map<String, Object> response = new HashMap<>();

        try {
            ModelCache cache = ModelCache.getInstance();

            if (!cache.isReady()) {
                response.put("status", "error");
                response.put("message", "Model not ready yet: " + cache.getError());
                return ResponseEntity.status(503).body(response);
            }

            // Build patient object from input
            HeartDiseaseCompleteData newPatient = new HeartDiseaseCompleteData(
                input.getAge(), input.getSex(), input.getCp(),
                input.getTrestbps(), input.getChol(), input.getFbs(),
                input.getRestecg(), input.getThalach(), input.getExang(),
                input.getOldpeak(), input.getSlope(), input.getCa(),
                input.getThal(), 0
            );

            // Instant prediction — no training, no loops
            int prediction = cache.predict(newPatient);

            response.put("prediction",    prediction);
            response.put("hasDisease",    prediction >= 1);
            response.put("bestK",         cache.getBestK());
            response.put("modelAccuracy", Math.round(cache.getModelAccuracy() * 10000.0) / 100.0);
            response.put("trainingSize",  cache.getTrainingSize());
            response.put("status",        "success");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> res = new HashMap<>();
        ModelCache cache = ModelCache.getInstance();
        res.put("status",  cache.isReady() ? "running" : "initializing");
        res.put("service", "Heart Disease Prediction API");
        res.put("model",   cache.isReady() ? "ready" : "loading");
        return ResponseEntity.ok(res);
    }
}
