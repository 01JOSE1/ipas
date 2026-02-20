package com.proyecto.ipas.negocio.servicio.poliza;

import com.proyecto.ipas.datos.entidad.*;
import com.proyecto.ipas.datos.mapeador.PolizaMapper;
import com.proyecto.ipas.datos.repositorio.*;
import com.proyecto.ipas.infraestructura.externo.almacenamiento.ArchivoAlmacenamientoServicio;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import com.proyecto.ipas.negocio.dominio.modelo.Poliza;
import com.proyecto.ipas.presentacion.excepcion.ArchivoInvalidoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.RecursoNOEncontradoException;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.RespuestaClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.RespuestaPolizaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolizaServicio {

    private static final Logger registro = LoggerFactory.getLogger(PolizaServicio.class);

    PolizaRepositorio polizaRepositorio;
    ClienteRepositorio clienteRepositorio;
    UsuarioRepositorio usuarioRepositorio;
    RamoRepositorio ramoRepositorio;
    AseguradoraRepositorio aseguradoraRepositorio;

    PolizaMapper polizaMapper;

    ArchivoAlmacenamientoServicio archivoAlmacenamientoServicio;
    private final PdfPolizaServicio pdfPolizaServicio;

    private final JdbcTemplate jdbcTemplate;

    public PolizaServicio(JdbcTemplate jdbcTemplate, PolizaRepositorio polizaRepositorio, ClienteRepositorio clienteRepositorio, UsuarioRepositorio usuarioRepositorio, RamoRepositorio ramoRepositorio, AseguradoraRepositorio aseguradoraRepositorio, PolizaMapper polizaMapper, ArchivoAlmacenamientoServicio archivoAlmacenamientoServicio, PdfPolizaServicio pdfPolizaServicio, PdfPolizaServicio pdfPolizaServicio1) {
        this.jdbcTemplate = jdbcTemplate;
        this.polizaRepositorio = polizaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.ramoRepositorio = ramoRepositorio;
        this.aseguradoraRepositorio = aseguradoraRepositorio;
        this.polizaMapper = polizaMapper;
        this.archivoAlmacenamientoServicio = archivoAlmacenamientoServicio;
        this.pdfPolizaServicio = pdfPolizaServicio1;
    }

    @Transactional(readOnly = true)
    public Page<RespuestaPolizaDTO> obtenerPolizasPaginados(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad);

        return polizaRepositorio.findAll(pageable).map(RespuestaPolizaDTO::new);
    }


    @Transactional(readOnly = true)
    public List<RespuestaPolizaDTO> obtenerPolizasCliente(Long idCliente) {
        registro.debug("Mostrando los registros de polizas del cliente: " + idCliente);
        return polizaRepositorio.findAllByCliente_IdCliente(idCliente).stream().map(RespuestaPolizaDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource obtenerArchivo (String nombreArchivo) {
        if (!polizaRepositorio.existsByNumeroPdf(nombreArchivo)) {
            throw new RecursoNOEncontradoException("Archivo pdf", "numero", nombreArchivo);
        }
        return archivoAlmacenamientoServicio.cargarRecurso(nombreArchivo);
    }

    @Transactional
    public void procesarDatosPdfConIa(MultipartFile archivo) throws IOException {

        try {
            registro.debug("Preparando documento pdf de la poliza a texto para el prompt: {}", archivo.getOriginalFilename());
            String texto = pdfPolizaServicio.prepararParaIa(archivo);

            System.out.println(texto);
        } catch (IOException ex) {
            throw new ArchivoInvalidoExcepcion(ex.getMessage());
        }
    }



    @Transactional
    public RespuestaPolizaDTO registrarPoliza(GestionPolizaDTO gestionPolizaDTO, Long idUsuario) {
        registro.debug("Registrando poliza con codigo: {}", gestionPolizaDTO.getCodigoPoliza());

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();


        if (polizaRepositorio.existsByCodigoPoliza(gestionPolizaDTO.getCodigoPoliza())) {
            errores.add(new ConflictoExcepcion.ErrorCampo("codigoPoliza", "El codigo de poliza ya existe"));
        }

        if ((gestionPolizaDTO.getPlaca() != null && !gestionPolizaDTO.getPlaca().isBlank()) && polizaRepositorio.existsByPlacaAndCliente_IdClienteNotAndEstado(gestionPolizaDTO.getPlaca(), gestionPolizaDTO.getIdCliente(), EstadoPoliza.VIGENTE)) {
            System.out.println(" SE ENCONTRO UNA COINCIDENCIA EN LA PLACA  " + gestionPolizaDTO.getPlaca());
            errores.add(new ConflictoExcepcion.ErrorCampo("placa", "La placa se encuentra activa en otro cliente"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos registrados", errores);
        }

        ClienteEntidad clienteEntidad = clienteRepositorio.findById(gestionPolizaDTO.getIdCliente()).orElseThrow(() -> new RecursoNOEncontradoException("Cliente", "id", gestionPolizaDTO.getIdCliente()));

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RecursoNOEncontradoException("usuario", "id", idUsuario));

        RamoEntidad ramoEntidad = ramoRepositorio.findById(gestionPolizaDTO.getIdRamo()).orElseThrow(() -> new RecursoNOEncontradoException("ramo", "id", gestionPolizaDTO.getIdRamo()));

        AseguradoraEntidad aseguradoraEntidad = aseguradoraRepositorio.findById(gestionPolizaDTO.getIdAseguradora()).orElseThrow(() -> new RecursoNOEncontradoException("aseguradora", "id", gestionPolizaDTO.getIdAseguradora()));

        try {
            Poliza poliza = polizaMapper.toPoliza(gestionPolizaDTO, ramoEntidad, aseguradoraEntidad);

            PolizaEntidad polizaEntidad = polizaMapper.toPolizaEntidad(poliza);
            polizaEntidad.setCliente(clienteEntidad);
            polizaEntidad.setUsuario(usuarioEntidad);
            polizaEntidad.setRamo(ramoEntidad);
            polizaEntidad.setAseguradora(aseguradoraEntidad);

            registro.debug("Almacenando archivo pdf para la poliza : {}", polizaEntidad.getIdPoliza());
            String nombreArchivo = archivoAlmacenamientoServicio.almacenar(gestionPolizaDTO.getArchivoPoliza(), polizaEntidad.getCodigoPoliza());

            polizaEntidad.setNumeroPdf(nombreArchivo);

            registro.debug("Archivo pdf almacenado con exito : {}", polizaEntidad.getNumeroPdf());

            PolizaEntidad polizaGuardada = polizaRepositorio.save(polizaEntidad);

            registro.debug("Poliza guardada: {}", polizaGuardada.getIdPoliza());

            return polizaMapper.toRespuestaPoliza(polizaGuardada);
        } catch (DataIntegrityViolationException ex) {
            registro.error("Error de integridad al crear poliza: {}", ex.getMessage());
            throw new NegocioExcepcion("No se pudo crear la poliza. Intenta nuevamente", "CREACION_POLIZA_FALLIDA");
        }
    }

    @Transactional(readOnly = true)
    public GestionPolizaDTO obtenerPoliza(Long idPoliza) {
        registro.debug("Obtener poliza con ID: {}", idPoliza);

        GestionPolizaDTO gestionPolizaDTO = polizaRepositorio.buscarDatosPoliza(idPoliza).orElseThrow( () -> new RecursoNOEncontradoException("poliza", "id", idPoliza) );

        return gestionPolizaDTO;
    }

    @Transactional
    public void actualizarPoliza(Long idPoliza, GestionPolizaDTO gestionPolizaDTO, Long idUsuarioActual) {
        registro.debug("Actualizando poliza con numero de documento: {}", gestionPolizaDTO.getCodigoPoliza());

        PolizaEntidad polizaEntidad = polizaRepositorio.findById(idPoliza).orElseThrow(() -> new RecursoNOEncontradoException("Poliza", "id", idPoliza));

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();

        if (polizaRepositorio.existsByCodigoPolizaAndIdPolizaNot(gestionPolizaDTO.getCodigoPoliza(), idPoliza)) {
            errores.add( new ConflictoExcepcion.ErrorCampo("codigoPoliza", "El codigo de poliza ya se encuentra registrado"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos registrados", errores);
        }

        if (gestionPolizaDTO.getArchivoPoliza() != null && !gestionPolizaDTO.getArchivoPoliza().isEmpty() && polizaEntidad.getNumeroPdf() != null) {
            registro.debug("Actualizando archivo pdf para la poliza : {}", polizaEntidad.getIdPoliza());
            String nombreArchivo = archivoAlmacenamientoServicio.almacenar(gestionPolizaDTO.getArchivoPoliza(), polizaEntidad.getCodigoPoliza());
            polizaEntidad.setNumeroPdf(nombreArchivo);
            registro.debug("Archivo pdf actualizado con exito : {}", polizaEntidad.getNumeroPdf());
        }

        if (gestionPolizaDTO.getIdCliente() != null) {
            ClienteEntidad clienteEntidad = clienteRepositorio.findById(gestionPolizaDTO.getIdCliente()).orElseThrow( () -> new RecursoNOEncontradoException("cliente", "id", gestionPolizaDTO.getIdCliente()));

            polizaEntidad.setCliente(clienteEntidad);
        }

        if (gestionPolizaDTO.getIdRamo() != null) {
            RamoEntidad ramoEntidad = ramoRepositorio.findById(gestionPolizaDTO.getIdRamo()).orElseThrow( () -> new RecursoNOEncontradoException("ramo", "id", gestionPolizaDTO.getIdRamo()));

            polizaEntidad.setRamo(ramoEntidad);
        }

        if (gestionPolizaDTO.getIdAseguradora() != null) {
            AseguradoraEntidad aseguradoraEntidad = aseguradoraRepositorio.findById(gestionPolizaDTO.getIdAseguradora()).orElseThrow( () -> new RecursoNOEncontradoException("aseguradora", "id", gestionPolizaDTO.getIdAseguradora()));

            polizaEntidad.setAseguradora(aseguradoraEntidad);
        }

        try {
            polizaMapper.toPoliza(polizaEntidad);

            gestionPolizaDTO.actualizarPoliza(polizaEntidad);

            jdbcTemplate.update("SET @usuario_actual = ?", idUsuarioActual);

            polizaRepositorio.saveAndFlush(polizaEntidad);

            registro.info("Poliza actualizada exitosamente con ID: {}", polizaEntidad.getCodigoPoliza());

        } catch (DataIntegrityViolationException ex) {
            registro.error("Error de integridad al actualizar poliza: {}", ex.getMessage());
            throw new NegocioExcepcion("No se pudo actualizar la poliza. Intenta nuevamente", "ACTUALIZACION_POLIZA_FALLIDA");
        }
    }

}