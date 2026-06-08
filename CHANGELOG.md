# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to adhere to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Unit test suite (JUnit 5 + Mockito + AssertJ) covering JWT generation/validation,
  the JWT authentication filter's fail-closed behaviour, the global exception
  handler, the R2 storage service, geo lookup, projection helpers, and the place
  diff checker.
- Gradle `integrationTest` task for the database-backed full-context smoke test,
  which is tagged `integration` and excluded from the default `test` task.
- GitHub Actions CI workflow (`.github/workflows/ci.yml`) that builds the project
  and runs unit tests on JDK 21.
- Configurable JWT settings (`jwt.secret`, `jwt.expiration-ms`) via environment
  variables.
- `.env.example` documenting all required environment variables.
- Project documentation: `README.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`,
  `SECURITY.md`, `CHANGELOG.md`, `ROADMAP.md`, `ARCHITECTURE.md`, `TESTING.md`,
  `DEPLOYMENT.md`, `API.md`, `CONFIGURATION.md`, `FAQ.md`, and GitHub issue/PR
  templates.

### Changed
- Bumped the Java toolchain from 18 (end-of-life) to 21 (LTS); sources are
  source-compatible and unchanged.
- Externalized all configuration secrets in `application.yml` (database, Google
  OAuth2, Cloudflare R2, JWT) to environment variables with safe, non-secret
  defaults for local URLs only.
- Lowered default Hibernate SQL logging from `DEBUG`/`TRACE` to `INFO`
  (still overridable) so bound parameter values are not logged by default.
- Hardened `.gitignore` to exclude `.env` and local secret configuration files.

### Fixed
- The JWT authentication filter no longer returns HTTP 500 on malformed, expired,
  or tampered tokens; it now fails closed and continues unauthenticated.
- `R2Service.deleteFiles` now deletes every requested key instead of returning
  after the first one, and treats a null/empty list as a safe no-op success.
- Removed debug `System.out.println` calls and an unguarded `List.get(0)` in
  `PlaceDiffChecker` that could throw on an empty photo list.

### Security
- The global exception handler no longer prints stack traces or returns raw
  exception messages to API clients (information-disclosure fix); it logs the
  detail server-side and returns a generic message.
- Removed previously committed live credentials from `application.yml`.
  > These credentials were public and must be rotated by the maintainer.

[Unreleased]: https://github.com/tursunalikhorunaliyev/kettu/commits/master
