package com.proyecto.ipas.negocio.servicio.poliza;

import com.proyecto.ipas.datos.repositorio.PolizaRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class PolizaEstadisticasServicio {

    PolizaRepositorio polizaRepositorio;

    private final static Logger registro = LoggerFactory.getLogger(PolizaEstadisticasServicio.class);


    public PolizaEstadisticasServicio(PolizaRepositorio polizaRepositorio) {
        this.polizaRepositorio = polizaRepositorio;
    }

    public Map<String, Long> obtenerEstadisticas() {

        registro.info("Obteniendo datos estadisticos de las polizas...");

        Map<String, Long> estadisticas = new LinkedHashMap<>();

        estadisticas.put("totalPolizas", polizaRepositorio.count());
        estadisticas.put("totalPolizasActivas", polizaRepositorio.contarPolizasActivas());
        estadisticas.put("polizasVencidasHastaHoyEsteMes", polizaRepositorio.contarPolizasVencidasMes());
        estadisticas.put("polizasCanceladasHastaHoyEsteMes",  polizaRepositorio.contarPolizasCanceladasMes());

        return estadisticas;
    }
}
