package com.sistema.educativo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.List;

public class CrearGrupoDTO {

    @NotBlank(message = "El nombre del grupo es requerido")
    private String nombreGrupo;

    @NotBlank(message = "El código del curso es requerido")
    private String codigo;

    @NotBlank(message = "El nombre de la materia es requerido")
    private String materia;

    private String facultad;

    @NotNull(message = "El semestre es requerido")
    @Min(value = 1, message = "El semestre debe ser mayor a 0")
    private Integer semestre;

    @NotNull(message = "El año escolar es requerido")
    private Integer anoEscolar;

    @NotBlank(message = "El periodo es requerido")
    private String periodo;

    private String horario;
    private String aula;

    @Min(value = 1, message = "Los créditos deben ser mayor a 0")
    private Integer creditos;

    @Min(value = 1, message = "El cupo debe ser mayor a 0")
    private Integer cupoMaximo;

    private List<Long> profesoresIds;

    // Constructors
    public CrearGrupoDTO() {}

    // Getters y Setters
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

    public List<Long> getProfesoresIds() {
        return profesoresIds;
    }

    public void setProfesoresIds(List<Long> profesoresIds) {
        this.profesoresIds = profesoresIds;
    }
}
