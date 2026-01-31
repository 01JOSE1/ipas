package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.RolEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepositorio extends JpaRepository<RolEntidad, Long> {

    Optional<RolEntidad> findByNombreRol(String nombre);
}
