
# Leave Management System - Backend (Spring Boot)

This is the backend of a Role-Based Leave Management System built using **Spring Boot**. It supports dynamic form templates, state machine workflows, and role-based access for Admins, Employees, Managers, and HRs.

## 🚀 Features

- 🔐 JWT-based authentication (via Keycloak)
- 📄 Dynamic form submission and retrieval
- 🔄 Workflow state transitions with role validation
- 🧠 Role-based endpoints for approval/rejection
- 🛠 Admins can define workflows and form schemas

## 🧰 Tech Stack

- Java 17+
- Spring Boot 3.x
- MySQL
- Keycloak for authentication
- Maven

## 📁 Project Structure

```bash
src/
├── controller/         # REST controllers
├── model/              # JPA entity models
├── repository/         # Spring Data JPA repositories
├── service/            # Business logic
└── config/             # Security configuration
```

## 🔧 Setup Instructions

### 1. Prerequisites

- Java 17+ installed
- MySQL running locally
- Keycloak instance (e.g., `http://localhost:8080`)
- Maven

### 2. Configure MySQL

Create a database and update the `application.properties`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/UserLeave
    username: root
    password: 
```

### 3. Configure Keycloak

In `application.properties`:

```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/myrealm
```

Roles used:

- `client_admin`
- `client_employee`
- `client_manager`
- `client_hr`

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

## 🔄 API Endpoints

### Admin

- `POST /admin/forms` — Save form schema
- `POST /admin/form-generate` — Get schema by form type
- `POST /workflow/create` — Create workflow template

### Employee

- `POST /forms/submit` — Submit leave request

### Manager/HR

- `GET /workflow/pending/manager` — Pending approvals for manager
- `GET /workflow/pending/hr` — Pending approvals for HR
- `POST /workflow/transition/{nextState}` — Transition workflow (approve/reject)

### Common

- `GET /workflow/details/{workflowId}` — View form data for a workflow

## 📌 Notes

- JWT tokens must be sent in the `Authorization: Bearer <token>` header.
- Role validation is enforced via `@AuthenticationPrincipal` and checked programmatically.

## 👩‍💻 Author

Built by Aparajita Kumari using Spring Boot, MySQL, and Keycloak.
