package dev.smo.spring.postgres.jpa.onetomany.controller;

import dev.smo.spring.postgres.jpa.onetomany.TestcontainersConfiguration;
import dev.smo.spring.postgres.jpa.onetomany.entities.Author;
import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import dev.smo.spring.postgres.jpa.onetomany.model.AuthorDTO;
import dev.smo.spring.postgres.jpa.onetomany.repository.AuthorRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "app.db.init.enabled=false"})
class AuthorControllerTestIT {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    AuthorRepository authorRepository;

    @LocalServerPort
    private int port;

    @PostConstruct
    public void init() {
        System.out.println("Running AuthorControllerTestIT...");
        RestAssured.baseURI = "http://localhost/";
        RestAssured.port = port;
    }

    private Author author1;
    private Book books1;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();

        Author a1 = Author.builder()
                .firstName("Author 1 first name")
                .lastName("Author 1 last name")
                .build();
        Book b1 = Book.builder()
                .title("Book 1 book title")
                .price(BigDecimal.valueOf(11.00))
                .publishDate(LocalDate.of(2024 , 1, 11))
                .build();
        a1.addBook(b1);
        author1 = authorRepository.save(a1);
        books1 = author1.getBooks().getFirst();

        Author a2 = Author.builder()
                .firstName("Author 2 first name")
                .lastName("Author 2 last name")
                .build();
        Book b2 = Book.builder()
                .title("Book 2 book title")
                .price(BigDecimal.valueOf(12.00))
                .publishDate(LocalDate.of(2024 , 2, 12))
                .build();
        a2.addBook(b2);

        Author a3 = Author.builder()
                .firstName("Author 3 first name")
                .lastName("Author 3 last name")
                .build();
        Book b3 = Book.builder()
                .title("Book 3 book title")
                .price(BigDecimal.valueOf(13.00))
                .publishDate(LocalDate.of(2024 , 3, 13))
                .build();
        a3.addBook(b3);
        authorRepository.saveAll(java.util.List.of(a2, a3));
    }

    @Test
    void contextLoads() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void getAuthorWithExistingId() {

        given()
                .contentType(ContentType.JSON)
        .when()
                .pathParam("id", author1.getId().toString())
                .get("/api/authors/{id}")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(author1.getId().intValue()))
                .body("firstName", equalTo(author1.getFirstName()))
                .body("lastName", equalTo(author1.getLastName()));
    }

    @Test
    void getAuthorWithNonExistingId() {
        long id = 0L;
        given()
                .contentType(ContentType.JSON)
        .when()
                .pathParam("id", id)
                .get("/api/authors/{id}")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllAuthors() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/api/authors")
        .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body(".", hasSize(3));
    }

    @Test
    void getBooksForAuthorWithId() {
        Response response = given()//Returning floats and doubles as BigDecimal
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when()
                    .pathParam("id", author1.getId().toString())
                    .get("/api/authors/{id}/books")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(ContentType.JSON)
                    .body(".", hasSize(1))
                    .body("[0].title", equalTo(books1.getTitle()))
                    .body("[0].price", comparesEqualTo(books1.getPrice()))
                    .body("[0].publishDate", equalTo(books1.getPublishDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .extract().response();
        log.info(response.asString());
    }

    @Test
    @Transactional
    @Rollback
    void createAuthor() {
        var newAuthor = AuthorDTO.builder().firstName("Author 1 created first name").lastName("Author 1 created last name").build();
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(newAuthor)
                .when()
                    .post("/api/authors")
                .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .contentType(ContentType.JSON)
                    .body("firstName", equalTo(newAuthor.getFirstName()))
                    .body("lastName", equalTo(newAuthor.getLastName()))
                .extract().path("id");
        assertThat(authorRepository.findById(id.longValue()).isEmpty()).isFalse();
        assertThat(authorRepository.findById(id.longValue()).get().getFirstName()).isEqualTo(newAuthor.getFirstName());
        assertThat(authorRepository.findById(id.longValue()).get().getLastName()).isEqualTo(newAuthor.getLastName());
    }

    @Test
    void createAuthorWithIdSet() {
        var newAuthor = AuthorDTO.builder().id(1L).firstName("Author 1 created first name").lastName("Author 1 created last name").build();
        given()
                .contentType(ContentType.JSON)
                .body(newAuthor)
                .when()
                    .post("/api/authors")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}