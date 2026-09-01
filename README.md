
# Event Ticketing Backend

**Simple, clean, production-ready Spring Boot backend for event & ticket management.**

---

## Overview

This repository implements an event ticketing backend with features such as:
- Event lifecycle (create, publish, cancel, update, delete)
- Ticket types and inventory management
- Ticket purchase flow with concurrency-safe stock updates
- QR code generation and ticket validation (scan)
- Role-based security (Keycloak): `ORGANIZER`, `ATTENDEE`, `STAFF`
- OpenAPI / Swagger documentation

The codebase favors clarity, separation of concerns (controller → service → repository), and pragmatic business rules.

---

## Tech stack

- Java 21
- Spring Boot 4.x (Web, Data JPA, Security, Validation)
- PostgreSQL
- Keycloak (OAuth2 / OIDC) as auth provider
- MapStruct (DTO ↔ Entity mapping)
- Lombok
- ZXing (QR generation)
- Springdoc OpenAPI (Swagger UI)

---

## Quick start (local)

### Prerequisites
- Java 21 SDK
- Maven
- Docker & Docker Compose (recommended)
- PostgreSQL (or run via Docker)
- Keycloak (local via Docker recommended)

### 1. Clone
```bash
git clone https://github.com/OneOmar/event-ticketing-backend.git
cd event-ticketing-backend
```

### 2. Configuration
Copy `src/main/resources/application.properties.example` → `src/main/resources/application.properties` and update DB / Keycloak settings.

> **Note:** If you use `git update-index --assume-unchanged` for `application.properties` (local override), remember that changes may be ignored by Git. Use environment variables for CI.

### 3. Build
MapStruct and Lombok use compile-time annotation processors. After changing mappers or DTOs always run a clean build:
```bash
mvn clean install
```
**Important:** If you modify MapStruct or Lombok related classes, do a full `mvn clean` before `install` to force regeneration of mapper classes.

### 4. Run
Start Postgres and Keycloak, then run:
```bash
mvn spring-boot:run
```
Or run with Docker Compose if `docker-compose.yml` is provided:
```bash
docker compose up --build
```

---

## APIs (examples)

> Base URL: `http://localhost:8080/api/v1`

### Create an event (ORGANIZER)
Request body example:
```json
{
  "title": "Publish An Event",
  "description": "PATH Testing",
  "location": "Casablanca",
  "bannerUrl": "https://example.com/banner.jpg",
  "startDate": "2026-09-15T18:00:00",
  "endDate": "2026-09-15T23:00:00",
  "capacity": 150,
  "ticketTypes": [
    { "name": "Standard", "price": 120, "quantity": 80 }
  ]
}
```
`POST /api/v1/events` (Authorization: `Bearer <token>` with `ORGANIZER` role)

### Publish an event (ORGANIZER)
`PATCH /api/v1/events/{eventId}/publish`

### Purchase tickets (ATTENDEE)
Request:
```json
{ "eventId": "UUID", "ticketTypeId": "UUID", "quantity": 2 }
```
`POST /api/v1/tickets/purchase` (role `ATTENDEE`)

### Get my tickets (ATTENDEE)
`GET /api/v1/tickets/me`

### Validate ticket (STAFF)
Request:
```json
{ "qrCode": "string-value-from-ticket" }
```
`POST /api/v1/tickets/validate` (role `STAFF`)

---

## Security & Roles

Security uses Keycloak JWT tokens. The application expects roles inside the `realm_access.roles` claim. JWT → Spring authorities mapping adds the `ROLE_` prefix.

If you change the JWT mapping or Keycloak configuration, restart the app and ensure tokens include `realm_access.roles`.

---

## Important notes & gotchas

- **MapStruct + Lombok**: Mapper implementations are generated at compile time. If you change mappers or DTOs you **must** run `mvn clean install` to regenerate classes. Failure leads to `ClassNotFound` or mapping problems.
- **Database migrations**: Keep a backup and run migrations locally when schema changes.
- **Concurrency (ticket purchase)**: TicketType purchase uses row-level locking (`FOR UPDATE`) to avoid overselling. Don't remove the locking unless you replace it with another safe strategy.
- **N+1 problem**: Use join-fetch queries / custom repository projections where necessary. There are example optimizations in `TicketRepository` to fetch QR codes with a single query when listing user tickets.
- **Config files**: Avoid committing environment-specific changes (DB ports, passwords). Use profiles or environment variables.
- **Logging**: SLF4J is used via Lombok `@Slf4j`. Exceptions are logged in `GlobalExceptionHandler`.

---

## Development workflow recommendations

- Create a feature branch: `git checkout -b feature/your-feature`
- Keep changes small and focused (one responsibility per PR)
- Run `mvn clean install` before pushing to ensure MapStruct and annotation processors generate code
- Use meaningful commit messages: `feat(tickets): add purchase flow with locking`

---

## Testing

- Manual via Swagger UI: `http://localhost:8080/swagger-ui.html` (or `/swagger-ui/index.html`)
- Use Postman / HTTPie with Bearer token from Keycloak.
- Unit tests exist for core services — run:
```bash
mvn test
```

---

## Useful Maven commands

- Clean & build: `mvn clean install`
- Run: `mvn spring-boot:run`
- Run tests only: `mvn test`
- Skip tests (not recommended locally): `mvn install -DskipTests`

---

## Contributing

1. Open an issue describing the change.
2. Create a branch `feature/xxx` based on `main`.
3. Make small commits, run `mvn clean install`.
4. Open a PR with description + testing steps.

---

## Maintainers
- Omar El Manssouri

---
