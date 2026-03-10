package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.rol;

import com.proyecto.ipas.datos.entidad.PolizaEntidad;
import com.proyecto.ipas.datos.entidad.RolEntidad;

import java.time.LocalDate;

public record RespuestaRolDTO(
    Long idRol,

    String nombre
) {

    public RespuestaRolDTO(RolEntidad entidad) {
        this(
                entidad.getIdRole(),
                entidad.getNombreRol()
        );

    }
}
