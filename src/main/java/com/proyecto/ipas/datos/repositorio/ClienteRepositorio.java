package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepositorio extends JpaRepository<ClienteEntidad, Long> {

    boolean existsByNumeroDocumento(String documento);

    ClienteEntidad findByNumeroDocumento(String documento);

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


    /**
     * Cantidad de clientes que ha gestionado en total
     */
    @Query("""
            SELECT COUNT(c)
            FROM ClienteEntidad c
            WHERE c.usuario.idUsuario = :idUsuario
        """)
    Long contarClientesPorUsuario(@Param("idUsuario") Long idUsuario);


    /**
     * Cantidad de gestiones que el usuario ha realizado en clientes y polizas en el mes
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM Auditorias
            WHERE usuario_id = :idUsuario
            AND accion = 'UPDATE'
            AND tabla_afectada IN ('Clientes','Polizas')
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    Long contarGestionesMes(@Param("idUsuario") Long idUsuario);

}
