package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad Rol.
 * 
 * Gestiona operaciones CRUD básicas para roles del sistema.
 * Los roles son registros estáticos (ADMINISTRADOR, ASESOR) utilizados en autenticación y autorización.
 */
@Repository
public interface RolRepositorio extends JpaRepository<RolEntidad, Long> {

    /**
     * Busca un rol por su nombre.
     * 
     * Utilizado durante el registro de usuarios y en configuración de Spring Security.
     * 
     * @param nombre el nombre del rol (típicamente "ADMINISTRADOR" o "ASESOR")
     * @return Optional con la entidad del rol si existe, Optional vacío si no
     */
    Optional<RolEntidad> findByNombreRol(String nombre);
}
