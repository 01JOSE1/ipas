package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.negocio.dominio.enums.EstadoUsuario;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoUsuario;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.rol.RespuestaRolDTO;

public record RespuestaUsuarioDTO(
        Long idUsuario,

        String nombre,

        String apellido,

        TipoDocumentoUsuario tipoDocumento,

        String numeroDocumento,

        String telefono,

        String direccion,

        String correo,

        EstadoUsuario estado,

        RespuestaRolDTO rol
) {

    public RespuestaUsuarioDTO(UsuarioEntidad usuarioEntidad) {
        this(
                usuarioEntidad.getIdUsuario(),
                usuarioEntidad.getNombre(),
                usuarioEntidad.getApellido(),
                usuarioEntidad.getTipoDocumento(),
                usuarioEntidad.getNumeroDocumento(),
                usuarioEntidad.getTelefono(),
                usuarioEntidad.getDireccion(),
                usuarioEntidad.getCorreo(),
                usuarioEntidad.getEstado(),
                new RespuestaRolDTO(usuarioEntidad.getRol())
        );
    }
}
