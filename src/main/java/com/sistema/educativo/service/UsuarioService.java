package com.sistema.educativo.service;

import com.sistema.educativo.dto.CrearUsuarioDTO;
import com.sistema.educativo.dto.UsuarioDTO;
import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Obtener todos los usuarios
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener usuarios paginados
    public Page<UsuarioDTO> obtenerPaginados(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    // Obtener usuario por ID
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(usuario);
    }

    // Crear nuevo usuario
    public UsuarioDTO crear(CrearUsuarioDTO dto) {
        // Verificar si ya existe el nombre de usuario
        if (usuarioRepository.existsByNombreUsuario(dto.getNombreUsuario())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Verificar si ya existe el email
        if (dto.getEmail() != null && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setEmail(dto.getEmail());
        usuario.setTelefonoMovil(dto.getTelefonoMovil());
        usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(dto.getTipoUsuario()));
        usuario.setDireccion(dto.getDireccion());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setEspecialidad(dto.getEspecialidad());
        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioGuardado);
    }

    // Actualizar usuario
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el nuevo email ya existe (si es diferente al actual)
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        usuario.setEmail(dto.getEmail());
        usuario.setTelefonoMovil(dto.getTelefonoMovil());
        usuario.setDireccion(dto.getDireccion());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setEspecialidad(dto.getEspecialidad());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioActualizado);
    }

    // Cambiar estado activo/inactivo
    public UsuarioDTO cambiarEstado(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(!usuario.getActivo());
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertirADTO(usuarioActualizado);
    }

    // Cambiar contraseña
    public void cambiarContrasena(Long id, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    // Eliminar usuario (soft delete - solo desactivar)
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // Método auxiliar para convertir entidad a DTO
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setTelefonoMovil(usuario.getTelefonoMovil());
        dto.setTipoUsuario(usuario.getTipoUsuario().toString());
        dto.setDireccion(usuario.getDireccion());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setEspecialidad(usuario.getEspecialidad());
        dto.setActivo(usuario.getActivo());
        return dto;
    }
}