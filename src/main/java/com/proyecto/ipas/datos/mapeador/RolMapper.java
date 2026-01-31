package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring", // Integración con Spring
        unmappedTargetPolicy = ReportingPolicy.IGNORE, // Ignora campos no mapeados
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // No sobrescribe con nulls
)
public interface RolMapper {

    /**
     * Entidad -> Modelo
     */
    @ObjectFactory
    default Rol reconstruirRol(RolEntidad entidad) {
        return Rol.reconstruir(entidad.getIdRole(), entidad.getNombreRol());
    }
    Rol toRol(RolEntidad rolEntidad);

    /**
     * Modelo -> Entidad
     */
    @Mapping(target = "descripcion", ignore = true)
    RolEntidad toRolEntidad(Rol rol);
}
