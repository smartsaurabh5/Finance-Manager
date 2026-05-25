# Personal Finance Manager Backend

Production-ready, beginner-friendly REST API for managing personal finances with Java 17, Spring Boot 3.2.5, Spring Security sessions, JPA, H2 for local development, and PostgreSQL for deployment.

## Features

- Session-based authentication with BCrypt, HttpOnly cookies, session fixation protection, logout invalidation, activity tracking, remember-me session duration, and account lockout after repeated failed logins.
- User-scoped categories, transactions, savings goals, reports, dashboard summary, recent transactions, and monthly budgets.
- Validation on request DTOs, global exception handling, consistent error response shape, duplicate category conflict handling, and protection against deleting categories used by transactions.
- Transaction filtering, pagination, sorting, and future-date prevention.
- Monthly/yearly reports with validation and CSV export.
- Render-ready configuration with `PORT`, PostgreSQL environment variables, health checks, graceful shutdown, and production-safe H2 disabling.
- OpenAPI/Swagger UI and JaCoCo coverage report generation.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Web, Data JPA, Security, Validation, Actuator
- H2 for local development
- PostgreSQL for production
- Springdoc OpenAPI / Swagger UI
- JUnit 5, Mockito, Spring Boot Test
- Docker multi-stage build

## Local Setup

### Prerequisites

- Java 17 JDK
- Maven, or use the included Maven wrapper

### Run Locally

```bash
./mvnw spring-boot:run
```

Local URLs:

- API home: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`
- H2 console: `http://localhost:8080/h2-console`

H2 defaults:

- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: `password`

## Authentication Flow

1. Register with `POST /api/auth/register`.
2. Login with `POST /api/auth/login`.
3. Use the returned `JSESSIONID` cookie on protected APIs.
4. Logout with `POST /api/auth/logout`.

Example:

```bash
curl -i -c cookies.txt -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"aakash@example.com","password":"Strong123","fullName":"Aakash","phoneNumber":"9999999999"}'

curl -i -c cookies.txt -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"aakash@example.com","password":"Strong123","rememberMe":true}'

curl -b cookies.txt http://localhost:8080/api/transactions
```

Security notes:

- Cookies are HttpOnly and configurable with `COOKIE_SECURE` and `COOKIE_SAME_SITE`.
- Session fixation protection changes the session id after authentication.
- Logout invalidates the server session and deletes the session cookie.
- Accounts are locked for 15 minutes after 5 failed login attempts.
- Roles are ready through `UserRole` and `ROLE_USER` authorities.
- CSRF is currently disabled because this is a JSON API commonly tested from Swagger/Postman and intended for same-origin or explicitly trusted frontend origins. For a browser frontend in production, enable CSRF with a cookie/token strategy such as `CookieCsrfTokenRepository` and send the token in an `X-XSRF-TOKEN` header for unsafe methods.

## Main API Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/categories`
- `POST /api/categories`
- `DELETE /api/categories/{name}`
- `GET /api/transactions?page=0&size=20&type=EXPENSE`
- `GET /api/transactions/page?page=0&size=20`
- `GET /api/transactions/recent`
- `POST /api/transactions`
- `PUT /api/transactions/{id}`
- `DELETE /api/transactions/{id}`
- `GET /api/goals`
- `POST /api/goals`
- `GET|PUT|DELETE /api/goals/{id}`
- `GET /api/reports/monthly/{year}/{month}`
- `GET /api/reports/yearly/{year}`
- `GET /api/reports/monthly/{year}/{month}/export.csv`
- `GET /api/dashboard/summary`
- `GET /api/budgets/{year}/{month}`
- `POST /api/budgets`
- `DELETE /api/budgets/{id}`

## Example Requests

Create a transaction:

```bash
curl -b cookies.txt -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"amount":250.75,"date":"2026-05-25","category":"Food","type":"EXPENSE","description":"Groceries"}'
```

Create a monthly budget:

```bash
curl -b cookies.txt -X POST http://localhost:8080/api/budgets \
  -H "Content-Type: application/json" \
  -d '{"categoryId":2,"year":2026,"month":5,"limitAmount":12000}'
```

Export a monthly report:

```bash
curl -b cookies.txt -o report.csv http://localhost:8080/api/reports/monthly/2026/5/export.csv
```

## Testing

Run all tests:

```bash
./mvnw test
```

Generate coverage:

```bash
./mvnw test
open target/site/jacoco/index.html
```

Current tests cover application startup, controllers, DTO contracts, security helpers, global exception handling, and service behavior for registration, default categories, account lockout, duplicate categories, category deletion rules, transaction data isolation, future date prevention, budgets, savings goals, reports, and CSV export. The Maven build enforces minimum 80% instruction and line coverage with JaCoCo.

## Docker

Build and run:

```bash
docker build -t finance-manager .
docker run -p 8080:8080 finance-manager
```

Production-style PostgreSQL run:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/financedb \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -e CORS_ALLOWED_ORIGINS=https://your-frontend.example.com \
  finance-manager
```

## Render Deployment

### Option A: Docker Web Service

1. Create a PostgreSQL database on Render.
2. Create a new Web Service from this repository.
3. Choose Docker runtime.
4. Add environment variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DATABASE_URL=jdbc:postgresql://<internal-host>:5432/<database>`
   - `DATABASE_USERNAME=<username>`
   - `DATABASE_PASSWORD=<password>`
   - `CORS_ALLOWED_ORIGINS=https://your-frontend.onrender.com`
   - `COOKIE_SECURE=true`
   - `COOKIE_SAME_SITE=none`
5. Health check path: `/actuator/health`

### Option B: Java Web Service

- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/manager-0.0.1-SNAPSHOT.jar`

Render sets `PORT`; the application binds with `server.port=${PORT:8080}`.

Deployment screenshots placeholders:

- `docs/screenshots/render-postgres.png`
- `docs/screenshots/render-env-vars.png`
- `docs/screenshots/render-health-check.png`
- `docs/screenshots/swagger-ui.png`

## Environment Variables

| Variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8080` | HTTP port, required by Render |
| `SPRING_PROFILES_ACTIVE` | none | Use `prod` on Render |
| `DATABASE_URL` | `jdbc:h2:mem:financedb` | JDBC connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | `password` | Database password |
| `DATABASE_DRIVER` | `org.h2.Driver` | Local driver override |
| `DATABASE_PLATFORM` | `org.hibernate.dialect.H2Dialect` | Local dialect override |
| `DDL_AUTO` | `update` | Hibernate schema mode |
| `H2_CONSOLE_ENABLED` | `true` | Disable in production |
| `SESSION_TIMEOUT` | `30m` | Default session timeout |
| `COOKIE_SECURE` | `false`, `true` in prod | Send cookies only over HTTPS |
| `COOKIE_SAME_SITE` | `lax`, `none` in prod | Cross-site cookie behavior |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | Comma-separated frontend origins |
| `SHOW_SQL` | `false` | SQL logging |

## Architecture

The project follows a simple Spring layered architecture:

- `controller`: HTTP endpoints and request/response handling.
- `service`: business rules, validation beyond DTO annotations, authorization/data isolation checks.
- `repository`: Spring Data JPA persistence.
- `entity`: JPA domain model.
- `dto`: API request/response contracts.
- `security`: Spring Security user details and session activity filter.
- `exception`: application exceptions and global error responses.

## Production Recommendations

- Add Flyway or Liquibase before changing production schemas repeatedly.
- Move session storage to Redis or JDBC Spring Session if horizontal scaling across multiple Render instances is required.
- Enable CSRF token protection when a browser frontend performs cookie-authenticated writes.
- Add structured JSON logging and request correlation IDs for observability.
- Add rate limiting on login and registration endpoints.
- Add integration tests with Testcontainers PostgreSQL when deployment behavior becomes critical.
- Add PDF export with a library such as OpenPDF only if the assignment requires actual PDFs; CSV export is implemented now.
