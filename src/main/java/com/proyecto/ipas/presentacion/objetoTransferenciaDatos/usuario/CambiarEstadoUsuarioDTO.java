package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario;

import com.proyecto.ipas.negocio.dominio.enums.EstadoUsuario;
import lombok.Data;

@Data
public class CambiarEstadoUsuarioDTO {

    private Long idUsuarioAccion;
    private Long idUsuarioCambio;
    private EstadoUsuario estadoCambio;
}
