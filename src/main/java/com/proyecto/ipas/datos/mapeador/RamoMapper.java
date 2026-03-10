package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RamoEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Ramo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

/**
 * Mapeador de Ramo entre capas de persistencia y dominio.
 * 
 * Utiliza MapStruct para convertir entre:
 * - RamoEntidad (capa de persistencia con JPA)
 * - Ramo (modelo de dominio con lógica de negocio)
 * 
 * Los ramos son registros maestros (AUTO, VIDA, INCENDIO, etc.)
 * que definen tipos de pólizas disponibles en el sistema.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RamoMapper {

    /**
     * Convierte RamoEntidad a modelo de dominio Ramo.
     * 
     * @param ramoEntidad la entidad de persistencia
     * @return modelo Ramo con lógica de negocio
     */
    Ramo toRamo(RamoEntidad ramoEntidad);

    /**
     * Factory method para crear nuevo Ramo desde la entidad.
     * 
     * Utiliza el factory del dominio para registrar un ramo con
     * su nombre y comisión asociada.
     * 
     * @param ramoEntidad la entidad con datos persistidos
     * @return Ramo recién registrado
     */
    @ObjectFactory
    default Ramo crearRamo(RamoEntidad ramoEntidad) {
        return Ramo.registrar(ramoEntidad.getNombre(), ramoEntidad.getComision());
    }

}
