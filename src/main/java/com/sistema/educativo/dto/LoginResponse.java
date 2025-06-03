package com.sistema.educativo.dto;

public class LoginResponse {

    private String token;
    private String tipo;
    private String nombreUsuario;
    private String email;
    private String tipoUsuario;
    private Long id;

    // Constructor
    public LoginResponse(String token, String nombreUsuario, String email, String tipoUsuario, Long id) {
        this.token = token;
        this.tipo = "Bearer";
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.id = id;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}