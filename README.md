# Gym crm

Gym CRM System is a microservices-based application for managing gyms, trainers, trainees, and workouts.  
The system is built with **Java 17**, **Spring Boot**, **PostgreSQL**, **ActiveMQ**, **Spring Cloud**, **MongoDB** and **Docker**.

---

## Services

| Service             | Port | Description | Database   |
|---------------------|------|-------------|------------|
| `discovery-service` | 8761 | Eureka Server for service discovery | -          |
| `api-gateway`       | 8765 | API Gateway with routing and JWT validation | -          |
| `gca-core-service`  | 8080 | Core business logic, authentication, JWT, database operations | PostgreSQL |
| `workload-service`  | 8081 | Trainer workload management via JMS and REST | MongoDB    |

---

# Running with Docker Compose (Recommended)

## Prerequisites (Docker)

* **Git**
* **Docker** та **Docker Compose**
* **Java Development Kit (JDK) 17**
* **Maven**

### 1. Cloning a repository

```bash
git clone https://github.com/ArturGoz/gym-crm
```

### 2. Building Docker images

```bash
cd gym-crm

docker build -t discovery-service:latest -f discovery-service/Dockerfile .
docker build -t workload-service:latest -f workload-service/Dockerfile .
docker build -t gca-core-service:latest -f gca-core-service/Dockerfile .
docker build -t api-gateway-service:latest -f api-gateway-service/Dockerfile .
```

### 3. Start

```bash
docker compose up -d
```

### 4. Stop

```bash
docker compose down
```

# Manual setup (Without Docker)

## Prerequisites

To run this application, you should have the following installed:

- **Java Development Kit (JDK) 17**
- **Maven**
- **Git**
- **PostgreSQL 13+**
- **Apache ActiveMQ**
- **MongoDB**

---

## Setup and Installation

### 1. Clone & Build

```bash
git clone https://github.com/ArturGoz/gym-crm
```

# 2. Global environment variables

### General
- JWT_SECRET=gym-crm-application-SECRETKEY123!@#

### Core service (Postgres)
- DB_URL=jdbc:postgresql://localhost:5432/gym
- DB_USERNAME=gca
- DB_PASSWORD=gca

## Workload Service (MongoDB)
- MONGODB_URI = mongodb://localhost:27017/gymdb

### ActiveMQ
- ACTIVEMQ_BROKER_URL=tcp://localhost:61616
- ACTIVEMQ_USER=admin
- ACTIVEMQ_PASSWORD=admin

# 3. Database Initialization

For Core service (Postgres):

```sql
CREATE DATABASE "gym";
CREATE USER gca WITH PASSWORD 'gca';
GRANT ALL PRIVILEGES ON DATABASE "gym" TO gca;
```

For Workload Service (MongoDB):

```nosql
use gymdb;
db.createCollection("trainer_workloads");
```

# 4. Service Startup Order (Important!)

1. **Start PostgreSQL** (if not already running):
   ```bash
   # Ubuntu/Debian
   sudo systemctl start postgresql
   
   # macOS
   brew services start postgresql
   
   # Windows - use Services panel or PostgreSQL service
   ```
2. **Start MongoDB**
   ```bash
   # Ubuntu/Debian
   sudo systemctl start mongod

   # macOS
   brew services start mongodb-community

   # Windows - use Services panel or `mongodb` command
   ```

3. **Start ActiveMQ**

   Navigate to the ActiveMQ directory and start the broker:
   ```bash
   # Windows
   bin\activemq.bat start
   
   # Linux/macOS
   bin/activemq start
   ```
   - ActiveMQ will start on default port `61616` (broker) and `8161` (web console)
   - Access the web console at `http://localhost:8161/admin` (default credentials: admin/admin)

4. **Start Discovery Server** (Eureka Server) - **MUST BE FIRST**:
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```
   Access at: `http://localhost:8761`

5. **Start Core Service**:
   ```bash
   cd gca-core-service
   mvn spring-boot:run
   ```
    - Database migrations will run automatically via Liquibase
    - Service will register with Eureka
    - Access at: `http://localhost:8080`

6. **Start Workload Service**:
   ```bash
   cd workload-service
   mvn spring-boot:run
   ```
    - Uses H2 in-memory database (auto-configured)
    - Service will register with Eureka
    - Access at: `http://localhost:8081`

7. **Start Gateway Service** - **MUST BE LAST**:
   ```bash
   cd gateway-service
   mvn spring-boot:run
   ```
    - API Gateway available at: `http://localhost:8765`
    - Routes requests to registered services

---