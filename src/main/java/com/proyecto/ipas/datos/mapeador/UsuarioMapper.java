package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import com.proyecto.ipas.negocio.dominio.modelo.Usuario;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.RespuestaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.UsuarioActualizarDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.VerDatosUsuarioPerfilDTO;
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
    UsuarioEntidad toUsuarioEntidad(Usuario usuario);


    /**
     * Entidad -> VerDatosUsuarioPerfilDTO
     */
    VerDatosUsuarioPerfilDTO toVerDatosUsuarioPerfilDTO(UsuarioEntidad usuarioEntidad);


    /**
     * verDatosUsuarioPerfil -> usuarioActualizarDTO
     */
    UsuarioActualizarDTO toUsuarioActualizarDTO(VerDatosUsuarioPerfilDTO verDatosUsuarioPerfilDTO);


    /**
     * Sincroniza el estado del objeto de dominio hacia la entidad de persistencia
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void sincronizar (Usuario usuario, @MappingTarget UsuarioEntidad usuarioEntidad);

}
