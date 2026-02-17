package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.RamoEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Ramo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RamoMapper {

    /**
     * Entidad -> Modelo
     */
    Ramo toRamo(RamoEntidad ramoEntidad);
    @ObjectFactory
    default Ramo crearRamo(RamoEntidad ramoEntidad) {
        return Ramo.registrar(ramoEntidad.getNombre(), ramoEntidad.getComision());
    }


}
