package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AseguradoraRepositorio extends JpaRepository<AseguradoraEntidad, Long> {
}
