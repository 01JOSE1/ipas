package com.proyecto.ipas.infraestructura.externo.ia;

import com.proyecto.ipas.presentacion.excepcion.IaProcesoExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia.PeticionIaDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
public class IaServicio {

    private final ChatClient chatClient;

    public IaServicio(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder()
                        // Bajo: decisiones consistentes y predecibles
                        .temperature(0.1)
                        .build())
                .build();
    }

    /**
     * Para procesos que retornan un DTO estructurado.
     * Ejemplo: extraer datos de un PDF y mapearlos a un objeto.
     */
    public <T> T procesar(PeticionIaDTO peticionIaDTO, Class<T> tipoRespuesta, String nombreProceso) {
        try {
            return chatClient
                    .prompt()
                    .system(peticionIaDTO.rolProceso())
                    .user(peticionIaDTO.datosPrompt())
                    .call()
                    .entity(tipoRespuesta);
        } catch (Exception e) {
            throw new IaProcesoExcepcion("Error de procedimiento de IA", "ERROR_"+nombreProceso.toUpperCase(), e.getCause());
        }
    }


}
