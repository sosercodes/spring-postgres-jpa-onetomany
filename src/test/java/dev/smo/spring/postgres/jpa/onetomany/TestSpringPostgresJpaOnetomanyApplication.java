package dev.smo.spring.postgres.jpa.onetomany;

import org.springframework.boot.SpringApplication;

public class TestSpringPostgresJpaOnetomanyApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringPostgresJpaOnetomanyApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
