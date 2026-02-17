package com.proyecto.ipas.presentacion.excepcion;

public class ArchivoInvalidoExcepcion extends BaseExcepcion {

    public ArchivoInvalidoExcepcion(String mensaje, String errorCodigo) {
        super(mensaje, errorCodigo);
    }

    public ArchivoInvalidoExcepcion(String mensaje) {
        super(mensaje, "ARCHIVO_INVALIDO_ERROR");
    }
}
