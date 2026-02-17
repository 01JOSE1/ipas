package com.proyecto.ipas.datos.mapeador;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.negocio.dominio.modelo.Cliente;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.RespuestaClienteDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ClienteMapper {


    /**
     * Modelo -> Entidad
     */
    @Mapping(source = "id", target = "idCliente")
    @Mapping(target = "estadoCivil", ignore = true)
    @Mapping(target = "telefono", ignore = true)
    @Mapping(target = "correo", ignore = true)
    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "ciudad", ignore = true)
    ClienteEntidad toClienteEntidad(Cliente cliente);


    /**
     * Entidad -> Modelo de Dominio
     */
    Cliente toCliente(ClienteEntidad clienteEntidad);
    @ObjectFactory
    default Cliente reconstruirCliente(ClienteEntidad clienteEntidad) {
        return Cliente.reconstruir(clienteEntidad.getIdCliente(), clienteEntidad.getNombre(), clienteEntidad.getApellido(), clienteEntidad.getTipoDocumento(), clienteEntidad.getNumeroDocumento(), clienteEntidad.getFechaNacimiento());
    }


    /**
     * GestionClienteDTO -> Modelo
     */
    Cliente toCliente(GestionClienteDTO gestionClienteDTO);
    @ObjectFactory
    default Cliente crearCliente(GestionClienteDTO gestionClienteDTO) {
        return Cliente.registrarNuevo(gestionClienteDTO.getNombre(), gestionClienteDTO.getApellido(), gestionClienteDTO.getTipoDocumento(), gestionClienteDTO.getNumeroDocumento(), gestionClienteDTO.getFechaNacimiento());
    }


    /**
     * Entidad -> RespuestaClienteDTO
     */
    RespuestaClienteDTO toRespuestaCliente(ClienteEntidad clienteEntidad);


    /**
     * Entidad -> GestionClienteDTO
     */
    GestionClienteDTO toGestionCliente(ClienteEntidad clienteEntidad);

}
