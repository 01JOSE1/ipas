package com.proyecto.ipas.negocio.servicio.autenticacion;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.datos.mapeador.RolMapper;
import com.proyecto.ipas.datos.mapeador.UsuarioMapper;
import com.proyecto.ipas.datos.repositorio.RolRepositorio;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import com.proyecto.ipas.negocio.dominio.modelo.Rol;
import com.proyecto.ipas.negocio.dominio.modelo.Usuario;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.RecursoNOEncontradoException;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.RegistroDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.RespuestaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.UsuarioActualizarDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.VerDatosUsuarioPerfilDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final UsuarioMapper usuarioMapper;
    private final RolMapper rolMapper;
    private final PasswordEncoder passwordEncoder;

    private static final Logger registro = LoggerFactory.getLogger( UsuarioServicio.class);
    private static final String ROL_PREDETERMINADO = "ASESOR";

    /**
     * Porque la inyección por constructor hace explícitas y obligatorias las dependencias,
     * permite objetos inmutables y testeables, y evita errores ocultos que @Autowired por campo puede causar.
     */
    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, RolRepositorio rolRepositorio, UsuarioMapper usuarioMapper, RolMapper rolMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.usuarioMapper = usuarioMapper;
        this.rolMapper = rolMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RespuestaDTO crearUsuario(RegistroDTO registroDTO){

        registro.debug("Creando usuario con email: {}", registroDTO.getCorreo());

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();

        if (usuarioRepositorio.existsByCorreo(registroDTO.getCorreo())) {
            errores.add(new ConflictoExcepcion.ErrorCampo("correo", registroDTO.getCorreo()));
        }

        if (errores.size()>0){
            throw new ConflictoExcepcion("Conflicto de datos del usuario", errores);
        }

        RolEntidad rolEntidad = rolRepositorio.findByNombreRol(ROL_PREDETERMINADO).orElseThrow(() -> new RecursoNOEncontradoException("ROL", "NOMBRE", ROL_PREDETERMINADO));

        Rol rolPredeterminado = rolMapper.toRol(rolEntidad);

        Usuario usuario = Usuario.registrarNuevo(registroDTO.getNombre(), registroDTO.getApellido(), registroDTO.getCorreo(), rolPredeterminado);

        UsuarioEntidad usuarioEntidad = usuarioMapper.toUsuarioEntidad(usuario);

        try {

            usuarioEntidad.setClave(passwordEncoder.encode(registroDTO.getClave()));

            UsuarioEntidad usuarioGuardado = usuarioRepositorio.save(usuarioEntidad);
            usuarioRepositorio.flush();
            registro.info("Usuario creado exitosamente con ID: {}", usuarioGuardado.getIdUsuario());

            return usuarioMapper.toRespuestaDTO(usuarioGuardado);

        } catch (DataIntegrityViolationException e) {
            registro.error("Error de integridad al crear usuario: {}", e.getMessage());
            throw new NegocioExcepcion("No se pudo crear el usuario. Intenta nuevamente", "CREACION_USUARIO_FALLIDA");
        }
    }


    public VerDatosUsuarioPerfilDTO verDatosUsuario(Long idUsuario){
        registro.debug("Leer datos del usuario con ID: {}", idUsuario);

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RecursoNOEncontradoException("USUARIO", "ID", idUsuario));

        return usuarioMapper.toVerDatosUsuarioPerfilDTO(usuarioEntidad);
    }


    @Transactional
    public void actualizarUsuario(Long idUsuario, UsuarioActualizarDTO usuarioActualizarDTO){

        registro.debug("Actualizando usuario con ID: {}", idUsuario);

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RecursoNOEncontradoException("USUARIO", "ID", idUsuario));

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();

        if (usuarioRepositorio.existsByNumeroDocumentoAndIdUsuarioNot(usuarioActualizarDTO.getNumeroDocumento(), usuarioEntidad.getIdUsuario())) {
            errores.add(new ConflictoExcepcion.ErrorCampo("numeroDocumento", "Numero de documento ya existe"));
        }

        if (usuarioRepositorio.existsBytelefonoAndIdUsuarioNot(usuarioActualizarDTO.getTelefono(), usuarioEntidad.getIdUsuario())) {
            errores.add(new ConflictoExcepcion.ErrorCampo("telefono", "Numero de telefono ya existe"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos del usuario", errores);
        }


        try {

            usuarioMapper.toUsuario(usuarioEntidad);

            usuarioActualizarDTO.actualizarUsuario(usuarioEntidad);

            usuarioRepositorio.save(usuarioEntidad);

            usuarioRepositorio.flush();

            registro.info("Usuario actualizado exitosamente con ID: {}", usuarioEntidad.getIdUsuario());

        } catch (DataIntegrityViolationException e) {
            registro.error("Error de integridad al actualizar el usuario: {}", e.getMessage());
            throw new NegocioExcepcion("No se pudo actualizar el usuario. Intenta nuevamente", "ACTUALIZACION_USUARIO_FALLIDA");
        }
    }

}



