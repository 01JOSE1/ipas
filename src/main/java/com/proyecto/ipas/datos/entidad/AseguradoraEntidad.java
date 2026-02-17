package com.proyecto.ipas.datos.entidad;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "aseguradoras")
public class AseguradoraEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aseguradora")
    private Long idAseguradora;

    @Column(name = "nombre_aseguradora", nullable = false, length = 40)
    private String nombre;

    @Column(name = "numero_documento", nullable = false, length = 15)
    private String numeroDocumento;

    @Column(length = 100)
    private String direccion;

    @Column(length = 40)
    private String ciudad;

    @Column(length = 15, nullable = false, unique = true)
    private String telefono;

    @Column(length = 30, nullable = false, unique = true)
    private String clave;

    public AseguradoraEntidad() {
    }

    public AseguradoraEntidad(Long idAseguradora, String nombre, String numeroDocumento, String direccion, String ciudad, String telefono, String clave) {
        this.idAseguradora = idAseguradora;
        this.nombre = nombre;
        this.numeroDocumento = numeroDocumento;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.clave = clave;
    }

    public Long getIdAseguradora() {
        return idAseguradora;
    }

    public void setIdAseguradora(Long idAseguradora) {
        this.idAseguradora = idAseguradora;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AseguradoraEntidad that = (AseguradoraEntidad) o;
        if (idAseguradora == null || that.idAseguradora == null) return false;
        return Objects.equals(idAseguradora, that.idAseguradora);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AseguradoraEntidad{" +
                "idAseguradora=" + idAseguradora +
                ", nombre='" + nombre + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
