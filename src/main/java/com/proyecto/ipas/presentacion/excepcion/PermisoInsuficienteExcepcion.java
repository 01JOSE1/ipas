package com.proyecto.ipas.presentacion.excepcion;

/**
 * Excepcion de validacion de negocio cuando el usuario no cuenta con los permisos suficientes para realizar la accion
 */
public class PermisoInsuficienteExcepcion extends BaseExcepcion {
    public PermisoInsuficienteExcepcion(String mensaje, String errorCodigo) {
        super(mensaje, errorCodigo);
    }

    public PermisoInsuficienteExcepcion(String mensaje) {
        super(mensaje, "PROHIBIDO");
    }
}
