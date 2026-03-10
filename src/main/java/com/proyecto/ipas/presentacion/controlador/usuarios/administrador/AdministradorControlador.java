package com.proyecto.ipas.presentacion.controlador.usuarios.administrador;

import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.usuario.administrador.AdministradorServicio;
import com.proyecto.ipas.negocio.servicio.autenticacion.UsuarioAutenticacionServicio;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.UsuarioActualizarDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Controlador principal del administrador.
 * 
 * Maneja el dashboard administrativo, perfil del administrador y su actualización.
 * Solo accesible por usuarios con rol ADMINISTRADOR.
 */
@Controller
@RequestMapping("/administrador")
public class AdministradorControlador {

    @Autowired
    private UsuarioAutenticacionServicio usuarioAutenticacionServicio;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private AdministradorServicio administradorServicio;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Muestra el dashboard principal del administrador con estadísticas del sistema.
     * 
     * Recupera y muestra:
     * - KPIs generales (usuarios, clientes, pólizas, etc.)
     * - Ranking de asesores por productividad
     * - Actividad reciente del sistema
     * 
     * @param sesion la sesión HTTP actual
     * @param modelo el modelo MVC
     * @return vista del dashboard administrativo
     */
    @GetMapping("/")
    public String dashboard(HttpSession sesion, Model modelo) {

        if (sesion != null) {
            AlertaRespuesta alerta = (AlertaRespuesta) sesion.getAttribute("alertaRespuesta");
            if (alerta != null) {
                modelo.addAttribute("alertaRespuesta", alerta);
                sesion.removeAttribute("alertaRespuesta");
            }
        }

        Map<String, Long> datosDashboardAsesor = administradorServicio.obtenerDatosDashboard();

        datosDashboardAsesor.forEach(modelo::addAttribute);

        modelo.addAttribute("polizasPorAsesor", administradorServicio.obtenerRankingAsesores());
        modelo.addAttribute("actividadReciente", administradorServicio.obtenerActividadReciente());

        return "usuarios/administradores/dashboardPrincipal";
    }

    /**
     * Muestra el perfil del administrador logueado con sus datos actuales.
     * 
     * @param model el modelo MVC
     * @param usuarioAutenticado el usuario autenticado del sistema
     * @return vista del perfil del administrador
     */
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        cargarCorreoPerfil(usuarioSesion, model);

        model.addAttribute("usuarioActualizarDTO",
                usuarioMapper.toUsuarioActualizarDTO(
                        usuarioAutenticacionServicio.verDatosUsuario(usuarioSesion.getIdUsuario())));

        return "usuarios/administradores/perfilAdmin";
    }

    /**
     * Actualiza los datos del perfil del administrador autenticado.
     * 
     * Valida los datos y actualiza correo, teléfono, documento y otros campos.
     * Maneja errores de validación y conflictos de datos (duplicados).
     * 
     * @param usuarioActualizarDTO datos actualizados del usuario
     * @param validacion resultado de la validación
     * @param model el modelo MVC
     * @param usuarioAutenticado el usuario autenticado
     * @param redirectAttributes para pasar atributos en la redirección
     * @return vista del perfil si hay errores, redirección al perfil si éxito
     */
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
            return "usuarios/administradores/perfilAdmin";
        }

        try {
            usuarioAutenticacionServicio.actualizarUsuario(usuarioSesion.getIdUsuario(), usuarioActualizarDTO);

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
            return "usuarios/administradores/perfilAdmin";
        }

        return "redirect:/administrador/perfil";
    }


    private void cargarCorreoPerfil(UsuarioSeguridad usuarioSesion, Model model) {
        model.addAttribute("correo", usuarioAutenticacionServicio.verDatosUsuario(usuarioSesion.getIdUsuario()).correo());
    }
}