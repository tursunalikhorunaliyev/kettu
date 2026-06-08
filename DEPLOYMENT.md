# Deployment

Kettu is a standard Spring Boot application that builds into a single executable
JAR. This guide covers running it locally and notes for production. It documents
only what the repository actually supports.

## Build artifact

```bash
./gradlew clean build
```

This produces an executable (fat) JAR in `build/libs/`:

- `kettu-0.0.1-SNAPSHOT.jar` — the runnable Spring Boot application.
- `kettu-0.0.1-SNAPSHOT-plain.jar` — the plain classes-only JAR (not runnable on
  its own).

Run the executable JAR with:

```bash
java -jar build/libs/kettu-0.0.1-SNAPSHOT.jar
```

(The version comes from `build.gradle` and will change as the project evolves.)

## Runtime requirements

- **JRE/JDK 21**.
- **PostgreSQL 14+ with the PostGIS extension**, reachable at the configured
  `DB_URL`. Flyway runs the migrations in `src/main/resources/db/migration` on
  startup, so the database user needs privileges to apply them.
- Network access to **Google OAuth2** and **Cloudflare R2**.

## Environment variables

All configuration is supplied via environment variables (no secrets are baked into
the artifact). The full list is in [CONFIGURATION.md](CONFIGURATION.md) and
[`.env.example`](.env.example). At minimum, production needs:

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- `JWT_SECRET` (a strong, private value — do **not** rely on the dev fallback)
- `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_ENDPOINT`, `R2_BUCKET`

## Local deployment

```bash
cp .env.example .env
# edit .env with real values, then export them:
set -a; source .env; set +a

./gradlew bootRun
# or run the built jar:
java -jar build/libs/kettu-0.0.1-SNAPSHOT.jar
```

The application listens on `http://localhost:8080`. Swagger UI is available at
`/swagger-ui.html`.

## Production deployment notes

- Provide all required environment variables through your platform's secret
  manager; never commit them.
- Override `JWT_SECRET` with a strong, random value and keep it private.
- The OAuth2 success handler currently redirects to a hard-coded
  `http://localhost:8080/auth-redirect.html?token=...` URL. For a non-local
  deployment this redirect target needs to be updated in
  `CustomOAuth2SuccessHandler`; making it configurable is on the
  [roadmap](ROADMAP.md). _Document/track this before deploying to a public host._
- Ensure the Google OAuth2 client's authorized redirect URIs match your deployment
  host.
- Run behind HTTPS (terminate TLS at a reverse proxy or load balancer).
- Set appropriate `spring.servlet.multipart` limits if your upload sizes differ
  from the defaults (10MB file / 50MB request).

### Container image

The Spring Boot Gradle plugin can build an OCI image without a Dockerfile:

```bash
./gradlew bootBuildImage
```

This **requires a working Docker/Buildpacks environment**. No `Dockerfile` is
committed in this repository.

## Logging

- Logging uses SLF4J/Logback (Spring Boot default), output to the console.
- SQL logging defaults to `INFO`; set `HIBERNATE_SQL_LOG_LEVEL=DEBUG` (and
  `HIBERNATE_BIND_LOG_LEVEL=TRACE`) only in non-production environments for
  debugging — bound parameter values may include sensitive data.
- The global exception handler logs unexpected errors server-side while returning a
  generic message to clients.

## Health checks

There are **no dedicated health/metrics endpoints**: Spring Boot Actuator is not a
dependency. As a basic liveness signal you can probe a lightweight public endpoint
(for example `GET /api/places`) or the root page. Adding Actuator for proper
health/readiness probes is on the [roadmap](ROADMAP.md).

## Rollback notes

- **Application:** redeploy the previous known-good JAR/image. The app is stateless
  apart from the database and object storage, so reverting the artifact is the
  primary rollback step.
- **Database:** Flyway migrations are forward-only. Do not delete or edit applied
  migration files. If a migration must be undone, add a new corrective migration;
  restore from a database backup only as a last resort. Test migrations in a staging
  environment before production.
