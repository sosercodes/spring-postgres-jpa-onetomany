package dev.smo.spring.postgres.jpa.onetomany.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(exclude = {"title", "price", "publishDate", "author"})
@ToString(exclude = {"author"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private BigDecimal price;

    private LocalDate publishDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    @JsonBackReference
    private Author author;

}
