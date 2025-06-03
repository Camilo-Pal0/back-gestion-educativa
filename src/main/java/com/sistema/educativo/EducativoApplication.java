package com.sistema.educativo;

import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EducativoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EducativoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Crear usuario admin si no existe
			if (!usuarioRepository.existsByNombreUsuario("admin")) {
				Usuario admin = new Usuario();
				admin.setNombreUsuario("admin");
				admin.setContrasena(passwordEncoder.encode("password123"));
				admin.setTipoUsuario(Usuario.TipoUsuario.ADMIN);
				admin.setEmail("admin@sistema.edu");
				admin.setActivo(true);
				usuarioRepository.save(admin);
				System.out.println("Usuario admin creado con contraseña: password123");
			}

			// Crear usuario profesor si no existe
			if (!usuarioRepository.existsByNombreUsuario("profesor1")) {
				Usuario profesor = new Usuario();
				profesor.setNombreUsuario("profesor1");
				profesor.setContrasena(passwordEncoder.encode("password123"));
				profesor.setTipoUsuario(Usuario.TipoUsuario.PROFESOR);
				profesor.setEmail("profesor1@sistema.edu");
				profesor.setEspecialidad("Matemáticas");
				profesor.setActivo(true);
				usuarioRepository.save(profesor);
				System.out.println("Usuario profesor1 creado con contraseña: password123");
			}
		};
	}
}