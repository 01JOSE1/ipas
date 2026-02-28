package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.AseguradoraEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AseguradoraRepositorio extends JpaRepository<AseguradoraEntidad, Long> {
    AseguradoraEntidad findByNumeroDocumento(String documento);

    @Query(value = """
        SELECT * FROM aseguradoras a 
        WHERE levenshtein(LOWER(a.nombre_aseguradora), LOWER(:nombre)) <= 5 
        ORDER BY levenshtein(LOWER(a.nombre_aseguradora), LOWER(:nombre)) ASC 
        LIMIT 1
    """, nativeQuery = true)
    Optional<AseguradoraEntidad> buscarPorSimilitud(@Param("nombre") String nombre);

    boolean existsByIdAseguradora (Long idAseguradora);
}
