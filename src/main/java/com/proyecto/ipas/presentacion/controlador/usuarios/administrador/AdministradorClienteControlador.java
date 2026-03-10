package com.proyecto.ipas.presentacion.controlador.usuarios.administrador;

import com.proyecto.ipas.negocio.servicio.cliente.ClienteEstadisticasServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/administrador/cliente")
public class AdministradorClienteControlador {

    @Autowired
    private ClienteEstadisticasServicio clienteEstadisticasServicio;

    @GetMapping("estadisticas")
    public String verEstadisticasClientes(Model modelo) {

        Map<String, Object> estadisticas = clienteEstadisticasServicio.obtenerEstadisticas();

        estadisticas.forEach( modelo::addAttribute );

        return "usuarios/administradores/clientes/estadisticas";
    }

}
