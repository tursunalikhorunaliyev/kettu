# FAQ

Short answers to common questions. See the linked documents for detail.

### How do I build the project?

```bash
./gradlew clean build
```

This compiles the code, runs the unit tests, and produces the JAR in `build/libs/`.

### How do I run it?

```bash
./gradlew bootRun
# or, after building:
java -jar build/libs/kettu-0.0.1-SNAPSHOT.jar
```

The app needs a PostgreSQL+PostGIS database and the environment variables from
[`.env.example`](.env.example). It listens on `http://localhost:8080`. See
[DEPLOYMENT.md](DEPLOYMENT.md).

### How do I run the tests?

```bash
./gradlew test            # unit tests (no database needed)
./gradlew integrationTest # full-context smoke test (needs a database)
```

Run one class or method:

```bash
./gradlew test --tests "com.khorunaliyev.kettu.config.security.JWTGeneratorTest"
```

More in [TESTING.md](TESTING.md).

### `./gradlew` says "permission denied". What do I do?

Make the wrapper executable:

```bash
chmod +x ./gradlew
```

### The build fails with "Cannot find a Java installation ... matching languageVersion=21".

Install **JDK 21**. The Gradle toolchain targets Java 21 and the build will not run
on an older or missing JDK.

### The app fails to start with a `ConnectException` / Flyway error.

The database is unreachable. Start PostgreSQL with the PostGIS extension and set
`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. See [CONFIGURATION.md](CONFIGURATION.md).

### Why does the default `./gradlew test` not run `KettuApplicationTests`?

That test boots the whole application and needs a database, so it is tagged
`integration` and excluded from the default `test` task. Run it explicitly with
`./gradlew integrationTest` when a database is available.

### Login / OAuth2 isn't working.

Set `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`, and make sure your Google OAuth2
client's authorized redirect URIs match your host. The login flow starts at
`GET /api/auth/login`.

### Image upload/download fails.

Set the Cloudflare R2 variables: `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_ENDPOINT`,
and `R2_BUCKET`.

### Where are the important files?

| What                | Where                                                        |
|---------------------|--------------------------------------------------------------|
| Entry point         | `src/main/java/com/khorunaliyev/kettu/KettuApplication.java` |
| Configuration       | `src/main/resources/application.yml`                         |
| Database migrations | `src/main/resources/db/migration/`                          |
| Controllers (API)   | `src/main/java/com/khorunaliyev/kettu/controller/`          |
| Business logic      | `src/main/java/com/khorunaliyev/kettu/services/`            |
| Tests               | `src/test/java/com/khorunaliyev/kettu/`                     |
| Build config        | `build.gradle`                                              |
| CI workflow         | `.github/workflows/ci.yml`                                  |

### Where is the API documentation?

Run the app and open Swagger UI at `http://localhost:8080/swagger-ui.html`, or read
[API.md](API.md).

### Is there a license?

Not yet — no `LICENSE` file is present, so the project is "all rights reserved" by
default until the maintainer adds one. See the README license section.
