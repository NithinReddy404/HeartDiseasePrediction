# CardioAI — Heart Disease Prediction System

An AI-powered web application that predicts heart disease risk using the **KNN (K-Nearest Neighbors)** algorithm trained on the Cleveland Heart Disease Dataset.

---

## Table of Contents
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the App](#running-the-app)
- [Using the Web Interface](#using-the-web-interface)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 18+ |
| Framework | Spring Boot 3.2.0 |
| Database | MySQL 8.0 |
| ML Algorithm | KNN (K-Nearest Neighbors) |
| Frontend | Single HTML file (Vanilla JS) |
| Build Tool | Maven |

---

## Prerequisites

Install these before anything else:

| Software | Download |
|---|---|
| Java JDK 18+ | https://jdk.java.net |
| IntelliJ IDEA (Community) | https://www.jetbrains.com/idea/download |
| MySQL Community Server 8.0 | https://dev.mysql.com/downloads/mysql/ |
| Google Chrome | https://www.google.com/chrome |

---

## Installation

### Step 1 — Clone the repository
```bash
git clone https://github.com/NithinReddy404/HeartDiseasePrediction.git
cd HeartDiseasePrediction
```

### Step 2 — Open in IntelliJ IDEA
1. Open IntelliJ IDEA
2. Click **File → Open**
3. Navigate to the `HeartDiseasePrediction` folder and click **OK**
4. IntelliJ will detect `pom.xml` — click **Load Maven Project**
5. Wait for dependencies to download (bottom progress bar)

### Step 3 — Set up MySQL
1. Install MySQL Community Server
2. During install, set a **root password** — remember it
3. MySQL will start automatically as a Windows service

### Step 4 — Update database password
Open `src/main/java/com/mycompany/heartdiseaseprediction/DBConnection.java`

Find this line and replace with your MySQL root password:
```java
private static final String PASSWORD = "YOUR_DB_PASSWORD_HERE";
```

### Step 5 — Fix the data file path
Open `HeartDiseasePrediction.java` and find the file path line, update it to match your machine:
```java
sc = new Scanner(new FileInputStream("C:/YOUR_PATH/processed.cleveland.data"));
```

---

## Running the App

### Option A — IntelliJ IDEA
1. Open `App.java`
2. Click the green **Run** button
3. Wait for: `Tomcat started on port 8080`

### Option B — Git Bash / Terminal
```bash
cd HeartDiseasePrediction
mvn spring-boot:run
```

> **First run only:** The app will build a `model.cache` file (~5-10 seconds). Every restart after that loads instantly from cache.

### Step — Open the frontend
1. Open `cardioai.html` in **Google Chrome**
2. The status dot in the top right should turn **green** ✅
3. If it stays red — make sure the Java backend is still running

---

## Using the Web Interface

### Patient Login
- Click **Register** to create an account → you get a unique **Patient ID**
- **Write down your Patient ID** — it cannot be recovered
- Login with Patient ID + password
- Enter your 13 clinical values → click **Run Prediction**

### Admin Login
- Click **Admin** tab on the login page
- Username: `admin`
- Password: `admin123`
- View all patients, search by ID, see predictions

---

## Project Structure

```
HeartDiseasePrediction/
├── src/main/java/com/mycompany/heartdiseaseprediction/
│   ├── App.java                      # Spring Boot entry point
│   ├── DBConnection.java             # MySQL connection setup
│   ├── HeartDiseasePrediction.java   # KNN algorithm core
│   ├── ModelCache.java               # Caches trained model to disk
│   ├── PatientController.java        # Patient API endpoints
│   ├── PredictionController.java     # Prediction API endpoint
│   ├── Patient.java                  # Patient model
│   ├── PatientInput.java             # API input DTO
│   ├── HeartDiseaseCompleteData.java # Complete record model
│   ├── HeartDiseaseMissingData.java  # Incomplete record model
│   └── processed.cleveland.data     # Cleveland dataset (303 records)
├── cardioai.html                     # Single-file web interface
├── pom.xml                           # Maven dependencies
└── model.cache                       # Auto-generated, do not commit
```

---

## API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/health` | Check if server is running |
| POST | `/api/patients/register` | Register new patient |
| POST | `/api/patients/login` | Patient login |
| GET | `/api/patients/{id}` | Get patient record |
| PUT | `/api/patients/{id}/clinical` | Save data + run prediction |
| GET | `/api/patients` | List all patients (admin) |

---

## Troubleshooting

| Problem | Fix |
|---|---|
| `Access denied for user root` | Wrong MySQL password in `DBConnection.java` |
| `File not found` for `.data` file | Update the hardcoded file path in `HeartDiseasePrediction.java` |
| Port 8080 already in use | Add `server.port=8081` to `application.properties` |
| Status dot stays red | Backend not running — start `App.java` first |
| `mvn: command not found` | Add Maven to PATH — see installation guide |
| First prediction is slow | Normal — building `model.cache`. Instant after that. |

---

## Disclaimer

This application is for **educational purposes only**. It is not a substitute for professional medical advice, diagnosis, or treatment.

---

## Model Performance

| Metric | Value |
|---|---|
| Algorithm | K-Nearest Neighbors |
| Dataset | Cleveland Heart Disease (303 records) |
| Training Size | 309 records (after KNN imputation) |
| Best K | 14 |
| Accuracy | 71.8% |
| Optimization | Simulated Annealing (74.4% peak) |
