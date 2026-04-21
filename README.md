# LLM-Supported Learning Platform for Web Development Education

This project was developed as part of a Bachelor's thesis.

This project was developed as part of a Bachelor's thesis. Its goal is to build a web-based learning platform for web development where a Large Language Model (LLM) acts as a learning coach rather than a direct solution generator. Instead of immediately providing full code answers, the system guides learners using hints, reflective questions, debugging prompts, and small-step scaffolding.

## Thesis Context

**Title:** Developing a Learning Platform for Web Development with LLM Support  
**Author:** Jüri Tsõmbaljuk  
**Supervisor:** Mohamad Gharib

## Project Overview

The platform allows a student to:

- create a student profile
- create a help session for a programming problem
- submit a question, code snippet, and what they have already tried
- continue the interaction as a multi-turn conversation
- receive coaching-style LLM responses instead of direct full solutions

The backend stores sessions and messages, including metadata about:

- coaching level
- prompt version
- LLM model
- whether a reply was filtered by policy

The frontend provides a simple interface for creating a session and chatting with the coach.

## Main Design Idea

The core idea of the project is that the LLM should function as a **coach**, not just a code generator.

The system aims to support:

- scaffolding
- reflection
- metacognition
- step-by-step reasoning
- responsible use of LLMs in programming learning

To support this, the backend includes:

- structured prompt building
- coaching level selection
- lightweight progress assessment
- policy filtering for overly direct replies
- fallback responses for failures or filtered outputs

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- Jakarta Validation
- Spring Security
- H2 database
- OpenAI Java SDK
- JUnit 5
- MockMvc

### Frontend
- React
- Vite
- Axios
- CSS

## Architecture Overview

### Backend modules
- **Controllers** – REST API endpoints
- **Services** – business logic, LLM orchestration, coaching logic
- **Repositories** – JPA access to database entities
- **DTOs** – request and response models
- **Entities** – `User`, `HelpSession`, `Message`
- **LLM layer** – abstraction over OpenAI and stub implementation
- **Policy layer** – filters overly direct or unsuitable LLM responses

### Frontend flow
1. Create a student user
2. Create a help session
3. Chat with the coach
4. View coach replies and conversation history

## Backend API

### Health
- `GET /api/health`

### Users
- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `DELETE /api/users/{id}`

### Help Sessions
- `POST /api/help-sessions`
- `GET /api/help-sessions/{id}`
- `GET /api/help-sessions/user/{userId}`

### Messages
- `GET /api/help-sessions/{id}/messages`
- `POST /api/help-sessions/{id}/messages`

## Running the Project

### Requirements
- Java 21
- Node.js
- npm
- Maven

## Backend
From the `backend` directory:

### Run with stub LLM
```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

### Run with OpenAI
```bash
SPRING_PROFILES_ACTIVE=openai ./mvnw spring-boot:run
```
### Run with OpenAI + dev profile
```bash
SPRING_PROFILES_ACTIVE=dev,openai ./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

## Frontend
From the frontend directory:

```bash
npm install
npm run dev
```

The frontend runs on:

```text
http://localhost:5173
```

## Database
The project currently uses an H2 file-based database for prototype development:

```properties
spring.datasource.url=jdbc:h2:file:./data/llmplatform
```

The H2 console is enabled only in the `dev` profile.

## OpenAI Configuration
The real LLM integration is enabled with the `openai` Spring profile.

The backend uses the OpenAI client from environment configuration, so your API key must be available in the environment when running with the `openai` profile.

If the `openai` profile is not enabled, the system uses a stub client for development and testing.

## Testing
Backend tests include:

- context loading
- controller tests
- validation tests
- message flow tests
- coach policy tests
- coaching level tests

Run backend tests with:

```bash
./mvnw test
```

## Current Limitations
This project is a prototype developed within the scope of a Bachelor's thesis.

Current limitations include:

- no authentication or user accounts beyond simple user creation
- simple UI focused on prototype evaluation rather than production polish
- lightweight heuristic progress detection
- policy filtering based on simple rule checks
- H2 used instead of a production database
- coaching quality depends on prompt design and LLM behavior

## Research Focus
This project is not intended to be a general-purpose coding assistant. Its main purpose is to explore how LLMs can be integrated into a learning platform in a way that supports:

- independent thinking
- conceptual understanding
- reflective learning
- guided debugging
- responsible interaction with LLMs
