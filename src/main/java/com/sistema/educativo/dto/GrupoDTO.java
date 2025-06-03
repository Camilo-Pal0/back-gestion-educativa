package com.sistema.educativo.dto;

import java.util.List;

public class GrupoDTO {
    private Long id;
    private String nombreGrupo;
    private String codigo;
    private String materia;
    private String facultad;
    private Integer semestre;
    private Integer anoEscolar;
    private String periodo;
    private String horario;
    private String aula;
    private Integer creditos;
    private Integer cupoMaximo;
    private Integer estudiantesInscritos;
    private Boolean activo;
    private List<UsuarioDTO> profesores;

    // Constructors
    public GrupoDTO() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }

    public Integer getAnoEscolar() {
        return anoEscolar;
    }

    public void setAnoEscolar(Integer anoEscolar) {
        this.anoEscolar = anoEscolar;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Integer getEstudiantesInscritos() {
        return estudiantesInscritos;
    }

    public void setEstudiantesInscritos(Integer estudiantesInscritos) {
        this.estudiantesInscritos = estudiantesInscritos;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<UsuarioDTO> getProfesores() {
        return profesores;
    }

    public void setProfesores(List<UsuarioDTO> profesores) {
        this.profesores = profesores;
    }
}