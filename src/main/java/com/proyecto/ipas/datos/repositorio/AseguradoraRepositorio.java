package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad Aseguradora.
 * 
 * Gestiona operaciones CRUD y búsquedas especializadas para aseguradoras del sistema.
 * Incluye búsqueda por similitud de nombre (tolerancia de typos) y validación de existencia.
 * Las aseguradoras son datos maestros asociados a pólizas de seguros.
 */
@Repository
public interface AseguradoraRepositorio extends JpaRepository<AseguradoraEntidad, Long> {

    /**
     * Busca una aseguradora por su número de documento único.
     * 
     * @param documento el número de documento de la aseguradora
     * @return la aseguradora si existe, null si no
     */
    AseguradoraEntidad findByNumeroDocumento(String documento);

    /**
     * Busca una aseguradora por similitud en el nombre.
     * 
     * Implementa búsqueda tolerante a typos/errores usando la función Levenshtein de MySQL.
     * Permite encontrar aseguradoras incluso cuando el nombre está ligeramente mal escrito.
     * La distancia máxima de diferencia es de 5 caracteres. Retorna la coincidencia más cercana.
     * 
     * @param nombre el nombre de la aseguradora (puede contener errores menores)
     * @return Optional con la aseguradora que mejor coincida, Optional vacío si no hay coincidencias
     */
    @Query(value = """
        SELECT * FROM aseguradoras a 
        WHERE levenshtein(LOWER(a.nombre_aseguradora), LOWER(:nombre)) <= 5 
        ORDER BY levenshtein(LOWER(a.nombre_aseguradora), LOWER(:nombre)) ASC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<AseguradoraEntidad> buscarPorSimilitud(@Param("nombre") String nombre);

    /**
     * Verifica si una aseguradora existe por su ID.
     * 
     * @param idAseguradora el ID de la aseguradora a validar
     * @return true si la aseguradora existe, false en caso contrario
     */
    boolean existsByIdAseguradora (Long idAseguradora);
}
