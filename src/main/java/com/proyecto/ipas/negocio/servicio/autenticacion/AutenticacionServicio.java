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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findByCorreo(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new UsuarioSeguridad(usuarioEntidad);

    }
}
