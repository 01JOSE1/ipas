package com.proyecto.ipas.presentacion.controlador;

import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.dominio.modelo.Usuario;
import com.proyecto.ipas.negocio.servicio.autenticacion.UsuarioServicio;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.autenticacion.RegistroDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.autenticacion.RespuestaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.mensajeFrontend.AlertaRespuesta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.usuario.UsuarioActualizarDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.usuario.VerDatosUsuarioPerfilDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuariosControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private UsuarioMapper usuarioMapper;


    @GetMapping("/registro")
    public String redirigirRegistro() {
        return "redirect:/usuarios/login";
    }

    @GetMapping("/")
    public String redirigir() {
        return "redirect:/usuarios/login";
    }

    /**
     * GET /users/create - Formulario de creación
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
        return "usuarios/loginRegistroFormulario"; // Retorna users/create.html
    }

    /**
     * POST /users/create - Procesar creación de usuario (form tradicional)
     */
    @PostMapping("/registro")
    public String createUser(
            @Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
            BindingResult validacion, // Debe ir justo después del objeto con @Valid si no Spring lanza excepción, no vuelve a la vista y apaarece error de seguridad.
            Model modelo,
            RedirectAttributes redirectAttributes
    ) {

        if (validacion.hasErrors()) {
            return "usuarios/loginRegistroFormulario";
        }

        try {
            RespuestaDTO respuestaDTO = usuarioServicio.crearUsuario(registroDTO);

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


    @GetMapping("/perfil")
    public String perfil(Model modelo, Authentication usuarioAutenticado) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        mantenerCorreoPerfil(usuarioSesion, modelo);

        modelo.addAttribute("usuarioActualizarDTO", usuarioMapper.toUsuarioActualizarDTO(usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario())));

        return "usuarios/actualizar";
    }

    private void mantenerCorreoPerfil(@AuthenticationPrincipal UsuarioSeguridad usuarioSesion, Model modelo) {
        modelo.addAttribute("correo", usuarioServicio.verDatosUsuario(usuarioSesion.getIdUsuario()).correo());
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@Valid @ModelAttribute("usuarioActualizarDTO") UsuarioActualizarDTO usuarioActualizarDTO, BindingResult validacion, Model modelo, Authentication usuarioAutenticado, RedirectAttributes redirectAttributes) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        if (validacion.hasErrors()) {
            mantenerCorreoPerfil(usuarioSesion, modelo);
            return "usuarios/actualizar";
        }

        try {

            usuarioServicio.actualizarUsuario(usuarioSesion.getIdUsuario(), usuarioActualizarDTO);

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.OK.value(),
                    TipoAlerta.EXITO,
                    "Recurso fue actualizado con exito",
                    "Datos actualizados con exito",
                    "USUARIO_ACTUALIZADO"
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
            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);
            return "redirect:/usuarios/perfil";
        } catch (ConflictoExcepcion ex) {
            ex.getCampoErrorLista().forEach(error -> {
                validacion.rejectValue(error.getCampo(), ex.getErrorCodigo(), error.getMensaje());
            });

            mantenerCorreoPerfil(usuarioSesion, modelo);
            return "usuarios/actualizar";
        }

        return "redirect:/usuarios/perfil";
    }



    @RequestMapping("/error-403")
    public String mostrarError403(HttpServletRequest peticion, Model modelo) {
        // Recuperamos el objeto 'AlertaRespuesta' que guardamos en el Handler
        Object error = peticion.getAttribute("error");

        if (error == null) {
            // Por si alguien entra a la URL directamente sin pasar por el error
            return "redirect:/";
        }

        // Lo pasamos al modelo de Spring MVC para que Thymeleaf lo vea
        modelo.addAttribute("error", error);

        // Retornamos la ruta lógica de la vista (sin .html)
        return "excepciones/error";
    }
}
