package com.khorunaliyev.kettu;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Full-context smoke test. It boots the entire Spring application (JPA, Flyway,
 * security, OAuth2 client) and therefore requires a reachable Postgres/PostGIS
 * database and the runtime environment variables documented in the README.
 *
 * <p>Tagged {@code integration} so it is excluded from the default {@code test}
 * task. Run it explicitly with {@code ./gradlew integrationTest} once a database
 * is available.</p>
 */
@Tag("integration")
@SpringBootTest
class KettuApplicationTests {

	@Test
	void contextLoads() {
	}

}
