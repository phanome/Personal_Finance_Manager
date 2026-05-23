# Personal Finance Manager

A modern, robust backend service for Personal Finance Management built with Spring Boot 3.2, Java 17, Spring Security, and JPA/Hibernate. The application uses H2 as an in-memory database and provides secure REST APIs to track transactions, manage categories, set saving goals, and generate financial reports.

---

## Features

- **User Authentication**: Secure register, login, and logout endpoints utilizing Spring Security session-based authentication.
- **Category Management**: Create, read, update, and delete categories to organize income and expenses. A set of default categories is seeded automatically on startup.
- **Transaction Tracking**: Track incomes and expenses with support for pagination, filtering by category, date range, and transaction type (Income/Expense). Supports soft-delete pattern.
- **Savings Goals**: Set goals with target amounts and dates, and track progress against them.
- **Financial Reports**: Get real-time aggregated reports for any month or year, showcasing breakdown of income/expenses by category and net savings.
- **High Test Coverage**: Comprehensive test suite consisting of unit and integration tests with >80% code coverage measured by JaCoCo.

---

## Tech Stack

- **Core Framework**: Spring Boot 3.2.0 (Java 17)
- **Database**: H2 Database (In-Memory)
- **ORM & JPA**: Hibernate, Spring Data JPA
- **Security**: Spring Security (Session-based auth, password hashing with BCrypt)
- **Utilities**: Lombok, Maven
- **Testing**: JUnit 5, Mockito, Spring Security Test, JaCoCo

---

## Getting Started

### Prerequisites

Ensure you have the following installed:
- **Java Development Kit (JDK) 17** or higher
- **Maven** (optional, wrapper `./mvnw` is included in the repository)

### Running the Application

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd "Personal Finance Manager"
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

3. H2 Console:
   The database console is available at `http://localhost:8080/h2-console`.
   - **JDBC URL**: `jdbc:h2:mem:testdb`
   - **Username**: `sa`
   - **Password**: (leave blank)

---

## API Documentation

All request and response bodies use JSON. Authentication is required for all endpoints except `/api/auth/register` and `/api/auth/login`.

### 1. Authentication
* **POST** `/api/auth/register` - Registers a new user.
  ```json
  {
    "username": "user@example.com",
    "password": "securePassword123",
    "fullName": "John Doe",
    "phoneNumber": "1234567890"
  }
  ```
* **POST** `/api/auth/login` - Authenticates user and sets session cookie.
  ```json
  {
    "username": "user@example.com",
    "password": "securePassword123"
  }
  ```
* **POST** `/api/auth/logout` - Destroys session.

### 2. Categories
* **GET** `/api/categories` - Returns all categories for the authenticated user.
* **POST** `/api/categories` - Creates a new category.
  ```json
  {
    "name": "Gym & Fitness"
  }
  ```
* **PUT** `/api/categories/{id}` - Updates a category name.
* **DELETE** `/api/categories/{id}` - Deletes a category.

### 3. Transactions
* **GET** `/api/transactions` - Retrieves transactions with pagination and filters (`type`, `categoryId`, `startDate`, `endDate`).
* **POST** `/api/transactions` - Records a new transaction.
  ```json
  {
    "amount": 120.50,
    "type": "EXPENSE",
    "description": "Weekly Groceries",
    "categoryId": 4,
    "transactionDate": "2026-05-23T12:00:00"
  }
  ```
* **PUT** `/api/transactions/{id}` - Updates transaction details.
* **DELETE** `/api/transactions/{id}` - Soft-deletes a transaction.

### 4. Savings Goals
* **GET** `/api/goals` - Lists savings goals.
* **POST** `/api/goals` - Creates a goal.
  ```json
  {
    "title": "New Laptop",
    "targetAmount": 1500.00,
    "targetDate": "2026-12-31"
  }
  ```
* **PUT** `/api/goals/{id}` - Updates goal.
* **DELETE** `/api/goals/{id}` - Deletes goal.

### 5. Reports
* **GET** `/api/reports/monthly?year=2026&month=5` - Monthly aggregated breakdown of income, expenses, and savings.
* **GET** `/api/reports/yearly?year=2026` - Yearly breakdown of income, expenses, and savings.

---

## Testing & Coverage

To run the tests and generate a JaCoCo coverage report:
```bash
./mvnw clean test
```
The coverage report will be generated at `target/site/jacoco/index.html`.
