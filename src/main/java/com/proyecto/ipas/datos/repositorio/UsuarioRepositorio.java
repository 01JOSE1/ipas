package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.negocio.dominio.enums.EstadoUsuario;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario.asesor.ActividadRecienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntidad, Long> {

    boolean existsByCorreo(String correo);

    boolean existsBytelefonoAndIdUsuarioNot(String telefono, Long idUsuario);

    boolean existsByNumeroDocumentoAndIdUsuarioNot(String numeroDocumento, Long idUsuario);

    Optional<UsuarioEntidad> findByCorreo(String correo);


    /**
     * Lista paginada de usuarios excluyendo uno específico (el actual)
     */
    Page<UsuarioEntidad> findByIdUsuarioNot(Long idUsuario, Pageable pageable);


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
    Long countByEstado(EstadoUsuario estado);



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
            AND fecha_accion >= CURRENT_DATE()
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
     * Asesores únicos con actividad desde la fecha inicial hasta el final de hoy
     */
    @Query(value = """
            SELECT COUNT(DISTINCT usuario_id)
            FROM Auditorias
            WHERE fecha_accion >= :fecha 
            AND fecha_accion < DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY)
        """, nativeQuery = true)
    Long contarAsesoresActividadUltimosDias(@Param("fecha") LocalDate fecha);


    /**
     * Listar la actividad reciente del usuario (asesor)
     */
    @Query(value = """
            SELECT 
                CASE 
                    WHEN tabla_afectada = 'Usuarios' THEN 'USUARIO'
                    WHEN tabla_afectada = 'Polizas'  THEN 'POLIZA'
                    WHEN tabla_afectada = 'Clientes' THEN 'CLIENTE'
                    ELSE 'OTRO'
                END as tipo,
                detalles as descripcion,
                fecha_accion as fecha
            FROM auditorias
            ORDER BY fecha_accion DESC
            LIMIT 6
        """, nativeQuery = true)
    List<ActividadRecienteDTO> encontrarUltimaActividad();
}
