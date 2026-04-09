# Fuel Consumption Calculator — Multi-Language with Database 
**Course:** OTP2 / SEP2 — AD 2026  
**Assignment:** Database-driven localization

---

## Overview

A JavaFX application that calculates fuel consumption and trip cost with multi-language support (English, French, Japanese, Persian/Farsi). Starting from Week 3, all UI strings are loaded from a MySQL database instead of `.properties` files, and every calculation is saved to the database.

---

## Technology Stack

- Java 21 + JavaFX 21
- MySQL / MariaDB
- `mysql-connector-j` 8.4.0
- Maven (build + packaging)
- JUnit 5 (testing)
- Mockito (mocking in tests)
- Docker + Kubernetes (deployment)
- SonarQube (code quality analysis and coverage reporting) 
- SpotBugs + Checkstyle + PMD + SonarAnalyzer ( Static code analysis tools)
- Jenkins (CI/CD pipeline)
- Xming (Windows X server for GUI in Docker/Kubernetes)

---

## Database Setup

### Step 1 — Create the database and tables

Run the provided schema file:

```bash
mysql -u root -p < schema.sql
```

This will:
- Create the `fuel_calculator_localization` database
- Create the `calculation_records` table (stores every user calculation)
- Create the `localization_strings` table (stores all UI text per language)
- Insert seed data for English, French, Japanese, and Persian

### Step 2 — Verify the data

```sql
USE fuel_calculator_localization;

-- Should return 10 rows per language (40 total)
SELECT language, COUNT(*) FROM localization_strings GROUP BY language;

-- Should be empty until you run a calculation
SELECT * FROM calculation_records;
```

---

## Database Schema

### `localization_strings` — UI text per language

```sql
CREATE TABLE localization_strings (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    `key`    VARCHAR(100) NOT NULL,
    value    VARCHAR(255) NOT NULL,
    language VARCHAR(10)  NOT NULL,
    UNIQUE KEY unique_key_lang (`key`, `language`)
);
```

| key | value (en) | value (fa) |
|-----|-----------|-----------|
| title | Fuel Consumption Calculator | ماشین‌حساب مصرف سوخت |
| distance | Distance (km): | مسافت (کیلومتر): |
| calculate | Calculate | محاسبه |
| ... | ... | ... |

### `calculation_records` — saved calculations

```sql
CREATE TABLE calculation_records (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    distance    DOUBLE      NOT NULL,
    consumption DOUBLE      NOT NULL,
    price       DOUBLE      NOT NULL,
    total_fuel  DOUBLE      NOT NULL,
    total_cost  DOUBLE      NOT NULL,
    language    VARCHAR(10),
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);
```

---

## Database Connection Configuration

Connection is configured in `DatabaseConnection.java`:

```java
// Running locally (IntelliJ):
private static final String URL = "jdbc:mysql://localhost:3306/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8";

// Running inside Docker:
private static final String URL = "jdbc:mysql://host.docker.internal:3306/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8";

private static final String USER     = "root";
private static final String PASSWORD = "your_password";
```

Change `USER` and `PASSWORD` to match your local MySQL setup.

---

## How to Run Locally

```bash
# 1. Clone the repository
git clone https://github.com/taifjalo/otp2-fuel-calculator-localization.git
cd otp2-fuel-calculator-localization

# 2. Set up the database
mysql -u root -p < schema.sql

# 3. Build the project
mvn clean package

# 4. Run the application
mvn javafx:run
```

---

## How to Run with Docker

```bash
# 1. Build the image
docker build -t taifjalo1/otp2-fuel-calculator-localization:latest .

# 2. Run the container
# On Windows — set DISPLAY for Xming first:
# PS> .\Xming.exe :0 -ac -multiwindow -clipboard
docker run --rm -e DISPLAY=host.docker.internal:0 taifjalo1/otp2-fuel-calculator-localization:latest
```

---

## How to Run with Kubernetes (Minikube)

```bash
# 1. Start Xming (Windows)
PS C:\Program Files (x86)\Xming> .\Xming.exe :0 -ac -multiwindow -clipboard

# 2. Set Minikube Docker environment
minikube start
minikube -p minikube docker-env --shell powershell | Invoke-Expression

# 3. Build image inside Minikube
docker build -t taifjalo1/otp2-fuel-calculator-localization:latest .

# 4. Deploy
kubectl apply -f fuelconsumption_deployment.yaml
kubectl get pods

# 5. Delete pod
kubectl delete pod <pod-name>
```

---

## How Localization Works

**Week 2 (old approach):**
```
.properties files → ResourceBundle → UI labels
```

**Week 3 (new approach):**
```
MySQL localization_strings table → LocalizationService (with cache) → UI labels
```

When the user clicks a language button:
1. `LocalizationService.loadStrings("fr")` queries the DB
2. Results are cached in memory — no DB hit on repeat visits
3. All labels update instantly
4. For Persian (`fa`), `NodeOrientation.RIGHT_TO_LEFT` is applied to the root VBox

---

## Supported Languages

| Button | Language | Code | Direction |
|--------|----------|------|-----------|
| EN | English | `en` | LTR |
| FR | French | `fr` | LTR |
| JP | Japanese | `ja` | LTR |
| IR | Persian (Farsi) | `fa` | RTL |