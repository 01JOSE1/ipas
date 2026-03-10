package com.proyecto.ipas.negocio.servicio.autenticacion;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import com.proyecto.ipas.infraestructura.seguridad.UsuarioSeguridad;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class   AutenticacionServicio implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public AutenticacionServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /**
     * Carga los detalles de seguridad de un usuario por su correo electrónico.
     * Este método es utilizado por Spring Security para la autenticación.
     *
     * @param email correo electrónico del usuario a buscar
     * @return objeto UsuarioSeguridad que implementa UserDetails con los datos del usuario
     * @throws UsernameNotFoundException si no existe un usuario registrado con el email proporcionado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findByCorreo(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new UsuarioSeguridad(usuarioEntidad);

    }
}
