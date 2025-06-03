package com.sistema.educativo.repository;

import com.sistema.educativo.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    Optional<Grupo> findByCodigo(String codigo);

    List<Grupo> findByActivoTrue();

    List<Grupo> findByPeriodo(String periodo);

    List<Grupo> findBySemestre(Integer semestre);

    @Query("SELECT g FROM Grupo g JOIN g.profesoresGrupos pg WHERE pg.profesor.id = :profesorId AND pg.activo = true")
    List<Grupo> findByProfesorId(@Param("profesorId") Long profesorId);

    @Query("SELECT COUNT(eg) FROM EstudianteGrupo eg WHERE eg.grupo.id = :grupoId AND eg.activo = true")
    Integer countEstudiantesActivos(@Param("grupoId") Long grupoId);

    Boolean existsByCodigo(String codigo);
}