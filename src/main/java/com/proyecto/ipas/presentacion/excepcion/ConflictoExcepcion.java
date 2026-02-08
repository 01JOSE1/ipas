package com.proyecto.ipas.presentacion.excepcion;


import java.util.List;

/**
 * Excepción de negocio utilizada para representar conflictos de datos
 * (por ejemplo, violaciones de unicidad, estados inválidos o reglas de dominio).
 *
 * Facilita la serialización JSON, permite representar 0..N errores
 * y es el estándar recomendado para APIs REST.
 *
 * Soporte para errores con y sin campo asociado
 * List.of() Una lista vacía indica un conflicto de negocio global,
 * mientras que una lista con elementos indica conflictos asociados a campos concretos.
 */
public class ConflictoExcepcion extends BaseExcepcion {

    private final List<ErrorCampo> campoErrorLista;

    public ConflictoExcepcion(String mensaje, List<ErrorCampo> campoErrorLista) {
        super(mensaje, "CONFLICTO");
        this.campoErrorLista = List.copyOf(campoErrorLista);
    }

    public List<ErrorCampo> getCampoErrorLista() {
        return campoErrorLista;
    }

    public static class ErrorCampo {
        private final String campo;
        private final String mensaje;

        public ErrorCampo(String campo, String mensaje) {
            this.campo = campo;
            this.mensaje = mensaje;
        }

        public String getCampo() {
            return campo;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
