package com.example.microservicioEvaluaciones.models.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EvaluacionRequest {
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer porcentaje;
    private Long cursoId;
}