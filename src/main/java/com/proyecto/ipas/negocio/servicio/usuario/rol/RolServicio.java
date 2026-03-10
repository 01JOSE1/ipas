package com.proyecto.ipas.negocio.servicio.usuario.rol;

import com.proyecto.ipas.datos.repositorio.RamoRepositorio;
import com.proyecto.ipas.datos.repositorio.RolRepositorio;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.RespuestaPolizaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.rol.RespuestaRolDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RolServicio {

    RolRepositorio rolRepositorio;

    private static final Logger registro = LoggerFactory.getLogger(RolServicio.class);

    public RolServicio(RolRepositorio rolRepositorio) {
        this.rolRepositorio = rolRepositorio;
    }

    @Transactional(readOnly = true)
    public List<RespuestaRolDTO> obtenerRoles() {
        registro.debug("Mostrando los registros de roles");
        return rolRepositorio.findAll().stream().map(RespuestaRolDTO::new).collect(Collectors.toList());
    }

}
