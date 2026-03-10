package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntidad, Long> {

    boolean existsByCorreo(String correo);

    boolean existsBytelefonoAndIdUsuarioNot(String telefono, Long idUsuario);

    boolean existsByNumeroDocumentoAndIdUsuarioNot(String numeroDocumento, Long idUsuario);

    Optional<UsuarioEntidad> findByCorreo(String correo);

    /**
     * Cantidad de gestiones que el usuario ha realizado en clientes y polizas en el mes
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM Auditorias
            WHERE usuario_id = :idUsuario
            AND accion = 'UPDATE'
            AND tabla_afectada IN ('Clientes','Polizas')
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    Long contarGestionesMes(@Param("idUsuario") Long idUsuario);


    /**
     * Cantidad de usuarios Activos
     */
    Long countByEstado(String estado);



    /**
     * ESTADISTICAS DE DASHBOARD ADMINISTRADOR:
     */
    /**
     * Cantidad de actualizaciones del dia de hoy
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM Auditorias
            WHERE accion = 'UPDATE'
            AND fecha_accion = CURRENT_DATE()
        """, nativeQuery = true)
    Long contarActualizacionesHoy();


    /**
     * Cantidad de actualizaciones de lo que ha trascurrido del mes actual
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM Auditorias
            WHERE accion = 'UPDATE'
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    Long contarActualizacionesMes();


    /**
     * Asesores únicos con actividad en los últimos 7 días
     */
    @Query(value = """
        SELECT COUNT(DISTINCT usuario_id)
        FROM Auditorias
        WHERE fecha_accion BETWEEN :fecha AND CURRENT_DATE
    """, nativeQuery = true)
    Long contarAsesoresActividadUltimosDias(@Param("fecha") LocalDate fecha);



}
