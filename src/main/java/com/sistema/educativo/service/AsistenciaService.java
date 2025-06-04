package com.sistema.educativo.service;

import com.sistema.educativo.dto.AsistenciaDTO;
import com.sistema.educativo.dto.TomarAsistenciaDTO;
import com.sistema.educativo.entity.Asistencia;
import com.sistema.educativo.entity.Grupo;
import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.AsistenciaRepository;
import com.sistema.educativo.repository.GrupoRepository;
import com.sistema.educativo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Tomar asistencia para un grupo
    public void tomarAsistencia(TomarAsistenciaDTO dto) {
        // Obtener el usuario actual (profesor)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario profesor = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el grupo existe
        Grupo grupo = grupoRepository.findById(dto.getGrupoId())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Verificar que el profesor está asignado al grupo
        boolean esProfesorDelGrupo = grupo.getProfesoresGrupos().stream()
                .anyMatch(pg -> pg.getProfesor().getId().equals(profesor.getId()) && pg.getActivo());

        if (!profesor.getTipoUsuario().equals(Usuario.TipoUsuario.ADMIN) && !esProfesorDelGrupo) {
            throw new RuntimeException("No tienes permiso para tomar asistencia en este grupo");
        }

        // Procesar cada registro de asistencia
        for (TomarAsistenciaDTO.RegistroAsistencia registro : dto.getAsistencias()) {
            Usuario estudiante = usuarioRepository.findById(registro.getEstudianteId())
                    .orElseThrow(() -> new RuntimeException("Estudiante no encontrado: " + registro.getEstudianteId()));

            // Buscar si ya existe una asistencia para este estudiante, grupo y fecha
            Asistencia asistencia = asistenciaRepository
                    .findByEstudianteIdAndGrupoIdAndFecha(
                            registro.getEstudianteId(),
                            dto.getGrupoId(),
                            dto.getFecha()
                    )
                    .orElse(new Asistencia());

            // Actualizar o crear la asistencia
            asistencia.setEstudiante(estudiante);
            asistencia.setGrupo(grupo);
            asistencia.setFecha(dto.getFecha());
            asistencia.setEstado(Asistencia.EstadoAsistencia.valueOf(registro.getEstado()));
            asistencia.setObservaciones(registro.getObservaciones());
            asistencia.setRegistradoPor(profesor);

            asistenciaRepository.save(asistencia);
        }
    }

    // Obtener asistencias de un grupo en una fecha
    public List<AsistenciaDTO> obtenerAsistenciasPorGrupoYFecha(Long grupoId, LocalDate fecha) {
        return asistenciaRepository.findByGrupoIdAndFecha(grupoId, fecha).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener lista de estudiantes de un grupo para tomar asistencia
    public List<AsistenciaDTO> obtenerListaEstudiantesParaAsistencia(Long grupoId, LocalDate fecha) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<AsistenciaDTO> lista = new ArrayList<>();

        // Obtener todos los estudiantes del grupo
        grupo.getEstudiantesGrupos().stream()
                .filter(eg -> eg.getActivo())
                .forEach(eg -> {
                    AsistenciaDTO dto = new AsistenciaDTO();
                    dto.setEstudianteId(eg.getEstudiante().getId());
                    dto.setEstudianteNombre(eg.getEstudiante().getNombreUsuario());
                    dto.setEstudianteEmail(eg.getEstudiante().getEmail());
                    dto.setGrupoId(grupoId);
                    dto.setGrupoCodigo(grupo.getCodigo());
                    dto.setGrupoMateria(grupo.getMateria());
                    dto.setFecha(fecha);

                    // Buscar si ya existe una asistencia
                    asistenciaRepository.findByEstudianteIdAndGrupoIdAndFecha(
                            eg.getEstudiante().getId(), grupoId, fecha
                    ).ifPresent(asistencia -> {
                        dto.setId(asistencia.getId());
                        dto.setEstado(asistencia.getEstado().toString());
                        dto.setObservaciones(asistencia.getObservaciones());
                    });

                    // Si no hay asistencia, poner estado por defecto
                    if (dto.getEstado() == null) {
                        dto.setEstado("PRESENTE");
                    }

                    lista.add(dto);
                });

        return lista;
    }

    // Obtener historial de asistencias de un grupo
    public List<AsistenciaDTO> obtenerHistorialPorGrupo(Long grupoId) {
        return asistenciaRepository.findByGrupoId(grupoId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener estadísticas de asistencia
    public EstadisticasAsistenciaDTO obtenerEstadisticas(Long grupoId) {
        EstadisticasAsistenciaDTO estadisticas = new EstadisticasAsistenciaDTO();

        estadisticas.setTotalPresentes(
                asistenciaRepository.countByGrupoAndEstado(grupoId, Asistencia.EstadoAsistencia.PRESENTE)
        );
        estadisticas.setTotalAusentes(
                asistenciaRepository.countByGrupoAndEstado(grupoId, Asistencia.EstadoAsistencia.AUSENTE)
        );
        estadisticas.setTotalTardanzas(
                asistenciaRepository.countByGrupoAndEstado(grupoId, Asistencia.EstadoAsistencia.TARDANZA)
        );
        estadisticas.setTotalJustificados(
                asistenciaRepository.countByGrupoAndEstado(grupoId, Asistencia.EstadoAsistencia.JUSTIFICADO)
        );

        return estadisticas;
    }

    // Verificar si ya se tomó asistencia
    public boolean yaSeTomoAsistencia(Long grupoId, LocalDate fecha) {
        return asistenciaRepository.existsAsistenciaByGrupoAndFecha(grupoId, fecha);
    }

    // Método auxiliar para convertir entidad a DTO
    private AsistenciaDTO convertirADTO(Asistencia asistencia) {
        AsistenciaDTO dto = new AsistenciaDTO();
        dto.setId(asistencia.getId());
        dto.setEstudianteId(asistencia.getEstudiante().getId());
        dto.setEstudianteNombre(asistencia.getEstudiante().getNombreUsuario());
        dto.setEstudianteEmail(asistencia.getEstudiante().getEmail());
        dto.setGrupoId(asistencia.getGrupo().getId());
        dto.setGrupoCodigo(asistencia.getGrupo().getCodigo());
        dto.setGrupoMateria(asistencia.getGrupo().getMateria());
        dto.setFecha(asistencia.getFecha());
        dto.setEstado(asistencia.getEstado().toString());
        dto.setObservaciones(asistencia.getObservaciones());
        return dto;
    }

    // DTO para estadísticas
    public static class EstadisticasAsistenciaDTO {
        private Long totalPresentes;
        private Long totalAusentes;
        private Long totalTardanzas;
        private Long totalJustificados;

        // Getters y Setters
        public Long getTotalPresentes() {
            return totalPresentes != null ? totalPresentes : 0L;
        }

        public void setTotalPresentes(Long totalPresentes) {
            this.totalPresentes = totalPresentes;
        }

        public Long getTotalAusentes() {
            return totalAusentes != null ? totalAusentes : 0L;
        }

        public void setTotalAusentes(Long totalAusentes) {
            this.totalAusentes = totalAusentes;
        }

        public Long getTotalTardanzas() {
            return totalTardanzas != null ? totalTardanzas : 0L;
        }

        public void setTotalTardanzas(Long totalTardanzas) {
            this.totalTardanzas = totalTardanzas;
        }

        public Long getTotalJustificados() {
            return totalJustificados != null ? totalJustificados : 0L;
        }

        public void setTotalJustificados(Long totalJustificados) {
            this.totalJustificados = totalJustificados;
        }
    }
}