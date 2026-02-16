# LLM-Supported Web Development Learning Platform

This project is developed as a Bachelor's thesis project.

The goal is to create a web-based learning platform for web development where a Large Language Model (LLM) acts as a learning coach instead of simply generating full solutions. The platform provides exercises, allows students to submit answers, and generates reflective feedback to support deeper understanding.

## Thesis Context

**Title:** Developing a Learning Platform for Web Development with LLM Support  

**Author:** Jüri Tsõmbaljuk

**Supervisor:** Mohamad Gharib 

## Tech Stack

### Backend
- Java 17+
- Spring Boot
- Spring Web (REST API)
- Spring Data JPA (Hibernate)
- Spring Validation
- Spring Security (currently open access)
- Database: PostgreSQL (currently H2)

### Frontend (planned)
- Not implemented yet

## API Endpoints

### Health
- `GET /api/health` → returns `OK`

### Exercises
- `GET /api/exercises`
- `GET /api/exercises/{id}`
- `POST /api/exercises`
- `PUT /api/exercises/{id}`
- `DELETE /api/exercises/{id}`

### Users
- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `DELETE /api/users/{id}`

### Submissions
- `GET /api/submissions`
- `GET /api/submissions/{id}`
- `POST /api/submissions`
- `DELETE /api/submissions/{id}`

## Running the Backend

### Requirements
- Java 17+
- Maven

### Run locally
1. Clone the repository
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
4. API will be available at:
   http://localhost:8080/api

## API Overview
- /api/exercises – manage exercises
- /api/submissions – submit solutions and receive feedback
- /api/users – user management
- /api/health – health check
