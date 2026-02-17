package com.proyecto.ipas.presentacion.controlador.asesor;

import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/asesor")
public class AsesorControlador {


    @GetMapping("/")
    public String showCreateForm(HttpSession sesion, Model model) {

        if (sesion != null) {
            AlertaRespuesta alerta = (AlertaRespuesta) sesion.getAttribute("alertaRespuesta");

            if (alerta != null) {
                model.addAttribute("alertaRespuesta", alerta);
                sesion.removeAttribute("alertaRespuesta");
            }
        }

        return "asesores/inicio"; // Retorna users/create.html
    }


}
