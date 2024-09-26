package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.model.BookDTO;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<BookDTO> findAllBooksForAuthorWithId(Long authorId);

    Optional<BookDTO> saveBookForAuthorWithId(BookDTO book, Long authorId);

}
