package dev.smo.spring.postgres.jpa.onetomany.mapper;

import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import dev.smo.spring.postgres.jpa.onetomany.model.BookDTO;

public interface BookMapper {
    BookDTO toBookDTO(Book book);
    Book toBook(BookDTO bookDTO);
}
