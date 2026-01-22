package com.example.microservicioEvaluaciones.controller;

import com.example.microservicioEvaluaciones.models.dto.EvaluacionRequest;
import com.example.microservicioEvaluaciones.models.dto.EvaluacionResponse;
import com.example.microservicioEvaluaciones.services.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluaciones")
public class EvaluacionController {

    @Autowired
    private EvaluacionService service;

    @PostMapping
    public ResponseEntity<EvaluacionResponse> crear(@RequestBody EvaluacionRequest request) {
        EvaluacionResponse response = service.crearEvaluacion(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<EvaluacionResponse>> listarPorCurso(@PathVariable Long cursoId) {
        List<EvaluacionResponse> response = service.listarPorCurso(cursoId);
        return ResponseEntity.ok(response);
    }
}