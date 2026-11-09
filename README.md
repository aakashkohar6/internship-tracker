# Internship Application Tracker

A REST API built with Java, Spring Boot, and MariaDB that allows users to securely track and manage their internship applications.

Users can register, log in, create internship applications, update their status, filter and paginate results, and delete applications. Each user can access only their own application data.

This project was built to practice backend development, REST API design, relational databases, authentication, security, automated testing, and deployment.

## Features

### Internship Application Management

- Create internship applications
- View individual applications
- Retrieve paginated application lists
- Filter applications by status and company
- Update application status
- Delete applications
- Track application dates
- Validate requests and return structured API errors

### Authentication and Security

- User registration and login
- BCrypt password hashing
- Session-based authentication
- Logout and session invalidation
- CSRF protection for state-changing requests
- Cryptographically random CSRF tokens
- Session ID rotation after successful login
- HTTP-only session cookies with `SameSite=Lax`
- User-specific application ownership
- Database queries restricted to the authenticated user

### Backend Infrastructure

- Database migrations with Flyway
- JDBC database access
- HikariCP connection pooling
- Swagger/OpenAPI documentation
- Spring Boot Actuator health checks
- Application logging with SLF4J
- Docker containerization
- Automated unit and integration tests
- GitHub Actions continuous integration

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | Backend framework |
| Spring Web MVC | REST API development |
| MariaDB | Relational database |
| Spring JDBC | Database operations |
| HikariCP | Database connection pooling |
| Flyway | Database schema migrations |
| Spring Security Crypto | BCrypt password hashing |
| Jakarta Servlet API | HTTP sessions and servlet filters |
| Springdoc OpenAPI | API documentation |
| Spring Boot Actuator | Health monitoring |
| SLF4J | Application logging |
| JUnit 5 | Automated testing |
| Mockito | Mocking dependencies in tests |
| Maven | Build and dependency management |
| Docker | Containerization |
| GitHub Actions | Continuous integration |
| Render | Cloud application hosting |
| Layerbase | Cloud MariaDB hosting |

**Note:** Authentication and CSRF protection are implemented using HTTP sessions and a custom servlet filter. The project does not use the full Spring Security authentication framework.

## Architecture

```text
HTTP Client
    |
    v
CSRF Filter
    |
    v
REST Controller
    |
    v
Service Layer
    |
    v
Repository Layer
    |
    v
MariaDB
```

### Layer Responsibilities

**Controller Layer**

Handles HTTP requests, responses, and authentication checks.

**Service Layer**

Contains business logic and request validation.

**Repository Layer**

Executes SQL queries using Spring JDBC.

**Database Layer**

Stores users and internship applications.

**CSRF Filter**

Validates CSRF tokens on protected state-changing requests before they reach the controllers.

## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Authenticate and create a session |
| GET | `/auth/csrf` | Retrieve the current session's CSRF token |
| POST | `/auth/logout` | Invalidate the authenticated session |

### Internship Application Endpoints

All application endpoints require authentication.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/applications` | Create a new internship application |
| GET | `/applications` | Retrieve the authenticated user's applications |
| GET | `/applications/{id}` | Retrieve an application by ID |
| PATCH | `/applications/{id}` | Update an application's status |
| DELETE | `/applications/{id}` | Delete an application |

Users can access and modify only applications belonging to their accounts.

### Filtering and Pagination

`GET /applications` supports the following query parameters:

| Parameter | Description | Required | Default |
|---|---|---|---|
| `status` | Filter applications by status | No | No filter |
| `company` | Filter applications by company | No | No filter |
| `page` | Zero-based page number | No | `0` |
| `size` | Maximum applications per page | No | `10` |

Example:

```http
GET /applications?status=INTERVIEW&page=0&size=5
```

The response includes pagination metadata:

- `content`
- `page`
- `size`
- `totalElements`
- `totalPages`

## Authentication and CSRF Protection

The application uses server-side HTTP sessions.

After successful login:

1. The server verifies the user's credentials.
2. The session ID is rotated to reduce session fixation risk.
3. The authenticated user's ID is stored in the session.
4. A random CSRF token is generated and stored in the session.
5. The client receives a `JSESSIONID` session cookie.

The client must preserve the session cookie for subsequent authenticated requests.

### CSRF Validation Rules

- Safe HTTP methods such as `GET` do not require a CSRF token.
- `POST /auth/register` and `POST /auth/login` are exempt from CSRF validation.
- Protected state-changing requests require an authenticated session.
- Protected state-changing requests require a valid CSRF token.
- Missing or invalid CSRF tokens return `403 Forbidden`.
- Unauthenticated protected requests return `401 Unauthorized`.

For protected state-changing requests, include:

```http
X-CSRF-Token: YOUR_CSRF_TOKEN
```

The token must match the value stored in the authenticated session.

## Example API Requests

The following examples use:

`http://localhost:8080`

For the deployed API, replace `http://localhost:8080` with `https://internship-tracker-c1ge.onrender.com`.

### 1. Register a User

```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "example_user",
  "password": "ChooseYourOwnPassword123!"
}
```

Expected response:

`201 Created`

Use your own credentials. The example credentials are placeholders.

### 2. Log In

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "example_user",
  "password": "ChooseYourOwnPassword123!"
}
```

Expected response:

`200 OK`

The server creates an authenticated session and returns a session cookie.

### 3. Retrieve the CSRF Token

```http
GET http://localhost:8080/auth/csrf
```

Expected response:

`200 OK`

The response body contains the CSRF token associated with the authenticated session.

Copy this token for subsequent protected requests.

### 4. Retrieve Applications

```http
GET http://localhost:8080/applications?page=0&size=10
```

Expected response:

`200 OK`

Only applications belonging to the authenticated user are returned.

### 5. Create an Application

```http
POST http://localhost:8080/applications
Content-Type: application/json
X-CSRF-Token: YOUR_CSRF_TOKEN

{
  "company": "Microsoft",
  "position": "Software Engineer Intern",
  "status": "APPLIED",
  "location": "Seattle"
}
```

Expected response:

`201 Created`

The application ID is generated automatically by the database.

### 6. Update Application Status

Replace `{id}` with an application ID belonging to your account.

```http
PATCH http://localhost:8080/applications/{id}
Content-Type: application/json
X-CSRF-Token: YOUR_CSRF_TOKEN

{
  "status": "OFFER"
}
```

Expected response:

`204 No Content`

### 7. Delete an Application

```http
DELETE http://localhost:8080/applications/{id}
X-CSRF-Token: YOUR_CSRF_TOKEN
```

Expected response:

`204 No Content`

### 8. Log Out

```http
POST http://localhost:8080/auth/logout
X-CSRF-Token: YOUR_CSRF_TOKEN
```

Expected response:

`204 No Content`

The authenticated session is invalidated.

**Important:** All authenticated requests must use the same session cookie.

IntelliJ IDEA's HTTP Client can preserve cookies between requests.

The repository also includes `requests.http` with example API requests.

## Running Locally

### Prerequisites

Install the following:

- Java 21
- Maven
- MariaDB
- Git

Docker is optional for local development.

### 1. Clone the Repository

```bash
git clone https://github.com/aakashkohar6/internship-tracker.git

cd internship-tracker
```

### 2. Create the Database

Connect to MariaDB using an administrative account.

```sql
CREATE DATABASE internship_tracker;
```

The project uses Flyway to manage the database schema.

Migration files are located in:

```text
src/main/resources/db/migration/
```

Flyway automatically executes pending migrations when the application starts.

These migrations manage:

- Application tables
- User accounts
- Application ownership
- Foreign key constraints
- Database schema updates

**Do not create application tables manually.**

### 3. Create a Database User

Create a dedicated MariaDB user.

```sql
CREATE USER 'internship_app_user'@'localhost'
IDENTIFIED BY 'your_password';
```

Grant permissions for local development:

```sql
GRANT ALL PRIVILEGES
ON internship_tracker.*
TO 'internship_app_user'@'localhost';
```

Flyway needs schema modification permissions to execute migrations.

The broad permissions above are intended for a local development database.

Production database permissions should be more restrictive.

### 4. Configure Environment Variables

The application uses environment variables for database configuration.

| Variable | Description | Required |
|---|---|---|
| `DB_URL` | MariaDB JDBC connection URL | Yes |
| `DB_USERNAME` | Database username | No, defaults to `internship_app_user` |
| `DB_PASSWORD` | Password for the application database user | Yes |

#### Windows PowerShell

```powershell
$env:DB_URL="jdbc:mariadb://localhost:3306/internship_tracker"
$env:DB_USERNAME="internship_app_user"
$env:DB_PASSWORD="your_password"
```

#### macOS/Linux

```bash
export DB_URL="jdbc:mariadb://localhost:3306/internship_tracker"
export DB_USERNAME="internship_app_user"
export DB_PASSWORD="your_password"
```

The database username is configured in `application.properties` using:

```properties
spring.datasource.username=${DB_USERNAME:internship_app_user}
```

This means the application uses the value of `DB_USERNAME` when provided. Otherwise, it defaults to `internship_app_user`.

Never commit real passwords, session cookies, CSRF tokens, or secret environment variables.

### 5. Build the Project

```bash
mvn clean package
```

### 6. Run the Application

```bash
mvn spring-boot:run
```

The application starts at:

`http://localhost:8080`

Flyway executes pending database migrations during startup.

## API Documentation

Swagger UI is available locally at:

`http://localhost:8080/swagger-ui/index.html`

The OpenAPI specification is available locally at:

`http://localhost:8080/v3/api-docs`

For the deployed application:

**Swagger UI:** https://internship-tracker-c1ge.onrender.com/swagger-ui/index.html

**OpenAPI Specification:** https://internship-tracker-c1ge.onrender.com/v3/api-docs

Swagger provides interactive documentation for the REST API.

Authenticated endpoints still require a valid session, and protected state-changing requests require a CSRF token.

## Health Checks

Spring Boot Actuator provides an application health endpoint:

```http
GET http://localhost:8080/actuator/health
```

The deployed health endpoint is available at:

https://internship-tracker-c1ge.onrender.com/actuator/health

Example response:

```json
{
  "groups": [
    "liveness",
    "readiness"
  ],
  "status": "UP"
}
```

The health indicators include database connectivity and other application checks.

Detailed health information is hidden through:

```properties
management.endpoint.health.show-details=never
```

## Logging

The application uses SLF4J for logging.

Implemented log events include:

- Successful authentication
- Successful session invalidation
- Rejected unauthenticated requests
- Failed CSRF validation

Logs are written to the application console.

Sensitive values such as passwords, session IDs, and CSRF tokens should not be logged.

## Database Migrations

Flyway manages version-controlled database schema changes.

Migration scripts are stored in:

```text
src/main/resources/db/migration/
```

The project currently includes migrations through version `V6`.

Flyway tracks executed migrations in its schema history table.

Database constraints include:

- Automatically generated application IDs
- User account records
- Foreign key relationships
- Non-null application ownership

Repository queries restrict application access using the authenticated user's ID.

## Automated Testing

The project includes:

- Service-layer unit tests
- Repository integration tests
- Mockito-based CSRF filter tests

CSRF filter tests cover:

- Safe request methods
- Login and registration exemptions
- Missing authenticated sessions
- Sessions without a user ID
- Missing CSRF tokens
- Invalid CSRF tokens
- Valid CSRF tokens

Run all automated tests with:

```bash
mvn test
```

The latest documented local test run completed with:

```text
Tests run: 31
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Repository integration tests use a separate MariaDB test database.

## Continuous Integration

GitHub Actions runs automated tests for the project.

The CI workflow uses:

- Java 21
- Maven
- MariaDB

Continuous integration helps detect regressions when code changes are pushed to GitHub.

## Docker

The repository includes:

- `Dockerfile`
- `.dockerignore`
- `compose.yaml`

The Dockerfile uses a multi-stage build:

1. Maven builds the Spring Boot application using Java 21.
2. A Java 21 runtime image runs the packaged application.

The application still requires a reachable MariaDB database and the correct environment variables.

## Deployment

The application is publicly deployed on Render as a Docker-based web service.

**Live API:** https://internship-tracker-c1ge.onrender.com

**Health Check:** https://internship-tracker-c1ge.onrender.com/actuator/health

The deployed application connects to a separate cloud-hosted MariaDB database on Layerbase.

### Deployment Configuration

The following environment variables are configured in Render:

| Variable | Purpose |
|---|---|
| `DB_URL` | Cloud MariaDB JDBC connection URL |
| `DB_USERNAME` | Cloud database username |
| `DB_PASSWORD` | Cloud database password |

Database credentials are stored as environment variables rather than committed to GitHub.

### Production Verification

The deployed API has been manually tested for:

- User registration and login
- Session cookie creation with `Secure`, `HttpOnly`, and `SameSite=Lax`
- CSRF token retrieval and validation
- Creating, retrieving, updating, and deleting internship applications
- Successful logout and session invalidation
- Rejection of unauthenticated requests
- Application health checks

The application uses Render's free service tier, so the first request after inactivity may be delayed while the service starts.

## Project Status

### Completed

- REST API CRUD operations
- Filtering and pagination
- Request validation and error handling
- Swagger/OpenAPI documentation
- Automated unit and integration tests
- Session-based authentication
- User-specific application ownership
- CSRF protection
- Health checks
- Application logging
- Docker containerization
- GitHub Actions continuous integration
- Public deployment on Render
- Cloud MariaDB integration with Layerbase
- Production API verification

## Repository

GitHub:

https://github.com/aakashkohar6/internship-tracker