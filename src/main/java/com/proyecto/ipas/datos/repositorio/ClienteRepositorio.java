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
     * Clientes que tienen al menos una poliza activa
     */
    @Query("""
            SELECT COUNT(c) 
            FROM ClienteEntidad c 
            WHERE EXISTS (
                SELECT 1 
                FROM PolizaEntidad p 
                WHERE p.cliente.idCliente = c.idCliente 
                AND p.estado = 'ACTIVA'
            )
        """)
    Long contarClientesConPolizaActiva();


    /**
     * Clientes que no tienen ninguna poliza activa
     */
    @Query("""
            SELECT COUNT(c) 
            FROM ClienteEntidad c 
            WHERE NOT EXISTS (
                SELECT 1 
                FROM PolizaEntidad p 
                WHERE p.cliente.idCliente = c.idCliente 
                AND p.estado = 'ACTIVA'
            )
        """)
    Long contarClientesSinPolizasActivas();


    /**
     * Cantidad de clientes creados este mes
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM Auditorias
            WHERE accion = 'INSERT'
            AND tabla_afectada = 'Clientes'
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    Long contarClientesCreadosMes();


    /**
     * Top ciudades con más clientes registrados
     * Retorna: List<Object[]> donde [0]=ciudad (String), [1]=cantidad (Long)
     */
    @Query("""
        SELECT c.ciudad, COUNT(c)
        FROM ClienteEntidad c
        WHERE c.ciudad IS NOT NULL
        GROUP BY c.ciudad
        ORDER BY COUNT(c) DESC
    """)
    List<Object[]> contarClientesPorCiudad();

    /**
     * Distribución de clientes por estado civil
     * Retorna: List<Object[]> donde [0]=estadoCivil (String), [1]=cantidad (Long)
     */
    @Query("""
        SELECT c.estadoCivil, COUNT(c)
        FROM ClienteEntidad c
        WHERE c.estadoCivil IS NOT NULL
        GROUP BY c.estadoCivil
    """)
    List<Object[]> contarClientesPorEstadoCivil();

    /**
     * Distribución de clientes por tipo de documento
     * Retorna: List<Object[]> donde [0]=tipoDocumento (String), [1]=cantidad (Long)
     */
    @Query("""
        SELECT c.tipoDocumento, COUNT(c)
        FROM ClienteEntidad c
        GROUP BY c.tipoDocumento
    """)
    List<Object[]> contarClientesPorTipoDocumento();


}
