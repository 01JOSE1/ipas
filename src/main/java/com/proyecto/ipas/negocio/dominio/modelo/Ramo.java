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

    /**
     * Factory method que registra un nuevo ramo de seguro en el sistema.
     * 
     * Se valida que el nombre del ramo sea válido y que la comisión sea un valor positivo.
     * 
     * @param nombre el tipo de ramo {@link TipoRamo} (AUTOMOVIL, VIDA, INCENDIO, etc.)
     * @param comision el porcentaje de comisión asociado al ramo (debe ser mayor que 0)
     * @return una nueva instancia de Ramo con ID null (a ser asignado por base de datos)
     * @throws IllegalArgumentException si el nombre es nulo o la comisión es inválida
     */
    public static Ramo registrar (TipoRamo nombre, BigDecimal comision) {
        validarNombre(nombre);
        validarComision(comision);
        return new Ramo(nombre, comision);
    }

    /**
     * Factory method que reconstruye un ramo desde los datos persistidos en base de datos.
     * 
     * Este método es utilizado por la capa de infraestructura para instanciar objetos Ramo
     * desde registros existentes. Valida que el ID sea válido y que nombre y comisión cumplan las restricciones.
     * 
     * @param id el identificador único del ramo en base de datos
     * @param nombre el tipo de ramo {@link TipoRamo}
     * @param comision el porcentaje de comisión del ramo
     * @return una instancia de Ramo reconstruida con los parámetros proporcionados
     * @throws IllegalArgumentException si el ID es nulo/no positivo, nombre es nulo, o comisión es inválida
     */
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

    /**
     * Consulta que verifica si el ramo es de tipo AUTOMOVIL.
     * 
     * Los ramos de tipo AUTOMOVIL requieren información adicional como la placa del vehículo
     * en las pólizas asociadas.
     * 
     * @return {@code true} si el nombre del ramo es AUTOMOVIL, {@code false} en caso contrario
     */
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
