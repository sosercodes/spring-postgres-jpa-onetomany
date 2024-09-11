package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.model.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @EntityGraph(attributePaths = { "books" })
    List<Author> findByFirstName(String firstName);

    void deleteByFirstName(String firstName);
}
