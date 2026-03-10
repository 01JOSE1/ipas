package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.aseguradora;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;

public record ListarParaSelectAseguradoraDTO(
        Long idAseguradora,

        String nombre
) {
    public ListarParaSelectAseguradoraDTO(AseguradoraEntidad entidad) {

        this(
                entidad.getIdAseguradora(),
                entidad.getNombre()
        );

    }
}
