package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.entities.Book;
import dev.smo.spring.postgres.jpa.onetomany.mapper.BookMapper;
import dev.smo.spring.postgres.jpa.onetomany.model.BookDTO;
import dev.smo.spring.postgres.jpa.onetomany.repository.AuthorRepository;
import dev.smo.spring.postgres.jpa.onetomany.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
    }

    @Override
    public List<BookDTO> findAllBooksForAuthorWithId(Long authorId) {
        List<Book> books = bookRepository.findBooksByAuthorId(authorId);
        return books.stream().map(bookMapper::toBookDTO).toList();
    }

    @Override
    @Transactional
    public Optional<BookDTO> saveBookForAuthorWithId(BookDTO bookDTO, Long authorId) {
        var author = authorRepository.findById(authorId);
        if (author.isEmpty()) {
            return Optional.empty();
        }
        var book = bookMapper.toBook(bookDTO);
        author.get().addBook(book);
        return Optional.of(bookMapper.toBookDTO(bookRepository.save(book)));
    }
}
