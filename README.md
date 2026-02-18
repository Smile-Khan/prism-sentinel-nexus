# 🌐 Prism × Sentinel Nexus Ecosystem

**Industrial-Grade Distributed Architecture for High-Concurrency Ticketing & Discovery**

The **Nexus Ecosystem** is a high-scale, production-oriented distributed system designed to address **real-time seat reservation** and **sub-millisecond data discovery** at scale.  
It leverages **CQRS (Command Query Responsibility Segregation)**, **event-driven architecture**, and **Change Data Capture (CDC)** to ensure scalability, consistency, and resilience under extreme load.

---

## 🏗️ Architectural Overview

The system decouples write-heavy transactional workflows from read-optimized discovery pipelines.

### 🔄 System Flow

1. **Write Path – Sentinel**
   - Handles high-velocity seat reservations
   - Uses **Redisson Distributed Locks** to prevent race conditions during flash sales

2. **Event Backbone – Kafka**
   - Emits asynchronous domain events after successful transactions
   - Ensures loose coupling between system components

3. **Synchronization – Debezium CDC**
   - Monitors PostgreSQL **Write-Ahead Logs (WAL)**
   - Streams row-level changes in near real time

4. **Read Path – Prism**
   - Consumes CDC events
   - Projects data into **Elasticsearch** for fast, flexible discovery
   - Supports fuzzy search and result highlighting

---

## 🚀 Technology Stack

### Backend & Runtime
- Java 21 (Virtual Threads / Project Loom)
- Spring Boot 3.4.2

### Data & Messaging
- PostgreSQL 16 (Source of Truth)
- Redis (Distributed Locking)
- Apache Kafka
- Debezium (Change Data Capture)

### Search & Discovery
- Elasticsearch 8.11 (Full-text search, highlighting)

### Observability & Docs
- Prometheus
- Grafana
- Micrometer
- OpenAPI 3.0 (Swagger UI)

---

## 🛠️ Key Features

- **Flash Sale Resilience**
  - Prevents over-selling using Redis-based distributed locks

- **Transactional Integrity**
  - Kafka events are published only after DB commits
  - Ensures HikariCP connections are released before network I/O

- **Advanced Discovery**
  - Native Elasticsearch Query DSL
  - Fuzzy matching (e.g., `Resrvation` → `Reservation`)
  - Match highlighting using `<em>` tags

- **Fault Tolerance**
  - Retryable Topics for transient failures
  - Dead Letter Topics (DLT) for poison messages

- **Industrial Hygiene**
  - Monorepo with centralized dependency management
  - Auto-generated Swagger documentation
  - Clean separation of write and read responsibilities

---

## 🚦 Getting Started

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- Docker & Docker Compose

---

### 🧱 Infrastructure Startup

Spin up Kafka, PostgreSQL, Redis, Elasticsearch, and Debezium:

```bash
docker-compose up -d
```

### 🔁 Debezium Connector Registration (CDC)
```
curl -X POST http://localhost:8083/connectors/ \
  -H "Content-Type: application/json" \
  -d @infrastructure/debezium-config.json
```

### 📊 Observability & Access Points

Prometheus Targets:
http://localhost:9090/targets

Swagger UI (Search Service):
http://localhost:8082/swagger-ui/index.html

Search API Endpoint:
POST /api/v1/discovery/search

### 👤 Lead Architect

**Pathan Ismailkhan (Smile-Khan)**

Java Backend Engineer | Distributed Systems | Event-Driven Architectures
