package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import dev.smo.spring.postgres.jpa.onetomany.mapper.BookMapper;
import dev.smo.spring.postgres.jpa.onetomany.model.BookDTO;
import dev.smo.spring.postgres.jpa.onetomany.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public List<BookDTO> findAllBooksForAuthorWithId(Long authorId) {
        List<Book> books = bookRepository.findBooksByAuthorId(authorId);
        return books.stream().map(bookMapper::toBookDTO).toList();
    }
}
