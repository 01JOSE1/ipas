package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ramo;

import com.proyecto.ipas.datos.entidad.RamoEntidad;
import com.proyecto.ipas.negocio.dominio.enums.TipoRamo;

public record ListarParaSelectRamoDTO(
        Long idRamo,
        TipoRamo nombre
) {
    public ListarParaSelectRamoDTO(RamoEntidad entidad) {

        this(
                entidad.getIdRamo(),
                entidad.getNombre()
        );

    }
}
