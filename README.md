# Kettu

[![CI](https://github.com/tursunalikhorunaliyev/kettu/actions/workflows/ci.yml/badge.svg)](https://github.com/tursunalikhorunaliyev/kettu/actions/workflows/ci.yml)

Kettu is a **Spring Boot backend for a location / "places" service**. Users sign in
with Google (OAuth2), receive a JWT, and can create and browse places with photos,
geo-located districts and regions, categories and tags. Images are stored in
Cloudflare R2 (S3-compatible) and geospatial lookups run on PostgreSQL + PostGIS.

> This is a backend/API project. There is no first-party web or mobile UI in this
> repository; only two static helper pages (`index.html`, `auth-redirect.html`) used
> during the OAuth2 login flow.

## Table of contents

- [Main features](#main-features)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Requirements](#requirements)
- [Installation](#installation)
- [Build, test, run](#build-test-run)
- [Configuration](#configuration)
- [API](#api)
- [Usage examples](#usage-examples)
- [Testing](#testing)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Status & roadmap](#status--roadmap)
- [License](#license)

## Main features

Based on the controllers and services actually present in the codebase:

- **Google OAuth2 login** that issues a JSON Web Token (`/api/auth/login`).
- **JWT-secured REST API** with a stateless `Authorization: Bearer <token>` filter.
- **Places**: create (multipart, with main + additional photos), update, change
  moderation status, list/filter, and check in-progress uploads.
- **Resources**: CRUD-style management of categories, countries, regions,
  districts and tags, including **bulk import from Excel/GeoJSON files**.
- **Geo lookup**: reverse-resolve a latitude/longitude point to its region and
  district using PostGIS (`ST_Contains`).
- **File storage**: upload (single/multiple) and download images via Cloudflare R2.
- **User profile**: `me`, profile/background photo update, first-login flag.
- **Role-based authorization** (`USER` / `ADMIN`), centralized exception handling,
  Flyway database migrations, and Swagger/OpenAPI documentation.

## Tech stack

| Concern            | Technology                                              |
|--------------------|---------------------------------------------------------|
| Language / runtime | Java 21 (LTS)                                           |
| Framework          | Spring Boot 3.4.x (Web, Data JPA, Security, OAuth2 client) |
| Build              | Gradle (wrapper, `./gradlew`)                           |
| Database           | PostgreSQL with PostGIS, Hibernate Spatial             |
| Migrations         | Flyway (`src/main/resources/db/migration`)             |
| Auth               | Google OAuth2 login → JWT (`io.jsonwebtoken` / jjwt)   |
| Object storage     | Cloudflare R2 via AWS SDK v2 (S3)                       |
| Spreadsheet import | Apache POI                                              |
| Image processing   | Thumbnailator                                          |
| Geometry           | JTS / `jts2geojson`                                    |
| API docs           | springdoc OpenAPI / Swagger UI                          |
| Tests              | JUnit 5, Mockito, AssertJ, Spring Security Test         |

## Project structure

```
kettu/
├── build.gradle                 # Gradle build, dependencies, Java 21 toolchain
├── settings.gradle              # rootProject.name = 'kettu'
├── gradlew / gradlew.bat        # Gradle wrapper
├── .env.example                 # Template for required environment variables
├── .github/workflows/ci.yml     # GitHub Actions CI (build + unit tests)
└── src/
    ├── main/
    │   ├── java/com/khorunaliyev/kettu/
    │   │   ├── KettuApplication.java        # Spring Boot entry point
    │   │   ├── component/                   # Reusable helpers (diff checkers, projection utils, user context)
    │   │   ├── config/                      # Security, OAuth2, JWT, async, locale, swagger, R2, exception advice
    │   │   ├── controller/                  # REST controllers (auth, place, geo, resource, storage, user)
    │   │   ├── dto/                         # Request/response DTOs and JPA projections
    │   │   ├── entity/                      # JPA entities (auth, place, resources, auditing, enums)
    │   │   ├── repository/                  # Spring Data JPA repositories
    │   │   ├── services/                    # Business logic (place, resource, geo, storage, user)
    │   │   └── util/query/                  # Native query strings & JPA specifications
    │   └── resources/
    │       ├── application.yml              # Configuration (all secrets externalized to env vars)
    │       ├── db/migration/               # Flyway SQL migrations (V1, V2)
    │       └── static/                      # OAuth2 helper HTML pages
    └── test/java/com/khorunaliyev/kettu/   # JUnit 5 unit tests + tagged integration smoke test
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for a deeper module/flow breakdown.

## Requirements

- **JDK 21** (the Gradle toolchain targets Java 21).
- **PostgreSQL 14+ with the PostGIS extension** — required to run the app and the
  integration smoke test.
- A **Google OAuth2 client** (client id + secret) for login.
- A **Cloudflare R2** bucket and credentials for image storage.

The unit-test suite (`./gradlew test`) needs none of the above — it runs fully
in-memory with mocks.

## Installation

```bash
git clone https://github.com/tursunalikhorunaliyev/kettu.git
cd kettu
chmod +x ./gradlew          # first checkout only, if needed
cp .env.example .env        # then fill in real values
```

## Build, test, run

```bash
# Compile, run unit tests, and produce the jar
./gradlew clean build

# Unit tests only (no database required)
./gradlew test

# Integration / full-context smoke test (requires Postgres+PostGIS and env vars)
./gradlew integrationTest

# Run the application (needs DB + env vars configured)
./gradlew bootRun
```

The app listens on `http://localhost:8080` by default.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

## Configuration

All secrets and environment-specific values are read from environment variables;
**no real credentials are committed**. See [`.env.example`](.env.example) and the
full reference in [CONFIGURATION.md](CONFIGURATION.md). Key variables:

| Variable               | Required  | Default                                  | Purpose                     |
|------------------------|-----------|------------------------------------------|-----------------------------|
| `DB_URL`               | no        | `jdbc:postgresql://localhost:5433/kettu` | JDBC URL                    |
| `DB_USERNAME`          | no        | `postgres`                               | DB user                     |
| `DB_PASSWORD`          | **yes**   | _(empty)_                                | DB password                 |
| `GOOGLE_CLIENT_ID`     | **yes**   | _(empty)_                                | Google OAuth2 client id     |
| `GOOGLE_CLIENT_SECRET` | **yes**   | _(empty)_                                | Google OAuth2 client secret |
| `JWT_SECRET`           | **yes**\* | dev fallback (override in prod!)         | JWT signing secret          |
| `JWT_EXPIRATION_MS`    | no        | `144000000` (~40h)                       | Token lifetime in ms        |
| `R2_ACCESS_KEY`        | **yes**   | _(empty)_                                | Cloudflare R2 access key     |
| `R2_SECRET_KEY`        | **yes**   | _(empty)_                                | Cloudflare R2 secret key     |
| `R2_ENDPOINT`          | **yes**   | _(empty)_                                | R2 S3 endpoint              |
| `R2_BUCKET`            | no        | `khorunaliyev-kettu`                     | R2 bucket name              |

\* `JWT_SECRET` has a development fallback so the app boots locally, but it **must**
be overridden with a strong, private value in production.

## API

The service exposes a JSON REST API under `/api/**`. Full endpoint reference,
request/response examples and error formats are in **[API.md](API.md)**. Quick map:

| Area      | Base path            | Notes                                          |
|-----------|----------------------|------------------------------------------------|
| Auth      | `/api/auth/**`       | `login` redirects to Google OAuth2             |
| Places    | `/api/places/**`     | `GET` is public; writes require authentication |
| Geo       | `/api/geo-data`      | point → region/district lookup                 |
| Resources | `/api/resources/**`  | category/country/region/district/tag; writes are `ADMIN` only |
| Files     | `/api/files/**`      | image upload/download via R2                    |
| User      | `/api/user/**`       | profile, `me`, first-login                     |

## Usage examples

```bash
# 1. Open the login flow in a browser (redirects to Google):
#    http://localhost:8080/api/auth/login
#    After consent you are redirected to /auth-redirect.html?token=<JWT>

# 2. Public: list places
curl "http://localhost:8080/api/places"

# 3. Geo lookup: resolve a point to region/district
curl "http://localhost:8080/api/geo-data?lat=41.31&long=69.24"

# 4. Authenticated: fetch the current user
curl "http://localhost:8080/api/user/me" \
  -H "Authorization: Bearer <JWT>"
```

See [API.md](API.md) for request bodies and response shapes.

## Testing

- **Unit tests** in `src/test/java` use JUnit 5 + Mockito + AssertJ and require no
  external services. They cover JWT generation/validation, the JWT auth filter's
  fail-closed behaviour, the global exception handler (incl. no information leakage),
  the R2 storage service, geo lookups, projection helpers and the place diff checker.
- **Integration test** `KettuApplicationTests` boots the full Spring context, is
  tagged `integration`, is excluded from the default `test` task, and is run with
  `./gradlew integrationTest` when a database is available.

Details and guidelines: **[TESTING.md](TESTING.md)**.

## Security

- All credentials are externalized to environment variables; secrets are never
  committed (see [`.gitignore`](.gitignore)).
- The global exception handler never leaks stack traces or internal messages to
  clients.
- The JWT filter fails closed on malformed/expired/tampered tokens.

> ⚠️ Earlier revisions of this repository committed live database, Google and
> Cloudflare credentials. Those values are now externalized, but because they were
> public they should be treated as **compromised and rotated**.

To report a vulnerability, see **[SECURITY.md](SECURITY.md)**.

## Troubleshooting

| Symptom                                                            | Fix                                                              |
|-------------------------------------------------------------------|-----------------------------------------------------------------|
| `Cannot find a Java installation ... matching languageVersion=21` | Install JDK 21 (the build toolchain).                           |
| `ConnectException` / Flyway fails on startup                      | Start PostgreSQL+PostGIS and set `DB_*` variables.              |
| OAuth2 login fails / redirect loop                               | Set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`.             |
| R2 upload errors                                                  | Set `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_ENDPOINT`, `R2_BUCKET`. |
| `permission denied: ./gradlew`                                   | `chmod +x ./gradlew`.                                          |

More questions: **[FAQ.md](FAQ.md)**.

## Contributing

Contributions are welcome. Please read **[CONTRIBUTING.md](CONTRIBUTING.md)** and the
**[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)** before opening an issue or pull request.
Use the provided issue and pull-request templates.

## Status & roadmap

**Status:** active development / pre-release (`0.0.1-SNAPSHOT`). The API and database
schema may still change. See **[ROADMAP.md](ROADMAP.md)** for planned work and
**[CHANGELOG.md](CHANGELOG.md)** for what has changed.

## License

No `LICENSE` file is currently present in this repository, so the project is **"all
rights reserved"** by default until the maintainer adds one. _Maintainer: add a
`LICENSE` file (e.g. MIT/Apache-2.0) and update this section accordingly._
