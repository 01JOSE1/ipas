package com.proyecto.ipas.infraestructura.configuracion;

import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.presentacion.excepcion.*;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador global de excepciones usando {@link ControllerAdvice}.
 * 
 * Captura TODAS las excepciones lanzadas en los controladores y las convierte 
 * en respuestas JSON estandarizadas que envía al frontend.
 * 
 * IMPORTANTE: Las excepciones de Spring Security que ocurren en FILTROS
 * (como configuración de seguridad) NO llegan aquí. Se manejan en:
 * - {@link ManejadorPuntoEntradaAutenticacion} : errores 401
 * - {@link ManejadorAccesoDenegado} : errores 403
 * 
 * PERO: Si lanzas excepciones de Security DENTRO de tus controladores/servicios,
 * SÍ llegarán aquí y puedes manejarlas.
 */
@ControllerAdvice
public class ManejadorExcepcionGlobal {

    private static final Logger registro = LoggerFactory.getLogger(ManejadorExcepcionGlobal.class);

    // EXCEPCIONES DE NEGOCIO

    /**
     * Determina si la petición es AJAX/REST (espera JSON) o tradicional web (espera HTML).
     * 
     * Comprueba el encabezado X-Requested-With o el encabezado Accept.
     * 
     * @param peticion la petición HTTP
     * @return {@code true} si es AJAX, {@code false} si es web
     */
    private boolean esAjaxPeticion(HttpServletRequest peticion) {
        String encabezadoAjax = peticion.getParameter("X-Requested-With");
        String encabezadoEstandar = peticion.getParameter("Accept");

        return "XMLHttpRequest".equalsIgnoreCase(encabezadoAjax) || (encabezadoEstandar != null && encabezadoEstandar.contains("application/json"));
    }

    /**
     * Maneja la excepción {@link RecursoNOEncontradoException} (HTTP 404).
     * 
     * Se lanza cuando un recurso del sistema no existe (Usuario, Rol, Póliza, Archivo, etc.).
     * Diferencia entre peticiones AJAX (JSON) y web (HTML con redirect).
     * 
     * @param ex la excepción de recurso no encontrado
     * @param peticion la petición HTTP
     * @return respuesta JSON (AJAX) o ModelAndView con vista de error (web)
     */
    @ExceptionHandler({RecursoNOEncontradoException.class, NoResourceFoundException.class})
    public Object manejarRecursoNOEncontrado(Exception ex, HttpServletRequest peticion) {
        registro.warn("Recurso no encontrado: {}", ex.getMessage());

        AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                HttpStatus.NOT_FOUND.value(),
                TipoAlerta.ADVERTENCIA,
                "Recurso No Encontrado",
                (ex instanceof RecursoNOEncontradoException) ? ex.getMessage() : "El recurso que buscas puede no existir: " +peticion.getRequestURI(),
                "RECURSO_NO_ENCONTRADO",
                peticion.getRequestURI()
        );

        if (esAjaxPeticion(peticion)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(alertaRespuesta);
        }

        ModelAndView vistaDato = new ModelAndView("excepciones/error");
        vistaDato.addObject("error", alertaRespuesta);
        return vistaDato;
    }

    /**
     * Maneja excepciones de permisos insuficientes (HTTP 403).
     * 
     * Se lanza cuando el usuario no tiene los permisos necesarios para realizar una acción.
     * Diferencia entre peticiones AJAX (JSON) y web (HTML).
     * 
     * @param ex la excepción de permiso insuficiente o acceso denegado
     * @param peticion la petición HTTP
     * @return respuesta JSON (AJAX) o ModelAndView con vista de error (web)
     */
    @ExceptionHandler({PermisoInsuficienteExcepcion.class, AccessDeniedException.class})
    public Object manejarProhibido(Exception ex, HttpServletRequest peticion) {

        registro.warn("Acceso denegado: {}", ex.getMessage());

        AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                HttpStatus.FORBIDDEN.value(),
                TipoAlerta.ERROR,
                "Acceso Denegado",
                "No tienes permisos para realizar esta acción",
                "PROHIBIDO",
                peticion.getRequestURI()
        );

        if (esAjaxPeticion(peticion)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(alertaRespuesta);
        }

        ModelAndView vistaDato = new ModelAndView("excepciones/error");
        vistaDato.addObject("error", alertaRespuesta);
        return vistaDato;
    }


    /**
     * Maneja ConflictException (409)
     * Conflictos (recurso ya existe, operación concurrente)
     */
//    @ExceptionHandler(ConflictoExcepcion.class)
//    public Object manejarConflicto(ConflictoExcepcion ex, HttpServletRequest peticion) {
//
//        registro.warn("Conflicto: {}", ex.getMessage());
//
//        AlertaRespuesta errorRespuesta = new AlertaRespuesta(
//                HttpStatus.CONFLICT.value(),
//                "Conflicto",
//                ex.getMessage(),
//                ex.getErrorCodigo(),
//                peticion.getRequestURI()
//        );
//
//        if (esAjaxPeticion(peticion)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorRespuesta);
//        }
//
//        ModelAndView vistaDato = new ModelAndView("excepciones/error");
//        vistaDato.addObject("error", errorRespuesta);
//        return vistaDato;
//    }


    /**
     * Maneja errores de validación (@Valid en DTOs)
     * Captura errores de @NotNull, @NotBlank, @Email, etc.
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Object manejarValidacionError(MethodArgumentNotValidException ex, HttpServletRequest peticion) {
//
//        registro.warn("Errores de validación en: {}", peticion.getRequestURI());
//
//        AlertaRespuesta errorRespuesta = new AlertaRespuesta(
//                HttpStatus.BAD_REQUEST.value(),
//                "Error de Validación",
//                "Los datos enviados no son válidos",
//                "VALIDACION_ERROR",
//                peticion.getRequestURI()
//        );
//
//        // Agregar errores específicos de cada campo
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String campoError = ((FieldError) error).getField();
//            String mensajeCampoError = error.getDefaultMessage();
//            errorRespuesta.agregarCampoError(campoError, mensajeCampoError);
//        });
//
//        if (esAjaxPeticion(peticion)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRespuesta);
//        }
//
//        ModelAndView vistaDato = new ModelAndView("error/validacion");
//        vistaDato.addObject("error", errorRespuesta);
//        return vistaDato;
//    }


    // EXCEPCIONES DE SPRING SECURITY (CONTROLADOR/SERVICE)

    /**
     * Captura BadCredentialsException si se lanza DENTRO de un controller/service
     * (NO desde el filtro de login, ese lo maneja AuthenticationFailureHandler)
     */
//    @ExceptionHandler(BadCredentialsException.class)
//    public Object handleBadCredentials(BadCredentialsException ex, HttpServletRequest peticion) {
//
//        registro.warn("Credenciales inválidas en {}", peticion.getRequestURI());
//
//        AlertaRespuesta errorRespuesta = new AlertaRespuesta(
//                HttpStatus.UNAUTHORIZED.value(),
//                "Credenciales Inválidas",
//                "Usuario o contraseña incorrectos",
//                "CREDENCIALES_INVALIDAS",
//                peticion.getRequestURI()
//        );
//
//        if (esAjaxPeticion(peticion)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRespuesta);
//        }
//
//        return new ModelAndView("redirect:/login?error=credenciales-invalidas");
//    }

    /**
     * Maneja el error cuando el archivo subido excede el tamaño máximo permitido (HTTP 413).
     * 
     * Se dispara cuando el tamaño del archivo supera lo configurado en
     * {@code spring.servlet.multipart.max-file-size}.
     * 
     * Diferencia entre peticiones AJAX (JSON) y web (redirect con mensaje).
     * 
     * @param ex la excepción de tamaño excedido
     * @param peticion la petición HTTP
     * @param redirectAttributes atributos para guardar mensajes en redirect
     * @return respuesta JSON (AJAX) o redirect a página anterior (web)
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object manejarTamañoMaximoArchivo(
            MaxUploadSizeExceededException ex,
            HttpServletRequest peticion,
            RedirectAttributes redirectAttributes) {

        registro.warn("413 - Archivo excede el tamaño máximo permitido: {}", ex.getMessage());

        AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                TipoAlerta.ERROR,
                "Archivo demasiado grande",
                "El archivo supera el tamaño máximo permitido. Por favor, verifica el tamaño e intenta nuevamente.",
                "ERROR_ARCHIVO_TAMANO_EXCEDIDO",
                peticion.getRequestURI()
        );

        if (esAjaxPeticion(peticion)) {
            return ResponseEntity
                    .status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(alertaRespuesta);
        }

        redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);


        return "redirect:/asesor/poliza/registro-poliza";
    }



    /**
     * Maneja errores al almacenar archivos en el sistema de archivos local (HTTP 500).
     * 
     * Se lanza cuando hay problemas I/O, permisos insuficientes, o espacios en disco.
     * 
     * @param ex la excepción de almacenamiento
     * @param peticion la petición HTTP
     * @return respuesta JSON con error 500
     */
    @ExceptionHandler(AlmacenamientoExcepcion.class)
    public Object manejarAlmacenamiento(Exception ex, HttpServletRequest peticion) {
        registro.error("Almacenamiento no encontrado: {}", ex.getMessage());

        AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                TipoAlerta.FATAL,
                "Error interno del servidor al almacenar archivos",
                "Ha ocurrido un error inesperado al momento de almacenar el archivo. Por favor, intenta nuevamente más tarde.",
                "ERROR_ALMACENAMIENTO_INTERNO",
                peticion.getRequestURI()
        );

        if (esAjaxPeticion(peticion)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alertaRespuesta);
        }

        ModelAndView vistaDato = new ModelAndView("excepciones/error");
        vistaDato.addObject("error", alertaRespuesta);
        return vistaDato;
    }


    // CAPTURAR TODAS LAS DEMAS EXCEPCIONES

    /**
     * Maneja TODAS las demás excepciones no capturadas (500)
     * Este es el catch-all para errores inesperados
     */
    @ExceptionHandler(Exception.class)
    public Object manejarExcepcionGlobal(Exception ex, HttpServletRequest peticion) {

        registro.error("Error inesperado en {}: {}", peticion.getRequestURI(), ex.getMessage(), ex);

        AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                TipoAlerta.FATAL,
                "Error Interno del Servidor",
                "Ha ocurrido un error inesperado. Por favor, intenta nuevamente más tarde.",
                "ERROR_INTERNO",
                peticion.getRequestURI()
        );

        if (esAjaxPeticion(peticion)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(alertaRespuesta);
        }

        ModelAndView vistaDato = new ModelAndView("excepciones/error");
        vistaDato.addObject("error", alertaRespuesta);
        return vistaDato;
    }


}
