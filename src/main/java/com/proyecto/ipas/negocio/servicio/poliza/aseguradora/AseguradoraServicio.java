package com.proyecto.ipas.negocio.servicio.poliza.aseguradora;

import com.proyecto.ipas.datos.repositorio.AseguradoraRepositorio;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.aseguradora.ListarParaSelectAseguradoraDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AseguradoraServicio {

    private static final Logger registro = LoggerFactory.getLogger (AseguradoraServicio.class);

    AseguradoraRepositorio aseguradoraRepositorio;

    public AseguradoraServicio(AseguradoraRepositorio aseguradoraRepositorio) {
        this.aseguradoraRepositorio = aseguradoraRepositorio;
    }

    /**
     * Obtiene la lista completa de aseguradoras disponibles en el sistema,
     * formateada para ser utilizada en elementos select o listas desplegables.
     *
     * @return lista de ListarParaSelectAseguradoraDTO con el ID y nombre de cada aseguradora
     */
    @Transactional(readOnly = true)
    public List<ListarParaSelectAseguradoraDTO> obtenerAseguradorasParaSelect() {
        registro.debug("Mostrando las aseguradoras");
        return aseguradoraRepositorio.findAll()
                .stream()
                .map(ListarParaSelectAseguradoraDTO::new)
                .toList();
    }
}
