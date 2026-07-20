# ATM System

A full-stack ATM application with a React frontend and a Spring Boot backend for user login, account management, deposits, withdrawals, and transaction history.

## Features

- Secure authentication with JWT
- User, account, and transaction management
- Deposit and withdrawal flows
- Protected dashboard and profile pages
- REST APIs backed by PostgreSQL
- Logging, validation, and global error handling

## Tech Stack

| Layer | Stack |
|---|---|
| Frontend | React, React Router, Axios, Tailwind CSS |
| Backend | Spring Boot, Spring Security, Spring Data JPA |
| Database | PostgreSQL |
| Build Tools | Maven, npm |

## Project Structure

```text
atm_backend/   # Spring Boot API
atm_frontend/  # React client
```

### Frontend Pages

- Login
- Dashboard
- Deposit
- Withdraw
- Profile

### Backend Modules

- Controllers
- Services
- Repositories
- Security
- DTOs and entities
- Global exception handling

## Getting Started

### Backend

```bash
cd atm_backend
mvn spring-boot:run
```

### Frontend

```bash
cd atm_frontend
npm install
npm start
```

## Configuration

Set up your local database and application settings in the backend config before running the app. Keep secrets and credentials out of source control.

## API Overview

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/users`
- `POST /api/accounts`
- `POST /api/transactions`

## Notes

- Backend runs on port `8080` by default.
- Frontend runs on port `3000` by default.
- The backend uses PostgreSQL and logs to the application log files.

## Author

**Om Naphade** · [LinkedIn](https://linkedin.com/in/omnaphade) · [Portfolio](https://om-naphade.netlify.app) · [GitHub](https://github.com/OmNaphade)
