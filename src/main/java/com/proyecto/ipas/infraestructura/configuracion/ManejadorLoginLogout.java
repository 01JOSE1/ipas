package com.proyecto.ipas.infraestructura.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class ManejadorLoginLogout {

    /**
     * Bean que proporciona el handler ejecutado después de un login exitoso.
     * 
     * Detecta si la petición es AJAX (JSON) o web (HTML redirect). Valida el rol del usuario
     * y lo redirige a su respectivo dashboard (ADMINISTRADOR a /administrador/, ASESOR a /asesor/).
     * 
     * @param objectMapper mapeador JSON para serializar respuestas
     * @return handler que procesa logins exitosos
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(ObjectMapper objectMapper) {
        return (peticion, respuesta, autenticacion) -> {
            boolean esAjax = "XMLHttpRequest".equals(peticion.getHeader("X-Requested-With"));

            UsuarioSeguridad usuarioAutenticado = (UsuarioSeguridad) autenticacion.getPrincipal();

            String url = "";

            var rol = usuarioAutenticado.getAuthorities().stream()
                    .map(r -> r.getAuthority())
                    .toList();

            if (rol.contains("ROLE_ADMINISTRADOR")) {
                url = "/administrador/";
            } else if (rol.contains("ROLE_ASESOR")) {
                url = "/asesor/";
            }

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpServletResponse.SC_OK,
                    TipoAlerta.EXITO,
                    "Login con exito",
                    "Bienvenido " + usuarioAutenticado.getNombre(),
                    "EXITO_LOGIN",
                    peticion.getRequestURI()
            );

            if (esAjax) {
                respuesta.setStatus(HttpServletResponse.SC_OK);
                respuesta.setContentType("application/json;charset=UTF-8");
                respuesta.getWriter().write(objectMapper.writeValueAsString(alertaRespuesta));
            } else {
                peticion.getSession().setAttribute("alertaRespuesta", alertaRespuesta);
                respuesta.sendRedirect(peticion.getContextPath() + url);
            }
        };
    }


    /**
     * Bean que proporciona el handler ejecutado cuando un login falla.
     * 
     * Mapea diferentes tipos de excepciones de Spring Security a mensajes específicos:
     * - DisabledException: cuenta inactiva
     * - LockedException: cuenta bloqueada/suspendida
     * - CredentialsExpiredException: credenciales expiradas
     * - AccountExpiredException: cuenta expirada
     * 
     * Diferencia entre peticiones AJAX (JSON) y web (HTML redirect).
     * 
     * @param objectMapper mapeador JSON para serializar respuestas
     * @return handler que procesa logins fallidos
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(ObjectMapper objectMapper) {

        return (peticion, respuesta, excepcion) -> {

            boolean esAjax = "XMLHttpRequest".equals(peticion.getHeader("X-Requested-With"));


            String message = "Email o contraseña incorrectos";
            String errorCode = "CREDENCIALES_INVALIDAS";
            int status = HttpServletResponse.SC_UNAUTHORIZED;
            TipoAlerta tipoAlerta = TipoAlerta.ERROR;

            if (excepcion instanceof DisabledException) {
                tipoAlerta = TipoAlerta.ADVERTENCIA;
                message = "Tu cuenta está inactiva y pendiente de confirmación por el administrador";
                errorCode = "CUENTA_INACTIVA";
                status = HttpServletResponse.SC_FORBIDDEN;

            } else if (excepcion instanceof LockedException) {
                message = "Tu cuenta ha sido bloqueada. Contacta al administrador";
                errorCode = "CUENTA_BLOQUEADA";
                status = HttpServletResponse.SC_FORBIDDEN;

            } else if (excepcion instanceof CredentialsExpiredException) {
                message = "Tus credenciales han expirado. Debes cambiar tu contraseña";
                errorCode = "CREDENCIALES_EXPIRADAS";
                status = HttpServletResponse.SC_FORBIDDEN;

            } else if (excepcion instanceof AccountExpiredException) {
                message = "Tu cuenta ha expirado. Contacta al administrador";
                errorCode = "CUENTA_EXPIRADA";
                status = HttpServletResponse.SC_FORBIDDEN;
            }


            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    status,
                    tipoAlerta,
                    "Login fallido",
                    message,
                    errorCode,
                    peticion.getRequestURI()
            );

            if (esAjax) {
                respuesta.setStatus(status);
                respuesta.setContentType("application/json;charset=UTF-8");
                respuesta.getWriter().write(objectMapper.writeValueAsString(alertaRespuesta));

            } else {
                peticion.getSession().setAttribute("alertaRespuesta", alertaRespuesta);
                respuesta.sendRedirect(peticion.getContextPath() + "/usuarios/login");
            }
        };
    }



    /**
     * Bean que proporciona el handler ejecutado después de un logout exitoso.
     * 
     * Guarda un mensaje de éxito en sesión y redirige al usuario a la página principal.
     * 
     * @return handler que procesa logins salientes (logouts)
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (peticion, respuesta, autenticacion) -> {

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    TipoAlerta.EXITO,
                    "Has cerrado sesión. Vuelve pronto."
            );

            peticion.getSession().setAttribute("alertaRespuesta", alertaRespuesta);

            respuesta.sendRedirect(peticion.getContextPath() + "/");
        };
    }


}
