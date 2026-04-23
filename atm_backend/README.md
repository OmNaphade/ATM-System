# ATM Backend Application

A comprehensive Spring Boot-based backend system for ATM (Automated Teller Machine) operations, providing secure user management, bank account handling, and transaction processing with robust concurrency control and comprehensive logging.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [Multi-threading & Concurrency](#multi-threading--concurrency)
- [Logging & Monitoring](#logging--monitoring)
- [Exception Handling](#exception-handling)
- [Testing](#testing)
- [Deployment](#deployment)
- [Usage Examples](#usage-examples)
- [Configuration](#configuration)

## Features

- **User Management**: Create, read, update, and delete user accounts
- **Bank Account Management**: Handle multiple accounts per user with different types
- **Transaction Processing**: Support for deposits, withdrawals, and transfers
- **Idempotent Transactions**: Prevent duplicate transaction processing
- **Concurrent Safety**: Pessimistic locking for financial operations
- **Asynchronous Processing**: Background operations for bulk deletions
- **Comprehensive Logging**: Structured logging with file rotation
- **Global Exception Handling**: Centralized error management
- **RESTful APIs**: Well-documented endpoints with OpenAPI/Swagger
- **Health Monitoring**: Spring Boot Actuator integration

## Technology Stack

### Backend Framework
- **Java 25**: Latest Java version with modern features
- **Spring Boot 3.5.14**: Production-ready framework
- **Spring Data JPA**: ORM and data access
- **Spring Web**: REST API development
- **Spring Security**: Authentication and authorization
- **Spring Actuator**: Application monitoring

### Database
- **PostgreSQL**: Robust relational database
- **Hibernate**: JPA implementation with advanced features
- **HikariCP**: High-performance connection pooling

### Testing
- **JUnit 5**: Modern testing framework
- **Mockito**: Mocking framework for unit tests
- **Spring Boot Test**: Integration testing support
- **TestRestTemplate**: HTTP client for integration tests
- **MockMvc**: Controller testing without full server

### Development Tools
- **Maven**: Dependency management and build tool
- **Lombok**: Code generation for boilerplate
- **ModelMapper**: Object mapping between DTOs and entities
- **Docker Compose**: Containerized database setup

### Logging & Monitoring
- **SLF4J**: Logging facade
- **Logback**: Logging implementation with file rotation
- **Spring Boot Actuator**: Health checks and metrics

## Architecture

### Layered Architecture

```
┌─────────────────┐
│   Controllers   │  REST API Layer
├─────────────────┤
│    Services     │  Business Logic Layer
├─────────────────┤
│  Repositories   │  Data Access Layer
├─────────────────┤
│    Database     │  PostgreSQL
└─────────────────┘
```

### Key Components

1. **Controllers**: Handle HTTP requests and responses
2. **Services**: Contain business logic and transaction management
3. **Repositories**: Data access objects with JPA queries
4. **Entities**: JPA entities representing database tables
5. **DTOs**: Data Transfer Objects for API communication
6. **Exception Handlers**: Global exception management
7. **Configuration**: Application and logging configuration

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP
);
```

### Bank Accounts Table
```sql
CREATE TABLE bank_accounts (
    account_id SERIAL PRIMARY KEY,
    account_number VARCHAR(255) UNIQUE NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    bank_name VARCHAR(255),
    balance DECIMAL(19,2) NOT NULL,
    creation_date DATE,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    transaction_time TIMESTAMP NOT NULL,
    reference_id VARCHAR(255) UNIQUE NOT NULL,
    account_id BIGINT NOT NULL REFERENCES bank_accounts(account_id),
    to_account_id BIGINT REFERENCES bank_accounts(account_id),
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP
);
```

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### User Management APIs

#### Create User
```http
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

#### Get All Users
```http
GET /api/users
```

#### Get User by ID
```http
GET /api/users/{id}
```

#### Update User
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "password": "newPassword456"
}
```

#### Delete User
```http
DELETE /api/users/{id}
```

#### Delete All Users (Async)
```http
DELETE /api/users/reset
```

### Bank Account APIs

#### Create Bank Account
```http
POST /api/accounts
Content-Type: application/json

{
  "accountNumber": "1234567890",
  "accountType": "SAVINGS",
  "bankName": "Example Bank",
  "balance": 1000.50,
  "userId": 1
}
```

#### Get All Accounts
```http
GET /api/accounts
```

#### Get Account by ID
```http
GET /api/accounts/{id}
```

#### Update Account
```http
PUT /api/accounts/{id}
Content-Type: application/json

{
  "accountNumber": "0987654321",
  "accountType": "CURRENT",
  "bankName": "Updated Bank",
  "balance": 2500.75,
  "userId": 1
}
```

#### Delete Account
```http
DELETE /api/accounts/{id}
```

#### Delete All Accounts
```http
DELETE /api/accounts/reset
```

### Transaction APIs

#### Create Transaction (Deposit)
```http
POST /api/transactions
Content-Type: application/json

{
  "accountId": 1,
  "amount": 500.00,
  "type": "DEPOSIT",
  "referenceId": "txn-ref-12345"
}
```

#### Create Transaction (Withdraw)
```http
POST /api/transactions
Content-Type: application/json

{
  "accountId": 1,
  "amount": 200.00,
  "type": "WITHDRAW",
  "referenceId": "txn-ref-67890"
}
```

#### Create Transaction (Transfer)
```http
POST /api/transactions
Content-Type: application/json

{
  "accountId": 1,
  "toAccountId": 2,
  "amount": 300.00,
  "type": "TRANSFER",
  "referenceId": "txn-ref-abcde"
}
```

#### Get All Transactions
```http
GET /api/transactions
```

#### Get Transaction by ID
```http
GET /api/transactions/{id}
```

#### Get Transactions by Account ID
```http
GET /api/transactions/account/{accountId}
```

#### Delete Transaction
```http
DELETE /api/transactions/{id}
```

#### Delete All Transactions
```http
DELETE /api/transactions/reset
```

## Multi-threading & Concurrency

### Thread Pool Configuration
```properties
# Async Configuration
spring.task.execution.pool.core-size=5
spring.task.execution.pool.max-size=20
spring.task.execution.pool.queue-capacity=500
spring.task.execution.thread-name-prefix=async-
```

### Asynchronous Operations

#### Bulk Deletion
- `deleteAllUsers()` runs asynchronously to prevent blocking the main thread
- Uses `@Async` annotation with custom thread pool
- Returns immediate response to client

#### Transaction Processing
- Synchronous for financial operations to ensure consistency
- Uses pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) for account balance updates
- Prevents race conditions during concurrent transactions

### Concurrency Safety Measures

1. **Database Transactions**: All operations wrapped in `@Transactional`
2. **Optimistic Locking**: Version fields in entities for conflict detection
3. **Pessimistic Locking**: Row-level locking for critical financial operations
4. **Thread-Safe Services**: Stateless service beans safe for concurrent access
5. **Connection Pooling**: HikariCP manages database connections efficiently

### Race Condition Prevention

- **User Creation**: Database unique constraints prevent duplicate emails
- **Account Updates**: Pessimistic locking ensures balance consistency
- **Transaction Idempotency**: Reference ID uniqueness prevents duplicate processing

## Logging & Monitoring

### Logging Configuration

#### Logback Configuration (`logback-spring.xml`)
```xml
<configuration>
    <property name="LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId}] %logger{36} - %msg%n"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder><pattern>${LOG_PATTERN}</pattern></encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/backend.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/backend-logs-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>100MB</totalSizeCap>
        </rollingPolicy>
        <encoder><pattern>${LOG_PATTERN}</pattern></encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

#### Application Properties
```properties
logging.level.root=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

### Log Levels
- **INFO**: General application flow, successful operations
- **WARN**: Potentially harmful situations (duplicate emails, low balance warnings)
- **ERROR**: Error conditions that might still allow the application to continue
- **DEBUG**: Detailed information for debugging (SQL queries, parameter binding)

### Monitoring Endpoints

#### Health Check
```http
GET /actuator/health
```

#### Application Metrics
```http
GET /actuator/metrics
```

#### Thread Dump
```http
GET /actuator/threaddump
```

## Exception Handling

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientResourcesException.class)
    public ResponseEntity<?> handleInsufficientResources(InsufficientResourcesException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<?> handleResourceExists(ResourceExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Data integrity violation: " + ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }
}
```

### Custom Exceptions

- **ResourceNotFoundException**: 404 - Entity not found
- **InsufficientResourcesException**: 400 - Insufficient balance
- **ResourceExistsException**: 409 - Duplicate resource
- **DataIntegrityViolationException**: 409 - Database constraint violations

## Testing

### Test Structure

```
src/test/java/com/omnaphade/
├── AtmBackendApplicationTests.java      # Integration tests
├── service/
│   ├── UserServiceTest.java            # User service unit tests
│   ├── BankAccountServiceTest.java     # Account service unit tests
│   └── TransactionServiceTest.java     # Transaction service unit tests
└── controller/
    ├── UserControllerTest.java         # User controller tests
    ├── BankAccountControllerTest.java  # Account controller tests
    └── TransactionControllerTest.java  # Transaction controller tests
```

### Test Coverage Summary

#### Service Layer Tests (39 test methods)
- **UserServiceTest**: 12 tests covering CRUD operations, validation, async operations
- **BankAccountServiceTest**: 12 tests covering account management, user validation
- **TransactionServiceTest**: 15 tests covering all transaction types, balance validation, locking

#### Controller Layer Tests (32 test methods)
- **UserControllerTest**: 10 tests covering REST endpoints, validation, error handling
- **BankAccountControllerTest**: 10 tests covering account operations, error scenarios
- **TransactionControllerTest**: 12 tests covering transaction creation, retrieval, deletion

#### Integration Tests (6 test methods)
- Full application context testing
- End-to-end user lifecycle
- Duplicate handling
- Health checks

### Key Test Scenarios

#### Happy Path Tests
- Successful CRUD operations
- Valid data processing
- Proper response formatting

#### Error Handling Tests
- Resource not found (404)
- Insufficient balance (400)
- Duplicate creation (409)
- Invalid input data (400)

#### Business Logic Tests
- Email uniqueness enforcement
- Balance validation
- Transaction idempotency
- Account type handling

#### Concurrency Tests
- Pessimistic locking simulation
- Async operation verification
- Thread safety validation

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage report
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=AtmBackendApplicationTests
```

## Deployment

### Prerequisites
- Java 25+
- Maven 3.6+
- PostgreSQL 12+
- Docker (optional)

### Local Development Setup

1. **Clone Repository**
```bash
git clone <repository-url>
cd atm_backend
```

2. **Start Database**
```bash
docker-compose up -d
```

3. **Configure Database**
Update `application.properties` with your database credentials.

4. **Build Application**
```bash
mvn clean install
```

5. **Run Application**
```bash
mvn spring-boot:run
```

### Production Deployment

#### Docker Deployment
```dockerfile
FROM openjdk:25-jdk-slim
COPY target/atm_backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### Docker Compose for Full Stack
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:latest
    environment:
      POSTGRES_DB: atm_db
      POSTGRES_USER: your_user
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  atm-backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/atm_db
      SPRING_DATASOURCE_USERNAME: your_user
      SPRING_DATASOURCE_PASSWORD: your_password

volumes:
  postgres_data:
```

#### Cloud Deployment
- **AWS**: Use Elastic Beanstalk or ECS
- **Azure**: Use App Service or AKS
- **GCP**: Use App Engine or GKE
- Configure environment variables for database connections
- Use managed PostgreSQL services (RDS, Cloud SQL, etc.)

### Environment Variables

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/atm_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Application Configuration
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_ORG_HIBERNATE_SQL=DEBUG
```

## Usage Examples

### Complete User and Account Workflow

1. **Create User**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "securePass123"
  }'
```

2. **Create Bank Account**
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "1234567890",
    "accountType": "SAVINGS",
    "bankName": "Example Bank",
    "balance": 1000.00,
    "userId": 1
  }'
```

3. **Make Deposit**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "amount": 500.00,
    "type": "DEPOSIT",
    "referenceId": "dep-001"
  }'
```

4. **Check Balance**
```bash
curl http://localhost:8080/api/accounts/1
```

### Error Handling Examples

#### Insufficient Balance
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "amount": 2000.00,
    "type": "WITHDRAW",
    "referenceId": "wd-001"
  }'
# Returns: 400 Bad Request - "Insufficient balance"
```

#### Duplicate Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": 1,
    "amount": 100.00,
    "type": "DEPOSIT",
    "referenceId": "dep-001"
  }'
# Returns: Existing transaction (idempotent)
```

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080
spring.application.name=atm_backend

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/atm_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Kolkata
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Async Configuration
spring.task.execution.pool.core-size=5
spring.task.execution.pool.max-size=20
spring.task.execution.pool.queue-capacity=500
spring.task.execution.thread-name-prefix=async-

# Logging Configuration
logging.level.root=INFO
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE

# Actuator Configuration
management.endpoints.web.exposure.include=*
```

### Docker Compose

```yaml
services:
  postgres:
    image: postgres:latest
    environment:
      POSTGRES_DB: atm_db
      POSTGRES_USER: your_user
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

## Security Considerations

- Passwords should be hashed (consider BCrypt)
- Input validation and sanitization
- Rate limiting for API endpoints
- HTTPS in production
- Database credentials encryption
- CORS configuration for frontend integration

## Performance Optimization

- Database indexing on frequently queried columns
- Connection pooling configuration
- Caching strategies for read-heavy operations
- Async processing for non-critical operations
- Query optimization and N+1 problem prevention

## Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Note**: This documentation provides a comprehensive overview of the ATM Backend Application. For detailed API specifications, refer to the Swagger UI at `http://localhost:8080/swagger-ui.html` when the application is running.</content>
<parameter name="filePath">d:\PRACTICE\atm_backend\README.md
