package com.proyecto.ipas.presentacion.controlador;

import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ipasControlador {

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
