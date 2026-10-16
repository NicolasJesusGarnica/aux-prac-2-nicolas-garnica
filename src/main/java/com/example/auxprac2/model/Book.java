package com.example.auxprac2.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entidad de dominio que representa un libro de la biblioteca.
 * Los datos se mantienen en memoria (no hay base de datos).
 */
@Schema(description = "Libro registrado en la biblioteca")
public record Book(

        @Schema(description = "Identificador único del libro", example = "1")
        Long id,

        @Schema(description = "Título del libro", example = "Cien años de soledad")
        String title,

        @Schema(description = "Autor del libro", example = "Gabriel García Márquez")
        String author,

        @Schema(description = "ISBN del libro", example = "9780307474728")
        String isbn,

        @Schema(description = "Año de publicación", example = "1967")
        int publicationYear,

        @Schema(description = "Categoría del libro", example = "Realismo mágico")
        String category,

        @Schema(description = "Indica si el libro está disponible para préstamo", example = "true")
        boolean available
) {
}