package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.model.BookDTO;

import java.util.List;

public interface BookService {

    List<BookDTO> findAllBooksForAuthorWithId(Long authorId);

}
