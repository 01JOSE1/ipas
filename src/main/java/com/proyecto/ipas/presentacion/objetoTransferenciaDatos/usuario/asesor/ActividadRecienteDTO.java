package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor;

import java.time.LocalDateTime;

public interface ActividadRecienteDTO {
    String getTipo();
    String getDescripcion();
    LocalDateTime getFecha();
}