package com.example.auxprac2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para crear o actualizar un libro")
public record BookRequest(

        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 150, message = "El titulo no puede superar los 150 caracteres")
        @Schema(description = "Título del libro", example = "Cien años de soledad")
        String title,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 120, message = "El autor no puede superar los 120 caracteres")
        @Schema(description = "Autor del libro", example = "Gabriel García Márquez")
        String author,

        @NotBlank(message = "El isbn es obligatorio")
        @Pattern(regexp = "^[0-9]{10,13}$", message = "El isbn debe tener entre 10 y 13 digitos numericos")
        @Schema(description = "ISBN del libro", example = "9780307474728")
        String isbn,

        @NotNull(message = "El anio de publicacion es obligatorio")
        @Min(value = 1450, message = "El anio de publicacion no puede ser menor a 1450")
        @Max(value = 2100, message = "El anio de publicacion no puede ser mayor a 2100")
        @Schema(description = "Año de publicación", example = "1967")
        Integer publicationYear,

        @NotBlank(message = "La categoria es obligatoria")
        @Size(max = 80, message = "La categoria no puede superar los 80 caracteres")
        @Schema(description = "Categoría del libro", example = "Realismo mágico")
        String category,

        @NotNull(message = "El campo available es obligatorio")
        @Schema(description = "Indica si el libro está disponible para préstamo", example = "true")
        Boolean available
) {
}