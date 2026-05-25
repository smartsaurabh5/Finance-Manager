# Personal Finance Manager Backend

A robust RESTful API for managing personal finances, built with Java 17 and Spring Boot 3.

## Features

- **Authentication & User Management**: Secure session-based authentication, user registration, and profile management.
- **Category Management**: Default categories (Salary, Food, Rent, etc.) and custom category creation.
- **Transaction Management**: Track incomes and expenses, filter by date ranges and categories.
- **Savings Goals**: Set financial goals and automatically track progress based on your net income.
- **Reports Module**: Generate monthly and yearly financial summaries (total income, total expense, net savings).

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.5** (Web, Data JPA, Security, Validation)
- **Database**: H2 (In-memory, for local development) & PostgreSQL (for Production)
- **Security**: Spring Security (Session-based, BCrypt)
- **Documentation**: OpenAPI / Swagger UI
- **Testing**: JUnit 5, Mockito, MockMvc

## Setup Instructions

### Prerequisites
- Java 17 JDK installed
- Maven installed

### Running Locally

1. **Clone the repository** (if applicable) or navigate to the project directory:
   ```bash
   cd Finance_Manager
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the Application**:
   - The API will run at: `http://localhost:8080`
   - **Swagger UI (API Docs)**: `http://localhost:8080/swagger-ui.html`
   - **H2 Database Console**: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:financedb`
     - Username: `sa`
     - Password: `password`

## API Endpoints Overview

For detailed API documentation, please visit the Swagger UI link above after starting the application. 
All endpoints under `/api/categories/**`, `/api/transactions/**`, `/api/goals/**`, and `/api/reports/**` require a valid session cookie obtained via `/api/auth/login`.

Key assignment endpoints:
- `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`
- `GET|POST /api/transactions`, `PUT|DELETE /api/transactions/{id}`
- `GET|POST /api/categories`, `DELETE /api/categories/{name}`
- `GET|POST /api/goals`, `GET|PUT|DELETE /api/goals/{id}`
- `GET /api/reports/monthly/{year}/{month}`, `GET /api/reports/yearly/{year}`

## Deployment Instructions (Render)

This application is ready to be deployed to [Render.com](https://render.com) using a PostgreSQL database.

1. **Create a PostgreSQL Database on Render**:
   - Go to the Render Dashboard and create a new PostgreSQL database.
   - Note the Internal Database URL.

2. **Create a Web Service on Render**:
   - Connect your GitHub repository.
   - Set the Environment to **Java**.
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/manager-0.0.1-SNAPSHOT.jar`

3. **Configure Environment Variables**:
   In the Render Web Service settings, add the following environment variables. Also, uncomment the PostgreSQL configuration in `src/main/resources/application.yml` and comment out the H2 configuration.
   
   - `DATABASE_URL`: `jdbc:postgresql://<render-internal-db-url>:5432/<db-name>`
   - `DATABASE_USERNAME`: `<your-db-username>`
   - `DATABASE_PASSWORD`: `<your-db-password>`

4. **Deploy**:
   Save and deploy. The application will start and automatically connect to your PostgreSQL database.
