package com.proyecto.ipas.datos.repositorio;


import com.proyecto.ipas.datos.entidad.PolizaEntidad;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO;
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

}
