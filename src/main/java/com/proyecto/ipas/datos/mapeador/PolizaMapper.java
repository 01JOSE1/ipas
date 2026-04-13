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

/**
 * Mapeador de Póliza entre capas de persistencia, dominio y presentación.
 * 
 * Utiliza MapStruct para convertir entre:
 * - PolizaEntidad (capa de persistencia con JPA)
 * - Poliza (modelo de dominio con lógica de negocio)
 * - DTOs de presentación (transferencia de datos al frontend)
 * 
 * Nota: Varios campos no se mapean directamente debido a que son manejados
 * por lógica específica en el dominio (cliente, usuario, ramo, aseguradora).
 * Utiliza RamoMapper y AseguradoraMapper para mapeos relacionados.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {RamoMapper.class, AseguradoraMapper.class}
)
public interface PolizaMapper {

    /**
     * Convierte GestionPolizaDTO a modelo de dominio Poliza.
     * 
     * Utilizado cuando se reciben datos de creación/actualización desde el frontend.
     * Requiere las entidades relacionadas (ramo y aseguradora) para mapeo completo.
     * 
     * @param gestionPolizaDTO DTO con datos de la póliza
     * @param ramoEntidad la entidad del ramo asociado
     * @param aseguradoraEntidad la entidad de la aseguradora
     * @return modelo Poliza
     */
    Poliza toPoliza (GestionPolizaDTO gestionPolizaDTO, RamoEntidad ramoEntidad, AseguradoraEntidad aseguradoraEntidad);

    /**
     * Convierte RamoEntidad a modelo de dominio Ramo.
     * 
     * @param ramoEntidad la entidad del ramo
     * @return modelo Ramo
     */
    Ramo toRamo(RamoEntidad ramoEntidad);

    /**
     * Convierte AseguradoraEntidad a modelo de dominio Aseguradora.
     * 
     * @param aseguradoraEntidad la entidad de la aseguradora
     * @return modelo Aseguradora
     */
    Aseguradora toAseguradora(AseguradoraEntidad aseguradoraEntidad);

    /**
     * Factory method para crear nueva Póliza desde GestionPolizaDTO.
     * 
     * Utiliza el factory del dominio para registrar una póliza con
     * todos sus datos iniciales (código, fechas, primas, estado pago, placa, ramo, aseguradora).
     * 
     * @param gestionPolizaDTO DTO con datos de gestión
     * @param ramoEntidad la entidad del ramo
     * @param aseguradoraEntidad la entidad de la aseguradora
     * @return Póliza recién registrada con estado válido
     */
    @ObjectFactory
    default Poliza crearPoliza(GestionPolizaDTO gestionPolizaDTO, RamoEntidad ramoEntidad, AseguradoraEntidad aseguradoraEntidad) {
        Ramo ramo = toRamo(ramoEntidad);
        Aseguradora aseguradora = toAseguradora(aseguradoraEntidad);
        return Poliza.registrar(gestionPolizaDTO.getCodigoPoliza(), gestionPolizaDTO.getFechaInicio(), gestionPolizaDTO.getFechaFin(), gestionPolizaDTO.getPrimaNeta(), gestionPolizaDTO.getPrimaTotal(), gestionPolizaDTO.getEstadoPago(), gestionPolizaDTO.getPlaca(), gestionPolizaDTO.getDescripcion(), ramo, aseguradora);
    }

    /**
     * Convierte modelo de dominio Poliza a PolizaEntidad para persistencia.
     * 
     * Mapea el campo 'id' del modelo al 'idPoliza' de la entidad.
     * Los campos numeroPdf, cliente, usuario, ramo y aseguradora se ignoran
     * porque son manejados por servicios específicos en el dominio.
     * 
     * @param poliza el modelo de dominio
     * @return entidad lista para ser persistida
     */
    @Mapping(target = "idPoliza", source = "id")
    @Mapping(target = "numeroPdf", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "ramo", ignore = true)
    @Mapping(target = "aseguradora", ignore = true)
    PolizaEntidad toPolizaEntidad(Poliza poliza);

    /**
     * Convierte PolizaEntidad a modelo de dominio Poliza.
     * 
     * @param polizaEntidad la entidad de persistencia
     * @return modelo Poliza con lógica de negocio
     */
    Poliza toPoliza (PolizaEntidad polizaEntidad);

    /**
     * Factory method para reconstruir Póliza desde la entidad.
     * 
     * Utiliza el factory del dominio para garantizar que se establecen
     * todos los invariantes correctamente recuperando datos persistidos.
     * Incluye la reconstrucción de objetos relacionados (ramo, aseguradora).
     * 
     * @param polizaEntidad la entidad con datos persistidos
     * @return Póliza reconstruida con estado válido
     */
    @ObjectFactory
    default Poliza reconstruirPoliza(PolizaEntidad polizaEntidad) {
        Ramo ramo = toRamo(polizaEntidad.getRamo());
        Aseguradora aseguradora = toAseguradora(polizaEntidad.getAseguradora());
        return Poliza.reconstruir(polizaEntidad.getIdPoliza(), polizaEntidad.getCodigoPoliza(), polizaEntidad.getFechaInicio(), polizaEntidad.getFechaFin(), polizaEntidad.getPrimaNeta(), polizaEntidad.getPrimaTotal(), polizaEntidad.getEstado(), polizaEntidad.getEstadoPago(), polizaEntidad.getPlaca(), ramo, aseguradora);
    }

    /**
     * Convierte PolizaEntidad a RespuestaPolizaDTO para retornar al frontend.
     * 
     * @param polizaEntidad la entidad con datos persistidos
     * @return DTO con datos públicos de la póliza
     */
    RespuestaPolizaDTO toRespuestaPoliza(PolizaEntidad polizaEntidad);

    /**
     * Sincroniza cambios del modelo de dominio hacia la entidad persistida.
     * 
     * Utiliza @MappingTarget para modificar la entidad in-place.
     * NullValuePropertyMappingStrategy.IGNORE evita sobrescribir campos válidos
     * con nulos del modelo. Este patrón preserva el dirty checking de Hibernate
     * durante la transacción activa.
     * 
     * Datos complejos (cliente, usuario, ramo, aseguradora) no se sincronizan
     * ya que requieren lógica de negocio específica.
     * 
     * @param poliza el modelo de dominio con cambios
     * @param entidad la entidad a actualizar (modificada in-place)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void sincronizar(Poliza poliza, @MappingTarget PolizaEntidad entidad);
}
