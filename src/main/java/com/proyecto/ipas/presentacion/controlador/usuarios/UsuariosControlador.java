package com.proyecto.ipas.presentacion.controlador.usuarios;

import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.autenticacion.UsuarioAutenticacionServicio;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.RegistroDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.RespuestaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de gestión de usuarios y autenticación.
 * 
 * Maneja registro, login y redireccionamiento de usuarios según su rol
 * (administrador o asesor). Todas las operaciones son MVC (retornan vistas).
 */
@Controller
@RequestMapping("/usuarios")
public class UsuariosControlador {

    @Autowired
    private UsuarioAutenticacionServicio usuarioAutenticacionServicio;

    @Autowired
    private UsuarioMapper usuarioMapper;

    /**
     * Redirige a la página de login/registro.
     * 
     * @return redirección a /usuarios/login
     */
    @GetMapping("/registro")
    public String redirigirRegistro() {
        return "redirect:/usuarios/login";
    }

    /**
     * Redirige a la página de login/registro.
     * 
     * @return redirección a /usuarios/login
     */
    @GetMapping("/")
    public String redirigir() {
        return "redirect:/usuarios/login";
    }

    /**
     * Muestra el formulario de login/registro con redirección automática si ya está autenticado.
     * 
     * Si el usuario está autenticado, lo redirige a su dashboard correspondiente
     * (administrador o asesor). En caso contrario, muestra el formulario.
     * Recupera alertas de sesiones previas para mostrar al usuario.
     * 
     * @param peticion la petición HTTP actual
     * @param model modelo para pasar datos a la vista
     * @param usuarioAutenticado el usuario autenticado (si existe)
     * @return nombre de la vista o redirección al dashboard
     */
    @GetMapping("/login")
    public String verLoginRegistroFormulario(HttpServletRequest peticion, Model model, Authentication usuarioAutenticado) {

        HttpSession sesion = peticion.getSession(false);

        if (sesion != null) {
            AlertaRespuesta alerta = (AlertaRespuesta) sesion.getAttribute("alertaRespuesta");
            if (alerta != null) {
                model.addAttribute("alertaRespuesta", alerta);
                sesion.removeAttribute("alertaRespuesta");
            }
        }

        if (usuarioAutenticado != null && usuarioAutenticado.isAuthenticated()) {
            var rol = usuarioAutenticado.getAuthorities().stream()
                    .map(r -> r.getAuthority())
                    .toList();

            if (rol.contains("ROLE_ADMINISTRADOR")) {
                return "redirect:/administrador/";
            } else if (rol.contains("ROLE_ASESOR")) {
                return "redirect:/asesor/";
            }
        }

        model.addAttribute("registroDTO", new RegistroDTO());
        return "usuarios/loginRegistroFormulario";
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Valida los datos del registro mediante DTO. Si hay conflictos de datos
     * (correo, teléfono o documento duplicados), retorna los errores al formulario.
     * En caso de éxito, redirige al login con mensaje de confirmación.
     * 
     * @param registroDTO datos del usuario a registrar (validaciones incluidas)
     * @param validacion resultado de la validación
     * @param modelo el modelo MVC
     * @param redirectAttributes para pasar atributos en la redirección
     * @return vista del formulario si hay errores, redirección a login si éxito
     */
    @PostMapping("/registro")
    public String createUser(
            @Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
            BindingResult validacion,
            Model modelo,
            RedirectAttributes redirectAttributes) {

        if (validacion.hasErrors()) {
            return "usuarios/loginRegistroFormulario";
        }

        try {
            RespuestaDTO respuestaDTO = usuarioAutenticacionServicio.crearUsuario(registroDTO);

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.CREATED.value(),
                    TipoAlerta.EXITO,
                    "Recurso fue creado con exito",
                    "Usuario " + respuestaDTO.nombre() + " Creado con exito",
                    "USUARIO_CREADO"
            );
            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);

        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });
            return "usuarios/loginRegistroFormulario";
        } catch (NegocioExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage());
            modelo.addAttribute("alertaRespuesta", alertaRespuesta);
            return "usuarios/loginRegistroFormulario";
        }

        return "redirect:/usuarios/login";
    }

    /**
     * Redirige al perfil del rol correspondiente.
     * 
     * Dependiendo del rol del usuario autenticado (ADMINISTRADOR o ASESOR),
     * redirige a su controlador específico. Cada controlador maneja su propia
     * vista y lógica de perfil.
     * 
     * @param usuarioAutenticado el usuario autenticado con su rol
     * @return redirección al dashboard del rol correspondiente
     */
    @GetMapping("/perfil")
    public String perfil(Authentication usuarioAutenticado) {
        var roles = usuarioAutenticado.getAuthorities().stream()
                .map(r -> r.getAuthority())
                .toList();

        if (roles.contains("ROLE_ADMINISTRADOR")) {
            return "redirect:/administrador/perfil";
        }
        return "redirect:/asesor/perfil";
    }


    @RequestMapping("/error-403")
    public String mostrarError403(HttpServletRequest peticion, Model modelo) {
        Object error = peticion.getAttribute("error");

        if (error == null) {
            return "redirect:/";
        }

        modelo.addAttribute("error", error);
        return "excepciones/error";
    }
}