# Spring Boot, Postgres, and OneToMany

This article shows how to use Spring Boot, Spring Data JPA and PostgreSQL to create a One-to-Many Relationship. 
Additionally it shows how to add REST endpoints for CRUD database operations.

We use Testcontainers to start an actual PostgreSQL container for testing and developement.
You need to have Docker or Docker Desktop to be running in the background.

This application uses the following Technologies.

- Spring Boot
- Spring Data JPA
- PostgreSQL
- JUnit 5
- Docker
- Testcontainers


## One-to-Many Relationships

If you create a database schema, a _one-to-many_ mapping means that one row in a table is mapped to multiple rows in another table.
The _"one" side_ has a _primary key_ and the _"many" side_ has a _foreign key_ which refers to the primary key of _"one" side_.

In Spring Data JPA OneToMany relationships allow you to model _one-to-many_ mapping where one _entity_ can have _multiple related entities_.

### Bidirectional vs Unidirectional

There are two choices how to create a `OneToMany` relationship.

- Use a unidirectional `@OneToMany` association
- Use a bidirectional `@OneToMany` association

**Bidirectional** relationships are generally recommended because they:

- Result in more efficient SQL queries[1][2]
- Allow navigation from both sides of the relationship
- Provide better control over the relationship

**Unidirectional** relationships are simpler but less efficient and they create an additonal table for the mapping.

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

### Performance Considerations

- Avoid eagerly fetching large collections. Use JPQL queries for better performance when dealing with large datasets
- Consider using `@ManyToOne` on the child side only if you don't need to access the collection from the parent side often, see [The best way to map a @OneToMany relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/).

### Conclusion

OneToMany relationships in Spring Data JPA provide a powerful way to model hierarchical data structures. By following best practices and understanding the implications of bidirectional vs unidirectional relationships, you can create efficient and maintainable data models for your application.

## Owning Side

Baeldung explains about the _Owning-Side_ in [Hibernate One to Many](https://www.baeldung.com/hibernate-one-to-many).
You can find the source-code at Github [Github - Hibernate-Annotations - HibernateManyIsOwningSide](https://github.com/eugenp/tutorials/tree/master/persistence-modules/hibernate-annotations/src/main/java/com/baeldung/hibernate/oneToMany).

## References

- [Mkyong - Spring Boot + Spring Data JPA + PostgreSQL example](https://mkyong.com/spring-boot/spring-boot-spring-data-jpa-postgresql/)
- [Vlad Mihalcea - The best way to map a @OneToMany relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)
- [CodeGaym - Cascading changes](https://codegym.cc/quests/lectures/en.questhibernate.level13.lecture05)