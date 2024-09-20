package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.TestcontainersConfiguration;
import dev.smo.spring.postgres.jpa.onetomany.entities.Author;
import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void contextLoads() {
        assertThat(postgres.isCreated()).isTrue();
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

    @Test
    @Transactional
    @Rollback
    void findByTitleWithAuthor() {
        Book b1 = Book.builder()
                .title("BookWithTitle")
                .price(BigDecimal.valueOf(39.99))
                .publishDate(LocalDate.of(2023, 5, 5))
                .build();
        Author a1 = Author.builder()
                .firstName("firstname")
                .lastName("lastname")
                .books(new ArrayList<>())
                .build();
        a1.addBook(b1);
        bookRepository.save(b1);
        List<Book> books = bookRepository.findByTitle("BookWithTitle");

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        assertEquals("BookWithTitle", books.getFirst().getTitle());
        assertNotNull(books.getFirst().getAuthor());
        assertEquals(books.getFirst().getAuthor().getFirstName(), "firstname");
    }

    @Test
    @Transactional
    @Rollback
    void findBooksByAuthorId() {
        Book b1 = Book.builder()
                .title("BookWithTitle")
                .price(BigDecimal.valueOf(39.99))
                .publishDate(LocalDate.of(2023, 5, 5))
                .build();
        Author a1 = Author.builder()
                .firstName("firstname")
                .lastName("lastname")
                .books(new ArrayList<>())
                .build();
        a1.addBook(b1);
        var as = authorRepository.save(a1);

        var books = bookRepository.findBooksByAuthorId(as.getId());
        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        assertEquals("BookWithTitle", books.getFirst().getTitle());
    }

}