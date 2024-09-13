package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.model.Author;

import java.util.Optional;

public interface AuthorService {

    Optional<Author> findById(Long id);

}
