package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Aseguradora;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AseguradoraMapper {

    /**
     * Entidad -> Modelo
     */
    Aseguradora toAseguradora(AseguradoraEntidad aseguradoraEntidad);
    @ObjectFactory
    default Aseguradora crearAseguradora(AseguradoraEntidad aseguradoraEntidad) {
        return Aseguradora.registrar(aseguradoraEntidad.getNombre(), aseguradoraEntidad.getNumeroDocumento(), aseguradoraEntidad.getTelefono(), aseguradoraEntidad.getClave());
    }
}
