package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza;

import com.proyecto.ipas.datos.entidad.PolizaEntidad;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPagoPoliza;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RespuestaPolizaDTO(
        Long idPoliza,

        String codigoPoliza,

        LocalDate fechaInicio,

        LocalDate fechaFin,

        BigDecimal primaNeta,

        BigDecimal primaTotal,

        EstadoPoliza estado,

        EstadoPagoPoliza estadoPago,

        String placa,

        String descripcion,

        String numeroPdf
) {

    public RespuestaPolizaDTO(PolizaEntidad entidad) {

        this(
                entidad.getIdPoliza(),
                entidad.getCodigoPoliza(),
                entidad.getFechaInicio(),
                entidad.getFechaFin(),
                entidad.getPrimaNeta(),
                entidad.getPrimaTotal(),
                entidad.getEstado(),
                entidad.getEstadoPago(),
                entidad.getPlaca(),
                entidad.getDescripcion(),
                entidad.getNumeroPdf()
        );

    }
}
