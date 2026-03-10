package com.proyecto.ipas.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador de punto de entrada de autenticación (HTTP 401 UNAUTHORIZED).
 * 
 * Se ejecuta cuando se intenta acceder a un recurso protegido sin haber iniciado sesión.
 * Diferencia entre peticiones AJAX/REST (retorna JSON) y peticiones web (retorna HTML con redirect).
 */
@Component
public class ManejadorPuntoEntradaAutenticacion implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Maneja el error cuando un usuario no autenticado intenta acceder a un recurso protegido.
     * 
     * Para peticiones AJAX: retorna respuesta JSON con estado 401.
     * Para peticiones web: redirige al formulario de login y guarda la alerta en sesión.
     * 
     * @param peticion la petición HTTP del cliente
     * @param respuesta la respuesta HTTP a enviar
     * @param excepcion la excepción de autenticación lanzada
     * @throws IOException si hay error al escribir la respuesta
     * @throws ServletException si hay error en el servlet
     */
    @Override
    public void commence(HttpServletRequest peticion, HttpServletResponse respuesta, AuthenticationException excepcion) throws IOException, ServletException {

        boolean esAjax = "XMLHttpRequest".equals(peticion.getHeader("X-Requested-With"));

        AlertaRespuesta alertaRespuesta =  new AlertaRespuesta(
                401,
                TipoAlerta.ERROR,
                "No autorizado",
                "Debes iniciar sesion para acceder a este recurso",
                "NO_AUTORIZADO",
                peticion.getRequestURI()
        );

        if (esAjax) {
            respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            respuesta.setContentType("application/json;charset=UTF-8");

            respuesta.getWriter().write(objectMapper.writeValueAsString(alertaRespuesta));
        } else {
            // Obtenemos la sesión y guardamos el mensaje manualmente
            peticion.getSession().setAttribute("alertaRespuesta", alertaRespuesta );
            respuesta.sendRedirect(peticion.getContextPath() + "/usuarios/login");
        }
    }
}
