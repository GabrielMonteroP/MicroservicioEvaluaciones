package com.example.microservicioEvaluaciones.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvaluacionResponse {
    private Long id;
    private String titulo;
    private String nombreCurso;
    private String estado;
    private Integer porcentaje;
}