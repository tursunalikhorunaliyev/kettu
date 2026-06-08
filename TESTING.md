# Testing

This document describes Kettu's testing strategy and how to run and write tests.

## Strategy

Tests are split into two tiers:

1. **Unit tests** — fast, isolated, no external dependencies. They instantiate the
   class under test directly and use Mockito for collaborators. These run on every
   build and in CI.
2. **Integration test** — the full-context Spring Boot smoke test
   (`KettuApplicationTests`). It boots the entire application (JPA, Flyway,
   security, OAuth2 client) and therefore needs a reachable PostgreSQL+PostGIS
   database and the runtime environment variables. It is tagged `integration` and
   excluded from the default `test` task.

The split is configured in [build.gradle](build.gradle): the `test` task excludes
the `integration` tag, and a dedicated `integrationTest` task includes it.

## Running tests

```bash
# All unit tests (no database required)
./gradlew test

# Compile + unit tests + jar
./gradlew clean build

# Integration smoke test (requires Postgres+PostGIS and env vars)
./gradlew integrationTest
```

### Running specific tests

```bash
# A single test class
./gradlew test --tests "com.khorunaliyev.kettu.config.security.JWTGeneratorTest"

# A single test method
./gradlew test --tests "com.khorunaliyev.kettu.services.geo.GeoServiceTest.mapsTupleToGeoDataResponse"

# A package (wildcard)
./gradlew test --tests "com.khorunaliyev.kettu.component.*"
```

Test reports are written to `build/reports/tests/test/index.html`.

## Current unit tests

| Test class                                     | What it verifies                                                      |
|------------------------------------------------|----------------------------------------------------------------------|
| `JWTGeneratorTest`                             | Token generation, subject extraction, validation, tampered/foreign-secret/expired tokens rejected. |
| `JWTAuthenticationFilterTest`                  | Filter fails closed: missing/malformed/unknown-user tokens stay anonymous without throwing; valid token authenticates. |
| `GlobalControllerExceptionHandlerAdvisorTest`  | 500 responses do not leak internals; 404/403 mapping.                |
| `R2ServiceTest`                                | Deletes every key; null/empty is a no-op success; S3 failure → 409; non-image files skipped on upload. |
| `GeoServiceTest`                               | `ResourceNotFoundException` when no district matches; tuple mapped to response. |
| `ProjectionUtilsTest`                          | Null handling; point lat/long mapping; image quality URL construction. |
| `PlaceDiffCheckerTest`                         | Name/description diffing (case/whitespace), photo-list diffing, empty-list regression. |

## Unit test guidelines

- Use JUnit 5 (`org.junit.jupiter`), Mockito (`@ExtendWith(MockitoExtension.class)`),
  and AssertJ (`assertThat`) — all provided by `spring-boot-starter-test`.
- Do not start a Spring context for unit tests; construct the class under test
  directly.
- Inject `@Value` fields that are not constructor parameters with
  `ReflectionTestUtils.setField(...)` (see `R2ServiceTest`).
- Name tests by behaviour, e.g. `deleteFilesReturnsConflictWhenS3Fails`.
- Cover the happy path **and** the failure/edge paths.

## Integration test guidelines

- Keep them tagged `@Tag("integration")` so they stay out of the default run.
- They require a configured database and environment variables; provide these via a
  test profile or environment when running `./gradlew integrationTest`.
- Future repository tests should use Testcontainers with a PostGIS image so spatial
  queries run against a real database (see [ROADMAP.md](ROADMAP.md)).

## Edge cases that must be covered

When adding or changing behaviour, include tests for:

- **Null and empty** inputs (empty lists, missing optional fields).
- **Invalid / malformed** inputs (bad tokens, invalid request bodies).
- **Boundary** values (size limits on validated DTO fields, e.g. name/description).
- **Authorization** outcomes (allowed vs denied).
- **Error paths** (downstream failures such as storage errors).
- **No information leakage** in error responses.

## Mocking rules

- Mock external collaborators (repositories, `S3Client`, services), not the class
  under test.
- Prefer real value objects/DTOs over mocks for simple data holders.
- Avoid mocking types you don't own beyond what the test needs; keep stubs minimal.
- Use lenient stubbing only when a shared setup intentionally over-specifies (see
  `R2ServiceTest`).

## Test data rules

- Use clearly fake, non-sensitive data (e.g. `alice@example.com`).
- Never use real credentials, tokens, or personal data in tests.

## Coverage expectations

There is **no enforced coverage gate currently**. The goal is meaningful coverage of
business logic and security-sensitive paths rather than a percentage target. Adding
JaCoCo and a baseline threshold is on the [roadmap](ROADMAP.md).

## CI behavior

GitHub Actions ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)) runs
`./gradlew clean build` on JDK 21 for every push and pull request to `master`, which
executes the unit tests. The database-backed `integrationTest` is **not** run in CI
yet because no database service is configured in the workflow.
