package com.sistema.educativo.service;

import com.sistema.educativo.dto.LoginRequest;
import com.sistema.educativo.dto.LoginResponse;
import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponse login(LoginRequest loginRequest) {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByNombreUsuario(loginRequest.getNombreUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el usuario está activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(loginRequest.getContrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token
        String token = jwtService.generateToken(
                usuario.getNombreUsuario(),
                usuario.getTipoUsuario().toString()
        );

        // Retornar respuesta
        return new LoginResponse(
                token,
                usuario.getNombreUsuario(),
                usuario.getEmail(),
                usuario.getTipoUsuario().toString(),
                usuario.getId()
        );
    }
}