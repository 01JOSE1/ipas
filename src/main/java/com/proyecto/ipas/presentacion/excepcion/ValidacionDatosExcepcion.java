package com.proyecto.ipas.presentacion.excepcion;



import java.util.ArrayList;
import java.util.List;

public class ValidacionDatosExcepcion extends BaseExcepcion {

    private List<ErrorCampo> campoErrorLista = new ArrayList<>();


    public ValidacionDatosExcepcion(String message) {
        super(message, "DATOS_INVALIDOS");
    }

    public List<ErrorCampo> getCampoErrorLista() {
        return campoErrorLista;
    }

    public void agregarCampoError(String campo, String mensaje){
        campoErrorLista.add(new ValidacionDatosExcepcion.ErrorCampo(campo, mensaje));
    }

    public static class ErrorCampo {
        private final String campo;
        private final String mensaje;

        public ErrorCampo(String campo, String mensaje) {
            this.campo = campo;
            this.mensaje = mensaje;
        }
    }
}

