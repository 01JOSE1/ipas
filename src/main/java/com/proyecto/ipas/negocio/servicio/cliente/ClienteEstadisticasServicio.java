package com.proyecto.ipas.negocio.servicio.cliente;

import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClienteEstadisticasServicio {

    ClienteRepositorio clienteRepositorio;

    private static final Logger registro = LoggerFactory.getLogger(ClienteEstadisticasServicio.class);

    public ClienteEstadisticasServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    public Map<String, Object> obtenerEstadisticas() {

        registro.info("Obteniendo estadisticas de los clientes...");

        Map<String, Object> estadisticas = new LinkedHashMap<>();

        estadisticas.put("totalClientes", clienteRepositorio.count());
        estadisticas.put("clientesNuevosMes", clienteRepositorio.contarClientesCreadosMes());
        estadisticas.put("clientesActivos", clienteRepositorio.contarClientesConPolizaActiva());
        estadisticas.put("clientesInactivos", clienteRepositorio.contarClientesSinPolizasActivas());

        // ── Datos para gráfica: activos vs inactivos ──────────────
        estadisticas.put("activosLabels",  List.of("Con póliza activa", "Sin póliza activa"));
        estadisticas.put("activosValores", List.of(
                clienteRepositorio.contarClientesConPolizaActiva(),
                clienteRepositorio.contarClientesSinPolizasActivas()
        ));

        // ── Datos para gráfica: top ciudades ─────────────────────
        List<Object[]> porCiudad = clienteRepositorio.contarClientesPorCiudad();
        estadisticas.put("ciudadLabels",  porCiudad.stream().map(r -> r[0].toString()).collect(Collectors.toList()));
        estadisticas.put("ciudadValores", porCiudad.stream().map(r -> r[1]).collect(Collectors.toList()));

        // ── Datos para gráfica: estado civil ─────────────────────
        List<Object[]> porEstadoCivil = clienteRepositorio.contarClientesPorEstadoCivil();
        estadisticas.put("estadoCivilLabels",  porEstadoCivil.stream().map(r -> r[0].toString()).collect(Collectors.toList()));
        estadisticas.put("estadoCivilValores", porEstadoCivil.stream().map(r -> r[1]).collect(Collectors.toList()));

        // ── Datos para gráfica: tipo de documento ────────────────
        List<Object[]> porTipoDoc = clienteRepositorio.contarClientesPorTipoDocumento();
        estadisticas.put("tipoDocLabels",  porTipoDoc.stream().map(r -> r[0].toString()).collect(Collectors.toList()));
        estadisticas.put("tipoDocValores", porTipoDoc.stream().map(r -> r[1]).collect(Collectors.toList()));

        return estadisticas;
    }
}
