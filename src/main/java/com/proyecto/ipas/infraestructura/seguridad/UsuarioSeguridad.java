package com.proyecto.ipas.infraestructura.seguridad;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioSeguridad  implements UserDetails {

    private final UsuarioEntidad usuarioEntidad;

    public UsuarioSeguridad(UsuarioEntidad usuarioEntidad) {
        this.usuarioEntidad = usuarioEntidad;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + usuarioEntidad.getRol().getNombreRol().toUpperCase())
        );
    }

    public Long getIdUsuario() {
        return usuarioEntidad.getIdUsuario();
    }

    public String getNombre() { return usuarioEntidad.getNombre(); }

    @Override
    public String getPassword() {
        return usuarioEntidad.getClave();
    }

    @Override
    public String getUsername() {
        return usuarioEntidad.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !usuarioEntidad.esSuspendido();
//        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return usuarioEntidad.esActivo();
//        return UserDetails.super.isEnabled();
    }
}
