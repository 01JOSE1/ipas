package com.proyecto.ipas.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador de acceso denegado (HTTP 403 FORBIDDEN).
 * 
 * Se ejecuta cuando un usuario autenticado intenta acceder a un recurso para el cual 
 * no tiene los permisos (rol) necesarios. Diferencia entre peticiones AJAX (JSON) y web (HTML).
 */
@Component
public class ManejadorAccesoDenegado implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maneja el error cuando un usuario autenticado pero sin permisos intenta acceder a un recurso.
     * 
     * Para peticiones AJAX: retorna respuesta JSON con estado 403.
     * Para peticiones web: hace forward a la vista de error 403 manteniendo la URL.
     * 
     * @param peticion la petición HTTP del cliente
     * @param respuesta la respuesta HTTP a enviar
     * @param excepcion la excepción de acceso denegado
     * @throws IOException si hay error al escribir la respuesta
     * @throws ServletException si hay error en el servlet
     */
    @Override
    public void handle(HttpServletRequest peticion, HttpServletResponse respuesta, AccessDeniedException excepcion) throws IOException, ServletException {

        boolean esAjax = "XMLHttpRequest".equals(peticion.getHeader("X-Requested-With"));

        AlertaRespuesta alertaRespuesta =  new AlertaRespuesta(
                403,
                TipoAlerta.ERROR,
                "Acceso denegado",
                "No tienes permisos para realizar esta acción",
                "PROHIBIDO",
                peticion.getRequestURI()
        );

        if (esAjax) {
            respuesta.setStatus(HttpServletResponse.SC_FORBIDDEN);
            respuesta.setContentType("application/json;charset=UTF-8");

            respuesta.getWriter().write(objectMapper.writeValueAsString(alertaRespuesta));
        } else {
            // Lo guardamos en el 'request' para que la vista lo encuentre ya que ModelAndView no esta disponible en el filtro de security
            peticion.setAttribute("error", alertaRespuesta);

            // Hacemos un FORWARD a la vista de error
            // Esto mantiene la URL actual pero muestra el HTML de error
            peticion.getRequestDispatcher("/usuarios/error-403").forward(peticion, respuesta);
        }
    }
}
