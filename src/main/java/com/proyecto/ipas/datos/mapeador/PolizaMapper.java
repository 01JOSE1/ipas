package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;
import com.proyecto.ipas.datos.entidad.PolizaEntidad;
import com.proyecto.ipas.datos.entidad.RamoEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Aseguradora;
import com.proyecto.ipas.negocio.dominio.modelo.Poliza;
import com.proyecto.ipas.negocio.dominio.modelo.Ramo;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.RespuestaPolizaDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {RamoMapper.class, AseguradoraMapper.class}
)
public interface PolizaMapper {

    /**
     * GestionPolizaDTO -> modelo
     */
    Poliza toPoliza (GestionPolizaDTO gestionPolizaDTO, RamoEntidad ramoEntidad, AseguradoraEntidad aseguradoraEntidad);
    Ramo toRamo(RamoEntidad ramoEntidad);
    Aseguradora toAseguradora(AseguradoraEntidad aseguradoraEntidad);
    @ObjectFactory
    default Poliza crearPoliza(GestionPolizaDTO gestionPolizaDTO, RamoEntidad ramoEntidad, AseguradoraEntidad aseguradoraEntidad) {

        Ramo ramo = toRamo(ramoEntidad);
        Aseguradora aseguradora = toAseguradora(aseguradoraEntidad);

        return Poliza.registrar(gestionPolizaDTO.getCodigoPoliza(), gestionPolizaDTO.getFechaInicio(), gestionPolizaDTO.getFechaFin(), gestionPolizaDTO.getPrimaNeta(), gestionPolizaDTO.getPrimaTotal(), gestionPolizaDTO.getEstado(), gestionPolizaDTO.getEstadoPago(), gestionPolizaDTO.getPlaca(), ramo, aseguradora);
    }


    /**
     * Modelo -> Entidad
     */
    @Mapping(target = "idPoliza", source = "id")
    @Mapping(target = "numeroPdf", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "ramo", ignore = true)
    @Mapping(target = "aseguradora", ignore = true)
    PolizaEntidad toPolizaEntidad(Poliza poliza);

    /**
     * Entidad -> Modelo
     */
    Poliza toPoliza (PolizaEntidad polizaEntidad);
    @ObjectFactory
    default Poliza reconstruirPoliza(PolizaEntidad polizaEntidad) {
        Ramo ramo = toRamo(polizaEntidad.getRamo());
        Aseguradora aseguradora = toAseguradora(polizaEntidad.getAseguradora());

        return Poliza.reconstruir(polizaEntidad.getIdPoliza(), polizaEntidad.getCodigoPoliza(), polizaEntidad.getFechaInicio(), polizaEntidad.getFechaFin(), polizaEntidad.getPrimaNeta(), polizaEntidad.getPrimaTotal(), polizaEntidad.getEstado(), polizaEntidad.getEstadoPago(), polizaEntidad.getPlaca(), ramo, aseguradora);
    }


    /**
     * Entidad -> RespuestaPolizaDTO
     */
    RespuestaPolizaDTO toRespuestaPoliza(PolizaEntidad polizaEntidad);


}
