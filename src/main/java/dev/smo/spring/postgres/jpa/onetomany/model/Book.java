package dev.smo.spring.postgres.jpa.onetomany.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @EqualsAndHashCode.Exclude
    private String title;
    @EqualsAndHashCode.Exclude
    private BigDecimal price;
    @EqualsAndHashCode.Exclude
    private LocalDate publishDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private Author author;

}
