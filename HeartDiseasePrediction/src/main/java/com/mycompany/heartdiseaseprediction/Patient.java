package com.mycompany.heartdiseaseprediction;

public class Patient {
    private int patientId;
    private String fullName;
    private String password;
    private Double age, sex, cp, trestbps, chol, fbs, restecg, thalach, exang, oldpeak, slope, ca, thal;
    private Integer prediction;
    private String createdAt;

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Double getAge() { return age; } public void setAge(Double age) { this.age = age; }
    public Double getSex() { return sex; } public void setSex(Double sex) { this.sex = sex; }
    public Double getCp() { return cp; } public void setCp(Double cp) { this.cp = cp; }
    public Double getTrestbps() { return trestbps; } public void setTrestbps(Double trestbps) { this.trestbps = trestbps; }
    public Double getChol() { return chol; } public void setChol(Double chol) { this.chol = chol; }
    public Double getFbs() { return fbs; } public void setFbs(Double fbs) { this.fbs = fbs; }
    public Double getRestecg() { return restecg; } public void setRestecg(Double restecg) { this.restecg = restecg; }
    public Double getThalach() { return thalach; } public void setThalach(Double thalach) { this.thalach = thalach; }
    public Double getExang() { return exang; } public void setExang(Double exang) { this.exang = exang; }
    public Double getOldpeak() { return oldpeak; } public void setOldpeak(Double oldpeak) { this.oldpeak = oldpeak; }
    public Double getSlope() { return slope; } public void setSlope(Double slope) { this.slope = slope; }
    public Double getCa() { return ca; } public void setCa(Double ca) { this.ca = ca; }
    public Double getThal() { return thal; } public void setThal(Double thal) { this.thal = thal; }
    public Integer getPrediction() { return prediction; } public void setPrediction(Integer prediction) { this.prediction = prediction; }
    public String getCreatedAt() { return createdAt; } public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
