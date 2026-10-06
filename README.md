# Internship Application Tracker
A REST API that allows users to track and manage internship applications, 
including creating, viewing, updating, searching, and deleting applications.

## Features

- Create internship applications
- View individual applications or application lists
- Update application status
- Delete applications
- Filter applications by status and company
- Paginate application results with page metadata
- Validate requests and return structured API errors

## Tech Stack

- Java
- Spring Boot
- MariaDB
- JDBC
- Maven

## Architecture

```text
HTTP Client
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
MariaDB
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/applications` | Create a new internship application |
| GET | `/applications` | Retrieve a paginated list of applications |
| GET | `/applications/{id}` | Retrieve an application by ID |
| PATCH | `/applications/{id}` | Update an application's status |
| DELETE | `/applications/{id}` | Delete an application by ID |

### GET `/applications` Query Parameters

| Parameter | Description | Required | Default |
|-----------|-------------|----------|---------|
| `status` | Filter applications by status | No | No filter |
| `company` | Filter applications by company name | No | No filter |
| `page` | Page number (zero-based) | No | `0` |
| `size` | Maximum number of applications per page | No | `10` |

## Example Requests

### Create an Application

**Request**

```http
POST /applications
Content-Type: application/json
```

```json
{
  "company": "Microsoft",
  "position": "Software Engineer Intern",
  "status": "APPLIED",
  "location": "Seattle"
}
```

**Response — `201 Created`**

```json
{
  "company": "Microsoft",
  "position": "Software Engineer Intern",
  "status": "APPLIED",
  "location": "Seattle",
  "id": 80
}
```
> The ID is generated automatically by the database using `AUTO_INCREMENT`.

### Get Applications with Filtering and Pagination

```http
GET /applications?status=INTERVIEW&page=0&size=5
```

**Response — `200 OK`**

```json
{
  "content": [
    {
      "company": "Discord",
      "id": 79,
      "location": "Remote",
      "position": "Backend Engineer Intern",
      "status": "INTERVIEW"
    }
  ],
  "page": 0,
  "size": 5,
  "totalElements": 18,
  "totalPages": 4
}
```

### Update Application Status

**Request**

```http
PATCH /applications/79
Content-Type: application/json
```

```json
{
  "status": "OFFER"
}
```

**Response — `204 No Content`**

### Delete an Application

```http
DELETE /applications/79
```

**Response — `204 No Content`**

## Running Locally

### Prerequisites

Before running the application, make sure you have:

- Java 21 or later
- Maven
- MariaDB
- Git

### Database Setup

Create the MariaDB database:

```sql
CREATE DATABASE internship_tracker;

USE internship_tracker;
```

Create the applications table:

```sql
CREATE TABLE internship_applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company VARCHAR(100),
    position VARCHAR(100),
    status VARCHAR(30),
    location VARCHAR(100)
);
```

### Database User

Create a dedicated MariaDB user for the application. Replace `your_password` with your own password:

```sql
CREATE USER 'internship_app_user'@'localhost'
IDENTIFIED BY 'your_password';

GRANT SELECT, INSERT, UPDATE, DELETE
ON internship_tracker.*
TO 'internship_app_user'@'localhost';
```

### Environment Variable

Set the `DB_PASSWORD` environment variable to the password you chose for the database user.

**Windows PowerShell:**

```powershell
$env:DB_PASSWORD="your_password"
```

**macOS/Linux:**

```bash
export DB_PASSWORD="your_password"
```

Do not commit database passwords or other credentials to Git.

### Clone and Run

Clone the repository:

```bash
git clone https://github.com/aakashkohar6/internship-tracker.git
cd internship-tracker
```

Build the project:

```bash
mvn clean package
```

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

For example:

```http
GET http://localhost:8080/applications
```