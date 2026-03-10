package com.proyecto.ipas.negocio.servicio.usuario.asesor;

import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import com.proyecto.ipas.datos.repositorio.PolizaRepositorio;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import com.proyecto.ipas.presentacion.excepcion.RecursoNOEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AsesorServicio {

    private static final Logger registro = LoggerFactory.getLogger(AsesorServicio.class);


    PolizaRepositorio polizaRepositorio;
    ClienteRepositorio clienteRepositorio;
    UsuarioRepositorio usuarioRepositorio;

    public AsesorServicio(PolizaRepositorio polizaRepositorio, ClienteRepositorio clienteRepositorio, UsuarioRepositorio usuarioRepositorio) {
        this.polizaRepositorio = polizaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obtenerDatosParaDashboard(Long idUsuario) {
        usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RecursoNOEncontradoException("Usuario", "id", idUsuario));

        Map<String, Long> datosDashboardAsesor = new LinkedHashMap<>();

        datosDashboardAsesor.put("polizasCreadasMes", polizaRepositorio.contarPolizasEsteMes(idUsuario));
        datosDashboardAsesor.put("gestionesMes", usuarioRepositorio.contarGestionesMes(idUsuario));
        datosDashboardAsesor.put("clientesGestionados", clienteRepositorio.contarClientesPorUsuario(idUsuario));

        LocalDate limite = LocalDate.now().plusDays(8);
        datosDashboardAsesor.put("polizasProximasVencer", polizaRepositorio.countPolizasPorVencer(limite));

        return datosDashboardAsesor;

    }
}