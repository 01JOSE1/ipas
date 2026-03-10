package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia;

/**
 * DTO para encapsular peticiones dirigidas al servicio de IA.
 * Contiene los datos del prompt, el rol del proceso y la información de mapeo
 * necesaria para enviar solicitudes a la IA y mapear las respuestas.
 *
 * Parámetros del record:
 * - rolProceso: El rol específico de ese proceso (contexto para la IA)
 * - datosPrompt: Los datos ya formateados listos para enviar a la IA
 * - tipoRespuesta: El DTO o clase a la que debe mapearse la respuesta de la IA
 */
public record PeticionIaDTO(
        String rolProceso,

        String datosPrompt,

        Class<?> tipoRespuesta
) {
    
    /**
     * Factory method para crear peticiones de IA que SÍ retornan un DTO de respuesta.
     * Este método se usa cuando el proceso de IA tiene un tipo de respuesta específico
     * que debe mapearse a un DTO.
     *
     * @param rolProceso el rol específico del proceso
     * @param datosPrompt los datos formateados para la IA
     * @param tipoRespuesta la clase o DTO a que debe mapearse la respuesta
     * @return una nueva instancia de PeticionIaDTO
     */
    public static PeticionIaDTO conRespuesta(String rolProceso, String datosPrompt, Class<?> tipoRespuesta) {
        return new PeticionIaDTO(rolProceso, datosPrompt, tipoRespuesta);
    }
}
