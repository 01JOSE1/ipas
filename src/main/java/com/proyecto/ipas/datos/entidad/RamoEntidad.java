package com.proyecto.ipas.datos.entidad;

import com.proyecto.ipas.negocio.dominio.enums.TipoRamo;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "ramos")
public class RamoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ramo")
    private Long idRamo;

    @Column(name = "nombre_ramo", nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private TipoRamo nombre;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal comision;

    public RamoEntidad() {
    }

    public RamoEntidad(Long idRamo, TipoRamo nombre, BigDecimal comision) {
        this.idRamo = idRamo;
        this.nombre = nombre;
        this.comision = comision;
    }

    public Long getIdRamo() {
        return idRamo;
    }

    public void setIdRamo(Long idRamo) {
        this.idRamo = idRamo;
    }

    public TipoRamo getNombre() {
        return nombre;
    }

    public void setNombre(TipoRamo nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RamoEntidad that = (RamoEntidad) o;

        if (idRamo == null || that.idRamo == null) return false;

        return Objects.equals(idRamo, that.idRamo);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RamoEntidad{" +
                "idRamo=" + idRamo +
                ", nombre='" + nombre + '\'' +
                ", comision=" + comision +
                '}';
    }
}
