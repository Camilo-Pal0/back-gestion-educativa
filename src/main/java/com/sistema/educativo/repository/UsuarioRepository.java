package com.sistema.educativo.repository;

import com.sistema.educativo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByNombreUsuario(String nombreUsuario);

    Boolean existsByEmail(String email);
}