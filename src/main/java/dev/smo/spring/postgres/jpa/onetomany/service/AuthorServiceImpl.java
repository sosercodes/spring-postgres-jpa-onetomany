package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.mapper.AuthorMapper;
import dev.smo.spring.postgres.jpa.onetomany.model.AuthorDTO;
import dev.smo.spring.postgres.jpa.onetomany.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;


    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public Optional<AuthorDTO> findById(Long id) {
        var author = authorRepository.findById(id);
        return author.map(authorMapper::toAuthorDTO);
    }

    @Override
    public List<AuthorDTO> findAll() {
        return authorRepository.findAll().stream().map(authorMapper::toAuthorDTO).toList();
    }

    @Override
    public AuthorDTO save(AuthorDTO authorDTO) {
        var author = authorMapper.toAuthor(authorDTO);
        return authorMapper.toAuthorDTO(authorRepository.save(author));
    }
}
