package dev.smo.spring.postgres.jpa.onetomany;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringPostgresJpaOnetomanyApplicationTests {

	@Autowired
	PostgreSQLContainer<?> postgres;

	@Test
	void contextLoads() {
		assertThat(postgres.isCreated()).isTrue();
		assertThat(postgres.isRunning()).isTrue();
	}

}
