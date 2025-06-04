package com.sistema.educativo.controller;

import com.sistema.educativo.dto.CrearGrupoDTO;
import com.sistema.educativo.dto.GrupoDTO;
import com.sistema.educativo.dto.UsuarioDTO;
import com.sistema.educativo.service.GrupoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    // Obtener todos los grupos
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<GrupoDTO>> obtenerTodos() {
        return ResponseEntity.ok(grupoService.obtenerTodos());
    }

    // Obtener grupos activos
    @GetMapping("/activos")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<GrupoDTO>> obtenerActivos() {
        return ResponseEntity.ok(grupoService.obtenerActivos());
    }

    // Obtener grupo por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<GrupoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.obtenerPorId(id));
    }

    // Obtener grupos de un profesor
    @GetMapping("/profesor/{profesorId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<GrupoDTO>> obtenerPorProfesor(@PathVariable Long profesorId) {
        return ResponseEntity.ok(grupoService.obtenerPorProfesor(profesorId));
    }

    // Crear nuevo grupo
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> crear(@Valid @RequestBody CrearGrupoDTO dto) {
        try {
            GrupoDTO nuevoGrupo = grupoService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGrupo);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Actualizar grupo
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody GrupoDTO dto) {
        try {
            GrupoDTO grupoActualizado = grupoService.actualizar(id, dto);
            return ResponseEntity.ok(grupoActualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Cambiar estado activo/inactivo
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GrupoDTO> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.cambiarEstado(id));
    }

    // Asignar profesor a grupo
    @PostMapping("/{grupoId}/profesores/{profesorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> asignarProfesor(@PathVariable Long grupoId, @PathVariable Long profesorId) {
        try {
            grupoService.asignarProfesor(grupoId, profesorId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Profesor asignado correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Inscribir estudiante en grupo
    @PostMapping("/{grupoId}/estudiantes/{estudianteId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> inscribirEstudiante(@PathVariable Long grupoId, @PathVariable Long estudianteId) {
        try {
            grupoService.inscribirEstudiante(grupoId, estudianteId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Estudiante inscrito correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Desinscribir estudiante de grupo
    @DeleteMapping("/{grupoId}/estudiantes/{estudianteId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> desinscribirEstudiante(@PathVariable Long grupoId, @PathVariable Long estudianteId) {
        try {
            grupoService.desinscribirEstudiante(grupoId, estudianteId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Estudiante desinscrito correctamente");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Obtener estudiantes de un grupo
    @GetMapping("/{grupoId}/estudiantes")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<List<UsuarioDTO>> obtenerEstudiantesDelGrupo(@PathVariable Long grupoId) {
        return ResponseEntity.ok(grupoService.obtenerEstudiantesDelGrupo(grupoId));
    }

    // Obtener estudiantes disponibles para inscribir
    @GetMapping("/{grupoId}/estudiantes/disponibles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> obtenerEstudiantesDisponibles(@PathVariable Long grupoId) {
        return ResponseEntity.ok(grupoService.obtenerEstudiantesDisponibles(grupoId));
    }
}