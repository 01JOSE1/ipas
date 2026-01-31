package com.proyecto.ipas.presentacion.excepcion;

/**
 * Excepción para validaciones de negocio (400)
 * Ejemplo: Email ya existe, Stock insuficiente, Datos inválidos
 */
public class NegocioExcepcion extends BaseExcepcion {

    public NegocioExcepcion(String mensaje, String errorCodigo) {
        super(mensaje, errorCodigo);
    }

    public NegocioExcepcion(String mensaje) {
        super(mensaje, "NEGOCIO_ERROR");
    }
}
