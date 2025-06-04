package com.sistema.educativo.repository;

import com.sistema.educativo.entity.EstudianteGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteGrupoRepository extends JpaRepository<EstudianteGrupo, Long> {
}