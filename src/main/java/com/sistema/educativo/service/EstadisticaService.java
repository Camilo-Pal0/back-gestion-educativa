package com.sistema.educativo.service;

import com.sistema.educativo.entity.Asistencia;
import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class EstadisticaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ProfesorGrupoRepository profesorGrupoRepository;

    @Autowired
    private EstudianteGrupoRepository estudianteGrupoRepository;

    // Estadísticas para Admin
    public Map<String, Object> obtenerEstadisticasAdmin() {
        Map<String, Object> stats = new HashMap<>();

        // Total de cursos activos
        long totalCursos = grupoRepository.findByActivoTrue().size();
        stats.put("totalCursos", totalCursos);

        // Total de profesores activos
        long totalProfesores = usuarioRepository.findAll().stream()
                .filter(u -> u.getTipoUsuario().equals(Usuario.TipoUsuario.PROFESOR) && u.getActivo())
                .count();
        stats.put("totalProfesores", totalProfesores);

        // Total de estudiantes activos
        long totalEstudiantes = usuarioRepository.findAll().stream()
                .filter(u -> u.getTipoUsuario().equals(Usuario.TipoUsuario.ESTUDIANTE) && u.getActivo())
                .count();
        stats.put("totalEstudiantes", totalEstudiantes);

        // Asistencia promedio global
        double asistenciaPromedio = calcularAsistenciaPromedioGlobal();
        stats.put("asistenciaPromedio", asistenciaPromedio);

        return stats;
    }

    // Estadísticas para Profesor
    public Map<String, Object> obtenerEstadisticasProfesor(Long profesorId) {
        Map<String, Object> stats = new HashMap<>();

        // Mis cursos
        long misCursos = grupoRepository.findByProfesorId(profesorId).stream()
                .filter(g -> g.getActivo())
                .count();
        stats.put("misCursos", misCursos);

        // Total de estudiantes en mis cursos
        long totalEstudiantes = grupoRepository.findByProfesorId(profesorId).stream()
                .filter(g -> g.getActivo())
                .mapToLong(g -> g.getEstudiantesGrupos().stream()
                        .filter(eg -> eg.getActivo())
                        .count())
                .sum();
        stats.put("totalEstudiantes", totalEstudiantes);

        // Clases del día
        long clasesHoy = contarClasesDelDia(profesorId);
        stats.put("clasesHoy", clasesHoy);

        // Asistencia promedio en mis clases
        double asistenciaPromedio = calcularAsistenciaPromedioProfesor(profesorId);
        stats.put("asistenciaPromedio", asistenciaPromedio);

        return stats;
    }

    // Calcular asistencia promedio global
    private double calcularAsistenciaPromedioGlobal() {
        long totalAsistencias = asistenciaRepository.count();
        if (totalAsistencias == 0) return 100.0;

        long asistenciasPresentes = asistenciaRepository.findAll().stream()
                .filter(a -> a.getEstado().equals(Asistencia.EstadoAsistencia.PRESENTE) ||
                        a.getEstado().equals(Asistencia.EstadoAsistencia.TARDANZA))
                .count();

        return (asistenciasPresentes * 100.0) / totalAsistencias;
    }

    // Calcular asistencia promedio del profesor
    private double calcularAsistenciaPromedioProfesor(Long profesorId) {
        // Obtener todos los grupos del profesor
        var grupos = grupoRepository.findByProfesorId(profesorId);

        long totalAsistencias = 0;
        long asistenciasPresentes = 0;

        for (var grupo : grupos) {
            var asistencias = asistenciaRepository.findByGrupoId(grupo.getId());
            totalAsistencias += asistencias.size();
            asistenciasPresentes += asistencias.stream()
                    .filter(a -> a.getEstado().equals(Asistencia.EstadoAsistencia.PRESENTE) ||
                            a.getEstado().equals(Asistencia.EstadoAsistencia.TARDANZA))
                    .count();
        }

        if (totalAsistencias == 0) return 100.0;
        return (asistenciasPresentes * 100.0) / totalAsistencias;
    }

    // Contar clases del día para un profesor
    private long contarClasesDelDia(Long profesorId) {
        // Por ahora retornamos el número de grupos ya que no tenemos horarios específicos
        // En el futuro, esto debería filtrar por día de la semana
        return grupoRepository.findByProfesorId(profesorId).stream()
                .filter(g -> g.getActivo())
                .count();
    }

    // Estadísticas de asistencias de hoy
    public Map<String, Object> obtenerAsistenciasHoy() {
        Map<String, Object> stats = new HashMap<>();

        LocalDate hoy = LocalDate.now();
        var asistenciasHoy = asistenciaRepository.findAsistenciasHoy();

        long totalHoy = asistenciasHoy.size();
        long presentesHoy = asistenciasHoy.stream()
                .filter(a -> a.getEstado().equals(Asistencia.EstadoAsistencia.PRESENTE))
                .count();

        stats.put("totalAsistenciasHoy", totalHoy);
        stats.put("presentesHoy", presentesHoy);

        return stats;
    }
}