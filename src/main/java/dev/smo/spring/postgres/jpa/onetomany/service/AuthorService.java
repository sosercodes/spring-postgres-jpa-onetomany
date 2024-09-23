package dev.smo.spring.postgres.jpa.onetomany.service;

import dev.smo.spring.postgres.jpa.onetomany.model.AuthorDTO;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Optional<AuthorDTO> findById(Long id);

    List<AuthorDTO> findAll();

    AuthorDTO save(AuthorDTO authorDTO);
}
