package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.TestcontainersConfiguration;
import dev.smo.spring.postgres.jpa.onetomany.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BookRepositoryTest {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    @Transactional
    @Rollback
    void save() {
        Book b1 = Book.builder()
                .title("Book 1")
                .price(BigDecimal.valueOf(39.99))
                .publishDate(LocalDate.of(2023, 5, 5))
                .build();
        Book b1saved = bookRepository.save(b1);
        assertNotNull(b1saved);
    }

    @Test
    @Transactional
    @Rollback
    void findByTitle() {
        Book b1 = Book.builder()
                .title("BookWithTitle")
                .price(BigDecimal.valueOf(39.99))
                .publishDate(LocalDate.of(2023, 5, 5))
                .build();
        bookRepository.save(b1);
        List<Book> books = bookRepository.findByTitle("BookWithTitle");

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        assertEquals("BookWithTitle", books.getFirst().getTitle());
    }
}