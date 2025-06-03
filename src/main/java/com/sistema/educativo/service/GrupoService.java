package com.sistema.educativo.service;

import com.sistema.educativo.dto.CrearGrupoDTO;
import com.sistema.educativo.dto.GrupoDTO;
import com.sistema.educativo.dto.UsuarioDTO;
import com.sistema.educativo.entity.Grupo;
import com.sistema.educativo.entity.ProfesorGrupo;
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