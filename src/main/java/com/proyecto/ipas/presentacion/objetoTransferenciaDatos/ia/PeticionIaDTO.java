package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia;

public record PeticionIaDTO(
        // El rol específico de ese proceso
        String rolProceso,

        // Los datos ya formateados listos para la IA
        String datosPrompt,

        // El DTO al que debe mapearse la respuesta
        Class<?> tipoRespuesta
) {
    

    // Factory method para procesos que SÍ retornan DTO
    public static PeticionIaDTO conRespuesta(String rolProceso, String datosPrompt, Class<?> tipoRespuesta) {
        return new PeticionIaDTO(rolProceso, datosPrompt, tipoRespuesta);
    }
}
