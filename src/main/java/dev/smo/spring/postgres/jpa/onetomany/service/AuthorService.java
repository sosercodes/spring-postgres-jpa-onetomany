package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.entities.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Optional<Author> findById(Long id);

    List<Author> findAll();
}
