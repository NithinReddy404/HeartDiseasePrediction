package com.mycompany.heartdiseaseprediction;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    // ── REGISTER ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String fullName = body.get("fullName");
            String password = body.get("password");
            if (fullName == null || password == null) {
                res.put("status", "error"); res.put("message", "fullName and password required");
                return ResponseEntity.badRequest().body(res);
            }
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO patients (full_name, password) VALUES (?, ?)",
                java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, fullName); ps.setString(2, password);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            int patientId = keys.getInt(1);
            res.put("status", "success"); res.put("patientId", patientId); res.put("fullName", fullName);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("status", "error"); res.put("message", e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }

    // ── LOGIN ─────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String patientId = body.get("patientId");
            String password  = body.get("password");
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT patient_id, full_name, age FROM patients WHERE patient_id=? AND password=?"
            );
            ps.setInt(1, Integer.parseInt(patientId)); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                res.put("status",          "success");
                res.put("patientId",       rs.getInt("patient_id"));
                res.put("fullName",        rs.getString("full_name"));
                res.put("hasClinicalData", rs.getObject("age") != null);
            } else {
                res.put("status",  "error");
                res.put("message", "Invalid Patient ID or password.");
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("status", "error"); res.put("message", e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }

    // ── GET PATIENT BY ID ─────────────────────────────────────
    @GetMapping("/{patientId}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable int patientId) {
        Map<String, Object> res = new HashMap<>();
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE patient_id=?");
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                res.put("status", "error"); res.put("message", "Patient not found");
                return ResponseEntity.status(404).body(res);
            }
            res.put("status",    "success");
            res.put("patientId", rs.getInt("patient_id"));
            res.put("fullName",  rs.getString("full_name"));
            res.put("age",       rs.getObject("age"));
            res.put("sex",       rs.getObject("sex"));
            res.put("cp",        rs.getObject("cp"));
            res.put("trestbps",  rs.getObject("trestbps"));
            res.put("chol",      rs.getObject("chol"));
            res.put("fbs",       rs.getObject("fbs"));
            res.put("restecg",   rs.getObject("restecg"));
            res.put("thalach",   rs.getObject("thalach"));
            res.put("exang",     rs.getObject("exang"));
            res.put("oldpeak",   rs.getObject("oldpeak"));
            res.put("slope",     rs.getObject("slope"));
            res.put("ca",        rs.getObject("ca"));
            res.put("thal",      rs.getObject("thal"));
            res.put("prediction",rs.getObject("prediction"));
            res.put("createdAt", rs.getString("created_at"));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("status", "error"); res.put("message", e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }

    // ── SAVE CLINICAL DATA + RUN PREDICTION ───────────────────
    @PutMapping("/{patientId}/clinical")
    public ResponseEntity<Map<String, Object>> saveClinical(
            @PathVariable int patientId,
            @RequestBody PatientInput input) {

        Map<String, Object> res = new HashMap<>();
        try {
            ModelCache cache = ModelCache.getInstance();

            if (!cache.isReady()) {
                res.put("status", "error");
                res.put("message", "Model still initializing, please wait a moment and try again.");
                return ResponseEntity.status(503).body(res);
            }

            // Build patient object
            HeartDiseaseCompleteData patient = new HeartDiseaseCompleteData(
                input.getAge(), input.getSex(), input.getCp(), input.getTrestbps(),
                input.getChol(), input.getFbs(), input.getRestecg(), input.getThalach(),
                input.getExang(), input.getOldpeak(), input.getSlope(), input.getCa(),
                input.getThal(), 0
            );

            // Instant prediction — uses cached training data, no re-training
            int prediction = cache.predict(patient);
            double accuracy = cache.getModelAccuracy();
            int bestK = cache.getBestK();

            // Save to DB
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE patients SET age=?,sex=?,cp=?,trestbps=?,chol=?,fbs=?,restecg=?," +
                "thalach=?,exang=?,oldpeak=?,slope=?,ca=?,thal=?,prediction=? WHERE patient_id=?"
            );
            ps.setDouble(1,  input.getAge());     ps.setDouble(2,  input.getSex());
            ps.setDouble(3,  input.getCp());      ps.setDouble(4,  input.getTrestbps());
            ps.setDouble(5,  input.getChol());    ps.setDouble(6,  input.getFbs());
            ps.setDouble(7,  input.getRestecg()); ps.setDouble(8,  input.getThalach());
            ps.setDouble(9,  input.getExang());   ps.setDouble(10, input.getOldpeak());
            ps.setDouble(11, input.getSlope());   ps.setDouble(12, input.getCa());
            ps.setDouble(13, input.getThal());    ps.setInt(14,    prediction);
            ps.setInt(15, patientId);
            ps.executeUpdate();

            res.put("status",        "success");
            res.put("prediction",    prediction);
            res.put("hasDisease",    prediction >= 1);
            res.put("bestK",         bestK);
            res.put("modelAccuracy", Math.round(accuracy * 10000.0) / 100.0);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.put("status", "error"); res.put("message", e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }

    // ── LIST ALL PATIENTS (admin) ──────────────────────────────
    @GetMapping
    public ResponseEntity<Map<String, Object>> listAll() {
        Map<String, Object> res = new HashMap<>();
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT patient_id, full_name, age, prediction, created_at FROM patients ORDER BY created_at DESC"
            );
            ResultSet rs = ps.executeQuery();
            List<Map<String,Object>> patients = new ArrayList<>();
            while (rs.next()) {
                Map<String,Object> p = new HashMap<>();
                p.put("patientId",  rs.getInt("patient_id"));
                p.put("fullName",   rs.getString("full_name"));
                p.put("age",        rs.getObject("age"));
                p.put("prediction", rs.getObject("prediction"));
                p.put("createdAt",  rs.getString("created_at"));
                patients.add(p);
            }
            res.put("status", "success");
            res.put("patients", patients);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("status", "error"); res.put("message", e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }
}
