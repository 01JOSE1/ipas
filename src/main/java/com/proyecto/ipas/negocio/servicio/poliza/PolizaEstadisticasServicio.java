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

    /**
     * Obtiene un conjunto completo de estadísticas sobre las pólizas del sistema.
     * Incluye totales, conteos por estado y datos desagregados para gráficas de ramos y aseguradoras.
     *
     * @return un mapa con las siguientes claves:
     *         - "totalPolizas": cantidad total de pólizas registradas
     *         - "totalPolizasActivas": cantidad de pólizas con estado activo
     *         - "polizasVencidasHastaHoyEsteMes": cantidad de pólizas vencidas en el mes actual
     *         - "polizasCanceladasHastaHoyEsteMes": cantidad de pólizas canceladas en el mes actual
     *         - "estadoLabels": etiquetas de estados de pólizas
     *         - "estadoValores": cantidad de pólizas por estado
     *         - "ramoLabels": nombres de los ramos de seguros
     *         - "ramoValores": cantidad de pólizas por ramo
     *         - "aseguradoraLabels": nombres de las aseguradoras
     *         - "aseguradoraValores": cantidad de pólizas por aseguradora
     */
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
