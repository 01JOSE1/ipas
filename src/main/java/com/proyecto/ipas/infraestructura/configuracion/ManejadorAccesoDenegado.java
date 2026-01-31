package com.proyecto.ipas.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Maneja errores 403 USUARIO AUTENTICADO PERO SIN PERMISOS
 */
@Component
public class ManejadorAccesoDenegado implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();


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
