package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario;


import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoUsuario;

public record VerDatosUsuarioPerfilDTO(

        String nombre,

        String apellido,

        TipoDocumentoUsuario tipoDocumento,

        String numeroDocumento,

        String telefono,

        String direccion,

        String correo

) {
}
