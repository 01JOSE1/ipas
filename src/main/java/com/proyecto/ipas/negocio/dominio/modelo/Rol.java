package com.proyecto.ipas.negocio.dominio.modelo;

import jakarta.persistence.Column;

import java.util.Objects;

public class Rol {

    /**
     * Atributos necearios para el modelo
     */
    private final Long idRole;
    private final String nombreRol;


//    CONSTRUCTOR PRIVADO
    private Rol(Long idRole, String nombreRol) {
        this.idRole = idRole;
        this.nombreRol = nombreRol;
    }


    //    FACTORY METHODS
    public static Rol crearNuevoRol(String nombreRol) {
        validarNombreRol(nombreRol);
        return new Rol(null, nombreRol.trim().toUpperCase());
    }

    public static Rol reconstruir(Long idRole, String nombreRol) {
        validarIdRole(idRole);
        validarNombreRol(nombreRol);
        return new Rol(idRole, nombreRol);
    }


    //    METODOS DE NEGOCIO
    public boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(this.nombreRol);
    }

    public boolean esAsesor() {
        return "ASESOR".equalsIgnoreCase(this.nombreRol);
    }


//    VALIDACIONES PRIVADAS

    private static void validarIdRole(Long idRole) {
        if (idRole == null || idRole <= 0) {
            throw new IllegalArgumentException("ID de rol debe ser positivo");
        }
    }

    private static void validarNombreRol(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        nombreRol = nombreRol.trim();

        if (nombreRol.length() < 2) {
            throw new IllegalArgumentException(
                    "Los nombres deben tener al menos 3 caracteres"
            );
        }

        if (nombreRol.length() > 15) {
            throw new IllegalArgumentException(
                    "El nombre del rol no pueden exceder 15 caracteres"
            );
        }

        if (!nombreRol.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException(
                    "Los nombres solo pueden contener letras y espacios"
            );
        }

    }


//    GETTERS - SOLO LECTURA
    public String getNombreRol() {
        return nombreRol;
    }

    public Long getIdRole() {
        return idRole;
    }


//    EQUALS, HASHCODE, TOSTRING

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return Objects.equals(idRole, rol.idRole);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idRole);
    }

    @Override
    public String toString() {
        return "Rol{" +
                "idRole=" + idRole +
                ", nombreRol='" + nombreRol + '\'' +
                '}';
    }
}
