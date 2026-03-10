package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.RamoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para la entidad Ramo.
 * 
 * Gestiona operaciones CRUD para ramos de seguros (ej: AUTO, VIDA, INCENDIO, etc.).
 * Hereda funcionalidades estándar de JpaRepository para búsqueda, creación, actualización y eliminación.
 * Los ramos son datos maestros que definen tipos de pólizas disponibles en el sistema.
 */
@Repository
public interface RamoRepositorio extends JpaRepository<RamoEntidad, Long> {
}
