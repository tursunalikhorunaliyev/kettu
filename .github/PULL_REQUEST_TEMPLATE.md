# Pull Request

## Summary

<!-- What does this PR do and why? Link any related issues (e.g. "Closes #123"). -->

## Type of change

<!-- Mark all that apply with an [x]. -->

- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Documentation only
- [ ] Refactor / internal change (no behaviour change)
- [ ] Build / CI / tooling
- [ ] Breaking change (fix or feature that changes existing behaviour)

## Testing performed

<!-- Describe how you verified the change. Include the commands you ran. -->

- [ ] `./gradlew clean build` passes locally
- [ ] `./gradlew test` passes
- [ ] `./gradlew integrationTest` (if the change touches database-backed code)

```
# paste relevant test output here
```

## Checklist

- [ ] The branch is up to date with `master`.
- [ ] New/changed behaviour is covered by tests.
- [ ] No secrets, credentials, or personal data are added.
- [ ] Public classes, methods, REST routes, DTO fields, DB columns, and package
      names are unchanged (or the breaking change is documented below).
- [ ] Relevant documentation (`README.md` and other `*.md`) is updated.
- [ ] Follows the conventions in [CONTRIBUTING.md](../CONTRIBUTING.md).

## Screenshots

<!-- Only if there is a user-visible change (e.g. the static OAuth pages). Otherwise remove this section. -->

## Breaking changes

<!-- Describe any breaking change and the migration path, or write "None". -->

## Security impact

<!-- Note any impact on auth, validation, secret handling, data exposure, or
     dependencies. Write "None" if not applicable. Do NOT include exploit details
     here — follow SECURITY.md for vulnerabilities. -->
