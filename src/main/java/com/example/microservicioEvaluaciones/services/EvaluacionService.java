package com.example.microservicioEvaluaciones.services;

import com.example.microservicioEvaluaciones.models.dto.CursoDto;
import com.example.microservicioEvaluaciones.models.dto.EvaluacionRequest;
import com.example.microservicioEvaluaciones.models.dto.EvaluacionResponse;
import com.example.microservicioEvaluaciones.repositories.EvaluacionRepository;
import com.example.microservicioEvaluaciones.models.entities.Evaluacion;
import com.example.microservicioEvaluaciones.exceptions.BusinessRuleException;
import com.example.microservicioEvaluaciones.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluacionService {

    @Autowired
    private EvaluacionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private final String URL_CURSOS = "http://localhost:8080/api/cursos/";

    public EvaluacionResponse crearEvaluacion(EvaluacionRequest request) {
        
        // --- 1. VALIDACIÓN Y OBTENCIÓN DE DATOS EXTERNOS ---
        CursoDto cursoDto; // Variable para guardar la info del curso
        
        try {
            // CAMBIO: Ahora mapeamos a CursoDto.class en lugar de Object.class
            cursoDto = restTemplate.getForObject(URL_CURSOS + request.getCursoId(), CursoDto.class);
            
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("El curso con ID " + request.getCursoId() + " no existe.");
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de Cursos.");
        }

        // --- 2. VALIDACIÓN DE FECHAS ---
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new BusinessRuleException("La fecha de término no puede ser anterior a la de inicio.");
        }

        // --- 3. VALIDACIÓN DE PORCENTAJES ---
        List<Evaluacion> evaluacionesExistentes = repository.findByCursoId(request.getCursoId());
        
        int sumaActual = evaluacionesExistentes.stream()
                .mapToInt(Evaluacion::getPorcentaje)
                .sum();

        if (sumaActual + request.getPorcentaje() > 100) {
            throw new BusinessRuleException("No se puede crear la evaluación. La suma superaría el 100%. Actual: " + sumaActual + "%");
        }

        // --- 4. MAPEO Y GUARDADO ---
        Evaluacion entidad = new Evaluacion();
        entidad.setTitulo(request.getTitulo());
        entidad.setDescripcion(request.getDescripcion());
        entidad.setFechaInicio(request.getFechaInicio());
        entidad.setFechaFin(request.getFechaFin());
        entidad.setPorcentaje(request.getPorcentaje());
        entidad.setCursoId(request.getCursoId());

        Evaluacion guardada = repository.save(entidad);

        // CAMBIO: Pasamos el nombre real del curso al mapper
        return mapToDto(guardada, cursoDto.getTitulo());
    }

    public List<EvaluacionResponse> listarPorCurso(Long cursoId) {
        // 1. Buscamos el nombre del curso primero (Esto también sirve de validación)
        CursoDto cursoDto;
        try {
            cursoDto = restTemplate.getForObject(URL_CURSOS + cursoId, CursoDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("El curso con ID " + cursoId + " no existe.");
        }

        // 2. Buscamos las evaluaciones en BD local
        List<Evaluacion> lista = repository.findByCursoId(cursoId);
        
        // 3. Mapeamos usando el nombre que recuperamos arriba
        return lista.stream()
                .map(evaluacion -> mapToDto(evaluacion, cursoDto.getTitulo()))
                .toList();
    }


    private EvaluacionResponse mapToDto(Evaluacion entidad, String nombreCurso) {
        

        String estado = LocalDateTime.now().isAfter(entidad.getFechaFin()) ? "CERRADA" : "ABIERTA";

        return new EvaluacionResponse(
            entidad.getId(), 
            entidad.getTitulo(),
            nombreCurso,
            estado, 
            entidad.getPorcentaje()
        );
    }
}