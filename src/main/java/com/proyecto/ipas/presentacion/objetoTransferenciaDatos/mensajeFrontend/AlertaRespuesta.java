package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;

import java.time.LocalDateTime;
import java.util.List;


/**
 * DTO para respuestas de error estandarizadas.
 * Este objeto se convierte a JSON cuando se hace una petición AJAX
 * o se usa para el modelo en Thymeleaf.
 *
 * @param <T> Tipo genérico para datos adicionales en la respuesta
 *
 * Parámetros del record:
 * - marcaTiempo: Marca de tiempo de la respuesta (yyyy-MM-dd HH:mm:ss)
 * - tipo: Tipo de alerta (éxito, advertencia, error)
 * - estado: Código de estado HTTP
 * - nombre: Nombre del error HTTP
 * - mensaje: Mensaje para el usuario final
 * - nombreCodigo: Código de error del sistema IPAS
 * - ruta: Ruta donde se ocasionó el problema
 * - campoErrorLista: Lista inmutable con validaciones de campos (si aplica)
 * - datoAdicional: Dato genérico adicional de tipo T
 */
public record AlertaRespuesta<T> (
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime marcaTiempo,

        TipoAlerta tipo,

        int estado,

        String nombre,

        String mensaje,

        String nombreCodigo,

        String ruta,

        List<CampoAlerta> campoErrorLista,

        T datoAdicional

){
    /**
     * Constructor compacto con algunos valores por defecto.
     * Inicializa marcaTiempo al momento actual si es null,
     * e inicializa campoErrorLista como una lista inmutable vacía si es null.
     */
    public AlertaRespuesta {
        if (marcaTiempo == null) {
            marcaTiempo = LocalDateTime.now();
        }

        if (campoErrorLista == null) {
            campoErrorLista = List.of();
        }
    }

    public void agregarCampoError(String campo, String mensaje){
        campoErrorLista.add(new CampoAlerta(campo, mensaje));
    }

    /**
     * Constructor simplificado sin validaciones de formularios
     */
    public AlertaRespuesta(int estado, TipoAlerta tipo, String nombre, String mensaje, String nombreCodigo, String ruta) {
        this(LocalDateTime.now(), tipo, estado, nombre, mensaje, nombreCodigo, ruta, List.of(), null);
    }

    public AlertaRespuesta(int estado, TipoAlerta tipo, String nombre, String mensaje, String nombreCodigo) {
        this(LocalDateTime.now(), tipo, estado, nombre, mensaje, nombreCodigo, null, List.of(), null);
    }

    public AlertaRespuesta(TipoAlerta tipo, String mensaje) {
        this(LocalDateTime.now(), tipo, 0, null, mensaje, null, null, List.of(), null);
    }

    /**
     * Constructor sin codigo de error del sistema IPAS
     */
    public AlertaRespuesta(int estado, String nombre, String mensaje, String ruta) {
        this(LocalDateTime.now(), null, estado, nombre, mensaje, "", ruta, List.of(), null);
    }

    /**
     * Record para manejra las validaciones en campos
     * Uso de formularios
     */
    public record CampoAlerta(
            String campo,

            String mensaje
    ){}
}
