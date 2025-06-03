package com.sistema.educativo.controller;

import com.sistema.educativo.dto.CrearUsuarioDTO;
import com.sistema.educativo.dto.UsuarioDTO;
import com.sistema.educativo.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // Crear nuevo usuario
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> crear(@Valid @RequestBody CrearUsuarioDTO dto) {
        try {
            UsuarioDTO nuevoUsuario = usuarioService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            UsuarioDTO usuarioActualizado = usuarioService.actualizar(id, dto);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Cambiar estado activo/inactivo
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UsuarioDTO> cambiarEstado(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id));
    }

    // Cambiar contraseña
    @PatchMapping("/{id}/contrasena")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> cambiarContrasena(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String nuevaContrasena = request.get("nuevaContrasena");
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "La contraseña no puede estar vacía");
            return ResponseEntity.badRequest().body(error);
        }

        usuarioService.cambiarContrasena(id, nuevaContrasena);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }

    // Eliminar usuario (soft delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario desactivado correctamente");
        return ResponseEntity.ok(response);
    }
}