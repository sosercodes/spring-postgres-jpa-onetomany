package dev.smo.spring.postgres.jpa.onetomany.repository;

import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitle(String title);

}
