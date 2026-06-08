# Security Policy

## Supported versions

Kettu is currently pre-release software (`0.0.1-SNAPSHOT`). Only the latest state of
the `master` branch is supported; there are no maintained release branches yet.

| Version            | Supported          |
|--------------------|--------------------|
| `master` (latest)  | ✅ Yes             |
| Older commits/tags | ❌ No              |

## Reporting a vulnerability

**Please do not report security vulnerabilities through public GitHub issues,
pull requests, or discussions.**

Instead, report privately so the issue can be fixed before it is disclosed:

- Preferred: open a private **GitHub Security Advisory** for this repository
  (`Security` tab → `Report a vulnerability`), if enabled by the maintainer.
- Otherwise, contact the maintainer directly.

> **Maintainer:** add a private security contact here (for example a dedicated
> email address). _Placeholder — to be filled in by the repository owner._

When reporting, please include:

- A description of the vulnerability and its impact.
- Steps to reproduce or a proof of concept.
- Affected endpoints, classes, or configuration.
- Any suggested remediation, if known.

Please allow a reasonable time for a fix before any public disclosure.

## What not to report publicly

- Working exploits or proof-of-concept code against the live service.
- Leaked credentials, tokens, or personal data (report privately instead).
- Any details that would let a third party reproduce an attack before a fix ships.

## Known historical exposure

Earlier revisions of this repository committed live credentials (database password,
Google OAuth client secret, Cloudflare R2 keys, JWT signing key) in
`application.yml`. These have since been externalized to environment variables.
Because the values were public in git history, they must be treated as compromised
and **rotated** in their respective providers. Do not reuse them.

## Secret-handling rules

- Never commit secrets. All credentials are read from environment variables; see
  [CONFIGURATION.md](CONFIGURATION.md) and [`.env.example`](.env.example).
- `.env` and local secret config files are git-ignored.
- `JWT_SECRET` ships with a development fallback only; production deployments **must**
  override it with a strong, private value.
- Never log secrets or personal data. SQL parameter logging defaults to a level that
  does not print bound values.

## Dependency vulnerability policy

- Dependencies are managed through Gradle and the Spring Boot dependency management
  plugin; prefer upgrading via Spring Boot's curated BOM.
- Keep Spring Boot and other dependencies on supported, patched versions.
- An automated dependency-scanning tool (e.g. Dependabot or OWASP Dependency-Check)
  is **currently not configured in the codebase**; enabling one is on the
  [roadmap](ROADMAP.md).

## Security testing expectations

- Authentication/authorization changes must include tests for both allowed and
  denied paths.
- Error handling must be tested to confirm internal details are not leaked to
  clients (see `GlobalControllerExceptionHandlerAdvisorTest`).
- Input validation should be covered for empty, null, oversized, and malformed
  inputs.
