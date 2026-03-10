package com.proyecto.ipas.negocio.servicio.cliente;

import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ClienteEstadisticasServicio {

    ClienteRepositorio clienteRepositorio;

    private static final Logger registro = LoggerFactory.getLogger(ClienteEstadisticasServicio.class);

    public ClienteEstadisticasServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    public Map<String, Long> obtenerEstadisticas() {

        registro.info("Obteniendo estadisticas de los clientes...");

        Map<String, Long> estadisticas = new LinkedHashMap<>();

        estadisticas.put("tolalClientes", clienteRepositorio.count());
        estadisticas.put("clientesNuevosMes", clienteRepositorio.contarClientesCreadosMes());
        estadisticas.put("clientesActivos", clienteRepositorio.contarClientesConPolizaActiva());
        estadisticas.put("clientesInactivos", clienteRepositorio.contarClientesSinPolizasActivas());

        return estadisticas;
    }
}
