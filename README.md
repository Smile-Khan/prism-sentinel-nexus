# The Prism x Sentinel Nexus

**Industrial-Grade Distributed Ticketing & Discovery Ecosystem**

## 🏗 Architecture
This Monorepo merges two flagship engines into a unified Event-Driven Architecture:

1.  **Sentinel (Write-Engine):** High-Concurrency Ticketing System.
    *   *Tech:* Spring Boot 3.4, Java 21 (Virtual Threads), Redis (Redisson Locking), PostgreSQL.
    *   *Role:* Handles "Flash Sale" traffic with ACID guarantees.
2.  **Prism (Read-Engine):** Real-Time Discovery Hub.
    *   *Tech:* Elasticsearch 8.11, Debezium CDC, Kafka.
    *   *Role:* Provides sub-millisecond fuzzy search and HTML highlighting.

## 🚀 Infrastructure (Docker Compose)
*   **Event Backbone:** Kafka + Zookeeper (Shared)
*   **Sentinel Core:** Postgres (Port 5432) + Redis (Port 6379)
*   **Prism Core:** Postgres (Port 5433 - Logical WAL) + Elasticsearch (Port 9200) + Debezium (Port 8083)
*   **Observability:** Prometheus + Grafana

## 🛠 Project Structure
*   `/sentinel-booking-service`: The Ticket Producer.
*   `/prism-command-service`: The Ingestor (Write-Side).
*   `/prism-search-service`: The Projector (Read-Side).