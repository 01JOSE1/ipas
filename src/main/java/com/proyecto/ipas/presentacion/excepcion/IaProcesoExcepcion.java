package com.proyecto.ipas.presentacion.excepcion;

public class IaProcesoExcepcion extends BaseExcepcion {

    public IaProcesoExcepcion(String mensaje, String errorCodigo) {
        super(mensaje, errorCodigo);
    }

    public IaProcesoExcepcion(String mensaje, String errorCodigo, Throwable cause) {
        super(mensaje, errorCodigo, cause);
    }
}
