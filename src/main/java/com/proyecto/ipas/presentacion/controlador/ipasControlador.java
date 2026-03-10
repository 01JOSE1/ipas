package com.proyecto.ipas.presentacion.controlador;

import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador principal de la aplicación IPAS.
 * 
 * Maneja la ruta raíz del sistema y recupera alertas de la sesión
 * para mostrarlas al usuario en la página de bienvenida.
 */
@Controller
public class ipasControlador {

    /**
     * Muestra la página de inicio del sistema.
     * 
     * Recupera los atributos de alerta de la sesión previa (si existen)
     * y los añade al modelo para mostrar el estado anterior de operaciones.
     * 
     * @param sesion la sesión HTTP actual
     * @param model modelo para pasar datos a la vista
     * @return nombre de la vista "ipas" (página principal)
     */
    @GetMapping("/")
    public String showCreateForm(HttpSession sesion, Model model) {


        if (sesion != null) {
            AlertaRespuesta alerta = (AlertaRespuesta) sesion.getAttribute("alertaRespuesta");

            if (alerta != null) {
                model.addAttribute("alertaRespuesta", alerta);
                sesion.removeAttribute("alertaRespuesta");
            }
        }

        return "ipas";
    }

}
