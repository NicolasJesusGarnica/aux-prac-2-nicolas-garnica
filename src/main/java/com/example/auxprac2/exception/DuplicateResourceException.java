package com.example.auxprac2.exception;

/**
 * Se lanza cuando se intenta registrar un libro con un ISBN que ya existe.
 * El controlador la traduce a una respuesta HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException isbn(String isbn) {
        return new DuplicateResourceException("Ya existe un libro registrado con el isbn " + isbn);
    }
}