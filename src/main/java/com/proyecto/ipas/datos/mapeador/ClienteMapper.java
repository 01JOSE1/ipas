package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Cliente;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.RespuestaClienteDTO;
import org.mapstruct.*;

/**
 * Mapeador de Cliente entre capas de persistencia, dominio y presentación.
 * 
 * Utiliza MapStruct para convertir entre:
 * - ClienteEntidad (capa de persistencia con JPA)
 * - Cliente (modelo de dominio con lógica de negocio)
 * - DTOs de presentación (transferencia de datos al frontend)
 * 
 * Nota: Varios campos no se mapean directamente entre Modelo <-> Entidad
 * debido a que son manejados por lógica de negocio separada.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClienteMapper {

    /**
     * Convierte modelo de dominio Cliente a ClienteEntidad para persistencia.
     * 
     * Mapea el campo 'id' del modelo al 'idCliente' de la entidad.
     * Los campos estadoCivil, teléfono, correo, dirección y ciudad se ignoran
     * porque son manejados por servicios específicos en el dominio.
     * 
     * @param cliente el modelo de dominio
     * @return entidad lista para ser persistida
     */
    @Mapping(source = "id", target = "idCliente")
    @Mapping(target = "estadoCivil", ignore = true)
    @Mapping(target = "telefono", ignore = true)
    @Mapping(target = "correo", ignore = true)
    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "ciudad", ignore = true)
    ClienteEntidad toClienteEntidad(Cliente cliente);

    /**
     * Convierte ClienteEntidad a modelo de dominio Cliente.
     * 
     * @param clienteEntidad la entidad de persistencia
     * @return modelo de dominio con lógica de negocio
     */
    Cliente toCliente(ClienteEntidad clienteEntidad);

    /**
     * Factory method para reconstruir Cliente desde la entidad.
     * 
     * Solo utiliza datos básicos del cliente (ID, nombre, apellido, documento, fecha nacimiento).
     * No incluye datos de contacto ni dirección que requieren validaciones adicionales.
     * 
     * @param clienteEntidad la entidad con datos persistidos
     * @return Cliente reconstruido con estado válido
     */
    @ObjectFactory
    default Cliente reconstruirCliente(ClienteEntidad clienteEntidad) {
        return Cliente.reconstruir(clienteEntidad.getIdCliente(), clienteEntidad.getNombre(), clienteEntidad.getApellido(), clienteEntidad.getTipoDocumento(), clienteEntidad.getNumeroDocumento(), clienteEntidad.getFechaNacimiento());
    }

    /**
     * Convierte GestionClienteDTO a modelo de dominio Cliente.
     * 
     * Utilizado cuando se reciben datos de actualizaciones desde el frontend.
     * 
     * @param gestionClienteDTO DTO con datos del cliente
     * @return modelo Cliente
     */
    Cliente toCliente(GestionClienteDTO gestionClienteDTO);

    /**
     * Factory method para crear nuevo Cliente desde GestionClienteDTO.
     * 
     * Utiliza el factory del dominio para registrar un cliente con los
     * datos básicos (nombre, apellido, documento, fecha nacimiento).
     * 
     * @param gestionClienteDTO DTO con datos de gestión
     * @return Cliente recién registrado
     */
    @ObjectFactory
    default Cliente crearCliente(GestionClienteDTO gestionClienteDTO) {
        return Cliente.registrarNuevo(gestionClienteDTO.getNombre(), gestionClienteDTO.getApellido(), gestionClienteDTO.getTipoDocumento(), gestionClienteDTO.getNumeroDocumento(), gestionClienteDTO.getFechaNacimiento());
    }

    /**
     * Convierte ClienteEntidad a RespuestaClienteDTO para retornar al frontend.
     * 
     * @param clienteEntidad la entidad con datos persistidos
     * @return DTO con datos públicos del cliente
     */
    RespuestaClienteDTO toRespuestaCliente(ClienteEntidad clienteEntidad);

    /**
     * Convierte ClienteEntidad a GestionClienteDTO para operaciones de gestión.
     * 
     * Incluye todos los datos necesarios para editar o visualizar un cliente.
     * 
     * @param clienteEntidad la entidad de persistencia
     * @return DTO para gestión de cliente
     */
    GestionClienteDTO toGestionCliente(ClienteEntidad clienteEntidad);

}
