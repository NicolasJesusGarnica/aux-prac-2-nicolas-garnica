package com.example.auxprac2.service;

import com.example.auxprac2.dto.BookRequest;
import com.example.auxprac2.model.Book;

import java.util.List;

/**
 * Contrato de la capa de servicio para la gestión de libros.
 * El controlador nunca accede directamente al almacenamiento en memoria.
 */
public interface BookService {

    Book create(BookRequest request);

    List<Book> findAll();

    Book findById(Long id);

    Book update(Long id, BookRequest request);

    void delete(Long id);
}