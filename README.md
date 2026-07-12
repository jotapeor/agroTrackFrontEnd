# AgroTrack Frontend

Server-rendered web client for AgroTrack, an agricultural fleet management system developed as an undergraduate thesis project (TCC). Consumes the [AgroTrack Backend](https://github.com/jotapeor/agroTrackBackEnd) REST API.

## Project Status

Under active development. The features currently implemented are:

- Login and session management
- Forced password change on first access
- Collaborator registration form (owner only)

Fleet management screens (vehicles, tracking, maintenance, trip logging, and related functionality) are planned but not yet implemented. This README will be updated as the scope grows.

## Tech Stack

- Java 21
- Spring Boot 4.1 (Web MVC, Thymeleaf)
- Bootstrap 5.3.8
- Maven

## Architecture

Classic MVC, server-side rendered with Thymeleaf. `ApiService` wraps a `RestClient` that calls the backend REST API. The JWT issued by the backend is stored in the HTTP session after login and attached to outgoing API calls; there is no client-side token storage. User role is decoded from the JWT payload to restrict owner-only pages.

## Routes

| Path | Access | Description |
|------|--------|--------------|
| `/` | Public | Landing page |
| `/login` | Public | Login form |
| `/dashboard` | Authenticated | Post-login landing page; prompts a password change on first access |
| `/novo-colaborador` | Authenticated (owner) | Collaborator registration form |
| `/logout` | Authenticated | Invalidates the session |

## Requirements

- JDK 21+
- Maven 3.9+
- A running instance of [agroTrackBackEnd](https://github.com/jotapeor/agroTrackBackEnd) on `localhost:8080`

## Setup

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8081`. Start the backend first; this application has no data layer of its own and depends entirely on the API being reachable.

## Configuration

The backend base URL is currently hardcoded in `ApiService`. To point at a different backend instance, update the `baseUrl` value or externalize it via `application.properties` and an environment variable (e.g. `API_BASE_URL`).

## Related Project

[agroTrackBackEnd](https://github.com/jotapeor/agroTrackBackEnd) — Spring Boot REST API providing authentication and user management for this client.
