package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.cliente;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.negocio.dominio.enums.EstadoCivilCliente;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoCliente;

import java.time.LocalDate;

public record RespuestaClienteDTO(

        Long idCliente,

        String nombre,

        String apellido,

        TipoDocumentoCliente tipoDocumento,

        String numeroDocumento,

        LocalDate fechaNacimiento,

        EstadoCivilCliente estadoCivil,

        String telefono,

        String correo,

        String dieccion,

        String ciudad

) {


    /**
     * Constructor de conveniencia utilizado para mapear una entidad ClienteEntidad
     * a un RespuestaClienteDTO.
     *
     * Este constructor es necesario para permitir el uso de referencias a métodos
     * como .map(RespuestaClienteDTO::new) al trabajar con streams o paginación,
     * facilitando la conversión directa de entidades JPA a DTOs sin exponer la entidad
     * al exterior.
     *
     * Su función principal es desacoplar la capa de persistencia de la capa de presentación,
     * garantizando que solo los datos necesarios del cliente sean retornados.
     */
    public RespuestaClienteDTO(ClienteEntidad entidad) {

        this(
                entidad.getIdCliente(),
                entidad.getNombre(),
                entidad.getApellido(),
                entidad.getTipoDocumento(),
                entidad.getNumeroDocumento(),
                entidad.getFechaNacimiento(),
                entidad.getEstadoCivil(),
                entidad.getTelefono(),
                entidad.getCorreo(),
                entidad.getDireccion(),
                entidad.getCiudad()
        );

    }

}
