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

@Repository
public interface PolizaRepositorio extends JpaRepository<PolizaEntidad, Long> {

    boolean existsByCodigoPoliza(String codigoPoliza);

    List<PolizaEntidad> findAllByCliente_IdCliente(Long idCliente);

    boolean existsByCodigoPolizaAndIdPolizaNot(String codigoPoliza, Long idPoliza);

    /**
     * Con el guion bajo nos permite entrar al objeto
     * Con el guion bajo evitamos ambiguedades.
     */
//    boolean existsByPlacaAndCliente_IdClienteNotAndEstado(String placa, Long idCliente, EstadoPoliza estadoVigente);


    /**
     * Verifica si existe una póliza ACTIVA y vigente asociada a la misma placa pero perteneciente a un cliente diferente.
     *
     * Esta validación se utiliza para evitar que una misma placa esté asegurada simultáneamente por más de un cliente dentro del sistema.
     *
     * Se considera vigente cuando:
     * - El estado administrativo es ACTIVA
     * - La fecha de vencimiento es mayor o igual a la fecha actual
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


    boolean existsByNumeroPdf(String numeroPdf);

    /**
     * TRIM(CONCAT(c.nombre, ' ', COALESCE(c.apellido, ''))): Esta función une el nombre con el apellido (sustituyendo el valor nulo por un texto vacío
     * para evitar que toda la mezcla se anule) y elimina los espacios sobrantes al principio o al final si alguno de los datos falta.
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
     * Polizas proximas a vencer.
     * polizas que le queden 8 o menos dias para vencer
     */
    @Query("""
            SELECT COUNT(p) 
            FROM PolizaEntidad p 
            WHERE p.fechaFin BETWEEN CURRENT_DATE AND :fechaLimite
        """)
    long countPolizasPorVencer(@Param("fechaLimite") LocalDate fechaLimite);


    /**
     * Cantidad de polizas que ha creado en el ultimo mes
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
     * Cantidad de polizas Activas
     */
    @Query("""
            SELECT COUNT(p)
            FROM PolizaEntidad p
            WHERE p.estado = 'ACTIVA'
        """)
    Long contarPolizasActivas();


    /**
     * Cantidad de polizas vencidas en este mes hasta el dia de hoy
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
     * Cantidad de polizas canceladas en este mes hasta el dia de hoy
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM Auditorias
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
