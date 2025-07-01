
Leave Management System - Backend (Spring Boot)

This project is a Spring Boot-based REST API backend that supports dynamic form and workflow generation, form submission, role-based approval flow, and user management using **Keycloak** for authentication and authorization.

##  Features

-  JWT-based authentication using Keycloak
-  Dynamic form templates and rendering
-  Configurable workflows with state transitions
-  Role-based access control for workflow approvals
-  Form submission and workflow instance tracking
-  Admin, Manager, HR role support
-  CORS support for frontend integration

---

##  Technologies Used

- Java 17+
- Spring Boot
- Spring Security (OAuth2 + Keycloak)
- JPA (Hibernate)
- MySQL (or any JPA-compatible DB)
- Jackson (for JSON parsing)

---

##  Setup Instructions

### Prerequisites

- Java 17+
- Maven
- MySQL running
- Keycloak running with realm & client setup (e.g. `user_leave_api`)
- Frontend 

### 1. Clone the Repository

```bash
git clone https://github.com/your-repo/user-leave-request-backend.git
cd user-leave-request-backend
```

### 2. Configure `application.properties`

Update `src/main/resources/application.properties` with:

```properties
spring.datasource.url=dbc:mysql://localhost:3306/UserLeave?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/myrealm
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/myrealm/protocol/openid-connect/certs
```

### 3. Build & Run

```bash
./mvnw spring-boot:run
```

---

##  Authentication

This app uses Keycloak for identity and access management. All API requests require a bearer token obtained via the Keycloak login flow.

---

## 📁 Package Structure

```
com.example.user_leave_request
├── controller
│   ├── AdminController.java
│   ├── FormController.java
│   ├── UserController.java
│   └── WorkflowController.java
├── dto
├── model
├── repository
├── service
```

---

## 📡 REST API Endpoints

### 🔧 Admin APIs

| Endpoint | Method | Description |
|---------|--------|-------------|
| `/admin/forms` | POST | Create new form template |
| `/admin/workflows` | POST | Create new workflow template |
| `/admin/form-generate` | POST | Generate form schema by formType |

###  Form APIs

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/forms/submit` | POST | Submit filled form |
| `/forms/{formType}` | GET | Get form template by type |

### 👤 User APIs

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/user/me` | GET | Get current user info from JWT |

###  Workflow APIs

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/workflow/transition` | POST | Transition workflow (generic) |
| `/workflow/transition/MANAGER_APPROVED` | POST | Manager approval |
| `/workflow/transition/HR_APPROVED` | POST | HR approval |
| `/workflow/transition/REJECTED` | POST | Reject application |
| `/workflow/transition/{formType}` | GET | Get transitions for workflow |
| `/workflow/details/{workflowId}` | GET | View workflow instance details |
| `/workflow/pending/manager` | GET | View manager-pending approvals |
| `/workflow/pending/hr` | GET | View HR-pending approvals |

---

##  Roles

Roles are managed in Keycloak. Common roles include:

- `admin`
- `user`
- `manager`
- `hr`

These are extracted from both `realm_access.roles` and `resource_access.user_leave_api.roles`.

---

##  Sample Form Submission Request

```json
POST /forms/submit
Authorization: Bearer <JWT>

{
  "formType": "leave-request",
  "data": {
    "fromDate": "2025-07-01",
    "toDate": "2025-07-10",
    "reason": "Vacation"
  }
}
```

---

##  Sample Workflow Template Payload

```json
{
  "formType": "leave-request",
  "states": ["PENDING", "MANAGER_APPROVED", "HR_APPROVED", "REJECTED"],
  "transitions": [
    { "from": "PENDING", "to": "MANAGER_APPROVED", "allowedRoles": ["manager"] },
    { "from": "MANAGER_APPROVED", "to": "HR_APPROVED", "allowedRoles": ["hr"] },
    { "from": "PENDING", "to": "REJECTED", "allowedRoles": ["manager", "hr"] }
  ]
}
```

---

##  Testing Tips

- Use **Postman** with an access token.
- Set the `Authorization` header:  
  ```
  Bearer <access_token>
  ```
- Decode your JWT at [jwt.io](https://jwt.io/) to inspect roles and claims.

---

##  Deployment

For production, you may:

- Use **Docker** for containerization
- Connect to **external Keycloak**
- Enable HTTPS & CORS restrictions
- Configure for PostgreSQL/MySQL in cloud

---

##  License

This project is licensed under MIT.

---

##  Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit changes
4. Push to the branch
5. Create a Pull Request

---

##  Author

- **Your Name** - [Aparajita](https://github.com/kumaparajita104)
