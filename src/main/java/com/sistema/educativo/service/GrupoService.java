package com.sistema.educativo.service;

import com.sistema.educativo.dto.CrearGrupoDTO;
import com.sistema.educativo.dto.GrupoDTO;
import com.sistema.educativo.dto.UsuarioDTO;
import com.sistema.educativo.entity.Grupo;
import com.sistema.educativo.entity.ProfesorGrupo;
import com.sistema.educativo.entity.EstudianteGrupo;
import com.sistema.educativo.entity.Usuario;
import com.sistema.educativo.repository.GrupoRepository;
import com.sistema.educativo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los grupos
    public List<GrupoDTO> obtenerTodos() {
        return grupoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener grupos activos
    public List<GrupoDTO> obtenerActivos() {
        return grupoRepository.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Obtener grupo por ID
    public GrupoDTO obtenerPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return convertirADTO(grupo);
    }

    // Obtener grupos por profesor
    public List<GrupoDTO> obtenerPorProfesor(Long profesorId) {
        return grupoRepository.findByProfesorId(profesorId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Crear nuevo grupo
    public GrupoDTO crear(CrearGrupoDTO dto) {
        // Verificar si ya existe el código
        if (grupoRepository.existsByCodigo(dto.getCodigo())) {
            throw new RuntimeException("Ya existe un grupo con el código: " + dto.getCodigo());
        }

        Grupo grupo = new Grupo();
        grupo.setNombreGrupo(dto.getNombreGrupo());
        grupo.setCodigo(dto.getCodigo());
        grupo.setMateria(dto.getMateria());
        grupo.setFacultad(dto.getFacultad());
        grupo.setSemestre(dto.getSemestre());
        grupo.setAnoEscolar(dto.getAnoEscolar());
        grupo.setPeriodo(dto.getPeriodo());
        grupo.setHorario(dto.getHorario());
        grupo.setAula(dto.getAula());
        grupo.setCreditos(dto.getCreditos());
        grupo.setCupoMaximo(dto.getCupoMaximo());
        grupo.setActivo(true);

        Grupo grupoGuardado = grupoRepository.save(grupo);

        // Asignar profesores si se proporcionaron
        if (dto.getProfesoresIds() != null && !dto.getProfesoresIds().isEmpty()) {
            for (Long profesorId : dto.getProfesoresIds()) {
                Usuario profesor = usuarioRepository.findById(profesorId)
                        .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + profesorId));

                if (!profesor.getTipoUsuario().equals(Usuario.TipoUsuario.PROFESOR)) {
                    throw new RuntimeException("El usuario " + profesorId + " no es un profesor");
                }

                ProfesorGrupo pg = new ProfesorGrupo(profesor, grupoGuardado);
                grupoGuardado.getProfesoresGrupos().add(pg);
            }
            grupoGuardado = grupoRepository.save(grupoGuardado);
        }

        return convertirADTO(grupoGuardado);
    }

    // Actualizar grupo
    public GrupoDTO actualizar(Long id, GrupoDTO dto) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Verificar si el nuevo código ya existe (si es diferente al actual)
        if (!grupo.getCodigo().equals(dto.getCodigo()) && grupoRepository.existsByCodigo(dto.getCodigo())) {
            throw new RuntimeException("Ya existe un grupo con el código: " + dto.getCodigo());
        }

        grupo.setNombreGrupo(dto.getNombreGrupo());
        grupo.setCodigo(dto.getCodigo());
        grupo.setMateria(dto.getMateria());
        grupo.setFacultad(dto.getFacultad());
        grupo.setSemestre(dto.getSemestre());
        grupo.setPeriodo(dto.getPeriodo());
        grupo.setHorario(dto.getHorario());
        grupo.setAula(dto.getAula());
        grupo.setCreditos(dto.getCreditos());
        grupo.setCupoMaximo(dto.getCupoMaximo());

        Grupo grupoActualizado = grupoRepository.save(grupo);
        return convertirADTO(grupoActualizado);
    }

    // Cambiar estado activo/inactivo
    public GrupoDTO cambiarEstado(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        grupo.setActivo(!grupo.getActivo());
        Grupo grupoActualizado = grupoRepository.save(grupo);
        return convertirADTO(grupoActualizado);
    }

    // Asignar profesor a grupo
    public void asignarProfesor(Long grupoId, Long profesorId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Usuario profesor = usuarioRepository.findById(profesorId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        if (!profesor.getTipoUsuario().equals(Usuario.TipoUsuario.PROFESOR)) {
            throw new RuntimeException("El usuario no es un profesor");
        }

        // Verificar si ya está asignado
        boolean yaAsignado = grupo.getProfesoresGrupos().stream()
                .anyMatch(pg -> pg.getProfesor().getId().equals(profesorId) && pg.getActivo());

        if (yaAsignado) {
            throw new RuntimeException("El profesor ya está asignado a este grupo");
        }

        ProfesorGrupo pg = new ProfesorGrupo(profesor, grupo);
        grupo.getProfesoresGrupos().add(pg);
        grupoRepository.save(grupo);
    }

    // Inscribir estudiante en grupo
    public void inscribirEstudiante(Long grupoId, Long estudianteId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        if (!estudiante.getTipoUsuario().equals(Usuario.TipoUsuario.ESTUDIANTE)) {
            throw new RuntimeException("El usuario no es un estudiante");
        }

        // Verificar cupo
        Integer inscritos = grupoRepository.countEstudiantesActivos(grupoId);
        if (inscritos >= grupo.getCupoMaximo()) {
            throw new RuntimeException("El grupo ha alcanzado su cupo máximo");
        }

        // Verificar si ya está inscrito
        boolean yaInscrito = grupo.getEstudiantesGrupos().stream()
                .anyMatch(eg -> eg.getEstudiante().getId().equals(estudianteId) && eg.getActivo());

        if (yaInscrito) {
            throw new RuntimeException("El estudiante ya está inscrito en este grupo");
        }

        EstudianteGrupo eg = new EstudianteGrupo(estudiante, grupo);
        grupo.getEstudiantesGrupos().add(eg);
        grupoRepository.save(grupo);
    }

    // Desinscribir estudiante de grupo
    public void desinscribirEstudiante(Long grupoId, Long estudianteId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        EstudianteGrupo estudianteGrupo = grupo.getEstudiantesGrupos().stream()
                .filter(eg -> eg.getEstudiante().getId().equals(estudianteId) && eg.getActivo())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El estudiante no está inscrito en este grupo"));

        estudianteGrupo.setActivo(false);
        grupoRepository.save(grupo);
    }

    // Obtener estudiantes de un grupo
    public List<UsuarioDTO> obtenerEstudiantesDelGrupo(Long grupoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        return grupo.getEstudiantesGrupos().stream()
                .filter(EstudianteGrupo::getActivo)
                .map(eg -> {
                    Usuario estudiante = eg.getEstudiante();
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setId(estudiante.getId());
                    dto.setNombreUsuario(estudiante.getNombreUsuario());
                    dto.setEmail(estudiante.getEmail());
                    dto.setTelefonoMovil(estudiante.getTelefonoMovil());
                    dto.setActivo(estudiante.getActivo());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Obtener estudiantes disponibles para inscribir
    public List<UsuarioDTO> obtenerEstudiantesDisponibles(Long grupoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // IDs de estudiantes ya inscritos
        List<Long> inscritosIds = grupo.getEstudiantesGrupos().stream()
                .filter(EstudianteGrupo::getActivo)
                .map(eg -> eg.getEstudiante().getId())
                .collect(Collectors.toList());

        // Obtener todos los estudiantes activos que no están inscritos
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getTipoUsuario().equals(Usuario.TipoUsuario.ESTUDIANTE))
                .filter(Usuario::getActivo)
                .filter(u -> !inscritosIds.contains(u.getId()))
                .map(estudiante -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setId(estudiante.getId());
                    dto.setNombreUsuario(estudiante.getNombreUsuario());
                    dto.setEmail(estudiante.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Método auxiliar para convertir entidad a DTO
    private GrupoDTO convertirADTO(Grupo grupo) {
        GrupoDTO dto = new GrupoDTO();
        dto.setId(grupo.getId());
        dto.setNombreGrupo(grupo.getNombreGrupo());
        dto.setCodigo(grupo.getCodigo());
        dto.setMateria(grupo.getMateria());
        dto.setFacultad(grupo.getFacultad());
        dto.setSemestre(grupo.getSemestre());
        dto.setAnoEscolar(grupo.getAnoEscolar());
        dto.setPeriodo(grupo.getPeriodo());
        dto.setHorario(grupo.getHorario());
        dto.setAula(grupo.getAula());
        dto.setCreditos(grupo.getCreditos());
        dto.setCupoMaximo(grupo.getCupoMaximo());
        dto.setActivo(grupo.getActivo());

        // Contar estudiantes inscritos
        Integer inscritos = grupoRepository.countEstudiantesActivos(grupo.getId());
        dto.setEstudiantesInscritos(inscritos != null ? inscritos : 0);

        // Agregar profesores
        List<UsuarioDTO> profesores = new ArrayList<>();
        for (ProfesorGrupo pg : grupo.getProfesoresGrupos()) {
            if (pg.getActivo()) {
                UsuarioDTO profesorDTO = new UsuarioDTO();
                profesorDTO.setId(pg.getProfesor().getId());
                profesorDTO.setNombreUsuario(pg.getProfesor().getNombreUsuario());
                profesorDTO.setEmail(pg.getProfesor().getEmail());
                profesorDTO.setEspecialidad(pg.getProfesor().getEspecialidad());
                profesores.add(profesorDTO);
            }
        }
        dto.setProfesores(profesores);

        return dto;
    }
}