package com.sistema.educativo;

import com.sistema.educativo.entity.EstudianteGrupo;
import com.sistema.educativo.entity.Grupo;
import com.sistema.educativo.entity.ProfesorGrupo;
import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.EstudianteGrupoRepository;
import com.sistema.educativo.repository.GrupoRepository;
import com.sistema.educativo.repository.ProfesorGrupoRepository;
import com.sistema.educativo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class EducativoApplication  {

	public static void main(String[] args) {
		SpringApplication.run(EducativoApplication .class, args);
	}

	@Bean
	CommandLineRunner init(UsuarioRepository usuarioRepository,
						   PasswordEncoder passwordEncoder,
						   GrupoRepository grupoRepository,
						   EstudianteGrupoRepository estudianteGrupoRepository,
						   ProfesorGrupoRepository profesorGrupoRepository) {
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

			// Crear algunos estudiantes de prueba
			for (int i = 1; i <= 5; i++) {
				String username = "estudiante" + i;
				if (!usuarioRepository.existsByNombreUsuario(username)) {
					Usuario estudiante = new Usuario();
					estudiante.setNombreUsuario(username);
					estudiante.setContrasena(passwordEncoder.encode("password123"));
					estudiante.setTipoUsuario(Usuario.TipoUsuario.ESTUDIANTE);
					estudiante.setEmail(username + "@sistema.edu");
					estudiante.setActivo(true);
					usuarioRepository.save(estudiante);
					System.out.println("Usuario " + username + " creado");
				}
			}

			// Crear un grupo de prueba
			if (grupoRepository.findByCodigo("MAT101").isEmpty()) {
				Grupo grupo = new Grupo();
				grupo.setCodigo("MAT101");
				grupo.setNombreGrupo("Grupo A");
				grupo.setMateria("Cálculo Diferencial");
				grupo.setFacultad("Ingeniería");
				grupo.setSemestre(1);
				grupo.setAnoEscolar(2024);
				grupo.setPeriodo("2024-1");
				grupo.setHorario("LUN-MIE-VIE 14:00-16:00");
				grupo.setAula("Edificio A - Salón 301");
				grupo.setCreditos(4);
				grupo.setCupoMaximo(30);
				grupo.setActivo(true);
				grupoRepository.save(grupo);

				// Asignar profesor al grupo
				Usuario profesor = usuarioRepository.findByNombreUsuario("profesor1").get();
				ProfesorGrupo pg = new ProfesorGrupo(profesor, grupo);
				profesorGrupoRepository.save(pg);

				// Inscribir estudiantes al grupo
				for (int i = 1; i <= 5; i++) {
					Usuario estudiante = usuarioRepository.findByNombreUsuario("estudiante" + i).get();
					EstudianteGrupo eg = new EstudianteGrupo(estudiante, grupo);
					estudianteGrupoRepository.save(eg);
				}

				System.out.println("Grupo MAT101 creado con profesor y estudiantes");
			}
		};
	}
}