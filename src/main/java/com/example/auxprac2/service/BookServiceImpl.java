package com.example.auxprac2.service;

import com.example.auxprac2.dto.BookRequest;
import com.example.auxprac2.exception.DuplicateResourceException;
import com.example.auxprac2.exception.ResourceNotFoundException;
import com.example.auxprac2.model.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del servicio de libros.
 * La información se mantiene temporalmente en memoria usando una estructura List,
 * tal como lo indica el enunciado (no se utiliza base de datos).
 */
@Service
public class BookServiceImpl implements BookService {

    private final List<Book> libros = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong secuenciaId = new AtomicLong(0);

    @Override
    public Book create(BookRequest request) {
        if (existsByIsbn(request.isbn())) {
            throw DuplicateResourceException.isbn(request.isbn());
        }

        Book book = new Book(
                secuenciaId.incrementAndGet(),
                request.title().trim(),
                request.author().trim(),
                request.isbn().trim(),
                request.publicationYear(),
                request.category().trim(),
                Boolean.TRUE.equals(request.available())
        );

        libros.add(book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        synchronized (libros) {
            return List.copyOf(libros);
        }
    }

    @Override
    public Book findById(Long id) {
        return buscarIndice(id)
                .map(libros::get)
                .orElseThrow(() -> ResourceNotFoundException.book(id));
    }

    @Override
    public Book update(Long id, BookRequest request) {
        synchronized (libros) {
            Optional<Integer> indice = buscarIndice(id);
            if (indice.isEmpty()) {
                throw ResourceNotFoundException.book(id);
            }

            Book actual = libros.get(indice.get());
            boolean mismoIsbn = actual.isbn().equals(request.isbn().trim());
            if (!mismoIsbn && existsByIsbn(request.isbn())) {
                throw DuplicateResourceException.isbn(request.isbn());
            }

            Book actualizado = new Book(
                    actual.id(),
                    request.title().trim(),
                    request.author().trim(),
                    request.isbn().trim(),
                    request.publicationYear(),
                    request.category().trim(),
                    Boolean.TRUE.equals(request.available())
            );

            libros.set(indice.get(), actualizado);
            return actualizado;
        }
    }

    @Override
    public void delete(Long id) {
        synchronized (libros) {
            Optional<Integer> indice = buscarIndice(id);
            if (indice.isEmpty()) {
                throw ResourceNotFoundException.book(id);
            }
            libros.remove(indice.get().intValue());
        }
    }

    private Optional<Integer> buscarIndice(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).id().equals(id)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    private boolean existsByIsbn(String isbn) {
        if (isbn == null) {
            return false;
        }
        String buscado = isbn.trim();
        synchronized (libros) {
            return libros.stream().anyMatch(libro -> libro.isbn().equals(buscado));
        }
    }
}