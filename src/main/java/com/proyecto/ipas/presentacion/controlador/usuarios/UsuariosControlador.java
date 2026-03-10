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

@Controller
@RequestMapping("/usuarios")
public class UsuariosControlador {

    @Autowired
    private UsuarioAutenticacionServicio usuarioAutenticacionServicio;

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
     * Cada controlador (asesor/admin) maneja su propia lógica de perfil.
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