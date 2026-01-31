package com.proyecto.ipas.presentacion.excepcion;

import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.mensajeFrontend.AlertaRespuesta;

import java.util.List;

public class ConflictoExcepcion extends BaseExcepcion {


    public ConflictoExcepcion(String message) {
        super(message, "CONFLICTO");
    }
}
