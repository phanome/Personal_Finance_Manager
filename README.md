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

## API Documentation

All request and response bodies use JSON. Authentication is required for all endpoints except `/api/auth/register` and `/api/auth/login`.

### 1. Authentication

#### Register a new user
**POST** `/api/auth/register`
```json
{
  "username": "demo@example.com",
  "password": "Demo1234!",
  "fullName": "John Doe",
  "phoneNumber": "1234567890"
}
```
**Response** (`201 Created`):
```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

#### Login
**POST** `/api/auth/login`
```json
{
  "username": "demo@example.com",
  "password": "Demo1234!"
}
```
**Response** (`200 OK`): Sets a `JSESSIONID` cookie for session-based auth.
```json
{
  "message": "Login successful"
}
```

#### Logout
**POST** `/api/auth/logout`

**Response** (`200 OK`):
```json
{
  "message": "Logout successful"
}
```

---

### 2. Categories

#### List all categories
**GET** `/api/categories`

**Response** (`200 OK`):
```json
{
  "categories": [
    { "name": "Salary", "type": "INCOME", "custom": false },
    { "name": "Food", "type": "EXPENSE", "custom": false },
    { "name": "Rent", "type": "EXPENSE", "custom": false }
  ]
}
```

#### Create a custom category
**POST** `/api/categories`
```json
{
  "name": "Gym & Fitness",
  "type": "EXPENSE"
}
```
**Response** (`201 Created`):
```json
{
  "name": "Gym & Fitness",
  "type": "EXPENSE",
  "custom": true
}
```

#### Delete a custom category
**DELETE** `/api/categories/{name}`

> **Note**: Default system categories cannot be deleted. A category cannot be deleted if it is referenced by active transactions.

---

### 3. Transactions

#### List transactions
**GET** `/api/transactions`

Optional query parameters: `startDate` (YYYY-MM-DD), `endDate` (YYYY-MM-DD), `categoryId`, `category`

**Response** (`200 OK`):
```json
{
  "transactions": [
    {
      "id": 1,
      "amount": 50000.0,
      "description": "May Salary",
      "date": "2026-05-01",
      "type": "INCOME",
      "category": "Salary"
    }
  ]
}
```

#### Create a transaction
**POST** `/api/transactions`
```json
{
  "amount": 2000,
  "description": "Electricity bill",
  "date": "2026-05-15",
  "category": "Utilities"
}
```
> **Note**: The transaction type (INCOME/EXPENSE) is determined automatically from the category. Date cannot be in the future.

#### Update a transaction
**PUT** `/api/transactions/{id}`
```json
{
  "amount": 2500,
  "description": "Updated electricity bill",
  "category": "Utilities"
}
```
> **Note**: The transaction date is immutable and cannot be changed.

#### Delete a transaction (soft-delete)
**DELETE** `/api/transactions/{id}`

---

### 4. Savings Goals

#### List all goals
**GET** `/api/goals`

**Response** (`200 OK`):
```json
{
  "goals": [
    {
      "id": 1,
      "goalName": "New Laptop",
      "targetAmount": 80000.0,
      "targetDate": "2026-12-31",
      "startDate": "2026-05-24",
      "currentProgress": 48500.0,
      "progressPercentage": 60.63,
      "remainingAmount": 31500.0
    }
  ]
}
```

#### Create a goal
**POST** `/api/goals`
```json
{
  "goalName": "New Laptop",
  "targetAmount": 80000,
  "targetDate": "2026-12-31",
  "startDate": "2026-01-01"
}
```
> **Note**: `startDate` is optional and defaults to today. `targetDate` must be in the future.

#### Get a specific goal
**GET** `/api/goals/{id}`

#### Update a goal
**PUT** `/api/goals/{id}`
```json
{
  "targetAmount": 90000,
  "targetDate": "2027-06-30"
}
```

#### Delete a goal
**DELETE** `/api/goals/{id}`

---

### 5. Reports

#### Monthly report
**GET** `/api/reports/monthly/{year}/{month}`

Example: `GET /api/reports/monthly/2026/5`

**Response** (`200 OK`):
```json
{
  "month": 5,
  "year": 2026,
  "totalIncome": {
    "Salary": 50000.0
  },
  "totalExpenses": {
    "Food": 1500.0,
    "Utilities": 2000.0
  },
  "netSavings": 46500.0
}
```

#### Yearly report
**GET** `/api/reports/yearly/{year}`

Example: `GET /api/reports/yearly/2026`

**Response** (`200 OK`):
```json
{
  "year": 2026,
  "totalIncome": {
    "Salary": 600000.0
  },
  "totalExpenses": {
    "Food": 18000.0,
    "Utilities": 24000.0
  },
  "netSavings": 558000.0
}
```

---

## Testing & Coverage

### Unit Tests

To run the full test suite and generate a JaCoCo coverage report:
```bash
./mvnw clean test
```

**Results:**
```
Tests run: 95, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The HTML coverage report is generated at `target/site/jacoco/index.html`.

### End-to-End Tests

An E2E test script (`financial_manager_tests.sh`) is included in the repository to validate the full API against any deployment:

```bash
# Run against local server
./financial_manager_tests.sh "http://localhost:8080/api"

# Run against live Render deployment
./financial_manager_tests.sh "https://personal-finance-manager-api-ys63.onrender.com/api"
```

**Live E2E Results:**
```
Base URL: https://personal-finance-manager-api-ys63.onrender.com/api
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%
```

---

## Quick Start with cURL

```bash
BASE_URL="https://personal-finance-manager-api-ys63.onrender.com/api"

# 1. Register a user
curl -s -X POST $BASE_URL/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"demo@example.com","password":"Demo1234!","fullName":"Demo User","phoneNumber":"1234567890"}'

# 2. Login (saves session cookie)
curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"username":"demo@example.com","password":"Demo1234!"}'

# 3. List categories
curl -s -b cookies.txt $BASE_URL/categories

# 4. Add a transaction
curl -s -X POST $BASE_URL/transactions \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"amount":50000,"description":"May Salary","date":"2026-05-01","category":"Salary"}'

# 5. View monthly report
curl -s -b cookies.txt $BASE_URL/reports/monthly/2026/5

# 6. Create a savings goal
curl -s -X POST $BASE_URL/goals \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"goalName":"New Laptop","targetAmount":80000,"targetDate":"2026-12-31"}'
```

---

## Project Structure

```
src/main/java/com/finance/manager/
├── config/                 # Security config, data initializer
├── controller/             # REST controllers (Auth, Category, Transaction, Goal, Report)
├── dto/
│   ├── request/            # Request DTOs with validation
│   └── response/           # Response DTOs
├── entity/                 # JPA entities (User, Category, Transaction, SavingsGoal)
├── enums/                  # TransactionType (INCOME, EXPENSE)
├── exception/              # Custom exceptions + global handler
├── repository/             # Spring Data JPA repositories
├── security/               # CustomUserDetails, CustomUserDetailsService
└── service/                # Business logic layer

src/test/java/com/finance/manager/
├── controller/             # Controller unit tests
├── dto/                    # DTO coverage tests
├── entity/                 # Entity coverage tests
├── exception/              # Exception handler tests
├── security/               # Security tests
└── service/                # Service unit tests
```

---

## License

This project is for educational and personal use.
