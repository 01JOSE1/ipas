package com.proyecto.ipas.presentacion.excepcion;


/**
 * Excepción cuando no se encuentra un recurso (404)
 * Ejemplo: Usuario no existe, Producto no encontrado
 */
public class RecursoNOEncontradoException extends BaseExcepcion {
    public RecursoNOEncontradoException(String recursoNombre, String campoNombre, Object campoValor) {
        super(
                String.format("%s no encontrado con %s: '%s'", recursoNombre, campoNombre, campoValor),
                "RECURSO_NO_ENCONTRADO",
                recursoNombre, campoNombre, campoValor
        );
    }

    public RecursoNOEncontradoException(String mensaje) {
        super(mensaje, "RECURSO_NO_ENCONTRADO");
    }
}
