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
    boolean existsByPlacaAndCliente_IdClienteNotAndEstado(String placa, Long idCliente, EstadoPoliza estadoVigente);

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

}
