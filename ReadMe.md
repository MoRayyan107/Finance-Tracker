# 💰 Finance Tracker API

A robust Spring Boot REST API for personal finance management.  
Track income and expenses with full CRUD operations, validation, JWT‑based security, and professional error handling.

---

## 🚀 Features

- **RESTful API** – Clean JSON endpoints
- **CRUD Operations** – Create, read, update, delete transactions
- **Data Validation** – Input checks (non-null, ranges, etc.)
- **Error Handling** – Custom exceptions → correct HTTP status codes
- **JWT Security** – Authenticated access to endpoints
- **Logging** – Production‑ready logging via SLF4J
- **MySQL / JPA** – Persistent data with relational DB
- **Layered Architecture** – Controller → Service → Repository
- **Testing** – Unit & integration tests with JUnit & Mockito
- **Code Coverage** – JaCoCo report

---

## 📋 API Endpoints

| Method   | Path                                 | Description                | Status         |
|----------|---------------------------------------|-----------------------------|----------------|
| `POST`   | `/api/auth/register`                  | Register a new user         | ✅ Implemented  |
| `POST`   | `/api/auth/login`                     | Authenticate & get JWT token | ✅ Implemented  |
| `POST`   | `/api/transaction/create`             | Create a new transaction     | ✅ Implemented  |
| `GET`    | `/api/transaction/fetch`              | Fetch all transactions       | ✅ Implemented  |
| `GET`    | `/api/transaction/{id}`               | Get transaction by its ID    | ✅ Implemented  |
| `PUT`    | `/api/transaction/update/{id}`        | Update transaction by ID     | ✅ Implemented  |
| `DELETE` | `/api/transaction/delete/{id}`        | Delete transaction by ID     | ✅ Implemented  |

### 🌐 HTML Endpoints

| Path                     | Description                               | Access                      |
|--------------------------|-------------------------------------------|------------------------------|
| `/`                      | Home page with application overview       | Public                       |
| `/login`                 | User login page                           | Public                       |
| `/register`              | New user registration                     | Public                       |
| `/dashboard`             | User dashboard with transaction summary   | Authenticated Users          |
| `/transactions`          | Transaction management interface          | Authenticated Users          |
| `/profile`               | User profile management                   | Authenticated Users          |
| `/h2-console`            | H2 Database console (dev environment)     | Dev mode only                |

---

## 🛠 Technologies Used

- **Java 21 (JDK 21)**
- **Spring Boot 3.5.5**
- **Spring Data JPA**
- **MySQL**
- **Maven**
- **Lombok**
- **SLF4J Logging**
- **JUnit 5 & Mockito**
- **JaCoCo** (coverage)

## 🖥️ Frontend Development

### Technologies
- **React**: For building the interactive UI components
- **TypeScript**: For type-safe JavaScript development
- **Bootstrap**: For responsive design and UI components
- **Redux**: For state management across components
- **Axios**: For API requests to the backend

### Frontend Structure

## 🏗 Project Structure

```
finance-tracker/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/rayyan/finance_tracker/
│   │   │   ├── FinanceTrackerApplication.java
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   ├── config/
│   │   │   └── exceptions/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/rayyan/finance_tracker/
└── target/site/jacoco/index.html
```

---

## 🚀 Getting Started

### Prerequisites

#### Development Environment
- **Java Development Kit (JDK)**: Version 21 or later
- **Maven**: Version 3.6+ for dependency management and build automation
- **IDE**: IntelliJ IDEA, Eclipse, VS Code with Spring extensions (recommended)
- **Git**: For version control and repository management

#### Database Requirements
- **Production**: MySQL 5.7+ or 8.0+ recommended
- **Testing**: H2 Database (included in dependencies, no separate installation needed)
- **Database Client**: MySQL Workbench, DBeaver, or similar tool (optional, for database management)

#### Security Configuration
- **JWT Secret Key**: A secure random string for signing JWT tokens
- **HTTPS**: SSL certificate for production deployment (recommended)

#### Optional Tools
- **Postman** or similar API testing tool for endpoint testing
- **Docker**: Version 20+ (if using containerization)
- **Browser Extensions**: JSON formatter for testing API responses in browser

### Installation & Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/MoRayyan107/Finance-Tracker.git
   cd Finance-Tracker
   ```

2. Configure `src/main/resources/application.properties`:
   ```properties
   !!NOTE: THIS WONT BE NECESSARY ONCE THE DB IS LAUNCED IN DOCKER

   spring.datasource.url=jdbc:mysql://localhost:3306/finance_tracker_db?createIfNotExist=true
   spring.datasource.username=your_mysql_user
   spring.datasource.password=your_mysql_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Create a `Secret file`:
   ```properties
   # JWT Secret Key - Keep this secure and do not commit to version control!
   application.security.jwt.prod.secret-key=your_secret_key_here
   ```
   This is done to prevent any sensitive information leaks 

3. Run the application:
   ```bash
   .\mvnw spring-boot:run
   ```
   → API runs on `http://localhost:8080`

---

## 🧪 Tests & Coverage

1. Run tests / generate coverage:
   ```bash
   .\mvnw clean test
   ```

2. View coverage:
   ```bash
   # macOS
   open target/site/jacoco/index.html

   # Linux
   xdg-open target/site/jacoco/index.html

   # Windows
   # Open target/site/jacoco/index.html manually
   ```

---

## 🔮 Future Enhancements

- [ ] **Enhanced Financial Features**
  - Budget planning and tracking
  - Expense categorization and tagging
  - Financial reports and analytics
  - Recurring transaction support

- [ ] **Advanced Security**
  - Role‑based authorization (USER vs ADMIN)
  - Two-factor authentication
  - OAuth integration (Google, GitHub)

- [ ] **Developer Experience**
  - API documentation with Swagger / OpenAPI
  - Comprehensive integration testing
  - CI/CD pipeline setup

- [ ] **Deployment**
  - Docker containerization
  - Kubernetes orchestration
  - Cloud deployment (AWS, Azure, GCP)

- [ ] **Frontend Extensions**
  - Mobile app version (React Native)
  - Offline support with local storage
  - Export functionality (CSV, PDF)
  - Data visualization enhancements

---

## 📌 Repository

GitHub: https://github.com/MoRayyan107/Finance-Tracker/tree/master

---

## 📝 License

This project is for educational / personal use.
