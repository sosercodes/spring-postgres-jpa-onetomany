package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.model.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @EntityGraph(attributePaths = { "books" })
    List<Author> findByFirstName(String firstName);

    @Override
    @Query("from Author a left join fetch a.books")
    List<Author> findAll();

    void deleteByFirstName(String firstName);
}
