package dev.smo.spring.postgres.jpa.onetomany.mapper;

import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import dev.smo.spring.postgres.jpa.onetomany.model.BookDTO;
import org.springframework.stereotype.Component;

@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDTO toBookDTO(Book book) {
        var bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setPrice(book.getPrice());
        bookDTO.setPublishDate(book.getPublishDate());
        return bookDTO;
    }

    @Override
    public Book toBook(BookDTO bookDTO) {
        var book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setPrice(bookDTO.getPrice());
        book.setPublishDate(bookDTO.getPublishDate());
        return book;
    }
}
