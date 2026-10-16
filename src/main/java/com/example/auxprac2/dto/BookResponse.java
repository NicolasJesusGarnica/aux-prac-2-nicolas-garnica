package com.example.auxprac2.dto;

import com.example.auxprac2.model.Book;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Libro devuelto por la API")
public record BookResponse(

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

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.id(),
                book.title(),
                book.author(),
                book.isbn(),
                book.publicationYear(),
                book.category(),
                book.available()
        );
    }
}