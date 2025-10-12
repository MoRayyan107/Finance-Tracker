# 💰 Finance Tracker

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-6DB33F?logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg)

> A professional finance management system with REST API backend and responsive frontend.

---

## 📚 Table of Contents
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [API Reference](#-api-reference)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Development](#-development)
- [Testing](#-testing)
- [Roadmap](#-roadmap)

## 🌟 Key Features

### Backend
- ✨ RESTful API architecture
- 🔐 JWT authentication & authorization
- 📝 Complete CRUD operations
- ✅ Input validation & error handling
- 📊 Transaction management
- 🗃️ MySQL persistence with JPA
- 📋 Comprehensive logging

### Frontend (Current)
- 🎨 Responsive design
- 📱 Mobile-friendly interface
- 🔄 Real-time updates
- 🎯 Intuitive UI/UX

## 🛠 Tech Stack

### Backend Core
```
Java 21          → Programming Language
Spring Boot 3.5  → Application Framework
Spring Security  → Authentication & Authorization
Spring Data JPA  → Data Access
MySQL            → Database
Maven            → Build Tool
```

### Frontend Current
```
HTML5    → Structure
CSS3     → Styling
JavaScript → Interactivity
```

### Development Tools
```
Git       → Version Control
JUnit 5   → Testing Framework
Mockito   → Mocking Library
JaCoCo    → Code Coverage
SLF4J     → Logging
Lombok    → Boilerplate Reduction
```

## 🔌 API Reference

### Authentication
| Method | Endpoint | Description | Status |
|--------|----------|-------------|---------|
| `POST` | `/api/auth/register` | Create account | ✅ |
| `POST` | `/api/auth/login` | Get JWT token | ✅ |

### Transactions
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| `POST` | `/api/transaction/create` | New transaction | Yes |
| `GET` | `/api/transaction/fetch` | List all | Yes |
| `GET` | `/api/transaction/{id}` | Get single | Yes |
| `PUT` | `/api/transaction/update/{id}` | Modify | Yes |
| `DELETE` | `/api/transaction/delete/{id}` | Remove | Yes |

### Frontend Routes
| Path | Description | Access | Status |
|------|-------------|--------|---------|
| `/` | Landing page | Public | ✅ |
| `/login` | Authentication | Public | ✅ |
| `/register` | New account | Public | ✅ |
| `/dashboard` | Main interface | Private | 🚧 |

## 🚀 Getting Started

### Prerequisites
- JDK 21+
- MySQL 8.0+
- Maven 3.6+
- Git

### Quick Start
```bash
# Clone repository
git clone https://github.com/MoRayyan107/Finance-Tracker.git

# Navigate to project
cd Finance-Tracker

# Install dependencies
mvn install

# Configure database (src/main/resources/application.properties)
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>

# Run application
mvn spring-boot:run
```

## 📁 Project Structure
```
finance-tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/rayyan/finance_tracker/
│   │   │       ├── config/         # Configurations
│   │   │       ├── controller/     # API endpoints
│   │   │       ├── entity/         # Data models
│   │   │       ├── repository/     # Data access
│   │   │       ├── service/        # Business logic
│   │   │       └── security/       # Auth & JWT
│   │   └── resources/
│   │       ├── static/            # Frontend assets
│   │       └── templates/         # HTML views
│   └── test/                      # Test suites
└── pom.xml                        # Dependencies
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Generate coverage report
mvn verify

# View coverage
open target/site/jacoco/index.html
```

## 🗺 Roadmap

### Phase 1 - Core Features
- [x] Basic CRUD operations
- [x] JWT authentication
- [x] Data validation
- [ ] Dashboard analytics

### Phase 2 - Enhanced Features
- [ ] Advanced filtering
- [ ] Report generation
- [ ] Budget planning
- [ ] Category management

### Phase 3 - Frontend Evolution
- [ ] Angular migration
- [ ] Material Design
- [ ] Charts & graphs
- [ ] Dark mode

### Phase 4 - Advanced Features
- [ ] Multi-currency support
- [ ] Export/Import
- [ ] API documentation
- [ ] Mobile responsiveness

## 📜 License
This project is licensed under the MIT License.

## 🤝 Contributing
Contributions welcome! Please read our [Contributing Guide](CONTRIBUTING.md).

---

<div align="center">
Made with ❤️ by Mo Rayyan
</div>
