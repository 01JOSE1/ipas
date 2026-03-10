package com.proyecto.ipas.presentacion.controlador.usuarios.administrador;

import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import com.proyecto.ipas.infraestructura.utilidades.TipoAlerta;
import com.proyecto.ipas.negocio.servicio.usuario.UsuarioServicio;
import com.proyecto.ipas.negocio.servicio.usuario.rol.RolServicio;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.PermisoInsuficienteExcepcion;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.mensajeFrontend.AlertaRespuesta;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.CambiarEstadoUsuarioDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.CambiarRolUsuarioDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.RespuestaUsuarioDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.rol.RespuestaRolDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/administrador/usuario")
public class AdministradorUsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private RolServicio rolServicio;


    @GetMapping("ver-usuarios")
    public String verUsuarios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int cantidad,
            Model modelo,
            Authentication autenticacion
    ) {
        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) autenticacion.getPrincipal();

        Page<RespuestaUsuarioDTO> paginaUsuarios;
        List<RespuestaRolDTO> roles;

        paginaUsuarios = usuarioServicio.obtenerUsuariosPaginados(pagina, cantidad, usuarioSesion.getIdUsuario());
        modelo.addAttribute("usuarios", paginaUsuarios.getContent());

        roles = rolServicio.obtenerRoles();
        modelo.addAttribute("roles", roles);

        modelo.addAttribute("paginaActual", pagina);
        modelo.addAttribute("totalPaginas", paginaUsuarios.getTotalPages());
        modelo.addAttribute("totalRegistros",  paginaUsuarios.getTotalElements());
        modelo.addAttribute("catidadPagina", cantidad);

        return "usuarios/listaUsuarios";
    }


    @PostMapping("cambiar-estado")
    public String cambiarEstado(@Valid @ModelAttribute("cambiarEstadoUsuarioDTO") CambiarEstadoUsuarioDTO cambiarEstadoUsuarioDTO,
                                Authentication usuarioAutenticado,
                                RedirectAttributes redirectAttributes,
                                Model modelo ) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        try {
            cambiarEstadoUsuarioDTO.setIdUsuarioAccion(usuarioSesion.getIdUsuario());

            usuarioServicio.cambiarEstadoUsuario(cambiarEstadoUsuarioDTO);

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.OK.value(),
                    TipoAlerta.EXITO,
                    "Cambio de estado del usuario con exito",
                    "Cambio de estado de usuario a "+ cambiarEstadoUsuarioDTO.getEstadoCambio().name(),
                    "USUARIO_"+cambiarEstadoUsuarioDTO.getEstadoCambio().name().toUpperCase()
            );

            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);

        } catch (NegocioExcepcion | PermisoInsuficienteExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage());
            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);
            return "redirect:/administrador/usuario/ver-usuarios";
        }

        return "redirect:/administrador/usuario/ver-usuarios";
    }


    @PostMapping("cambiar-rol")
    public String cambiarRol(@Valid @ModelAttribute("cambiarRolUsuarioDTO")CambiarRolUsuarioDTO cambiarRolUsuarioDTO,
                             Authentication usuarioAutenticado,
                             RedirectAttributes redirectAttributes,
                             Model modelo ) {

        UsuarioSeguridad usuarioSesion = (UsuarioSeguridad) usuarioAutenticado.getPrincipal();

        try {
            cambiarRolUsuarioDTO.setIdUsuarioAccion(usuarioSesion.getIdUsuario());

            String nombreRol = usuarioServicio.cambiarRolUsuario(cambiarRolUsuarioDTO);

            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(
                    HttpStatus.OK.value(),
                    TipoAlerta.EXITO,
                    "Cambio de rol del usuario con exito",
                    "Cambio de rol de usuario a "+ nombreRol,
                    "USUARIO_"+nombreRol
            );

            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);

        } catch (NegocioExcepcion | PermisoInsuficienteExcepcion ex) {
            AlertaRespuesta alertaRespuesta = new AlertaRespuesta(TipoAlerta.ERROR, ex.getMessage());
            redirectAttributes.addFlashAttribute("alertaRespuesta", alertaRespuesta);
            return "redirect:/administrador/usuario/ver-usuarios";
        }

        return "redirect:/administrador/usuario/ver-usuarios";
    }
}
