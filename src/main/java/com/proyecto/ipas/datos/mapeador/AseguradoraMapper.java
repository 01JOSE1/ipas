package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Aseguradora;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

/**
 * Mapeador de Aseguradora entre capas de persistencia y dominio.
 * 
 * Utiliza MapStruct para convertir entre:
 * - AseguradoraEntidad (capa de persistencia con JPA)
 * - Aseguradora (modelo de dominio con lógica de negocio)
 * 
 * Las aseguradoras son entidades maestras que brindan las pólizas
 * de seguros en el sistema. Contienen información identificatoria y de contacto.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AseguradoraMapper {

    /**
     * Convierte AseguradoraEntidad a modelo de dominio Aseguradora.
     * 
     * @param aseguradoraEntidad la entidad de persistencia
     * @return modelo Aseguradora con lógica de negocio
     */
    Aseguradora toAseguradora(AseguradoraEntidad aseguradoraEntidad);

    /**
     * Factory method para crear nueva Aseguradora desde la entidad.
     * 
     * Utiliza el factory del dominio para registrar una aseguradora
     * con su nombre, número de documento, teléfono y clave de acceso.
     * 
     * @param aseguradoraEntidad la entidad con datos persistidos
     * @return Aseguradora recién registrada
     */
    @ObjectFactory
    default Aseguradora crearAseguradora(AseguradoraEntidad aseguradoraEntidad) {
        return Aseguradora.registrar(aseguradoraEntidad.getNombre(), aseguradoraEntidad.getNumeroDocumento(), aseguradoraEntidad.getTelefono(), aseguradoraEntidad.getClave());
    }
}
