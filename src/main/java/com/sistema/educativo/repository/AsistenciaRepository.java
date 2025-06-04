package com.sistema.educativo.repository;

import com.sistema.educativo.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // Encontrar asistencia por estudiante, grupo y fecha
    Optional<Asistencia> findByEstudianteIdAndGrupoIdAndFecha(Long estudianteId, Long grupoId, LocalDate fecha);

    // Obtener asistencias de un grupo en una fecha
    List<Asistencia> findByGrupoIdAndFecha(Long grupoId, LocalDate fecha);

    // Obtener asistencias de un grupo
    List<Asistencia> findByGrupoId(Long grupoId);

    // Obtener asistencias de un estudiante
    List<Asistencia> findByEstudianteId(Long estudianteId);

    // Obtener asistencias de un estudiante en un grupo
    List<Asistencia> findByEstudianteIdAndGrupoId(Long estudianteId, Long grupoId);

    // Contar asistencias por estado
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.grupo.id = :grupoId AND a.estado = :estado")
    Long countByGrupoAndEstado(@Param("grupoId") Long grupoId, @Param("estado") Asistencia.EstadoAsistencia estado);

    // Obtener asistencias de hoy
    @Query("SELECT a FROM Asistencia a WHERE a.fecha = CURRENT_DATE")
    List<Asistencia> findAsistenciasHoy();

    // Verificar si ya se tomó asistencia
    @Query("SELECT COUNT(a) > 0 FROM Asistencia a WHERE a.grupo.id = :grupoId AND a.fecha = :fecha")
    boolean existsAsistenciaByGrupoAndFecha(@Param("grupoId") Long grupoId, @Param("fecha") LocalDate fecha);
}