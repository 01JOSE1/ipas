package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import org.mapstruct.*;

/**
 * Mapeador de Rol entre capas de persistencia y dominio.
 * 
 * Utiliza MapStruct para convertir entre:
 * - RolEntidad (capa de persistencia con JPA)
 * - Rol (modelo de dominio con lógica de negocio)
 * 
 * Configuración:
 * - Spring component: se registra como bean automáticamente
 * - IGNORE: no reporta errores por campos no mapeados
 * - NullValuePropertyMappingStrategy.IGNORE: no sobrescribe destinos nulos
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RolMapper {

    /**
     * Factory method para reconstruir Rol desde la entidad.
     * 
     * Utiliza el método factory del dominio para garantizar
     * que se establecen todos los invariantes correctamente.
     * 
     * @param entidad la entidad del rol con datos persistidos
     * @return Rol reconstruido con estado válido
     */
    @ObjectFactory
    default Rol reconstruirRol(RolEntidad entidad) {
        return Rol.reconstruir(entidad.getIdRole(), entidad.getNombreRol());
    }

    /**
     * Convierte RolEntidad a modelo de dominio Rol.
     * 
     * @param rolEntidad la entidad de persistencia
     * @return modelo Rol con lógica de negocio
     */
    Rol toRol(RolEntidad rolEntidad);

    /**
     * Convierte modelo de dominio Rol a RolEntidad para persistencia.
     * 
     * El campo 'descripción' se ignora porque no es manejado por el modelo.
     * 
     * @param rol el modelo de dominio
     * @return entidad lista para ser persistida
     */
    @Mapping(target = "descripcion", ignore = true)
    RolEntidad toRolEntidad(Rol rol);
}
