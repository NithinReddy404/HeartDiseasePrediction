package com.mycompany.heartdiseaseprediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "heartdiseasedb";
    private static final String USER = "root";
    private static final String PASSWORD = "*****";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Connection error: " + e);
            return null;
        }
    }

    public static void createDatabase() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement st = con.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database created/exists.");
        } catch (Exception e) {
            System.out.println("Error creating database: " + e);
        }
    }

    public static void createHeartDiseaseData() {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            // Original heart data table
            String sql = "CREATE TABLE IF NOT EXISTS heartdata ("
                    + "age DOUBLE, sex DOUBLE, cp DOUBLE, trestbps DOUBLE, chol DOUBLE,"
                    + "fbs DOUBLE, restecg DOUBLE, thalach DOUBLE, exang DOUBLE,"
                    + "oldpeak DOUBLE, slope DOUBLE, ca VARCHAR(5), thal VARCHAR(5), num INT"
                    + ")";
            st.executeUpdate(sql);

            // Patients table with name, password, patient ID, and clinical data
            String patients = "CREATE TABLE IF NOT EXISTS patients ("
                    + "patient_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "full_name VARCHAR(100) NOT NULL,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "age DOUBLE DEFAULT NULL,"
                    + "sex DOUBLE DEFAULT NULL,"
                    + "cp DOUBLE DEFAULT NULL,"
                    + "trestbps DOUBLE DEFAULT NULL,"
                    + "chol DOUBLE DEFAULT NULL,"
                    + "fbs DOUBLE DEFAULT NULL,"
                    + "restecg DOUBLE DEFAULT NULL,"
                    + "thalach DOUBLE DEFAULT NULL,"
                    + "exang DOUBLE DEFAULT NULL,"
                    + "oldpeak DOUBLE DEFAULT NULL,"
                    + "slope DOUBLE DEFAULT NULL,"
                    + "ca DOUBLE DEFAULT NULL,"
                    + "thal DOUBLE DEFAULT NULL,"
                    + "prediction INT DEFAULT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            st.executeUpdate(patients);

            System.out.println("Tables created/exist.");
        } catch (Exception e) {
            System.out.println("Error creating tables: " + e);
        }
    }
}
