package com.sistema.educativo.controller;

import com.sistema.educativo.dto.AsistenciaDTO;
import com.sistema.educativo.dto.TomarAsistenciaDTO;
import com.sistema.educativo.service.AsistenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    // Tomar asistencia
    @PostMapping("/tomar")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<?> tomarAsistencia(@Valid @RequestBody TomarAsistenciaDTO dto) {
        try {
            asistenciaService.tomarAsistencia(dto);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Asistencia registrada correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Obtener lista de estudiantes para tomar asistencia
    @GetMapping("/grupo/{grupoId}/fecha/{fecha}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<AsistenciaDTO>> obtenerListaAsistencia(
            @PathVariable Long grupoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerListaEstudiantesParaAsistencia(grupoId, fecha));
    }

    // Verificar si ya se tomó asistencia
    @GetMapping("/grupo/{grupoId}/fecha/{fecha}/verificar")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<Map<String, Boolean>> verificarAsistencia(
            @PathVariable Long grupoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("yaRegistrada", asistenciaService.yaSeTomoAsistencia(grupoId, fecha));
        return ResponseEntity.ok(response);
    }

    // Obtener historial de asistencias de un grupo
    @GetMapping("/grupo/{grupoId}/historial")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<AsistenciaDTO>> obtenerHistorial(@PathVariable Long grupoId) {
        return ResponseEntity.ok(asistenciaService.obtenerHistorialPorGrupo(grupoId));
    }

    // Obtener estadísticas de asistencia
    @GetMapping("/grupo/{grupoId}/estadisticas")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<AsistenciaService.EstadisticasAsistenciaDTO> obtenerEstadisticas(@PathVariable Long grupoId) {
        return ResponseEntity.ok(asistenciaService.obtenerEstadisticas(grupoId));
    }

    // Obtener historial con filtros
    @GetMapping("/historial")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<AsistenciaDTO>> obtenerHistorialFiltrado(
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String estado) {

        List<AsistenciaDTO> historial;

        if (grupoId != null) {
            historial = asistenciaService.obtenerHistorialPorGrupo(grupoId);
        } else {
            // Si no se especifica grupo, devolver vacío o implementar lógica para todos los grupos
            return ResponseEntity.ok(new ArrayList<>());
        }

        // Aplicar filtros adicionales si se proporcionan
        if (fechaInicio != null || fechaFin != null || estado != null) {
            historial = historial.stream()
                    .filter(a -> fechaInicio == null || !a.getFecha().isBefore(fechaInicio))
                    .filter(a -> fechaFin == null || !a.getFecha().isAfter(fechaFin))
                    .filter(a -> estado == null || estado.isEmpty() || a.getEstado().equals(estado))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(historial);
    }
}