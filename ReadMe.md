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

## 📦 Project Structure```
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
## Getting Started
### Prerequisites
- Java 23+
- MySQL 5.5+
- Maven 3.6+

## 🚀 Future Additions

| Category | Feature | Status | Priority |
|----------|---------|--------|----------|
| **Authentication** | JWT-based User Authentication | Planned | High |
| **Authentication** | Role-based Access Control | Planned | High |
| **Data Security** | User-specific Data Isolation | Planned | High |
| **API Enhancements** | Pagination & Filtering | Planned | Medium |
| **Financial Features** | Budget Management System | Planned | Medium |
| **Financial Features** | Financial Reports & Analytics | Planned | Medium |
| **Integration** | Bank API Integration (Plaid) | Planned | Low |
| **Notifications** | Email/SMS Alerts | Planned | Low |
| **UI/UX** | Web Dashboard Frontend | Planned | Medium |
| **Deployment** | Docker Containerization | Planned | Medium |
| **Deployment** | Cloud Deployment (AWS/Azure) | Planned | Low |
| **Monitoring** | Application Performance Monitoring | Planned | Low |

## 🏆 Current Achievements
- ✅ 75%+ Test Coverage - Comprehensive service layer testing
- ✅ Full CRUD Operations - Complete transaction management
- ✅ Validation System - Robust input validation and error handling
- ✅ Professional Architecture - Clean layered structure
- ✅ Production-ready Logging - SLF4J implementation throughout


