package com.proyecto.ipas.negocio.servicio.poliza;

import com.proyecto.ipas.datos.repositorio.PolizaRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PolizaEstadisticasServicio {

    PolizaRepositorio polizaRepositorio;

    private final static Logger registro = LoggerFactory.getLogger(PolizaEstadisticasServicio.class);


    public PolizaEstadisticasServicio(PolizaRepositorio polizaRepositorio) {
        this.polizaRepositorio = polizaRepositorio;
    }

    public Map<String, Object> obtenerEstadisticas() {

        registro.info("Obteniendo datos estadisticos de las polizas...");

        Map<String, Object> estadisticas = new LinkedHashMap<>();

        estadisticas.put("totalPolizas", polizaRepositorio.count());
        estadisticas.put("totalPolizasActivas", polizaRepositorio.contarPolizasActivas());
        estadisticas.put("polizasVencidasHastaHoyEsteMes", polizaRepositorio.contarPolizasVencidasMes());
        estadisticas.put("polizasCanceladasHastaHoyEsteMes",  polizaRepositorio.contarPolizasCanceladasMes());

        // ── Datos para gráfica: distribución por estado ───────────
        // Convierte List<Object[]> → dos listas paralelas (labels + valores)
        List<Object[]> porEstado = polizaRepositorio.contarPolizasPorEstado();
        estadisticas.put("estadoLabels",  porEstado.stream().map(r -> r[0].toString()).collect(Collectors.toList()));
        estadisticas.put("estadoValores", porEstado.stream().map(r -> r[1]).collect(Collectors.toList()));

        // ── Datos para gráfica: top ramos ─────────────────────────
        List<Object[]> porRamo = polizaRepositorio.contarPolizasPorRamo();
        estadisticas.put("ramoLabels",  porRamo.stream().map(r -> r[0].toString()).collect(Collectors.toList()));
        estadisticas.put("ramoValores", porRamo.stream().map(r -> r[1]).collect(Collectors.toList()));

        // ── Datos para gráfica: top aseguradoras ─────────────────
        List<Object[]> porAseguradora = polizaRepositorio.contarPolizasPorAseguradora();
        estadisticas.put("aseguradoraLabels",  porAseguradora.stream().map(r -> r[0].toString()).collect(Collectors.toList()));
        estadisticas.put("aseguradoraValores", porAseguradora.stream().map(r -> r[1]).collect(Collectors.toList()));

        return estadisticas;
    }
}
