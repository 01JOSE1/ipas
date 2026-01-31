package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import com.proyecto.ipas.negocio.dominio.modelo.Usuario;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.autenticacion.RegistroDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.autenticacion.RespuestaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.usuario.UsuarioActualizarDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.usuario.VerDatosUsuarioPerfilDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // Integración con Spring
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Ignora campos no mapeados
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, // No sobrescribe con nulls
        uses = {RolMapper.class} // Le dice a MapStruct que use RolMapper
)
public interface UsuarioMapper {

    // MAPEOS BASICOS

    /**
     * Entidad -> RespuestaDTO
     */
    RespuestaDTO toRespuestaDTO (UsuarioEntidad usuarioEntidad);



    /**
     * Entidad -> Modelo de Dominio
     */
    Usuario toUsuario(UsuarioEntidad usuarioEntidad);
    Rol toRol(RolEntidad rolEntidad);
    @ObjectFactory
    default Usuario reconstruirUsuario(UsuarioEntidad usuarioEntidad) {

        Rol rol = toRol(usuarioEntidad.getRol());

        return Usuario.reconstruir(usuarioEntidad.getIdUsuario(), usuarioEntidad.getNombre(), usuarioEntidad.getApellido(), usuarioEntidad.getCorreo(), usuarioEntidad.getEstado(), rol);
    }



    /**
     * Modelo de Dominio -> Entidad
     */
    @Mapping(target = "idUsuario", source = "id")
    @Mapping(target = "correo", source = "email")
    @Mapping(target = "rol", source = "rol")
    @Mapping(target = "clave", ignore = true)
    UsuarioEntidad toUsuarioEntidad(Usuario usuario);


    /**
     * Entidad -> VerDatosUsuarioPerfilDTO
     */
    VerDatosUsuarioPerfilDTO toVerDatosUsuarioPerfilDTO(UsuarioEntidad usuarioEntidad);


    /**
     * verDatosUsuarioPerfil -> usuarioActualizarDTO
     */
    UsuarioActualizarDTO toUsuarioActualizarDTO(VerDatosUsuarioPerfilDTO verDatosUsuarioPerfilDTO);

}
