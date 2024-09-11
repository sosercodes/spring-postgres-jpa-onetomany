package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.TestcontainersConfiguration;
import dev.smo.spring.postgres.jpa.onetomany.model.Author;
import dev.smo.spring.postgres.jpa.onetomany.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AuthorRepositoryTest {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    AuthorRepository authorRepository;

    @Test
    void findByFirstName() {
        Author a1 = Author.builder()
                .firstName("firstname")
                .lastName("lastname")
                .books(new ArrayList<>())
                .build();
        Book b1 = Book.builder()
                .title("book 1")
                .price(BigDecimal.valueOf(10.00))
                .publishDate(LocalDate.of(2024 , 1, 10))
                .author(a1)
                .build();
        a1.getBooks().add(b1);
        var authors = authorRepository.save(a1);

        List<Author> craig = authorRepository.findByFirstName("firstname");

        assertNotNull(authors);
        assertEquals(craig.size(), 1);
        assertEquals(craig.getFirst().getFirstName(), a1.getFirstName());
        assertEquals(craig.getFirst().getLastName(), a1.getLastName());
        assertNotNull(craig.getFirst().getBooks());
        assertEquals(craig.getFirst().getBooks().size(), 1);
        assertEquals(craig.getFirst().getBooks().getFirst().getTitle(), b1.getTitle());
    }
}