package com.proyecto.ipas.negocio.dominio.modelo;

import com.proyecto.ipas.negocio.dominio.enums.TipoRamo;

import java.math.BigDecimal;
import java.util.Objects;

public class Ramo {
    private Long id;
    private TipoRamo nombre;
    private BigDecimal comision;

    public Ramo(TipoRamo nombre, BigDecimal comision) {
        this.nombre = nombre;
        this.comision = comision;
    }

    public static Ramo registrar (TipoRamo nombre, BigDecimal comision) {
        validarNombre(nombre);
        validarComision(comision);
        return new Ramo(nombre, comision);
    }

    public static Ramo reconstruir (Long id, TipoRamo nombre, BigDecimal comision) {
        validarIdRamo(id);
        validarNombre(nombre);
        validarComision(comision);
        Ramo ramo = new Ramo(nombre, comision);
        ramo.id = id;
        return ramo;
    }

    private static void validarIdRamo(Long idRamo) {
        if (idRamo == null || idRamo <= 0) {
            throw new IllegalArgumentException("El ID del ramo es obligatorio");
        }
    }

    private static void validarNombre(TipoRamo nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre del ramo es obligatorio");
        }
    }

    private static void validarComision(BigDecimal comision) {
        if (comision == null) {
            throw new IllegalArgumentException("El comision del ramo es obligatorio");
        }

        if (comision.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El comision del ramo debe ser mayor o igual a 0");
        }
    }

    public boolean esRamoAutomovil () {
        return TipoRamo.AUTOMOVIL.equals(nombre);
    }

    public Long getId() {
        return id;
    }

    public TipoRamo getNombre() {
        return nombre;
    }

    public BigDecimal getComision() {
        return comision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ramo ramo = (Ramo) o;
        return Objects.equals(id, ramo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ramo{" +
                "id=" + id +
                ", nombre=" + nombre +
                ", comision=" + comision +
                '}';
    }
}
