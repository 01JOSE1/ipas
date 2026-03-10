package com.proyecto.ipas.infraestructura.externo.ia;

import com.proyecto.ipas.presentacion.excepcion.IaProcesoExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia.PeticionIaDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
/**
 * Servicio que integra con un modelo de IA generativa para procesar datos estructurados.
 * 
 * Utiliza Spring AI para enviar prompts a un modelo de lenguaje y recibir respuestas
 * tipadas que pueden ser deserializadas a objetos específicos.
 * 
 * Configuración: Temperature 0.1 (respuestas consistentes y predecibles).
 */
public class IaServicio {

    private final ChatClient chatClient;

    /**
     * Constructor que configura el cliente de chat con opciones predeterminadas.
     * 
     * @param builder el builder de ChatClient proporcionado por Spring AI
     */
    public IaServicio(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(ChatOptions.builder()
                        // Bajo: decisiones consistentes y predecibles
                        .temperature(0.1)
                        .build())
                .build();
    }

    /**
     * Procesa una petición de IA y retorna un DTO tipado.
     * 
     * Envía un rol del sistema y datos de entrada al modelo de IA, esperando
     * una respuesta que será deserializada al tipo especificado.
     * Ejemplo: extraer datos estructurados de un PDF y mapearlos a un objeto.
     * 
     * @param <T> el tipo de respuesta esperada
     * @param peticionIaDTO la petición con el rol y datos para el prompt
     * @param tipoRespuesta la clase del tipo esperado para deserializar
     * @param nombreProceso nombre del proceso para identificación en errores
     * @return una instancia del tipo T con los datos extraídos por la IA
     * @throws IaProcesoExcepcion si falla el procesamiento de IA
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
