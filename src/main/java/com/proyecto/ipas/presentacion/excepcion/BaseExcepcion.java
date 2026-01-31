package com.proyecto.ipas.presentacion.excepcion;

/**
 * Excepción base para todas las excepciones personalizadas del proyecto
 * Esto permite capturar todas nuestras excepciones de forma genérica si es necesario
 */
public class BaseExcepcion extends RuntimeException {

    private final String errorCodigo;
    private final Object[] argumentos;

    public BaseExcepcion(String mensaje, String errorCodigo) {
        super(mensaje);
        this.errorCodigo = errorCodigo;
        this.argumentos = new Object[0];
    }

    public BaseExcepcion(String messaje, String errorCodigo, Object... argumentos) {
        super(messaje);
        this.errorCodigo = errorCodigo;
        this.argumentos = argumentos;
    }

    public Object[] getArgumentos() {
        return argumentos;
    }

    public String getErrorCodigo() {
        return errorCodigo;
    }
}


