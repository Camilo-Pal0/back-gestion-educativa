package com.sistema.educativo.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class TomarAsistenciaDTO {

    @NotNull(message = "El grupo es requerido")
    private Long grupoId;

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @NotNull(message = "Las asistencias son requeridas")
    private List<RegistroAsistencia> asistencias;

    // Clase interna para cada registro
    public static class RegistroAsistencia {
        @NotNull
        private Long estudianteId;

        @NotNull
        private String estado;

        private String observaciones;

        // Getters y Setters
        public Long getEstudianteId() {
            return estudianteId;
        }

        public void setEstudianteId(Long estudianteId) {
            this.estudianteId = estudianteId;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }

    // Getters y Setters
    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<RegistroAsistencia> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<RegistroAsistencia> asistencias) {
        this.asistencias = asistencias;
    }
}