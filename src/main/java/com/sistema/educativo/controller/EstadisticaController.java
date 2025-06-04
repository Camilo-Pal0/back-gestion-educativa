package com.sistema.educativo.controller;

import com.sistema.educativo.service.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sistema.educativo.repository.UsuarioRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Estadísticas para admin
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasAdmin() {
        return ResponseEntity.ok(estadisticaService.obtenerEstadisticasAdmin());
    }

    // Estadísticas para profesor
    @GetMapping("/profesor")
    @PreAuthorize("hasAuthority('PROFESOR')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasProfesor(Authentication auth) {
        String username = auth.getName();
        var usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(estadisticaService.obtenerEstadisticasProfesor(usuario.getId()));
    }

    // Estadísticas de asistencias de hoy
    @GetMapping("/asistencias-hoy")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROFESOR')")
    public ResponseEntity<Map<String, Object>> obtenerAsistenciasHoy() {
        return ResponseEntity.ok(estadisticaService.obtenerAsistenciasHoy());
    }
}