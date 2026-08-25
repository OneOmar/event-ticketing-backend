# Keycloak Integration — Event Ticketing Backend

---

## Overview

This project uses **Keycloak** as an authentication and authorization server.

Keycloak provides:

*  User authentication (login/signup)
*  Role-based access control (RBAC)
*  JWT token generation
*  OAuth2 / OpenID Connect support

The Spring Boot backend acts as a **Resource Server** that validates JWT tokens issued by Keycloak.

---

## Architecture

```text
Client (Frontend / Postman)
        ↓
   Keycloak (Auth Server)
        ↓  (JWT Token)
Spring Boot API (Resource Server)
        ↓
    Database
```

---

## Local Setup (Docker)

### Services

| Service    | Port | Description           |
| ---------- | ---- | --------------------- |
| PostgreSQL | 5432 | Main database         |
| Adminer    | 8081 | DB management UI      |
| Keycloak   | 8082 | Authentication server |

---

### ▶️ Start services

```bash
docker-compose up -d
```

---

## Keycloak Setup

### 1. Access Admin Console

```
http://localhost:8082
```

Login:

* Username: `admin`
* Password: `admin`

---

### 2. Create a Realm

* Go to **Realm Settings**
* Click **Create Realm**
* Name:

```
event-ticketing
```

---

### 3. Create a Client

* Go to **Clients → Create**

#### Configuration:

| Field       | Value               |
| ----------- | ------------------- |
| Client ID   | event-ticketing-app |
| Client Type | OpenID Connect      |
| Access Type | Public              |

---

### 4. Configure Client

* Enable:

    * ✅ Standard Flow
    * ❌ Direct Access Grants (optional)

* Valid Redirect URI:

```
*
```

(For development only)

---

### 5. Create Roles

Go to:

```
Realm Roles → Create Role
```

Create:

* `ATTENDEE`
* `STAFF`
* `ORGANIZER`

---

### 6. Create Users

Go to:

```
Users → Create User
```

Then:

* Set username
* Set password
* Disable "Temporary"

---

### 7. Assign Roles

* Go to user → **Role Mapping**
* Assign roles:

Example:

```
STAFF → can validate tickets
ATTENDEE → can purchase tickets
```

---

## Spring Boot Configuration

### application.properties

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8082/realms/event-ticketing
```

---

## JWT Authentication Flow

```text
User logs in → Keycloak
        ↓
Receives JWT Token
        ↓
Sends request with Authorization header
        ↓
Spring validates token
        ↓
Access granted / denied
```

---

### Example Request

```http
POST /api/v1/tickets/validate
Authorization: Bearer <access_token>
```

---

## Security Configuration

### SecurityConfig Highlights

```java
@EnableMethodSecurity
```

→ Enables `@PreAuthorize`

---

### JWT Role Mapping

```java
converter.setAuthorityPrefix("ROLE_");
converter.setAuthoritiesClaimName("realm_access.roles");
```

 Important:

```text
Keycloak role: STAFF
→ Spring role: ROLE_STAFF
```

---

## Endpoint Security

Example:

```java
@PreAuthorize("hasRole('STAFF')")
```

---

## Current User Handling

Centralized using:

```
CurrentUserProvider
```

Responsibilities:

* Extract user ID from JWT
* Fetch user from DB
* Avoid duplication in services

---

## User Provisioning

Custom filter:

```
UserProvisioningFilter
```

Purpose:

* Automatically create/update user in DB
* Sync with Keycloak user

---

## Testing with Postman

### 1. Get Token

```
POST http://localhost:8082/realms/event-ticketing/protocol/openid-connect/token
```

Body (x-www-form-urlencoded):

```
client_id=event-ticketing-app
username=your_user
password=your_password
grant_type=password
```

---

### 2. Use Token

```http
Authorization: Bearer <access_token>
```

---

##  ⚠️ Important Notes

### ⚠️ Dev Mode Only

```yaml
command:
  - start-dev
  - --db=dev-file
```

  Not for production

---

### ⚠️ Weak Credentials

```text
admin / admin
```
  Change in production

---

### ⚠️ Public Client

```text
No client secret used
```

Use confidential client in production

---

## Production Improvements

* Use external DB for Keycloak (PostgreSQL)
* Enable HTTPS
* Configure proper redirect URIs
* Use client secret
* Add token expiration policies
* Enable refresh tokens

---

## Summary

```text
✔ Keycloak handles authentication
✔ Spring Boot validates JWT tokens
✔ Roles control access (RBAC)
✔ Clean integration with SecurityConfig
✔ Centralized user handling
```

---

## Useful URLs

* Keycloak Admin: http://localhost:8082
* API: http://localhost:8080
* Adminer: http://localhost:8081

---

## 💡 Final Thought

This setup provides a **production-ready authentication system** with:

* Strong security
* Clean architecture
* Scalability

---
