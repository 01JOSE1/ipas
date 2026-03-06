package com.proyecto.ipas.presentacion.controlador.usuarios.asesor;

import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.asesor.AsesorServicio;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/asesor")
public class AsesorControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private AsesorServicio asesorServicio;

    /* -------------------------------------------------------
       DASHBOARD
    ------------------------------------------------------- */
    @GetMapping("/")
    public String dashboard(HttpSession sesion, Model modelo, Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        if (sesion != null) {
            AlertaRespuesta alerta = (AlertaRespuesta) sesion.getAttribute("alertaRespuesta");
            if (alerta != null) {
                modelo.addAttribute("alertaRespuesta", alerta);
                sesion.removeAttribute("alertaRespuesta");
            }
        }

        Map<String, Long> datosDashboardAsesor = asesorServicio.obtenerDatosParaDashboard(usuarioSesion.getIdUsuario());

        datosDashboardAsesor.forEach(modelo::addAttribute);

        return "usuarios/asesores/dashboardPrincipal";
    }

    /* -------------------------------------------------------
       PERFIL — GET
    ------------------------------------------------------- */
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        cargarCorreoPerfil(usuarioSesion, model);

        model.addAttribute("usuarioActualizarDTO",
                usuarioMapper.toUsuarioActualizarDTO(
                        usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario())));

        return "usuarios/asesores/perfilAsesor";
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
            cargarCorreoPerfil(usuarioSesion, model);
            return "usuarios/perfilAsesor";
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

            cargarCorreoPerfil(usuarioSesion, model);
            return "usuarios/asesores/perfilAsesor";
        }

        return "redirect:/asesor/perfil";
    }

    /**
     * Carga el correo en el modelo para mostrarlo deshabilitado en el formulario de perfil.
     */
    private void cargarCorreoPerfil(UsuarioSeguridad usuarioSesion, Model model) {
        model.addAttribute("correo", usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario()).correo());
    }
}