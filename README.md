# Personal Finance Manager

A modern, robust backend REST API for Personal Finance Management built with **Spring Boot 3.2**, **Java 17**, **Spring Security**, and **Spring Data JPA**. The application uses **PostgreSQL** for persistent data storage and provides secure REST APIs to track transactions, manage categories, set savings goals, and generate financial reports.

---

## Live Demo

> **Base URL:** `https://personal-finance-manager-api-ys63.onrender.com/api`

The API is deployed on Render (free tier). The first request may take ~20–30 seconds to wake up the service.

---

## Features

- **User Authentication**: Secure register, login, and logout endpoints using Spring Security session-based authentication (cookie-based).
- **Category Management**: Create and delete custom categories to organize income and expenses. 7 default categories are seeded automatically on startup: `Salary`, `Food`, `Rent`, `Transportation`, `Entertainment`, `Healthcare`, `Utilities`.
- **Transaction Tracking**: Track incomes and expenses with filtering by category, date range. Supports soft-delete pattern for safe data removal.
- **Savings Goals**: Set goals with target amounts and dates, and track real-time progress against them. Progress is dynamically calculated from your net savings.
- **Financial Reports**: Get real-time aggregated reports for any month or year, with breakdown of income/expenses by category and net savings.
- **High Test Coverage**: Comprehensive test suite with **95 unit tests** and **>80% code coverage** measured by JaCoCo.

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| **Core Framework** | Spring Boot 3.2.0 (Java 17) |
| **Database** | PostgreSQL (production), H2 (tests) |
| **ORM** | Hibernate, Spring Data JPA |
| **Security** | Spring Security (Session-based auth, BCrypt password hashing) |
| **Build Tool** | Maven (wrapper included) |
| **Utilities** | Lombok |
| **Testing** | JUnit 5, Mockito, Spring Security Test, JaCoCo |
| **Deployment** | Render (Docker) |

---

## Getting Started

### Prerequisites

Ensure you have the following installed:
- **Java Development Kit (JDK) 17** or higher
- **PostgreSQL** (running on `localhost:5432`)
- **Maven** (optional — wrapper `./mvnw` is included)

### Database Setup

1. Start PostgreSQL and create the database:
   ```bash
   psql -U postgres
   CREATE DATABASE financedb;
   \q
   ```

2. Update the database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/financedb
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

> **Note**: Hibernate is configured with `ddl-auto=update`, so all tables are created/updated automatically on startup.

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/phanome/Personal_Finance_Manager.git
   cd Personal_Finance_Manager
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

### Verifying the Database

You can query PostgreSQL directly to see your data:
```bash
psql -d financedb -c "\dt"
psql -d financedb -c "SELECT * FROM transactions;"
psql -d financedb -c "SELECT * FROM categories;"
psql -d financedb -c "SELECT * FROM users;"
psql -d financedb -c "SELECT * FROM savings_goals;"
```

---

