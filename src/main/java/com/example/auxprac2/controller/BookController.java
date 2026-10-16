package com.example.auxprac2.controller;

import com.example.auxprac2.dto.BookRequest;
import com.example.auxprac2.dto.BookResponse;
import com.example.auxprac2.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Libros", description = "API REST para administrar los libros de la biblioteca")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Registrar un libro", description = "Crea un libro nuevo y devuelve el recurso generado con su id.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Libro registrado correctamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o isbn duplicado",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El isbn ya está registrado",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        BookResponse created = BookResponse.from(bookService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los libros", description = "Devuelve la lista completa de libros almacenados en memoria.")
    public ResponseEntity<List<BookResponse>> findAll() {
        List<BookResponse> libros = bookService.findAll().stream()
                .map(BookResponse::from)
                .toList();
        return ResponseEntity.ok(libros);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un libro por ID", description = "Busca un libro específico por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese id",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> findById(
            @Parameter(description = "Identificador del libro", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(BookResponse.from(bookService.findById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un libro", description = "Reemplaza todos los datos del libro indicado por el id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese id",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El isbn ya pertenece a otro libro",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class)))
    })
    public ResponseEntity<BookResponse> update(
            @Parameter(description = "Identificador del libro", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(BookResponse.from(bookService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un libro", description = "Elimina el libro indicado por el id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Libro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese id",
                    content = @Content(schema = @Schema(implementation = com.example.auxprac2.dto.ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador del libro", example = "1")
            @PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}