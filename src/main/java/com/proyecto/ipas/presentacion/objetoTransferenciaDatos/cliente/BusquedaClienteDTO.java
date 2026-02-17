package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente;

public record BusquedaClienteDTO(
        Long idCliente,
        String nombreCompleto,
        String numeroDocumento
) {
}
