package com.proyecto.ipas.negocio.servicio.poliza.ramo;

import com.proyecto.ipas.datos.repositorio.RamoRepositorio;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.ramo.ListarParaSelectRamoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RamoServicio {

    private static final Logger registro = LoggerFactory.getLogger (RamoServicio.class);

    RamoRepositorio ramoRepositorio;

    public RamoServicio(RamoRepositorio ramoRepositorio) {
        this.ramoRepositorio = ramoRepositorio;
    }

    /**
     * Obtiene la lista completa de ramos (tipos de seguros) disponibles en el sistema,
     * formateada para ser utilizada en elementos select o listas desplegables.
     *
     * @return lista de ListarParaSelectRamoDTO con el ID y nombre de cada ramo
     */
    @Transactional(readOnly = true)
    public List<ListarParaSelectRamoDTO> obtenerRamosParaSelect() {
        registro.debug("Mostrando los ramos de las polizas");
        return ramoRepositorio.findAll()
                .stream()
                .map(ListarParaSelectRamoDTO::new)
                .toList();
    }
}
