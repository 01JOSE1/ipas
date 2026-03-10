package com.proyecto.ipas.infraestructura.seguridad;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación de UserDetails que encapsula la entidad Usuario para Spring Security.
 * 
 * Convierte el modelo de Usuario de dominio al modelo que Spring Security entiende,
 * proporcionando autoridades (roles) e información de estado de la cuenta.
 */
public class UsuarioSeguridad implements UserDetails {

    private final UsuarioEntidad usuarioEntidad;

    /**
     * Constructor que encapsula la entidad usuario para su uso en Spring Security.
     * 
     * @param usuarioEntidad la entidad usuario persistida en base de datos
     */
    public UsuarioSeguridad(UsuarioEntidad usuarioEntidad) {
        this.usuarioEntidad = usuarioEntidad;
    }

    /**
     * Retorna las autoridades (roles) del usuario en formato Spring Security.
     * 
     * @return una colección con un único rol formateado como "ROLE_NOMBREROL"
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + usuarioEntidad.getRol().getNombreRol().toUpperCase())
        );
    }

    /**
     * Obtiene el identificador único del usuario.
     * 
     * @return el ID del usuario en base de datos
     */
    public Long getIdUsuario() {
        return usuarioEntidad.getIdUsuario();
    }

    /**
     * Obtiene el nombre del usuario para mostrar en la interfaz.
     * 
     * @return el nombre del usuario
     */
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

    /**
     * Verifica si la cuenta del usuario está bloqueada o suspendida.
     * 
     * @return {@code true} si la cuenta NO está suspendida, {@code false} si está suspendida
     */
    @Override
    public boolean isAccountNonLocked() {
        return !usuarioEntidad.esSuspendido();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Verifica si el usuario está habilitado (activo) en el sistema.
     * 
     * @return {@code true} si el usuario está en estado ACTIVO, {@code false} en caso contrario
     */
    @Override
    public boolean isEnabled() {
        return usuarioEntidad.esActivo();
    }
}
