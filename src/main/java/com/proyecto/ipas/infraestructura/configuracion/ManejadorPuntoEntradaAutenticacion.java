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
 * Manejador de errores 401 USUARIO NO AUTENTICADO
 * Se ejecuta cuando se intenta acceder sin el login
 */
@Component
public class ManejadorPuntoEntradaAutenticacion implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
