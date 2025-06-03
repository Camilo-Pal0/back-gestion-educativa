package com.sistema.educativo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_grupo", nullable = false)
    private String nombreGrupo;

    @Column(nullable = false)
    private String codigo; // Código del curso (ej: "MAT101", "FIS202")

    private String materia; // Nombre completo de la materia

    private String facultad; // Facultad o departamento

    private Integer semestre; // Semestre (1-10 típicamente)

    @Column(name = "ano_escolar", nullable = false)
    private Integer anoEscolar;

    private String periodo; // "2024-1", "2024-2", etc.

    private String horario; // "LUN-MIE 14:00-16:00"

    private String aula; // "Edificio A - Salón 301"

    private Integer creditos; // Número de créditos

    @Column(name = "cupo_maximo")
    private Integer cupoMaximo;

    private Boolean activo = true;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private Set<ProfesorGrupo> profesoresGrupos = new HashSet<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private Set<EstudianteGrupo> estudiantesGrupos = new HashSet<>();

    // Constructors
    public Grupo() {}

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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Set<ProfesorGrupo> getProfesoresGrupos() {
        return profesoresGrupos;
    }

    public void setProfesoresGrupos(Set<ProfesorGrupo> profesoresGrupos) {
        this.profesoresGrupos = profesoresGrupos;
    }

    public Set<EstudianteGrupo> getEstudiantesGrupos() {
        return estudiantesGrupos;
    }

    public void setEstudiantesGrupos(Set<EstudianteGrupo> estudiantesGrupos) {
        this.estudiantesGrupos = estudiantesGrupos;
    }
}