package com.example.auxprac2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Respuesta de error de la API")
public record ErrorResponse(

        @Schema(description = "Fecha y hora del error", example = "2026-03-01T10:15:30")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "Código de estado HTTP", example = "404")
        int status,

        @Schema(description = "Descripción del error", example = "No existe un libro con el id 99")
        String message,

        @Schema(description = "Ruta que originó la petición", example = "/api/books/99")
        String path,

        @Schema(description = "Detalle de los campos inválidos (solo en errores de validación)")
        Map<String, String> fields
) {
}