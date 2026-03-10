package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsesorRankingDTO {
    private String nombre;
    private Long totalPolizas;
    private Double porcentaje;
}