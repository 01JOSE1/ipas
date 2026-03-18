package com.proyecto.ipas.presentacion.controlador.usuarios.asesor;

import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.infraestructura.externo.almacenamiento.ArchivoAlmacenamientoServicio;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.usuario.asesor.AsesorServicio;
import com.proyecto.ipas.negocio.servicio.autenticacion.UsuarioAutenticacionServicio;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.UsuarioActualizarDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Controlador principal del asesor.
 * 
 * Maneja el dashboard del asesor, perfil y su actualización.
 * Solo accesible por usuarios con rol ASESOR.
 */
@Controller
@RequestMapping("/asesor")
public class AsesorControlador {

    @Autowired
    private UsuarioAutenticacionServicio usuarioAutenticacionServicio;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private AsesorServicio asesorServicio;

    @Autowired
    ArchivoAlmacenamientoServicio archivoAlmacenamientoServicio;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Muestra el dashboard principal del asesor con sus estadísticas personales.
     * 
     * Recupera y muestra:
     * - Cantidad de clientes asignados
     * - Pólizas activas y vencidas
     * - Gestiones realizadas en el mes
     * - Actividad reciente
     * 
     * @param sesion la sesión HTTP actual
     * @param modelo el modelo MVC
     * @param usuarioAutenticado el asesor logueado
     * @return vista del dashboard del asesor
     */
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

    /**
     * Muestra el perfil del asesor logueado con sus datos actuales.
     * 
     * @param model el modelo MVC
     * @param usuarioAutenticado el usuario autenticado del sistema
     * @return vista del perfil del asesor
     */
    @GetMapping("/perfil")
    public String perfil(Model model, Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        cargarCorreoPerfil(usuarioSesion, model);

        model.addAttribute("usuarioActualizarDTO",
                usuarioMapper.toUsuarioActualizarDTO(
                        usuarioAutenticacionServicio.verDatosUsuario(usuarioSesion.getIdUsuario())));

        return "usuarios/asesores/perfilAsesor";
    }

    /**
     * Actualiza los datos del perfil del asesor autenticado.
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
            return "usuarios/asesores/perfilAsesor";
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
            return "usuarios/asesores/perfilAsesor";
        }

        return "redirect:/asesor/perfil";
    }

    /**
     * Carga el correo en el modelo para mostrarlo deshabilitado en el formulario de perfil.
     */
    private void cargarCorreoPerfil(UsuarioSeguridad usuarioSesion, Model model) {
        model.addAttribute("correo", usuarioAutenticacionServicio.verDatosUsuario(usuarioSesion.getIdUsuario()).correo());
    }

    @GetMapping("/ayuda")
    public String ayuda(Model model) {

        return "usuarios/asesores/ayuda";
    }

    @GetMapping("/ver-manual")
    public ResponseEntity<Resource> ayuda() {

        final Path rutaManual = Paths.get("manuales");

        Resource recurso = archivoAlmacenamientoServicio.cargarRecurso("Manual_de_asesor.pdf", rutaManual);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf") // Le dice al navegador que es un PDF
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }


}