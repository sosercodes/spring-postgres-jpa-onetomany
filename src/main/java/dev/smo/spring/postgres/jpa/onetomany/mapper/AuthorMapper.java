package dev.smo.spring.postgres.jpa.onetomany.mapper;

import dev.smo.spring.postgres.jpa.onetomany.entities.Author;
import dev.smo.spring.postgres.jpa.onetomany.model.AuthorDTO;

public interface AuthorMapper {
    AuthorDTO toAuthorDTO(Author author);
    Author toAuthor(AuthorDTO authorDTO);
}
