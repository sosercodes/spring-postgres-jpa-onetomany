package dev.smo.spring.postgres.jpa.onetomany.mapper;

import dev.smo.spring.postgres.jpa.onetomany.entities.Author;
import dev.smo.spring.postgres.jpa.onetomany.model.AuthorDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public AuthorDTO toAuthorDTO(Author author) {
        if (author == null) {
            return null;
        }
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setFirstName(author.getFirstName());
        authorDTO.setLastName(author.getLastName());
        return authorDTO;
    }

    @Override
    public Author toAuthor(AuthorDTO authorDTO) {
        if (authorDTO == null) {
            return null;
        }
        Author author = new Author();
        author.setId(authorDTO.getId());
        author.setFirstName(authorDTO.getFirstName());
        author.setLastName(authorDTO.getLastName());
        return author;
    }
}
