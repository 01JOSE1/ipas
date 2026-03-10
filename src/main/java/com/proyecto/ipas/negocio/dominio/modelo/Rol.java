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
    /**
     * Factory method que crea un nuevo rol en el sistema.
     * 
     * El nombre del rol se valida y normaliza a mayúsculas. Este método es utilizado
     * cuando se registra un nuevo rol desde la capa de aplicación.
     * 
     * @param nombreRol el nombre del rol (2-15 caracteres, solo letras y espacios)
     * @return una nueva instancia de Rol con ID null (a ser asignado por base de datos)
     * @throws IllegalArgumentException si el nombre del rol incumple las restricciones de validación
     */
    public static Rol crearNuevoRol(String nombreRol) {
        validarNombreRol(nombreRol);
        return new Rol(null, nombreRol.trim().toUpperCase());
    }

    /**
     * Factory method que reconstruye un rol desde los datos persistidos en base de datos.
     * 
     * Este método es utilizado por la capa de infraestructura para instanciar objetos Rol
     * desde registros existentes. Valida que el ID sea válido y que el nombre cumpla las restricciones.
     * 
     * @param idRole el identificador único del rol en base de datos
     * @param nombreRol el nombre del rol
     * @return una instancia de Rol reconstruida con los parámetros proporcionados
     * @throws IllegalArgumentException si el ID es nulo o no positivo, o si el nombre es inválido
     */
    public static Rol reconstruir(Long idRole, String nombreRol) {
        validarIdRole(idRole);
        validarNombreRol(nombreRol);
        return new Rol(idRole, nombreRol);
    }


    //    METODOS DE NEGOCIO
    /**
     * Consulta que verifica si el rol es ADMINISTRADOR.
     * 
     * @return {@code true} si el nombre del rol es "ADMINISTRADOR", {@code false} en caso contrario
     */
    public boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(this.nombreRol);
    }

    /**
     * Consulta que verifica si el rol es ASESOR.
     * 
     * @return {@code true} si el nombre del rol es "ASESOR", {@code false} en caso contrario
     */
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
