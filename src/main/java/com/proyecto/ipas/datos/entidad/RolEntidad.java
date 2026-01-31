package com.proyecto.ipas.datos.entidad;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class RolEntidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long idRole;

    @Column(name = "nombre_rol", nullable = false, length = 50, unique = true)
    private String nombreRol;

    private String descripcion;

    public RolEntidad() {
    }

    public RolEntidad(Long idRole, String nombreRol, String descripcion) {
        this.idRole = idRole;
        this.nombreRol = nombreRol;
        this.descripcion = descripcion;
    }

    public Long getIdRole() {
        return idRole;
    }

    public void setIdRole(Long idRole) {
        this.idRole = idRole;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RolEntidad that = (RolEntidad) o;
        if (idRole == null || that.idRole == null) return false;

        return Objects.equals(idRole, that.idRole);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RolEntidad{" +
                "idRole=" + idRole +
                ", nombreRol='" + nombreRol + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
