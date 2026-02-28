package com.proyecto.ipas.presentacion.controlador.asesor;

import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.EnCreacion;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.aseguradora.AseguradoraServicio;
import com.proyecto.ipas.negocio.servicio.cliente.ClienteServicio;
import com.proyecto.ipas.negocio.servicio.poliza.PolizaServicio;
import com.proyecto.ipas.negocio.servicio.ramo.RamoServicio;
import com.proyecto.ipas.presentacion.excepcion.ArchivoInvalidoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.ValidacionDatosExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.RespuestaPolizaDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/asesor")
public class AsesorPolizaControlador {

    @Autowired
    private PolizaServicio polizaServicio;

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private RamoServicio ramoServicio;

    @Autowired
    private AseguradoraServicio aseguradoraServicio;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("ver-archivo/{nombreArchivo}")
    public ResponseEntity<Resource> verArchivo(@PathVariable String nombreArchivo) {
        // 1. Cargamos el archivo como un recurso de Spring
        Resource recurso = polizaServicio.obtenerArchivo(nombreArchivo);

        // 2. Retornamos el archivo con las cabeceras correctas
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf") // Le dice al navegador que es un PDF
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"") // "inline" abre en el navegador, "attachment" descarga
                .body(recurso);
    }

    @GetMapping("ver-archivo-temporal/{nombreArchivo}")
    public ResponseEntity<Resource> verArchivoTemporal(@PathVariable String nombreArchivo) {
        // 1. Cargamos el archivo temporal como un recurso de Spring
        Resource recurso = polizaServicio.obtenerArchivoTemporal(nombreArchivo);

        // 2. Retornamos el archivo con las cabeceras correctas
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf") // Le dice al navegador que es un PDF
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"") // "inline" abre en el navegador, "attachment" descarga
                .body(recurso);
    }

    @GetMapping("ver-polizas")
    public String verPolizas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int cantidad,
            Model modelo
    ) {
        Page<RespuestaPolizaDTO> paginaPolizas;

        paginaPolizas = polizaServicio.obtenerPolizasPaginados(pagina, cantidad);

        modelo.addAttribute("polizas", paginaPolizas.getContent());
        modelo.addAttribute("paginaActual", pagina);
        modelo.addAttribute("totalPaginas", paginaPolizas.getTotalPages());
        modelo.addAttribute("totalRegistros",  paginaPolizas.getTotalElements());
        modelo.addAttribute("catidadPagina", cantidad);

        return "polizas/listaPolizas";
    }

    @GetMapping("ver-polizas/{idCliente}")
    public String verPolizasCliente(Model modelo, @PathVariable Long idCliente) {

        List<RespuestaPolizaDTO> polizas = polizaServicio.obtenerPolizasCliente(idCliente);

        modelo.addAttribute("polizas", polizas);

        return "polizas/listaPolizasCliente";
    }

    public void alistarFormulario (Long idCliente, Model modelo, GestionPolizaDTO gestionPolizaDTO) {
        if (idCliente != null) {
            gestionPolizaDTO.setIdCliente(idCliente);
            GestionClienteDTO gestionClienteDTO = clienteServicio.obtenerCliente(idCliente);

            gestionPolizaDTO.setNombreCliente(gestionClienteDTO.getNombre() + " " + (gestionClienteDTO.getApellido() != null ? " " + gestionClienteDTO.getApellido() : ""));
        }

        modelo.addAttribute("gestionPolizaDTO", gestionPolizaDTO);

        modelo.addAttribute("ramos", ramoServicio.obtenerRamosParaSelect());
        modelo.addAttribute("aseguradoras", aseguradoraServicio.obtenerAseguradorasParaSelect());
    }

    @GetMapping({"cargar-pdf-poliza", "cargar-pdf-poliza/{idCliente}"})
    public String verFormularioPdf(@PathVariable(required = false) Long idCliente, Model modelo) {
        if (idCliente != null) {
            modelo.addAttribute("idCliente", idCliente);
        }
        return "polizas/subirPdf";
    }


    @PostMapping({"generar-datos-ia", "generar-datos-ia/{idCliente}"})
    public String generarDatosConIa(@RequestParam("archivo") MultipartFile archivo,
                                    @PathVariable(required = false) Long idCliente,
                                    HttpSession session,
                                    Model modelo,
                                    RedirectAttributes redirectAttributes) {

        try {

            GestionPolizaDTO gestionPolizaDTO = polizaServicio.procesarDatosPdfConIa(archivo, idCliente);

            if (gestionPolizaDTO.isClienteExiste()) {

                // Guardar la póliza en sesión para recuperarla después y romper el flujo
                gestionPolizaDTO.setArchivoPoliza(null); // No serializar el MultipartFile
                session.setAttribute("polizaPendiente", gestionPolizaDTO);

                redirectAttributes.addFlashAttribute("gestionClienteDTO", gestionPolizaDTO.getGestionClienteDTO());

                redirectAttributes.addFlashAttribute("alertaRespuesta",
                        new AlertaRespuesta(HttpStatus.OK.value(), TipoAlerta.ADVERTENCIA,
                                "El cliente no existe, créalo primero para continuar con la póliza",
                                "El cliente no existe, créalo primero para continuar con la póliza",
                                "CLIENTE_REQUERIDO"));

                return "redirect:/asesor/registro-cliente";
            }

            redirectAttributes.addFlashAttribute("gestionPolizaDTO", gestionPolizaDTO);

            redirectAttributes.addFlashAttribute("alertaRespuesta",
                    new AlertaRespuesta(HttpStatus.OK.value(), TipoAlerta.EXITO,
                            "Datos generados correctamente con IA",
                            "Datos generados correctamente con IA",
                            "DATOS_GENERADOS"));

            return "redirect:/asesor/registro-poliza";

        } catch (IOException | ArchivoInvalidoExcepcion ex) {
            modelo.addAttribute("alertaRespuesta",
                    new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage()));
            return "polizas/subirPdf";
        }
    }


    @GetMapping("registro-poliza")
    public String verRegistroPoliza(GestionPolizaDTO gestionPolizaDTO, Model modelo) {
        modelo.addAttribute("modo", "CREAR");

        GestionClienteDTO gestionClienteDTO = new GestionClienteDTO();

        alistarFormulario(gestionClienteDTO.getIdCliente(), modelo, gestionPolizaDTO);

        return "polizas/registroFormulario";
    }

    @GetMapping("registro-poliza/{idCliente}")
    public String verRegistroPoliza1(GestionPolizaDTO gestionPolizaDTO, Model modelo, @PathVariable Long idCliente) {
        modelo.addAttribute("modo", "CREAR");

        alistarFormulario(idCliente, modelo, gestionPolizaDTO);

        return "polizas/registroFormulario";
    }

    @PostMapping("registro-poliza/{idCliente}")
    public String registrarCliente(@Validated({Default.class, EnCreacion.class}) @ModelAttribute("gestionPolizaDTO") GestionPolizaDTO gestionPolizaDTO,
                                   BindingResult validacion,
                                   @PathVariable Long idCliente,
                                   Model modelo,
                                   RedirectAttributes redirectAttributes,
                                   Authentication usuarioAutenticado
    ) {
        UsuarioSeguridad sesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        if (validacion.hasErrors()) {
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(idCliente, modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        }

        try {
            RespuestaPolizaDTO respuestaPolizaDTO = polizaServicio.registrarPoliza(gestionPolizaDTO, sesion.getIdUsuario());

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.CREATED.value(),
                    TipoAlerta.EXITO,
                    "Recurso fue creado con exito",
                    "Poliza " + respuestaPolizaDTO.codigoPoliza() + " creada con exito",
                    "POLIZA_CREADA"
            );

            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);
        } catch  (NegocioExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage());
            modelo.addAttribute("alertaRespuesta", alertaRespuesta);
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        } catch (ArchivoInvalidoExcepcion ex) {
            validacion.rejectValue("archivoPoliza", ex.getErrorCodigo(), ex.getMessage());
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        } catch (ValidacionDatosExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);

            return "polizas/registroFormulario";
        }

        return "redirect:/asesor/ver-polizas/" + gestionPolizaDTO.getIdCliente();
    }

    @GetMapping("actualizacion-poliza/{idPoliza}")
    public String verDatosCliente(Model modelo, @PathVariable Long idPoliza) {
        GestionPolizaDTO gestionPolizaDTO = polizaServicio.obtenerPoliza(idPoliza);
        modelo.addAttribute("modo", "EDITAR");
        alistarFormulario(null, modelo, gestionPolizaDTO);
        return "polizas/registroFormulario";
    }

    @PostMapping("actualizacion-poliza/{idPoliza}")
    public String actualizarPoliza(@Valid @ModelAttribute("gestionPolizaDTO") GestionPolizaDTO gestionPolizaDTO,
                                    BindingResult validacion,
                                    Model modelo,
                                    RedirectAttributes redirectAttributes,
                                    @PathVariable Long idPoliza,
                                    Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        if (validacion.hasErrors()) {
            modelo.addAttribute("modo", "EDITAR");
            alistarFormulario(null, modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        }

        try {
            polizaServicio.actualizarPoliza(idPoliza, gestionPolizaDTO, usuarioSesion.getIdUsuario());

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.OK.value(),
                    TipoAlerta.EXITO,
                    "Recurso fue actualizado con exito",
                    "Datos actualizados con exito",
                    "POLIZA_ACTUALIZADA"
            );

            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);
        } catch  (NegocioExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage());
            modelo.addAttribute("alertaRespuesta", alertaRespuesta);
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        } catch (ArchivoInvalidoExcepcion ex) {
            validacion.rejectValue("archivoPoliza", ex.getErrorCodigo(), ex.getMessage());
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);
            return "polizas/registroFormulario";
        } catch (ValidacionDatosExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            modelo.addAttribute("modo", "CREAR");
            alistarFormulario(gestionPolizaDTO.getIdCliente(), modelo, gestionPolizaDTO);

            return "polizas/registroFormulario";
        }

        return "redirect:/asesor/ver-polizas/" + gestionPolizaDTO.getIdCliente();
    }

}
