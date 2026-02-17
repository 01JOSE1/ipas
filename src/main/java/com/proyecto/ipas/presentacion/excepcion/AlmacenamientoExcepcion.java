package com.proyecto.ipas.presentacion.excepcion;

public class AlmacenamientoExcepcion extends BaseExcepcion {

    public AlmacenamientoExcepcion(String mensaje, String errorCodigo) {
        super(mensaje, errorCodigo);
    }

    public AlmacenamientoExcepcion(String mensaje) {
        super(mensaje, "ALMACENAMIENTO_ERROR");
    }
}
