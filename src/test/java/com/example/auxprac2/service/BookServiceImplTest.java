package com.example.auxprac2.service;

import com.example.auxprac2.dto.BookRequest;
import com.example.auxprac2.exception.DuplicateResourceException;
import com.example.auxprac2.exception.ResourceNotFoundException;
import com.example.auxprac2.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Servicio de libros almacenado en memoria")
class BookServiceImplTest {

    private BookServiceImpl bookService;

    private static BookRequest request(String isbn, String title) {
        return new BookRequest(title, "Gabriel Garcia Marquez", isbn, 1967, "Realismo magico", true);
    }

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl();
    }

    @Test
    @DisplayName("Crea un libro y le asigna un id incremental")
    void createAssignsIncrementalId() {
        Book first = bookService.create(request("9780307474728", "Cien anios de soledad"));
        Book second = bookService.create(request("9780306406157", "Ficciones"));

        assertEquals(1L, first.id());
        assertEquals(2L, second.id());
        assertEquals(2, bookService.findAll().size());
    }

    @Test
    @DisplayName("Rechaza un isbn duplicado")
    void createRejectsDuplicateIsbn() {
        bookService.create(request("9780307474728", "Cien anios de soledad"));

        assertThrows(DuplicateResourceException.class,
                () -> bookService.create(request("9780307474728", "Otro libro")));
    }

    @Test
    @DisplayName("Busca un libro existente por id")
    void findByIdReturnsBook() {
        Book created = bookService.create(request("9780307474728", "Cien anios de soledad"));

        assertEquals(created.id(), bookService.findById(created.id()).id());
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException al buscar un id inexistente")
    void findByIdThrowsWhenMissing() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.findById(99L));
    }

    @Test
    @DisplayName("Actualiza un libro existente conservando su id")
    void updateReplacesData() {
        Book created = bookService.create(request("9780307474728", "Cien anios de soledad"));

        Book updated = bookService.update(created.id(), request("9780307474728", "Titulo editado"));

        assertEquals(created.id(), updated.id());
        assertEquals("Titulo editado", updated.title());
        assertEquals(1, bookService.findAll().size());
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException al actualizar un id inexistente")
    void updateThrowsWhenMissing() {
        assertThrows(ResourceNotFoundException.class,
                () -> bookService.update(99L, request("9780307474728", "Cien anios de soledad")));
    }

    @Test
    @DisplayName("Elimina un libro existente")
    void deleteRemovesBook() {
        Book created = bookService.create(request("9780307474728", "Cien anios de soledad"));

        bookService.delete(created.id());

        assertTrue(bookService.findAll().isEmpty());
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException al eliminar un id inexistente")
    void deleteThrowsWhenMissing() {
        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(99L));
    }

    @Test
    @DisplayName("findAll devuelve una copia inmutable de la lista interna")
    void findAllReturnsSnapshot() {
        bookService.create(request("9780307474728", "Cien anios de soledad"));

        List<Book> libros = bookService.findAll();

        assertThrows(UnsupportedOperationException.class, libros::clear);
        assertEquals(1, bookService.findAll().size());
    }
}