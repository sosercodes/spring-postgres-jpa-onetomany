package dev.smo.spring.postgres.jpa.onetomany.controller;

import dev.smo.spring.postgres.jpa.onetomany.exception.ResourceNotFoundException;
import dev.smo.spring.postgres.jpa.onetomany.model.AuthorDTO;
import dev.smo.spring.postgres.jpa.onetomany.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Authors", description = "the Author Api")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Get a author by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the author",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthorDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Author not found", content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> get(@PathVariable("id") Long id) {
        var author = authorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id: '" + id + "' not found!"));

        return ResponseEntity.ok(author);
    }

    @Operation(summary = "Get all authors", description = "fetches all author entities and their books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all authors",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuthorDTO.class))) })
    })
    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAll() {
        var authors = authorService.findAll();
        return ResponseEntity.ok(authors);
    }
}
