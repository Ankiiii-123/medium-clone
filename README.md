# Medium Clone — Java Spring Boot Backend

A RESTful blogging application inspired by Medium, built using Java and Spring Boot. The project demonstrates backend development concepts including JWT-based authentication, authorization, article management, database integration, input validation, pagination, sorting, and automated testing.

## Features

- **User Registration and Login:** Register users and authenticate using email and password.
- **Password Encryption:** Securely hash passwords using BCrypt.
- **JWT Authentication:** Protect API endpoints using JSON Web Tokens.
- **Article Management:** Create, retrieve, update, and delete articles.
- **Authorization:** Restrict article updates and deletion to the article's author.
- **Pagination:** Retrieve articles in manageable pages.
- **Sorting:** Sort articles by fields such as creation date.
- **Input Validation:** Reject invalid requests, including blank article titles and content.
- **Exception Handling:** Handle errors such as duplicate registrations, invalid credentials, missing articles, and unauthorized operations.
- **Interactive API Documentation:** Explore and test endpoints using Swagger UI.
- **Automated Testing:** Test application services, authentication, authorization, validation, pagination, and sorting.

## Tech Stack

| Category | Technologies |
|---|---|
| Language | Java |
| Backend Framework | Spring Boot |
| Database | MySQL |
| ORM and Persistence | Spring Data JPA, Hibernate |
| Security | Spring Security, JWT |
| Password Hashing | BCrypt |
| API Design | REST |
| API Documentation | Springdoc OpenAPI, Swagger UI |
| Build Tool | Maven |
| Testing | JUnit 5, Mockito, Spring Boot Test, MockMvc |
| Version Control | Git, GitHub |

## Getting Started

### Prerequisites

Make sure you have installed:

- JDK compatible with the project configuration
- MySQL Server
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/Ankiiii-123/medium-clone.git
cd medium-clone
```

### 2. Create the Database

Open MySQL and execute:

```sql
CREATE DATABASE medium_clone;
```

### 3. Configure the Application

Open `src/main/resources/application.properties` and configure your local database credentials and JWT secret.

Example configuration:

```properties
spring.application.name=medium-clone

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/medium_clone
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

Configure the `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` environment variables before running the application.

**Security note:** Never commit database credentials, JWT secrets, or other sensitive configuration to GitHub.

The example above assumes your application supports these environment variable placeholders. Keep your existing working configuration if your setup differs.

### 4. Run the Application

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

Ensure MySQL is running and the application configuration is correct.

## API Documentation — Swagger UI

The project uses Springdoc OpenAPI to generate interactive API documentation.

With the application running, open:

**http://localhost:8080/swagger-ui/index.html**

Swagger UI allows you to:

- Explore available API endpoints and HTTP methods.
- Inspect request parameters, request bodies, and responses.
- Execute API requests directly from the browser.
- Authenticate using JWT bearer tokens for protected endpoints.

### Using JWT Authentication in Swagger UI

1. Register a user through the registration endpoint.
2. Log in and copy the JWT token returned in the response.
3. Click **Authorize** in Swagger UI.
4. Enter the token according to the configured bearer authentication scheme.
5. Execute protected requests and inspect their responses.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/users/register` | Register a new user | Not required |
| POST | `/users/login` | Log in and obtain a JWT | Not required |
| GET | `/users/profile` | Access the protected profile endpoint | Required |

### Articles

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/articles` | Create an article | Required |
| GET | `/articles` | Retrieve paginated articles | Required |
| GET | `/articles/{id}` | Retrieve an article by ID | Required |
| PUT | `/articles/{id}` | Update an article | Required; article owner |
| DELETE | `/articles/{id}` | Delete an article | Required; article owner |

For protected endpoints, send the JWT in the request header:

```http
Authorization: Bearer <your-jwt-token>
```

## Pagination and Sorting

The article listing endpoint supports pagination and sorting through query parameters.

### Retrieve the First Page

```http
GET /articles?page=0&size=2
```

This requests the first page with up to two articles.

### Sort by Creation Date

```http
GET /articles?page=0&size=5&sort=createdAt,desc
```

This requests up to five articles, sorted by creation date in descending order.

- `page` specifies the zero-based page number.
- `size` specifies the number of articles per page.
- `sort` specifies the field and sort direction.

## Error Handling and Validation

The application handles common error scenarios, including:

- Duplicate email registration.
- Invalid login credentials.
- Requests without valid authentication.
- Attempts to modify articles owned by another user.
- Requests for articles that do not exist.
- Invalid article requests, including blank titles and content.

The application uses appropriate HTTP status codes for common error scenarios, including `400`, `401`, `403`, `404`, and `409`, depending on the exception and request.

## Running Tests

The project includes unit tests and integration tests for important application behaviors.

Run the complete Maven verification lifecycle:

**Windows PowerShell**

```powershell
.\mvnw.cmd clean verify
```

**macOS or Linux**

```bash
./mvnw clean verify
```

The tests cover scenarios such as:

- User registration and login.
- Password validation.
- Article creation, retrieval, updating, and deletion.
- Article ownership and authorization.
- Handling missing articles and users.
- Rejecting blank article titles and content.
- Requests with and without JWT authentication.
- Pagination and sorting through the API.

## Project Structure

The application follows a layered backend architecture:

- **Controller:** Handles incoming HTTP requests and returns responses.
- **Service:** Contains business logic for users and articles.
- **Repository:** Provides database access through Spring Data JPA.
- **Entity:** Represents persistent database objects.
- **DTO:** Defines request and response data structures.
- **Security:** Handles JWT authentication and endpoint protection.
- **Exception Handling:** Provides centralized handling for application errors.
- **Tests:** Verifies service logic and API behavior.

## Key Learning Outcomes

Through this project, I practised:

- Designing RESTful APIs with Spring Boot.
- Implementing JWT authentication and authorization.
- Integrating a Spring Boot application with MySQL.
- Using Spring Data JPA for database operations.
- Implementing validation and centralized exception handling.
- Adding pagination and sorting to REST endpoints.
- Writing unit and integration tests.
- Documenting APIs with OpenAPI and Swagger UI.

## Author

**Ankita Pundir**

GitHub: [Ankiiii-123](https://github.com/Ankiiii-123)

Project Repository: [medium-clone](https://github.com/Ankiiii-123/medium-clone)
