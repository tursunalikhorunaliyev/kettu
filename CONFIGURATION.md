# Configuration

All runtime configuration lives in `src/main/resources/application.yml`. Every
secret and environment-specific value is read from an **environment variable** using
the `${VAR:default}` syntax, so no credentials are stored in the repository.

## Config files

| File                                          | Purpose                                                      |
|-----------------------------------------------|--------------------------------------------------------------|
| `src/main/resources/application.yml`          | Single source of application configuration.                  |
| `.env.example`                                | Template listing every environment variable (copy to `.env`).|
| `src/main/resources/db/migration/*.sql`       | Flyway schema migrations applied on startup.                 |

`.env`, `application-local.yml`, and similar local secret files are git-ignored.

## Environment variables

| Variable                  | Required  | Default                                  | Description                                                  |
|---------------------------|-----------|------------------------------------------|--------------------------------------------------------------|
| `DB_URL`                  | No        | `jdbc:postgresql://localhost:5433/kettu` | JDBC URL for PostgreSQL (PostGIS).                           |
| `DB_USERNAME`             | No        | `postgres`                               | Database username.                                          |
| `DB_PASSWORD`             | **Yes**   | _(empty)_                                | Database password.                                         |
| `GOOGLE_CLIENT_ID`        | **Yes**   | _(empty)_                                | Google OAuth2 client id (public by OAuth design).          |
| `GOOGLE_CLIENT_SECRET`    | **Yes**   | _(empty)_                                | Google OAuth2 client secret (sensitive).                   |
| `JWT_SECRET`              | **Yes**\* | dev fallback value                        | HMAC signing secret for JWTs.                              |
| `JWT_EXPIRATION_MS`       | No        | `144000000` (~40 hours)                  | Token lifetime in milliseconds.                            |
| `R2_ACCESS_KEY`           | **Yes**   | _(empty)_                                | Cloudflare R2 access key (sensitive).                      |
| `R2_SECRET_KEY`           | **Yes**   | _(empty)_                                | Cloudflare R2 secret key (sensitive).                      |
| `R2_REGION`               | No        | `auto`                                   | R2 region.                                                |
| `R2_ENDPOINT`            | **Yes**   | _(empty)_                                | R2 S3-compatible endpoint URL.                            |
| `R2_BUCKET`              | No        | `khorunaliyev-kettu`                     | R2 bucket name.                                           |
| `HIBERNATE_SQL_LOG_LEVEL` | No        | `INFO`                                   | Log level for Hibernate SQL statements.                   |
| `HIBERNATE_BIND_LOG_LEVEL`| No        | `INFO`                                   | Log level for SQL bound parameters (use `TRACE` for debug only). |

\* `JWT_SECRET` has a development fallback so the app can boot locally, but it
**must** be overridden in any shared or production environment.

## Required vs optional

- **Required for the app to function:** `DB_PASSWORD`, `GOOGLE_CLIENT_ID`,
  `GOOGLE_CLIENT_SECRET`, `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_ENDPOINT`, and a
  production-grade `JWT_SECRET`.
- **Optional (sensible defaults):** `DB_URL`, `DB_USERNAME`, `JWT_EXPIRATION_MS`,
  `R2_REGION`, `R2_BUCKET`, and the logging levels.

Defaults are only meant for local development; they intentionally do not include any
real credential.

## Fixed (non-env) settings in `application.yml`

These are configured directly and are not environment-driven:

- `spring.mvc.throw-exception-if-no-handler-found: true` and
  `spring.web.resources.add-mappings: false` — so unknown paths produce a handled
  404 instead of a default static lookup.
- Multipart limits: `max-file-size: 10MB`, `max-request-size: 50MB`.
- JPA: `hibernate.ddl-auto: none` (schema is owned by Flyway), PostGIS dialect.
- Flyway: `enabled: true`, `baseline-on-migrate: true`.

## Local development example

```bash
cp .env.example .env
# Edit .env with your local values, then load it into your shell:
set -a; source .env; set +a

./gradlew bootRun
```

Minimal `.env` for local development (replace placeholders):

```bash
DB_URL=jdbc:postgresql://localhost:5433/kettu
DB_USERNAME=postgres
DB_PASSWORD=your-local-password
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret
JWT_SECRET=any-strong-local-secret
R2_ACCESS_KEY=your-r2-access-key
R2_SECRET_KEY=your-r2-secret-key
R2_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
R2_BUCKET=khorunaliyev-kettu
```

## Production-safe configuration rules

- Supply secrets via your platform's secret manager; never commit them or bake them
  into images.
- Always set a strong, unique `JWT_SECRET`; never rely on the development fallback.
- Keep `HIBERNATE_SQL_LOG_LEVEL`/`HIBERNATE_BIND_LOG_LEVEL` at `INFO` (or higher) in
  production so bound parameter values are not logged.
- Ensure the database user has the privileges Flyway needs to apply migrations.
- Rotate any credential that was ever committed to git history (see
  [SECURITY.md](SECURITY.md)).
