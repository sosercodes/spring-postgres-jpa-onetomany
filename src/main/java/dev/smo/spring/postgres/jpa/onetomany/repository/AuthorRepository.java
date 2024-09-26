package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.entities.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @EntityGraph(attributePaths = { "books" })
    List<Author> findByFirstName(String firstName);

    @Query("select a from Author a left join fetch a.books where a.id = :id")
    Optional<Author> findByIdWithBooks(Long id);

    @Override
    @Query("from Author a left join fetch a.books")
    List<Author> findAll();

    void deleteByFirstName(String firstName);
}
