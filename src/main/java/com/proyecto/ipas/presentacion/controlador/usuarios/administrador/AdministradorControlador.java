package com.proyecto.ipas.presentacion.controlador;

import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.autenticacion.UsuarioServicio;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.UsuarioActualizarDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrador")
public class AdministradorControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioMapper usuarioMapper;

    /* -------------------------------------------------------
       DASHBOARD
    ------------------------------------------------------- */
    @GetMapping("/")
    public String dashboard(HttpSession sesion, Model model, Authentication usuarioAutenticado) {

        if (sesion != null) {
            AlertaRespuesta alerta = (AlertaRespuesta) sesion.getAttribute("alertaRespuesta");
            if (alerta != null) {
                model.addAttribute("alertaRespuesta", alerta);
                sesion.removeAttribute("alertaRespuesta");
            }
        }

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();
        cargarDatosLayout(usuarioSesion, model);

        model.addAttribute("activePage", "dashboard");
        model.addAttribute("pageTitle", "Dashboard");

        return "usuarios/administradores/inicio";
    }

    /* -------------------------------------------------------
       PERFIL — GET
    ------------------------------------------------------- */
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        cargarDatosLayout(usuarioSesion, model);
        cargarCorreoPerfil(usuarioSesion, model);

        model.addAttribute("activePage", "perfil");
        model.addAttribute("pageTitle", "Mi Perfil");
        model.addAttribute("usuarioActualizarDTO",
                usuarioMapper.toUsuarioActualizarDTO(
                        usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario())));

        return "usuarios/perfilAdmin";
    }

    /* -------------------------------------------------------
       PERFIL — POST (actualizar)
    ------------------------------------------------------- */
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @Valid @ModelAttribute("usuarioActualizarDTO") UsuarioActualizarDTO usuarioActualizarDTO,
            BindingResult validacion,
            Model model,
            Authentication usuarioAutenticado,
            RedirectAttributes redirectAttributes) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        if (validacion.hasErrors()) {
            cargarDatosLayout(usuarioSesion, model);
            cargarCorreoPerfil(usuarioSesion, model);
            model.addAttribute("activePage", "perfil");
            model.addAttribute("pageTitle", "Mi Perfil");
            return "usuarios/perfilAdmin";
        }

        try {
            usuarioServicio.actualizarUsuario(usuarioSesion.getIdUsuario(), usuarioActualizarDTO);

            redirectAttributes.addFlashAttribute("alertaRespuesta", new AlertaRespuesta(
                    HttpStatus.OK.value(),
                    TipoAlerta.EXITO,
                    "Actualización exitosa",
                    "Datos actualizados con éxito",
                    "USUARIO_ACTUALIZADO"
            ));

        } catch (NegocioExcepcion ex) {
            redirectAttributes.addFlashAttribute("alertaRespuesta", new AlertaRespuesta(
                    HttpStatus.BAD_REQUEST.value(),
                    TipoAlerta.ERROR,
                    "Error al actualizar",
                    ex.getMessage(),
                    "ERROR_ACTUALIZACION"
            ));

        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error ->
                    validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje()));

            cargarDatosLayout(usuarioSesion, model);
            cargarCorreoPerfil(usuarioSesion, model);
            model.addAttribute("activePage", "perfil");
            model.addAttribute("pageTitle", "Mi Perfil");
            return "usuarios/perfilAdmin";
        }

        return "redirect:/administrador/perfil";
    }

    /* -------------------------------------------------------
       UTILIDADES PRIVADAS
    ------------------------------------------------------- */
    private void cargarDatosLayout(UsuarioSeguridad usuarioSesion, Model model) {
        var datos = usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario());
        model.addAttribute("nombreAdmin", datos.nombre() + " " + datos.apellido());
        model.addAttribute("correoAdmin", datos.correo());
        model.addAttribute("rolUsuario", "Administrador");
    }

    private void cargarCorreoPerfil(UsuarioSeguridad usuarioSesion, Model model) {
        model.addAttribute("correo", usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario()).correo());
    }
}