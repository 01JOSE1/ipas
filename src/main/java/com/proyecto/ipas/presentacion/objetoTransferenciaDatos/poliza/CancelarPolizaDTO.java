package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelarPolizaDTO {

    @NotNull
    private Long idPoliza;

    private String codigoPoliza;

    @NotBlank(message = "Debe ingresar un motivo de cancelación")
    @Size(min = 5, max = 500, message = "Debe ingresar mas información")
    private String motivo;
}
