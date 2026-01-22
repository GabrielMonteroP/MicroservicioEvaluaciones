package com.example.microservicioEvaluaciones.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) { super(mensaje); }
}