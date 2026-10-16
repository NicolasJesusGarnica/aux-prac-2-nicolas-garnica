package com.example.auxprac2.exception;

/**
 * Se lanza cuando se consulta, actualiza o elimina un libro que no existe.
 * El controlador la traduce a una respuesta HTTP 404 NOT FOUND.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException book(Long id) {
        return new ResourceNotFoundException("No existe un libro con el id " + id);
    }
}