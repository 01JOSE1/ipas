package com.proyecto.ipas.infraestructura.configuracion;

import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.presentacion.excepcion.*;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador global de excepciones usando @ControllerAdvice
 * Captura TODAS las excepciones lanzadas en los controladores
 * y las convierte en respuestas estandarizadas
 *
 * IMPORTANTE: Las excepciones de Spring Security que ocurren en FILTROS
 * NO llegan aquí. Se manejan en CustomAuthenticationEntryPoint y CustomAccessDeniedHandler.
 *
 * PERO: Si lanzas excepciones de Security DENTRO de tus controladores/servicios,
 * SÍ llegarán aquí y puedes manejarlas.
 */
@ControllerAdvice
public class ManejadorExcepcionGlobal {

    private static final Logger registro = LoggerFactory.getLogger(ManejadorExcepcionGlobal.class);

    // EXCEPCIONES DE NEGOCIO

    /**
     * Determina si la petición es AJAX/REST (espera JSON) o web (espera HTML)
     */
    private boolean esAjaxPeticion(HttpServletRequest peticion) {
        String encabezadoAjax = peticion.getParameter("X-Requested-With");
        String encabezadoEstandar = peticion.getParameter("Accept");

        return "XMLHttpRequest".equalsIgnoreCase(encabezadoAjax) || (encabezadoEstandar != null && encabezadoEstandar.contains("application/json"));
    }

    /**
     * Maneja la excepcion recurso no encontrado (404)
     * Cuando algun recurso del sistema ipas no existe (Archivo, usuario, rol, poliza)
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

//    /**
//     * Maneja la excepcion usuario suspendido (423)
//     * Cuando el usuario no cumple con alguna regla de la logica de negocio
//     */
//    @ExceptionHandler(UsuarioSuspendidoExcepcion.class)
//    public Object manejarUsuarioSuspendido(UsuarioSuspendidoExcepcion ex, HttpServletRequest peticion) {
//        registro.warn("Cuenta suspendida: {}", ex.getMessage());
//
//        AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
//                HttpStatus.LOCKED.value(),
//                TipoAlerta.ERROR,
//                "Cuenta suspendida",
//                "La cuenta se encuentra suspendida",
//                ex.getErrorCodigo(),
//                peticion.getRequestURI()
//        );
//
//        if (esAjaxPeticion(peticion)) {
//            return ResponseEntity.status(HttpStatus.LOCKED).body(alertaRespuesta);
//        }
//
//        ModelAndView vistaDato = new ModelAndView("excepciones/error");
//        vistaDato.addObject("error", alertaRespuesta);
//        return vistaDato;
//    }

    /**
     * Maneja PermisoInsuficienteExcepcion ForbiddenException (403)
     * Falta de permisos para realizar la acción
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
     * Maneja excepcion de negocio (400)
     * Errores relacionadas con las diferentes validaciones de negocio
     */
//    @ExceptionHandler(NegocioExcepcion.class)
//    public Object manejarNegocioExcepcion(NegocioExcepcion ex, HttpServletRequest peticion) {
//        registro.warn("Error de Negocio: {}", ex.getMessage());
//
//        AlertaRespuesta errorRespuesta = new AlertaRespuesta(
//                HttpStatus.BAD_REQUEST.value(),
//                "Error de Validación",
//                ex.getMessage(),
//                ex.getErrorCodigo(),
//                peticion.getRequestURI()
//        );
//
//        if (esAjaxPeticion(peticion)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRespuesta);
//        }
//
//        ModelAndView vistaDato = new ModelAndView("error/400");
//        vistaDato.addObject("error", errorRespuesta);
//        return vistaDato;
//    }


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
