package dev.smo.spring.postgres.jpa.onetomany;

import dev.smo.spring.postgres.jpa.onetomany.entities.Author;
import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import dev.smo.spring.postgres.jpa.onetomany.repository.AuthorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
public class SpringPostgresJpaOnetomanyApplication {

	@Autowired
	private AuthorRepository authorRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringPostgresJpaOnetomanyApplication.class, args);
	}

	@Bean
	@Transactional
	@ConditionalOnProperty(prefix = "app", name = "db.init.enabled", havingValue = "true")
	public CommandLineRunner initApplication() {
		return args -> {
			log.debug("Initializing application...");
			Author a1 = Author.builder()
					.firstName("Craig")
					.lastName("Walls")
					.books(new ArrayList<>())
					.build();
			Book b1 = Book.builder()
					.title("Spring in Action")
					.price(BigDecimal.valueOf(51.40))
					.publishDate(LocalDate.of(2022, 3, 1))
					.build();
			a1.addBook(b1);
			authorRepository.saveAll(List.of(a1));
			log.debug("Application initialized");
		};
	}

}
