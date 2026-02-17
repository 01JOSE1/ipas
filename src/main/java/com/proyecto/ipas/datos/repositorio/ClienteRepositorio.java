package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepositorio extends JpaRepository<ClienteEntidad, Long> {

    boolean existsByNumeroDocumento(String documento);

    boolean existsByCorreo(String correo);

    boolean existsByTelefono(String telefono);

    boolean existsByNumeroDocumentoAndIdClienteNot(String documento, Long idCliente);

    boolean existsByCorreoAndIdClienteNot(String correo, Long idCliente);

    boolean existsByTelefonoAndIdClienteNot(String telefono, Long idCliente);


    @Query("""
        SELECT new com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO(
            c.idCliente,
            CONCAT(c.nombre, ' ', c.apellido),
            c.numeroDocumento
        )
        FROM ClienteEntidad c
        WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
           OR LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))
           OR c.numeroDocumento LIKE CONCAT('%', :termino, '%')
    """)
    List<BusquedaClienteDTO> buscarClientes(@Param("termino") String termino);
}
