package com.proyecto.ipas.presentacion.controlador.usuarios.administrador;

import com.proyecto.ipas.negocio.servicio.poliza.PolizaEstadisticasServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.Map;

@Controller
@RequestMapping("/administrador/poliza")
public class AdministradorPolizaControlador {

    @Autowired
    private PolizaEstadisticasServicio polizaEstadisticasServicio;

    @GetMapping("estadisticas")
    public String verEstadisticasPolizas(Model modelo) {

        Map<String, Object> estadisticas = polizaEstadisticasServicio.obtenerEstadisticas();

        estadisticas.forEach( modelo::addAttribute );

        return "usuarios/administradores/polizas/estadisticas";
    }

}
