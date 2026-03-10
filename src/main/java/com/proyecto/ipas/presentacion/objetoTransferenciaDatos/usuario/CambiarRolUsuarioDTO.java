package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario;

import lombok.Data;

@Data
public class CambiarRolUsuarioDTO {

    private Long idUsuarioAccion;
    private Long idUsuarioCambio;
    private Long idRolCambio;
}
