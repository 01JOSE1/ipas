package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.RamoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RamoRepositorio extends JpaRepository<RamoEntidad, Long> {
}
