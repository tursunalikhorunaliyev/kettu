# Roadmap

This roadmap lists realistic, incremental improvements for Kettu. It is a living
document and not a commitment to dates or ordering. Items are derived from the
current state of the codebase.

## Testing

- Add controller / web-layer tests (`@WebMvcTest` + MockMvc) for the REST
  controllers, including authorization (allowed vs `401`/`403`) paths.
- Add repository / persistence tests using Testcontainers with a PostGIS image so
  spatial queries (`findDistrictByGeoData`) are exercised against a real database.
- Add service-layer tests for the place creation/update/status flows and the
  Excel/GeoJSON import services.
- Introduce code-coverage reporting (e.g. JaCoCo) and a baseline coverage target.

## Documentation

- Add a `LICENSE` file and update the README license section accordingly.
- Generate and publish the OpenAPI specification as a checked-in artifact.
- Add architecture decision records (ADRs) for major design choices.

## Security hardening

- Rotate all credentials that were previously committed (database, Google OAuth2,
  Cloudflare R2, JWT secret).
- Enable automated dependency scanning (Dependabot and/or OWASP Dependency-Check).
- Review and tighten CORS configuration for the intended client origins.
- Make the OAuth2 success redirect target and the admin bootstrap identity
  configurable instead of hard-coded.
- Consider shorter-lived access tokens plus a refresh-token flow.

## Performance & scalability

- Add pagination to list endpoints that can grow unbounded (e.g. places listing).
- Review fetch strategies / N+1 risks on JPA associations and projections.
- Add timeouts and retry/backoff for outbound calls to Cloudflare R2.

## CI/CD

- Add a database-backed job that runs `integrationTest` with a PostGIS service
  container.
- Add build caching and dependency caching to speed up CI.
- Add release automation (versioning, changelog, artifact publishing) once the
  project leaves the `SNAPSHOT` stage.

## Possible feature improvements

- Configurable role assignment / admin management instead of a hard-coded admin
  email.
- Health and metrics endpoints (Spring Boot Actuator) for observability.
- Containerization (Dockerfile / `bootBuildImage`) with documented runtime config.

> Items here describe direction, not guarantees. Anything not yet implemented in the
> codebase is intentionally listed as future work.
