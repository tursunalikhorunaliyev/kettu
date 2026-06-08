# Contributing to Kettu

Thanks for your interest in improving Kettu! This guide explains how to set up the
project, the conventions we follow, and what we expect before a pull request is
merged. Please also read the [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting the code

1. **Fork** the repository on GitHub.
2. **Clone** your fork:
   ```bash
   git clone https://github.com/<your-username>/kettu.git
   cd kettu
   ```
3. Add the upstream remote so you can stay in sync:
   ```bash
   git remote add upstream https://github.com/tursunalikhorunaliyev/kettu.git
   ```

## Local setup

- Install **JDK 21** (the Gradle toolchain targets Java 21).
- Copy the environment template and fill in your own values:
  ```bash
  cp .env.example .env
  ```
- For features that touch the database or run the full app you also need
  **PostgreSQL + PostGIS**; unit tests do not. See [CONFIGURATION.md](CONFIGURATION.md).

Verify your environment builds cleanly before changing anything:

```bash
./gradlew clean build
```

## Branch naming

Create a topic branch off `master`. Use a short, descriptive, kebab-case name with a
type prefix:

- `feature/<short-description>` — new functionality
- `fix/<short-description>` — bug fixes
- `docs/<short-description>` — documentation only
- `test/<short-description>` — tests only
- `chore/<short-description>` — build, CI, tooling

Example: `fix/jwt-filter-null-token`.

## Before opening a pull request

Your change **must** build and pass the unit tests locally:

```bash
./gradlew clean build   # compiles + runs unit tests
./gradlew test          # unit tests only
```

If your change touches database-backed code and you have a database available, also
run:

```bash
./gradlew integrationTest
```

Add or update tests for any behaviour you change or fix (see [TESTING.md](TESTING.md)).

## Code style

This repository does not currently enforce an automated formatter or linter, so
please match the conventions already present in the code:

- Java 21, 4-space indentation.
- Package layout follows responsibility: `controller`, `services`, `repository`,
  `entity`, `dto`, `config`, `component`, `util`.
- Constructor injection via Lombok (`@RequiredArgsConstructor` / `@AllArgsConstructor`).
  Avoid field injection.
- DTOs are plain POJOs/records; validate request DTOs with Jakarta Bean Validation
  (`@Valid`, `@NotNull`, `@Size`, …).
- Log with SLF4J (`@Slf4j`). **Never** `System.out.println` and **never** log
  secrets or personal data.
- Never expose raw exception messages or stack traces to API clients; route errors
  through the global exception handler.
- Keep secrets out of source and configuration — read them from environment
  variables (see `application.yml`).

## Commit messages

Write clear, imperative-mood messages. [Conventional Commits](https://www.conventionalcommits.org/)
are encouraged but not strictly required:

```
fix(security): fail closed on malformed JWT instead of returning 500

The JWT filter previously let jjwt exceptions propagate, producing a 500.
It now catches parsing errors and continues unauthenticated.
```

Keep the subject under ~72 characters and explain the "why" in the body when it is
not obvious.

## Pull request checklist

Before requesting review, confirm:

- [ ] The branch is up to date with `master`.
- [ ] `./gradlew clean build` passes locally.
- [ ] New/changed behaviour is covered by tests.
- [ ] No secrets, credentials, or personal data are added.
- [ ] Public APIs, routes, DTO fields and DB columns are unchanged unless the PR
      explicitly documents and justifies a breaking change.
- [ ] Documentation (README/relevant `*.md`) is updated if behaviour changed.
- [ ] The PR description uses the [pull request template](.github/PULL_REQUEST_TEMPLATE.md).

## Not breaking existing behavior

Backward compatibility matters. Unless a PR clearly states and justifies otherwise:

- Do not rename public classes, methods, REST routes, request/response fields,
  database columns, or package names.
- Keep configuration backward-compatible (new settings should have safe defaults).
- Database changes must be additive Flyway migrations; do not edit already-applied
  migration files.

## Reporting bugs

Open an issue using the [bug report template](.github/ISSUE_TEMPLATE/bug_report.md).
Include steps to reproduce, expected vs actual behaviour, your environment (JDK, OS,
database), and relevant logs (with secrets removed).

## Requesting features

Open an issue using the [feature request template](.github/ISSUE_TEMPLATE/feature_request.md).
Describe the problem first, then your proposed solution and any alternatives.

For security issues, **do not open a public issue** — follow [SECURITY.md](SECURITY.md).
