package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import com.proyecto.ipas.negocio.dominio.modelo.Usuario;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.RespuestaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.UsuarioActualizarDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.VerDatosUsuarioPerfilDTO;
import org.mapstruct.*;

/**
 * Mapeador de Usuario entre capas de persistencia, dominio y presentación.
 * 
 * Utiliza MapStruct para convertir entre:
 * - UsuarioEntidad (capa de persistencia con JPA)
 * - Usuario (modelo de dominio con lógica de negocio)
 * - DTOs de presentación (transferencia de datos al frontend)
 * 
 * Configuración:
 * - Spring component: se registra como bean automáticamente
 * - IGNORE: no reporta errores por campos no mapeados
 * - NullValuePropertyMappingStrategy.IGNORE: no sobrescribe destinos nulos
 * - Usa RolMapper para mapeos relacionados
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {RolMapper.class}
)
public interface UsuarioMapper {

    /**
     * Convierte UsuarioEntidad a RespuestaDTO para retornar al frontend.
     * 
     * @param usuarioEntidad la entidad de usuario
     * @return DTO con datos públicos del usuario
     */
    RespuestaDTO toRespuestaDTO (UsuarioEntidad usuarioEntidad);

    /**
     * Convierte UsuarioEntidad a modelo de dominio Usuario.
     * 
     * @param usuarioEntidad la entidad de persistencia
     * @return modelo de dominio con lógica de negocio
     */
    Usuario toUsuario(UsuarioEntidad usuarioEntidad);

    /**
     * Convierte RolEntidad a modelo de dominio Rol.
     * 
     * @param rolEntidad la entidad del rol
     * @return modelo Rol con lógica de negocio
     */
    Rol toRol(RolEntidad rolEntidad);

    /**
     * Factory method para reconstruir Usuario desde la entidad.
     * 
     * Utiliza el método factory del dominio para garantizar
     * que se establecen todos los invariantes correctamente.
     * 
     * @param usuarioEntidad la entidad con datos persistidos
     * @return Usuario reconstruido con estado válido
     */
    @ObjectFactory
    default Usuario reconstruirUsuario(UsuarioEntidad usuarioEntidad) {

        Rol rol = toRol(usuarioEntidad.getRol());

        return Usuario.reconstruir(usuarioEntidad.getIdUsuario(), usuarioEntidad.getNombre(), usuarioEntidad.getApellido(), usuarioEntidad.getCorreo(), usuarioEntidad.getEstado(), rol);
    }

    /**
     * Convierte modelo de dominio Usuario a UsuarioEntidad para persistencia.
     * 
     * Mapea el campo 'id' del modelo al 'idUsuario' de la entidad,
     * y 'email' del modelo a 'correo' de la entidad.
     * 
     * @param usuario el modelo de dominio
     * @return entidad lista para ser persistida
     */
    @Mapping(target = "idUsuario", source = "id")
    @Mapping(target = "correo", source = "email")
    @Mapping(target = "rol", source = "rol")
    UsuarioEntidad toUsuarioEntidad(Usuario usuario);

    /**
     * Convierte UsuarioEntidad a DTO para mostrar datos en perfil del usuario.
     * 
     * @param usuarioEntidad la entidad con datos persistidos
     * @return DTO con datos para visualización en perfil
     */
    VerDatosUsuarioPerfilDTO toVerDatosUsuarioPerfilDTO(UsuarioEntidad usuarioEntidad);

    /**
     * Convierte VerDatosUsuarioPerfilDTO a UsuarioActualizarDTO.
     * 
     * Sirve como puente entre DTOs durante el flujo de actualización de perfil.
     * 
     * @param verDatosUsuarioPerfilDTO DTO con datos actuales
     * @return DTO con estructura para actualización
     */
    UsuarioActualizarDTO toUsuarioActualizarDTO(VerDatosUsuarioPerfilDTO verDatosUsuarioPerfilDTO);

    /**
     * Sincroniza cambios del modelo de dominio hacia la entidad persistida.
     * 
     * Utiliza @MappingTarget para modificar la entidad in-place.
     * NullValuePropertyMappingStrategy.IGNORE evita sobrescribir campos válidos
     * con nulos del modelo. Este patrón preserva el dirty checking de Hibernate
     * durante la transacción activa.
     * 
     * @param usuario el modelo de dominio con cambios
     * @param usuarioEntidad la entidad a actualizar (modificada in-place)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void sincronizar (Usuario usuario, @MappingTarget UsuarioEntidad usuarioEntidad);

}
