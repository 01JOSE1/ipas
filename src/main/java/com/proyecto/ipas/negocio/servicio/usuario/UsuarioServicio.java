package com.proyecto.ipas.negocio.servicio.usuario;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.datos.mapeador.RolMapper;
import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import com.proyecto.ipas.datos.repositorio.PolizaRepositorio;
import com.proyecto.ipas.datos.repositorio.RolRepositorio;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import com.proyecto.ipas.negocio.dominio.modelo.Usuario;
import com.proyecto.ipas.presentacion.excepcion.RecursoNOEncontradoException;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.CambiarEstadoUsuarioDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.CambiarRolUsuarioDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.RespuestaUsuarioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UsuarioServicio {

    UsuarioRepositorio usuarioRepositorio;
    PolizaRepositorio polizaRepositorio;
    ClienteRepositorio clienteRepositorio;
    RolRepositorio rolRepositorio;

    UsuarioMapper usuarioMapper;
    RolMapper rolMapper;

    private static final Logger registro = LoggerFactory.getLogger(UsuarioServicio.class);

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, PolizaRepositorio polizaRepositorio, ClienteRepositorio clienteRepositorio, RolRepositorio rolRepositorio, UsuarioMapper usuarioMapper, RolMapper rolMapper) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.polizaRepositorio = polizaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.usuarioMapper = usuarioMapper;
        this.rolMapper = rolMapper;
    }

    /**
     * Obtiene una página de usuarios excluyendo el usuario especificado.
     *
     * @param pagina número de la página (0-basado)
     * @param cantidad cantidad de registros por página
     * @param idUsuario ID del usuario a excluir de los resultados
     * @return Page con los usuarios paginados convertidos a RespuestaUsuarioDTO
     */
    @Transactional(readOnly = true)
    public Page<RespuestaUsuarioDTO> obtenerUsuariosPaginados(int pagina, int cantidad, Long idUsuario) {
        registro.debug("Mostrando los registros de usuarios");
        Pageable pageable = PageRequest.of(pagina, cantidad);

        return usuarioRepositorio.findByIdUsuarioNot(idUsuario, pageable).map(RespuestaUsuarioDTO::new);

    }

    /**
     * Cambia el estado de un usuario (activo, inactivo o suspendido).
     * El cambio de estado es registrado indicando quién realizó la acción.
     *
     * @param cambiarEstadoUsuarioDTO objeto DTO que contiene el ID del usuario a cambiar, el ID del usuario que realiza la acción y el nuevo estado
     * @throws RecursoNOEncontradoException si alguno de los IDs de usuarios no existe en la base de datos
     */
    @Transactional
    public void cambiarEstadoUsuario(CambiarEstadoUsuarioDTO cambiarEstadoUsuarioDTO) {
        registro.debug("Cambiando estado de usuario id {} ", cambiarEstadoUsuarioDTO.getIdUsuarioCambio());

        UsuarioEntidad usuarioEntidadCambio = usuarioRepositorio.findById(cambiarEstadoUsuarioDTO.getIdUsuarioCambio()).orElseThrow(() -> new RecursoNOEncontradoException("Usuario", "idUsuario", cambiarEstadoUsuarioDTO.getIdUsuarioCambio()));
        UsuarioEntidad usuarioEntidadAccion = usuarioRepositorio.findById(cambiarEstadoUsuarioDTO.getIdUsuarioAccion()).orElseThrow(() -> new RecursoNOEncontradoException("Usuario", "idUsuario", cambiarEstadoUsuarioDTO.getIdUsuarioAccion()));

        Usuario usuarioCambio = usuarioMapper.toUsuario(usuarioEntidadCambio);
        Usuario usuarioAccion = usuarioMapper.toUsuario(usuarioEntidadAccion);


        switch (cambiarEstadoUsuarioDTO.getEstadoCambio()) {
            case ACTIVO:
                usuarioCambio.activar(usuarioAccion);
                break;

            case INACTIVO:
                usuarioCambio.desactivar(usuarioAccion);
                break;

            case SUSPENDIDO:
                usuarioCambio.suspender(usuarioAccion);
                break;
        }

        usuarioMapper.sincronizar(usuarioCambio, usuarioEntidadCambio);
    
        usuarioRepositorio.save(usuarioEntidadCambio);

        registro.debug("Cambio de estado de usuario con exito: {}", usuarioEntidadCambio.getIdUsuario());

    }



    /**
     * Cambia el rol asignado a un usuario.
     * El cambio es registrado indicando quién realizó la acción.
     *
     * @param cambiarRolUsuarioDTO objeto DTO que contiene el ID del usuario cuyo rol será modificado, el ID del usuario que realiza la acción y el ID del nuevo rol
     * @return el nombre del nuevo rol asignado al usuario
     * @throws RecursoNOEncontradoException si alguno de los IDs de usuarios o del rol no existe
     */
    @Transactional
    public String cambiarRolUsuario (CambiarRolUsuarioDTO cambiarRolUsuarioDTO) {
        registro.debug("Cambiando rol de usuario id {} ", cambiarRolUsuarioDTO.getIdUsuarioCambio());

        UsuarioEntidad usuarioEntidadCambio = usuarioRepositorio.findById(cambiarRolUsuarioDTO.getIdUsuarioCambio()).orElseThrow(() -> new RecursoNOEncontradoException("Usuario", "idUsuario", cambiarRolUsuarioDTO.getIdUsuarioCambio()));
        UsuarioEntidad usuarioEntidadAccion = usuarioRepositorio.findById(cambiarRolUsuarioDTO.getIdUsuarioAccion()).orElseThrow(() -> new RecursoNOEncontradoException("Usuario", "idUsuario", cambiarRolUsuarioDTO.getIdUsuarioAccion()));
        RolEntidad rolEntidad = rolRepositorio.findById(cambiarRolUsuarioDTO.getIdRolCambio()).orElseThrow(() -> new RecursoNOEncontradoException("Rol", "idRol", cambiarRolUsuarioDTO.getIdRolCambio()));

        Usuario usuarioCambio = usuarioMapper.toUsuario(usuarioEntidadCambio);
        Usuario usuarioAccion = usuarioMapper.toUsuario(usuarioEntidadAccion);
        Rol rolCambio = rolMapper.toRol(rolEntidad);

        usuarioCambio.cambiarRol(rolCambio, usuarioAccion);

        usuarioMapper.sincronizar(usuarioCambio, usuarioEntidadCambio);

        usuarioRepositorio.save(usuarioEntidadCambio);

        registro.debug("Cambio de rol el usuario con exito {} ", cambiarRolUsuarioDTO.getIdUsuarioCambio());

        return usuarioEntidadCambio.getRol().getNombreRol();
    }

}
