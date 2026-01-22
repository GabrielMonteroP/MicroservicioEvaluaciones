package com.example.microservicioEvaluaciones.repositories;

import com.example.microservicioEvaluaciones.models.entities.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByCursoId(Long cursoId);
}