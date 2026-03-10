package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Cliente.
 * 
 * Gestiona todas las operaciones CRUD e queries personalizadas para clientes del sistema.
 * Incluye métodos para validación de unicidad, búsqueda avanzada, y cálculo de estadísticas
 * para dashboards del administrador y asesores.
 */
public interface ClienteRepositorio extends JpaRepository<ClienteEntidad, Long> {

    /**
     * Verifica si existe un cliente registrado con el número de documento especificado.
     * 
     * @param documento el número de documento a validar
     * @return true si existe un cliente con ese documento, false en caso contrario
     */
    boolean existsByNumeroDocumento(String documento);

    /**
     * Busca un cliente por su número de documento.
     * 
     * @param documento el número de documento
     * @return la entidad del cliente, null si no existe
     */
    ClienteEntidad findByNumeroDocumento(String documento);

    /**
     * Verifica si existe un cliente registrado con el correo especificado.
     * 
     * @param correo el correo electrónico a validar
     * @return true si existe un cliente con ese correo, false en caso contrario
     */
    boolean existsByCorreo(String correo);

    /**
     * Verifica si existe un cliente registrado con el teléfono especificado.
     * 
     * @param telefono el teléfono a validar
     * @return true si existe un cliente con ese teléfono, false en caso contrario
     */
    boolean existsByTelefono(String telefono);

    /**
     * Verifica si existe otro cliente con el número de documento especificado (excluyendo uno específico).
     * 
     * Utilizado para validar unicidad durante actualizaciones de perfil del cliente.
     * 
     * @param documento el número de documento a validar
     * @param idCliente el ID del cliente actual a excluir
     * @return true si existe otro cliente con ese documento, false en caso contrario
     */
    boolean existsByNumeroDocumentoAndIdClienteNot(String documento, Long idCliente);

    /**
     * Verifica si existe otro cliente con el correo especificado (excluyendo uno específico).
     * 
     * @param correo el correo a validar
     * @param idCliente el ID del cliente actual a excluir
     * @return true si existe otro cliente con ese correo, false en caso contrario
     */
    boolean existsByCorreoAndIdClienteNot(String correo, Long idCliente);

    /**
     * Verifica si existe otro cliente con el teléfono especificado (excluyendo uno específico).
     * 
     * @param telefono el teléfono a validar
     * @param idCliente el ID del cliente actual a excluir
     * @return true si existe otro cliente con ese teléfono, false en caso contrario
     */
    boolean existsByTelefonoAndIdClienteNot(String telefono, Long idCliente);

    /**
     * Busca clientes por término de búsqueda (nombre, apellido o número de documento).
     * 
     * La búsqueda es case-insensitive para nombre y apellido, y soporta búsqueda parcial.
     * El número de documento busca coincidencias exactas.
     * 
     * @param termino la cadena a buscar (nombre, apellido o documento)
     * @return lista de DTOs con datos básicos de clientes que coinciden
     */
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
     * Calcula la cantidad total de clientes gestionados por un usuario (asesor).
     * 
     * Utilizado para mostrar en el perfil del asesor cuántos clientes ha asignados o reclutado.
     * 
     * @param idUsuario el ID del asesor
     * @return la cantidad de clientes bajo responsabilidad del asesor
     */
    @Query("""
            SELECT COUNT(c)
            FROM ClienteEntidad c
            WHERE c.usuario.idUsuario = :idUsuario
        """)
    Long contarClientesPorUsuario(@Param("idUsuario") Long idUsuario);

    /**
     * Calcula la cantidad de clientes que tienen al menos una póliza activa.
     * 
     * Utilizado en estadísticas del dashboard para visualizar cobertura de seguros.
     * Una póliza se considera activa cuando su estado es 'ACTIVA'.
     * 
     * @return la cantidad de clientes con pólizas activas
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
     * Calcula la cantidad de clientes sin pólizas activas.
     * 
     * Utilizado en dashboards para identificar oportunidades de venta.
     * Son clientes registrados en el sistema pero sin cobertura de seguros activos.
     * 
     * @return la cantidad de clientes sin pólizas activas
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
     * Calcula la cantidad de nuevos clientes registrados en el mes actual.
     * 
     * Utilizado en estadísticas mensuales para mostrar crecimiento de la base de clientes.
     * Se cuenta basándose en registros de auditoría con acción INSERT.
     * 
     * @return la cantidad de clientes creados desde el 1 de este mes
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
     * Obtiene el top de ciudades ordenadas por cantidad de clientes registrados.
     * 
     * Utilizado para análisis geográfico de la cartera de clientes.
     * Retorna una lista de Object[] donde [0] es la ciudad (String) y [1] es la cantidad (Long).
     * 
     * @return lista de Object[] con ciudades y cantidades ordenadas descendentemente
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
     * Obtiene la distribución de clientes según su estado civil.
     * 
     * Utilizado para análisis demográfico de la base de clientes.
     * Retorna una lista de Object[] donde [0] es el estado civil (String) y [1] es la cantidad (Long).
     * 
     * @return lista de Object[] con estados civiles y cantidades
     */
    @Query("""
        SELECT c.estadoCivil, COUNT(c)
        FROM ClienteEntidad c
        WHERE c.estadoCivil IS NOT NULL
        GROUP BY c.estadoCivil
    """)
    List<Object[]> contarClientesPorEstadoCivil();

    /**
     * Obtiene la distribución de clientes según el tipo de documento utilizado.
     * 
     * Utilizado para entender qué tipos de identificación prevalecen en la cartera.
     * Retorna una lista de Object[] donde [0] es el tipo (Cédula, Pasaporte, etc.)
     * y [1] es la cantidad (Long).
     * 
     * @return lista de Object[] con tipos de documento y cantidades
     */
    @Query("""
        SELECT c.tipoDocumento, COUNT(c)
        FROM ClienteEntidad c
        GROUP BY c.tipoDocumento
    """)
    List<Object[]> contarClientesPorTipoDocumento();


}
