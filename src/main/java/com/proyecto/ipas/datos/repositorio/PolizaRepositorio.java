package com.proyecto.ipas.datos.repositorio;


import com.proyecto.ipas.datos.entidad.PolizaEntidad;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor.AsesorRankingDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad Póliza.
 * 
 * Gestiona operaciones CRUD y queries complejas para pólizas de seguros.
 * Incluye validaciones de unicidad, búsquedas relacionadas con clientes, 
 * cálculos de vigencia, y estadísticas para dashboards administrativos.
 */
@Repository
public interface PolizaRepositorio extends JpaRepository<PolizaEntidad, Long> {

    /**
     * Verifica si existe una póliza registrada con el código especificado.
     * 
     * @param codigoPoliza el código único de la póliza
     * @return true si existe una póliza con ese código, false en caso contrario
     */
    boolean existsByCodigoPoliza(String codigoPoliza);

    /**
     * Obtiene todas las pólizas asociadas a un cliente específico.
     * 
     * @param idCliente el ID del cliente
     * @return lista de pólizas del cliente (puede estar vacía)
     */
    List<PolizaEntidad> findAllByCliente_IdCliente(Long idCliente);

    /**
     * Verifica si existe otra póliza con el código especificado (excluyendo una específica).
     * 
     * Utilizado para validar unicidad durante actualizaciones de pólizas existentes.
     * 
     * @param codigoPoliza el código a validar
     * @param idPoliza el ID de la póliza actual a excluir
     * @return true si existe otra póliza con ese código, false en caso contrario
     */
    boolean existsByCodigoPolizaAndIdPolizaNot(String codigoPoliza, Long idPoliza);

    /**
     * Verifica si existe una póliza activa y vigente para una placa en otro cliente.
     * 
     * Utilizado para validar que una misma placa vehicular no esté asegurada 
     * simultáneamente bajo múltiples clientes. Una póliza se considera vigente cuando:
     * - Estado administrativo es ACTIVA
     * - Fecha de vencimiento es mayor o igual a hoy
     * 
     * @param placa la placa del vehículo
     * @param clienteId el ID del cliente actual a excluir
     * @param estado el estado a buscar (típicamente ACTIVA)
     * @return true si existe otra póliza activa para esa placa, false en caso contrario
     */
    @Query("""
    SELECT COUNT(p) > 0
    FROM PolizaEntidad p
    WHERE p.placa = :placa
    AND p.cliente.idCliente <> :clienteId
    AND p.estado = :estado
    AND p.fechaFin >= CURRENT_DATE
    """)
    boolean existePlacaActivaEnOtroCliente(@Param("placa") String placa, @Param("clienteId") Long clienteId, @Param("estado") EstadoPoliza estado);

    /**
     * Verifica si existe una póliza registrada con el número de PDF especificado.
     * 
     * @param numeroPdf el nombre del archivo PDF con patrón UUID_codigo.pdf
     * @return true si existe una póliza con ese PDF, false en caso contrario
     */
    boolean existsByNumeroPdf(String numeroPdf);

    /**
     * Busca una póliza específica junto con datos relacionados de cliente, ramo y aseguradora.
     * 
     * Retorna un DTO con toda la información necesaria para gestionar la póliza,
     * incluyendo datos del cliente concatenados (nombre + apellido).
     * 
     * @param idPoliza el ID de la póliza
     * @return Optional con el DTO de gestión si la póliza existe, Optional vacío si no
     */
    @Query("""
        SELECT new com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO(
            p.idPoliza,
            p.codigoPoliza,
            p.fechaInicio,
            p.fechaFin,
            p.primaNeta,
            p.primaTotal,
            p.estado,
            p.estadoPago,
            p.placa,
            p.descripcion,
            c.idCliente,
            TRIM(CONCAT(c.nombre, ' ', COALESCE(c.apellido, ''))),
            r.idRamo,
            a.idAseguradora,
            p.numeroPdf
        )
        FROM PolizaEntidad p
        JOIN p.cliente c
        JOIN p.ramo r
        JOIN p.aseguradora a
        WHERE p.idPoliza = :idPoliza
    """)
    Optional<GestionPolizaDTO> buscarDatosPoliza(@Param("idPoliza") Long idPoliza);


    /**
     * Calcula la cantidad de pólizas próximas a vencer en un rango de fechas.
     * 
     * Utilizado para mostrar alertas de renovación. Una póliza se considera próxima a vencer
     * cuando su fecha de vencimiento está entre hoy y la fecha límite especificada.
     * 
     * @param fechaLimite la fecha máxima del rango (típicamente hoy + 8 días)
     * @return la cantidad de pólizas en riesgo de vencimiento
     */
    @Query("""
            SELECT COUNT(p) 
            FROM PolizaEntidad p 
            WHERE p.fechaFin BETWEEN CURRENT_DATE AND :fechaLimite
        """)
    long countPolizasPorVencer(@Param("fechaLimite") LocalDate fechaLimite);

    /**
     * Calcula la cantidad de pólizas creadas por un asesor en el mes actual.
     * 
     * Utilizado para mostrar productividad de asesores en dashboards.
     * 
     * @param idUsuario el ID del asesor
     * @return la cantidad de pólizas creadas desde el 1 de este mes
     */
    @Query("""
            SELECT COUNT(p)
            FROM PolizaEntidad p
            WHERE p.usuario.idUsuario = :idUsuario
            AND MONTH(p.fechaInicio) = MONTH(CURRENT_DATE)
            AND YEAR(p.fechaInicio) = YEAR(CURRENT_DATE)
        """)
    Long contarPolizasEsteMes(@Param("idUsuario") Long idUsuario);

    /**
     * Calcula la cantidad total de pólizas activas en el sistema.
     * 
     * Utilizado para KPIs del dashboard administrativo.
     * 
     * @return la cantidad de pólizas con estado ACTIVA
     */
    @Query("""
            SELECT COUNT(p)
            FROM PolizaEntidad p
            WHERE p.estado = 'ACTIVA'
        """)
    Long contarPolizasActivas();

    /**
     * Calcula la cantidad de pólizas vencidas durante el mes actual hasta hoy.
     * 
     * Utilizado para monitorear pólizas que han expirado en el período actual.
     * 
     * @return la cantidad de pólizas cuya fecha de vencimiento ya pasó este mes
     */
    @Query("""
            SELECT COUNT(p)
            FROM PolizaEntidad p
            WHERE p.fechaFin <= CURRENT_DATE
            AND MONTH(p.fechaFin) = MONTH(CURRENT_DATE)
            AND YEAR(p.fechaFin) = YEAR(CURRENT_DATE)
        """)
    Long contarPolizasVencidasMes();

    /**
     * Calcula la cantidad de pólizas que han sido canceladas en el mes actual.
     * 
     * Se detecta mediante auditoría de cambios de estado a CANCELADA.
     * Utilizado para métricas de churn o terminación de contratos.
     * 
     * @return la cantidad de pólizas canceladas desde el 1 de este mes
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM auditorias
            WHERE accion = 'UPDATE'
            AND tabla_afectada = 'Polizas'
            AND fecha_accion <= CURRENT_DATE
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
            AND detalles LIKE '%Estado: VENCIDA -> CANCELADA;%'
        """, nativeQuery = true)
    Long contarPolizasCanceladasMes();


    /**
     * pólizas activas por el nombre del usuario (asesor)
     */
    @Query("""
            SELECT new com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor.AsesorRankingDTO(
                p.usuario.nombre, COUNT(p), 0.0)
            FROM PolizaEntidad p
            WHERE p.estado = 'ACTIVA'
            GROUP BY p.usuario.id, p.usuario.nombre
            ORDER BY COUNT(p) DESC
        """)
    List<AsesorRankingDTO> encontrarTopAsesores(Pageable pageable);


    /**
     * Distribución de pólizas por estado (ACTIVA, VENCIDA, CANCELADA, etc.)
     * Retorna: List<Object[]> donde [0]=estado (String), [1]=cantidad (Long)
     */
    @Query("""
        SELECT p.estado, COUNT(p)
        FROM PolizaEntidad p
        GROUP BY p.estado
    """)
    List<Object[]> contarPolizasPorEstado();

    /**
     * Top 5 ramos con más pólizas registradas
     * Retorna: List<Object[]> donde [0]=nombreRamo (String), [1]=cantidad (Long)
     */
    @Query("""
        SELECT r.nombre, COUNT(p)
        FROM PolizaEntidad p
        JOIN p.ramo r
        GROUP BY r.nombre
        ORDER BY COUNT(p) DESC
    """)
    List<Object[]> contarPolizasPorRamo();

    /**
     * Top 5 aseguradoras con más pólizas registradas
     * Retorna: List<Object[]> donde [0]=nombreAseguradora (String), [1]=cantidad (Long)
     */
    @Query("""
        SELECT a.nombre, COUNT(p)
        FROM PolizaEntidad p
        JOIN p.aseguradora a
        GROUP BY a.nombre
        ORDER BY COUNT(p) DESC
    """)
    List<Object[]> contarPolizasPorAseguradora();
}
