# Medium Clone — REST API

A backend application inspired by Medium, built using Java and Spring Boot. The project provides REST APIs for user authentication and article management, with JWT-based security and MySQL persistence.

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Database:** MySQL
- **ORM:** Spring Data JPA / Hibernate
- **Security:** Spring Security, JWT, BCrypt password hashing
- **API Documentation:** Swagger UI / OpenAPI
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito

## Features

### User Authentication
- User registration and login
- Password hashing using BCrypt
- JWT-based authentication
- Protected endpoints using Spring Security

### Article Management
- Create articles
- Retrieve all articles
- Retrieve an article by ID
- Update existing articles
- Delete articles
- Restrict article updates and deletion to the article owner
- Automatically track article creation and update timestamps

### Pagination and Sorting
- Paginated article retrieval
- Configurable page number and page size
- Sorting articles by supported fields, including creation date

### API Documentation
- Interactive API documentation using Swagger UI
- Test API endpoints directly from the browser
- JWT authorization support for protected endpoints

### Testing
- Unit tests for user and article services
- Tests for successful operations, validation of business rules, and error scenarios
- Mockito-based dependency isolation

## API Endpoints

All endpoints use the base URL `http://localhost:8080`.

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/users/register` | Register a user | Public |
| POST | `/users/login` | Log in and obtain a JWT | Public |
| POST | `/articles` | Create an article | Required |
| GET | `/articles` | Retrieve paginated articles | Required |
| GET | `/articles/{id}` | Retrieve an article by ID | Required |
| PUT | `/articles/{id}` | Update an article | Required |
| DELETE | `/articles/{id}` | Delete an article | Required |

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- MySQL Server
- IntelliJ IDEA or another Java IDE
- Git

### 1. Clone the repository

```bash
git clone https://github.com/Ankiiii-123/medium-clone.git
cd medium-clone
```

### 2. Create the database

Open MySQL and execute:

```sql
CREATE DATABASE medium_clone;
```

### 3. Configure the application

Configure your database credentials and JWT settings in `src/main/resources/application.properties`.

Example configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/medium_clone
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.jpa.hibernate.ddl-auto=update

jwt.secret=YOUR_LONG_RANDOM_SECRET
jwt.expiration=86400000
```

Replace the placeholders with your local configuration. Keep database passwords and JWT secrets private; do not commit real credentials to GitHub. The expiration value above represents 24 hours in milliseconds if your JWT service uses milliseconds.

### 4. Run the application

Run `Medium