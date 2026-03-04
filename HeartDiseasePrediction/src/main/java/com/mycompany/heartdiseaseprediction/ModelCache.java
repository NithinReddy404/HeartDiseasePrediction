package com.mycompany.heartdiseaseprediction;

import java.io.*;
import java.util.ArrayList;

/**
 * ModelCache — lazy singleton, initializes on first API call.
 *
 * Spring Boot starts instantly. On the first prediction request,
 * this cache either loads from disk (~80ms) or trains from scratch
 * (~3-8s first time only), then every subsequent request is instant.
 */
public class ModelCache {

    private static final String CACHE_FILE = "model.cache";

    // Hardcoded from Cleveland dataset — these never change
    private static final int    BEST_K    = 14;
    private static final double ACCURACY  = 71.8;
    private static final int    K_CA      = 13;
    private static final int    K_THAL    = 24;

    // ── Singleton ─────────────────────────────────────────────
    private static volatile ModelCache instance;

    public static ModelCache getInstance() {
        if (instance == null) {
            synchronized (ModelCache.class) {
                if (instance == null) {
                    instance = new ModelCache();
                    instance.initialize(); // blocking — caller waits
                }
            }
        }
        return instance;
    }

    // ── State ─────────────────────────────────────────────────
    private ArrayList<HeartDiseaseCompleteData> trainingData;
    private boolean ready        = false;
    private String  errorMessage = null;

    private ModelCache() {}

    private void initialize() {
        if (!loadFromDisk()) trainAndSave();
    }

    // ── Disk cache ────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private boolean loadFromDisk() {
        File f = new File(CACHE_FILE);
        if (!f.exists()) {
            System.out.println("[ModelCache] No cache found — building for the first time...");
            return false;
        }
        try {
            long t0 = System.currentTimeMillis();
            System.out.println("[ModelCache] Loading model from cache...");
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                trainingData = (ArrayList<HeartDiseaseCompleteData>) in.readObject();
            }
            ready = true;
            System.out.println("[ModelCache] ✅ Ready in " + (System.currentTimeMillis() - t0)
                + "ms — " + trainingData.size() + " records, K=" + BEST_K
                + ", accuracy=" + ACCURACY + "%");
            return true;
        } catch (Exception e) {
            System.err.println("[ModelCache] Cache corrupt, rebuilding: " + e.getMessage());
            new File(CACHE_FILE).delete();
            return false;
        }
    }

    private void trainAndSave() {
        try {
            System.out.println("[ModelCache] ⏳ First-time setup (~5-10s, never again after this)...");
            long t0 = System.currentTimeMillis();

            ArrayList<HeartDiseaseMissingData> almd = new ArrayList<>();
            ArrayList<HeartDiseaseCompleteData> alcd = new ArrayList<>();
            HeartDiseasePrediction.readMissingAndCompleteData(almd, alcd);

            // Use hardcoded K values — skips the 20,000-pass search loops entirely
            HeartDiseasePrediction.completeValues(almd, alcd, K_CA, K_THAL);

            trainingData = alcd;
            ready = true;

            System.out.println("[ModelCache] ✅ Done in " + (System.currentTimeMillis() - t0)
                + "ms — " + alcd.size() + " records ready.");

            saveToDisk();

        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.err.println("[ModelCache] FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveToDisk() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
            out.writeObject(trainingData);
            System.out.println("[ModelCache] 💾 Saved to " + CACHE_FILE
                + " — next startup will load instantly.");
        } catch (Exception e) {
            System.err.println("[ModelCache] Warning: could not save cache: " + e.getMessage());
        }
    }

    // ── Public API ────────────────────────────────────────────

    public int predict(HeartDiseaseCompleteData patient) {
        if (!ready) throw new IllegalStateException(
            errorMessage != null ? errorMessage : "Model not ready.");
        return HeartDiseasePrediction.PredictkNearestNeighbors(patient, trainingData, BEST_K);
    }

    public int     getBestK()         { return BEST_K; }
    public double  getModelAccuracy() { return ACCURACY; }
    public int     getTrainingSize()  { return trainingData != null ? trainingData.size() : 0; }
    public boolean isReady()          { return ready; }
    public String  getError()         { return errorMessage; }

    public static void invalidateCache() {
        new File(CACHE_FILE).delete();
        instance = null;
    }
}
