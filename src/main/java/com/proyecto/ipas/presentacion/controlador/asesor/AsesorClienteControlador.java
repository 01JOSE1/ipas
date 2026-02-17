package com.proyecto.ipas.presentacion.controlador.asesor;

import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.EnActualizacion;
import com.proyecto.ipas.infraestructura.utilidades.EnCreacion;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.cliente.ClienteServicio;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.RespuestaClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/asesor")
public class AsesorClienteControlador {

    @Autowired
    private ClienteServicio clienteServicio;

    @GetMapping("ver-clientes")
    public String verClientes(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int cantidad,
            Model modelo
    ) {

        Page<RespuestaClienteDTO> paginaClientes;

        paginaClientes = clienteServicio.obtenerClientesPaginados(pagina, cantidad);

        modelo.addAttribute("clientes", paginaClientes.getContent());
        modelo.addAttribute("paginaActual", pagina);
        modelo.addAttribute("totalPaginas", paginaClientes.getTotalPages());
        modelo.addAttribute("totalRegistros",  paginaClientes.getTotalElements());
        modelo.addAttribute("catidadPagina", cantidad);

        return "clientes/listaClientes";
    }

    @GetMapping("/buscar-cliente")
    @ResponseBody
    public List<BusquedaClienteDTO> buscarClientes(@RequestParam String termino) {
        return clienteServicio.buscarClientes(termino);
    }


    @GetMapping("gestion-cliente/{idCliente}")
    public String funcionesCliente( Model modelo, @PathVariable Long idCliente) {
        GestionClienteDTO gestionClienteDTO = clienteServicio.obtenerCliente(idCliente);
        modelo.addAttribute("gestionClienteDTO", gestionClienteDTO);
        return "clientes/funcionesCliente";
    }

    @GetMapping("registro-cliente")
    public String verRegistroCliente(GestionClienteDTO gestionClienteDTO, Model modelo) {
        modelo.addAttribute("modo", "CREAR");

        modelo.addAttribute("gestionClienteDTO", gestionClienteDTO);

        return "clientes/registroFormulario";

    }


    @PostMapping("registro-cliente")
    public String registrarCliente(@Validated({Default.class, EnCreacion.class}) @ModelAttribute("gestionClienteDTO") GestionClienteDTO gestionClienteDTO,
                                   BindingResult validacion,
                                   Model modelo,
                                   RedirectAttributes redirectAttributes,
                                   Authentication usuarioAutenticado
    ) {

        UsuarioSeguridad sesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        if (validacion.hasErrors()) {
            modelo.addAttribute("modo", "CREAR");
            return "clientes/registroFormulario";

        }

        try {
            RespuestaClienteDTO respuestaClienteDTO = clienteServicio.registrarCliente(sesion.getIdUsuario(), gestionClienteDTO);

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.CREATED.value(),
                    TipoAlerta.EXITO,
                    "Recurso fue creado con exito",
                    "Cliente " + respuestaClienteDTO.nombre() + " creado con exito",
                    "CLIENTE_CREADO"
            );

            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);

        } catch (NegocioExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage());
            modelo.addAttribute("alertaRespuesta", alertaRespuesta);
            modelo.addAttribute("modo", "CREAR");
            return "clientes/registroFormulario";
        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            modelo.addAttribute("modo", "CREAR");
            return "clientes/registroFormulario";
        }

        return "redirect:/asesor/ver-clientes";
    }


    @GetMapping("actualizacion-cliente")
    public String verDatosCliente(Model modelo, @RequestParam Long idCliente) {

        GestionClienteDTO gestionClienteDTO = clienteServicio.obtenerCliente(idCliente);

        modelo.addAttribute("modo", "EDITAR");

        modelo.addAttribute("gestionClienteDTO", gestionClienteDTO);

        return "clientes/registroFormulario";

    }

    @PostMapping("actualizacion-cliente/{idCliente}")
    public String actualizarCliente(@Validated({Default.class, EnActualizacion.class}) @ModelAttribute("gestionClienteDTO") GestionClienteDTO gestionClienteDTO,
                                    BindingResult validacion,
                                    Model modelo,
                                    RedirectAttributes redirectAttributes,
                                    @PathVariable Long idCliente,
                                    Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();


        if (validacion.hasErrors()) {
            modelo.addAttribute("modo", "EDITAR");
            return "clientes/registroFormulario";
        }

        try {
            clienteServicio.actualizarCliente(idCliente, gestionClienteDTO, usuarioSesion.getIdUsuario());

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.OK.value(),
                    TipoAlerta.EXITO,
                    "Recurso fue actualizado con exito",
                    "Datos actualizados con exito",
                    "CLIENTE_ACTUALIZADO"
            );

            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);

        } catch (NegocioExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.BAD_REQUEST.value(),
                    TipoAlerta.ERROR,
                    "Error al actualizar",
                    ex.getMessage(),
                    "ERROR_ACTUALIZACION"
            );
            modelo.addAttribute("alertaRespuesta", alertaRespuesta);
            modelo.addAttribute("modo", "EDITAR");
            return "clientes/registroFormulario";
        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            modelo.addAttribute("modo", "EDITAR");
            return "clientes/registroFormulario";
        }

        return "redirect:/asesor/ver-clientes";

    }
}
