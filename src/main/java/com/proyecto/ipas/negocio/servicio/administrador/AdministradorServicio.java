package com.proyecto.ipas.negocio.servicio.administrador;

import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import com.proyecto.ipas.datos.repositorio.PolizaRepositorio;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdministradorServicio {

    PolizaRepositorio  polizaRepositorio;
    UsuarioRepositorio usuarioRepositorio;
    ClienteRepositorio clienteRepositorio;


    private final static Logger registro = LoggerFactory.getLogger(AdministradorServicio.class);

    public AdministradorServicio(PolizaRepositorio polizaRepositorio, UsuarioRepositorio usuarioRepositorio, ClienteRepositorio clienteRepositorio) {
        this.polizaRepositorio = polizaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    public Map<String, Long> obtenerDatosDashboard() {

        registro.info("Obteniendo datos estadisticos para el dashboard del administrador");

        Map<String, Long> datosDashboard = new LinkedHashMap<>();

        datosDashboard.put("usuariosActivos", usuarioRepositorio.countByEstado("ACTIVO"));
        datosDashboard.put("totalPolizasActivas", polizaRepositorio.contarPolizasActivas());
        datosDashboard.put("totalClientes", clienteRepositorio.count());
        datosDashboard.put("actualizacionesHoy", usuarioRepositorio.contarActualizacionesHoy());
        datosDashboard.put("actualizacionesMes", usuarioRepositorio.contarActualizacionesMes());

        LocalDate limite = LocalDate.now().plusDays(7);
        datosDashboard.put("asesoresConActividad", usuarioRepositorio.contarAsesoresActividadUltimosDias(limite));

        return datosDashboard;
    }
}
