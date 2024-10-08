# Spring Boot, Postgres, and OneToMany

This article shows how to use Spring Boot, Spring Data JPA and PostgreSQL to create a One-to-Many Relationship. 
Additionally it shows how to add REST endpoints for CRUD database operations and test them with REST Assured.

We use Testcontainers to start an actual PostgreSQL container for testing and developement.
You need to have Docker or Docker Desktop to be running in the background.

This application uses the following Technologies.

- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- Testcontainers
- JUnit 5
- REST Assured
- OpenApi / Swagger-Ui

## Authors API

### GET

Find all authors.

```bash
curl -sS localhost:8080/api/authors | jq
```

Get a specific author with an `id`.

```bash
$ curl -sS localhost:8080/api/authors/1 | jq
{
  "id": 1,
  "firstName": "Craig",
  "lastName": "Walls"
}
```

Get all books for author with `id`.

```bash
$ curl -sS localhost:8080/api/authors/1/books | jq
[
  {
    "id": 1,
    "title": "Spring in Action",
    "price": 51.40,
    "publishDate": "2022-03-01"
  }
]
```

### POST

Create an `Author`. Set `firstname` and `lastname`.

```bash
curl localhost:8080/api/authors -v -H'Content-Type: application/json' -d '{"firstName":"Alex","lastName":"Xu"}'
```

Create a `Book` and add it to an author.

```bash
curl "localhost:8080/api/authors/1/books" -v -H'Content-Type: application/json' -d '{"title":"System Design Interview – An insiders guide","price": 39.71, "publishDate":  "2020-06-12"}'
```

## One-to-Many Relationships

If you create a database schema, a _one-to-many_ mapping means that one row in a table is mapped to multiple rows in another table.
The _"one" side_ has a _primary key_ and the _"many" side_ has a _foreign key_ which refers to the primary key of _"one" side_.

In Spring Data JPA OneToMany relationships allow you to model _one-to-many_ mapping where one _entity_ can have _multiple related entities_.

### Bidirectional vs Unidirectional

There are two choices how to create a `OneToMany` relationship.

- Use a unidirectional `@OneToMany` association
- Use a bidirectional `@OneToMany` association

**Bidirectional** relationships are generally recommended because they:

- Result in more efficient SQL queries
- Allow navigation from both sides of the relationship
- Provide better control over the relationship

**Unidirectional** relationships are simpler but less efficient and they create an additional table for the mapping.

### Basic Implementation

Here's an overview of how to implement and use _bidirectional OneToMany_ relationships.

To create a `OneToMany` relationship.

On the "one" side, use the `@OneToMany` annotation.

```java
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    // getters and setters
}
```

On the "many" side, use the `@ManyToOne` annotation:

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
    
    // getters and setters
}
```

Let's break down the properties of the `@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)` association.

The `mappedBy` property is used to specify the field in the child entity that _owns the relationship_. 
In this case, `author` indicates that the Book entity has a field named `author` that represents the owning side of the relationship.

Key points about `mappedBy`:

- It establishes a bidirectional relationship
- It's used on the non-owning side of the relationship (usually the "one" side)
- It helps avoid duplicate foreign key columns

The `cascade` property defines how state transitions are cascaded from parent entities to child entities. `CascadeType.ALL` means that all operations (PERSIST, MERGE, REMOVE, REFRESH, DETACH) will be cascaded from the parent entity to its associated child entities, see [Cascade changes](https://codegym.cc/quests/lectures/en.questhibernate.level13.lecture05).

Effects of `CascadeType.ALL` are:

- **Persist**: When you save the parent entity, all its associated child entities are also saved
- **Merge**: Changes made to the parent entity are propagated to its associated child entities
- **Remove**: Deleting the parent entity will also delete all its associated child entities

The `orphanRemoval = true` property is used to ensure that there are no child entities without parent entities.
It's not exactly the same as `Cascade.REMOVE`.
It assures that if there are several parent entities, the child is only deleted if all parent entities deleted .

### Best Practices

The following best practices are recommended.

- **Cascade operations**: Use `cascade = CascadeType.ALL` to propagate operations to child entities
- **Fetch type**: Default fetch type is LAZY for `@OneToMany`, which is usually preferred
- **Orphan removal**: Use `orphanRemoval = true` to automatically remove child entities when they're removed from the collection
- **Bidirectional relationship management**: Always maintain both sides of the relationship and add the following methods to the `Book` entity.

    ```java
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
    ```
- **Owning Side**: _It’s a good practice to mark the many-to-one side as the owning side (JPA specification under section 2.9)_
    In this example `Book` should be the owning side and `Author` the inverse side.
    
    >"The Owning side in the relationship will hold the foreign key in the database" - _Spring Framework Guru - Spring Framework 5 Beginner to Guru Chapter 8_ 
    
    But how can we achieve this?

    By including the `mappedBy` attribute in the `Author` class, we mark it as the inverse side.
    At the same time, we also annotate the `Book.author` field with `@ManyToOne`, which makes `Book` the _owning side_.
    Now Hibernate knows that the `author` reference in `Book` is more important and will save this reference to the database, even if we forget to set the reference in `Author`.
    
    See [Baeldung - Hibernate One to Many](https://www.baeldung.com/hibernate-one-to-many) and [Github - Hibernate-Annotations - HibernateManyIsOwningSide](https://github.com/eugenp/tutorials/tree/master/persistence-modules/hibernate-annotations/src/main/java/com/baeldung/hibernate/oneToMany).


### Performance Considerations

- Avoid eagerly fetching large collections. Use JPQL queries for better performance when dealing with large datasets
- Consider using `@ManyToOne` on the child side only if you don't need to access the collection from the parent side often, see [The best way to map a @OneToMany relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/).

### Conclusion

OneToMany relationships in Spring Data JPA provide a powerful way to model hierarchical data structures. By following best practices and understanding the implications of bidirectional vs unidirectional relationships, you can create efficient and maintainable data models for your application.


## JPQL - Java Persistence Query Language

Here's an introduction to using joins in JPQL (Java Persistence Query Language) with Hibernate.
You can use these expressions as an argument with the `@Query` annotation.

### JPQL Joins

JPQL allows you to join entities in queries using several types of joins.

- Inner Joins
- Left Joins
- Fetch Joins
- Joins between Unrelated Entities

#### Inner Joins

Inner joins return results only when there is a match in both joined entities. The basic syntax is:

```jpaql
SELECT a, b FROM Author a JOIN a.books b
```

The definition of the Author entity provides all information Hibernate needs to join it to the Book entity, 
and you don’t have to provide an additional ON statement.

#### Left Joins

Left joins return all results from the left entity, even if there's no match in the right entity.

```jpaql
SELECT a, b FROM Author a LEFT JOIN a.books b
```

This will return all authors, even those without a book.

#### Fetch Joins

Fetch joins allow you to eagerly load associated entities in a single query.

```jpaql
SELECT a FROM Author a JOIN FETCH a.books b
```

This loads both the Author and the associated Books in one query.

#### Joining Unrelated Entities

For Hibernate 5.1+, you can join unrelated entities using an ON clause:

```jpaql
SELECT a.firstName, a.lastName, e.phoneNumber 
FROM Author a JOIN PhoneBookEntry e 
ON a.firstName = e.firstName AND a.lastName = e.lastName
```

This allows joining entities that don't have a defined relationship.

#### Additional Join Conditions

Sometimes you want to join the related entities which fulfill additional conditions. 
You can do this for INNER JOINs, and LEFT JOINs with an additional ON statement.

```jpaql
SELECT a, b FROM Author a JOIN a.books b ON b.publishDate > :publishDate
```

#### Path expressions or implicit joins

Path expressions create implicit joins and are one of the benefits provided by the entity model.
You can use the ‘.’ operator to navigate to related entities.

```jpaql
SELECT a FROM Author a WHERE a.books.title LIKE '%Spring%'
```

### Key Points

- Use JOIN for inner joins and LEFT JOIN for left outer joins
- JOIN FETCH eagerly loads associated entities
- Joins are typically based on entity relationships defined in mappings
- For complex joins on unrelated entities, use Hibernates ON clause syntax
- Avoid overusing joins, as they can impact query performance for large datasets

When writing JPQL queries, consider the relationships between your entities and choose the appropriate join type based on your data access requirements.

## Rest-Assured

Our integration tests use REST Assured in conjunction with Testcontainers to test the Spring REST services,
that run in within a containerized environment, ensuring a more realistic and comprehensive testing scenario.

For example we use the following `@GetMapping` in the `AuthorController` to get a `Author` from the database.

```java
@RestController
@RequestMapping("/api/author")
public class AuthorController {

    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> get(@PathVariable("id") Long id) {
        var author = authorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id: '" + id + "' not found!"));

        return ResponseEntity.ok(author);
    }
}
```

Here's a basic example of how a our integration test using REST Assured might look:

```java
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

    private String baseUri;

    @PostConstruct
    public void init() {
        RestAssured.baseURI = "http://localhost/";
        RestAssured.port = port;
    }

    @Test
    void getAuthorWithExistingId() {
        Author a1 = Author.builder()
                .firstName("firstname")
                .lastName("lastname")
                .books(new ArrayList<>())
                .build();
        Book b1 = Book.builder()
                .title("book")
                .price(BigDecimal.valueOf(10.00))
                .publishDate(LocalDate.of(2024 , 1, 10))
                .author(a1)
                .build();
        a1.getBooks().add(b1);
        var author = authorRepository.save(a1);

        given()
                .contentType(ContentType.JSON)
        .when()
                .pathParam("id", author.getId().toString())
                .get("/api/author/{id}")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(author.getId().intValue()))
                .body("firstName", equalTo(author.getFirstName()))
                .body("lastName", equalTo(author.getLastName()))
                .body("books", hasSize(1))
                .body("books[0].title", equalTo(b1.getTitle()))
                .body("books[0].price", equalTo(b1.getPrice().floatValue()))
                .body("books[0].publishDate", equalTo(b1.getPublishDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }
}
```

This setup allows us to:

- Use Testcontainers to start a PostgreSQL database in a Docker container
- Start the Spring Boot application with the test database
- Use Rest-Assured to make HTTP requests to the Spring Boot REST API and validate the responses

We use the Maven Failsafe Plugin to run integration tests in Maven projects. 
You can execute them by running:

```bash
mvn verify
```

Integration tests are run during the `integration-test` phase of the Maven build lifecycle. So this command will run both unit tests and integration tests.

### Returning floats and doubles as BigDecimal

You can configure Rest-Assured to return floats and doubles as `BIGDECIMAL`.
But, there might be some strange errors while comparing `BIGDECIMAL` numbers.

```bash
java.lang.AssertionError: 1 expectation failed.
JSON path [0].price doesn't match.
Expected: <11.0>
  Actual: <11.00>
```

The reason is that the correct way to compare `BigDecimal` is to use the `comparesEqualTo` matcher instead of `equalTo`.

```java
class AuthorControllerTestIT {

    @Autowired
    PostgreSQLContainer<?> postgres;

    @Autowired
    AuthorRepository authorRepository;
    
    private Author author1;
    private Book books1;

    // setup ...
  
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
                    .body("[0].price", equalTo(books1.getPrice()))
                    .body("[0].publishDate", equalTo(books1.getPublishDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .extract().response();
        log.info(response.asString());
    }
}
```

## Documenting our REST API using OpenAPI 3.0

Springdoc-openapi is a popular library for automatically generating OpenAPI 3 documentation for Spring Boot applications.

To use springdoc-openapi add the following dependency:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```

This dependency includes Swagger UI, allowing you to view the generated API documentation in a user-friendly interface.

Once you've added the dependency and started your Spring Boot application, we can run our application and find the OpenAPI descriptions at /v3/api-docs`.

```bash
http://localhost:8080/v3/api-docs
```

We can configure the path in `application.properties` and set it to `/api-docs`.

```text
springdoc.api-docs.path=/api-docs
```

The OpenAPI definitions are in JSON format by default. For yaml format, we can add the `.yaml` suffix.

```bash
http://localhost:8080/api-docs.yaml
```

We can find the Swagger UI interface at: `/swagger-ui.html`

```bash
http://localhost:8080/swagger-ui.html
```

Did you notice that ugly heading `author-controller` on the top? We can change that using The `@Tag` annotation. 
And you can also customize the generated documentation using annotations like `@Operation("")` and `@ApiResponses` to give the user some hints.

```java
@Tag(name = "Authors", description = "the Author Api")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Get a author by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the author",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class)) }),
            @ApiResponse(responseCode = "404", description = "Author not found", content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<Author> get(@PathVariable("id") Long id) {
        var author = authorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id: '" + id + "' not found!"));

        return ResponseEntity.ok(author);
    }
}
```


## References

OneToMany

- [Mkyong - Spring Boot + Spring Data JPA + PostgreSQL example](https://mkyong.com/spring-boot/spring-boot-spring-data-jpa-postgresql/)
- [Vlad Mihalcea - The best way to map a @OneToMany relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)
- [CodeGaym - Cascading changes](https://codegym.cc/quests/lectures/en.questhibernate.level13.lecture05)

JPA / Hibernate

- [@TRANSACTIONAL IN SPRING BOOT](https://youlearncode.com/transactional-in-spring-boot/)
- [Hibernate Entity Lifecycle](https://www.baeldung.com/hibernate-entity-lifecycle#managed-entity)

JPQL

- [How to join unassociated entities with JPA and Hibernate](https://thorben-janssen.com/how-to-join-unrelated-entities/)
- [JPQL – How to Define Queries in JPA and Hibernate](https://thorben-janssen.com/jpql/)
- [What is the difference between JOIN and JOIN FETCH when using JPA and Hibernate](https://stackoverflow.com/questions/17431312/what-is-the-difference-between-join-and-join-fetch-when-using-jpa-and-hibernate)

Rest-Assured

- [A Guide to REST-assured](https://www.baeldung.com/rest-assured-tutorial)
- [Getting and Verifying Response Data with REST-assured](https://www.baeldung.com/rest-assured-response)
- [How to combine Testcontainers, REST-Assured and WireMock](https://medium.com/@nihatonder87/how-to-combine-testcontainers-rest-assured-and-wiremock-8e5cb3ede16e)
- [Rest-Assured :: Wiki :: Returning floats and doubles as BigDecimal](https://github.com/rest-assured/rest-assured/wiki/Usage#returning-floats-and-doubles-as-bigdecimal)
- [JUnit Assert with BigDecimal](https://stackoverflow.com/questions/35573187/junit-assert-with-bigdecimal)

Open-Api / Swagger

- [Documenting a Spring REST API Using OpenAPI 3.0](https://www.baeldung.com/spring-rest-openapi-documentation)
- [Fady Kuzman - Using Swagger 3 in Spring Boot 3](https://medium.com/@f.s.a.kuzman/using-swagger-3-in-spring-boot-3-c11a483ea6dc)

Mapstruct

- [Quick Guide to MapStruct](https://www.baeldung.com/mapstruct)