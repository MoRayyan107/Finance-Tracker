# 💰 Finance Tracker API

A robust Spring Boot REST API for personal finance management. Track income and expenses with full CRUD operations, validation, and professional error handling.

## 🚀 Features

- **RESTful API** - Clean JSON endpoints
- **CRUD Operations** - Create, read, update, delete transactions
- **Data Validation** - Comprehensive input validation
- **Error Handling** - Custom exceptions with proper HTTP status codes
- **Logging** - Production-ready logging with SLF4J
- **MySQL Integration** - Persistent data storage
- **Professional Architecture** - Layered structure (Controller → Service → Repository)
- **Comprehensive Testing** - 75%+ test coverage with JUnit and Mockito

## 📋 API Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| `POST` | `/api/transaction/create` | Create a new transaction | ✅ Implemented |
| `GET` | `/api/transaction/fetch` | Get all transactions | ✅ Implemented |
| `GET` | `/api/transaction/{id}` | Get transaction by ID | ✅ Implemented |
| `PUT` | `/api/transaction/update/{id}` | Update a transaction | ✅ Implemented |
| `DELETE` | `/api/transaction/delete/{id}` | Delete a transaction | ✅ Implemented |

## 🛠️ Technologies Used

- **Java 23**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **MySQL Database**
- **Maven**
- **Lombok**
- **SLF4J Logging**
- **JUnit 5**
- **Mockito**
- **JaCoCo** (Code Coverage)

## 📦 Project Structure
```
finance-tracker/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/rayyan/finance_tracker/
│   │   │   ├── FinanceTrackerApplication.java
│   │   │   ├── entity/Transaction.java
│   │   │   ├── repository/TransactionRepository.java
│   │   │   ├── service/TransactionService.java
│   │   │   ├── controller/TransactionController.java
│   │   │   └── exceptions/
│   │   │       ├── ValidationException.java
│   │   │       ├── TransactionNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/application.properties
│   └── test/
│       └── java/com/rayyan/finance_tracker/service/TransactionServicesTest.java
└── target/site/jacoco/index.html
```

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **MySQL 5.7** or higher
- **Maven 3.6** or higher

### Installation & Setup

1.  **Clone the repository and navigate into it:**
    ```
    git clone https://github.com/MoRayyan107/Finance-Tracker.git
    cd Finance-Tracker
    ```

2.  **Create the MySQL database:**
    ```sql
    CREATE DATABASE finance_tracker_db;
    ```

3.  **Configure your database connection** in `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/finance_tracker_db
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password
    # ... other properties
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The API will be available at `http://localhost:8080`.

### Running Tests & Viewing Coverage

1.  **Run the test suite and generate coverage report:**
    ```bash
    .\mvnw clean test
    ```

2.  **Open the coverage report:**
    ```bash
    # Open the HTML report in your browser
    open target/site/jacoco/index.html # On macOS
    # or
    xdg-open target/site/jacoco/index.html # On Linux
    # On Windows, navigate to the folder and open index.html
    ```

## 🔮 Future Enhancements

- [ ] **User Authentication & Authorization** with Spring Security & JWT
- [ ] **Advanced Financial Features** (Budgeting, Reports, Analytics)
- [ ] **API Documentation** with OpenAPI (Swagger)
- [ ] **Docker Containerization** for easy deployment
- [ ] **Frontend Application** (React/Angular) to consume the API

## 📝 License

This project is licensed for educational and personal use.


