package com.proyecto.ipas.presentacion.excepcion;


import java.util.List;

public class ValidacionDatosExcepcion extends BaseExcepcion {

    private List<ErrorCampo> campoErrorLista;


    public ValidacionDatosExcepcion(String message,  List<ErrorCampo> campoErrorLista) {
        super(message, "DATOS_INVALIDOS");
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

