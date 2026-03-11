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

/**
 * Repositorio de acceso a datos para la entidad Usuario.
 * 
 * Maneja todas las operaciones CRUD e queries personalizadas para usuarios del sistema.
 * Incluye métodos para validación, búsqueda, y cálculo de estadísticas utilizadas en dashboards.
 * Extiende JpaRepository para heredar operaciones estándar (save, delete, findAll, etc.).
 */
@Repository
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntidad, Long> {

    /**
     * Verifica si existe un usuario registrado con el correo especificado.
     * 
     * @param correo el correo electrónico a validar
     * @return true si existe un usuario con ese correo, false en caso contrario
     */
    boolean existsByCorreo(String correo);

    /**
     * Verifica si existe otro usuario con el teléfono especificado (excluyendo el usuario actual).
     * 
     * Utilizado para validar unicidad de teléfono durante actualizaciones de perfil.
     * 
     * @param telefono el teléfono a validar
     * @param idUsuario el ID del usuario actual a excluir de la búsqueda
     * @return true si existe otro usuario con ese teléfono, false en caso contrario
     */
    boolean existsBytelefonoAndIdUsuarioNot(String telefono, Long idUsuario);

    /**
     * Verifica si existe otro usuario con el número de documento especificado (excluyendo el usuario actual).
     * 
     * Utilizado para validar unicidad de documento durante actualizaciones de perfil.
     * 
     * @param numeroDocumento el número de documento a validar
     * @param idUsuario el ID del usuario actual a excluir de la búsqueda
     * @return true si existe otro usuario con ese documento, false en caso contrario
     */
    boolean existsByNumeroDocumentoAndIdUsuarioNot(String numeroDocumento, Long idUsuario);

    /**
     * Busca un usuario por correo electrónico.
     * 
     * Utilizado durante el login y recuperación de contraseña.
     * 
     * @param correo el correo electrónico del usuario
     * @return Optional conteniendo el usuario si existe, Optional vacío si no
     */
    Optional<UsuarioEntidad> findByCorreo(String correo);

    /**
     * Obtiene una lista paginada de usuarios excluyendo uno específico (el usuario actual).
     * 
     * Utilizado para listar usuarios disponibles al administrador sin mostrar el suyo propio.
     * 
     * @param idUsuario el ID del usuario a excluir
     * @param pageable información de paginación (páginas, tamaño, ordenamiento)
     * @return Page de usuarios que cumplen el filtro
     */
    Page<UsuarioEntidad> findByIdUsuarioNot(Long idUsuario, Pageable pageable);


    /**
     * Calcula la cantidad de gestiones (actualizaciones) realizadas por un usuario en el mes actual.
     * 
     * Gestiones se refieren a cambios realizados en clientes y pólizas, registradas en auditoría.
     * Utilizado para estadísticas de actividad del asesor.
     * 
     * @param idUsuario el ID del usuario
     * @return la cantidad de actualizaciones en clientes y pólizas este mes
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM auditorias
            WHERE usuario_id = :idUsuario
            AND accion = 'UPDATE'
            AND tabla_afectada IN ('Clientes','Polizas')
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    Long contarGestionesMes(@Param("idUsuario") Long idUsuario);

    /**
     * Cuenta la cantidad total de usuarios en un estado específico.
     * 
     * @param estado el estado del usuario (ACTIVO, SUSPENDIDO, INACTIVO, etc.)
     * @return la cantidad de usuarios en ese estado
     */
    Long countByEstado(EstadoUsuario estado);

    /**
     * Calcula la cantidad total de actualizaciones registradas en la base de datos hoy.
     * 
     * Utilizado en estadísticas del dashboard del administrador para mostrar actividad diaria.
     * 
     * @return la cantidad de operaciones UPDATE realizadas desde las 00:00 de hoy
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM auditorias
            WHERE accion = 'UPDATE'
            AND fecha_accion >= CURRENT_DATE()
        """, nativeQuery = true)
    Long contarActualizacionesHoy();

    /**
     * Calcula la cantidad total de actualizaciones realizadas en el mes actual hasta hoy.
     * 
     * Utilizado en estadísticas del dashboard del administrador para monitorear actividad mensual.
     * 
     * @return la cantidad de operaciones UPDATE desde el 1 de este mes hasta hoy
     */
    @Query(value = """
            SELECT COUNT(*)
            FROM auditorias
            WHERE accion = 'UPDATE'
            AND MONTH(fecha_accion) = MONTH(CURRENT_DATE())
            AND YEAR(fecha_accion) = YEAR(CURRENT_DATE())
        """, nativeQuery = true)
    Long contarActualizacionesMes();


    /**
     * Conta la cantidad de asesores únicos con actividad en un rango de fechas.
     * 
     * Utilizado para mostrar cuántos asesores distintos han realizado operaciones
     * en un período específico (últimos días, semana, etc.) en el dashboard.
     * 
     * @param fecha la fecha inicial del rango (inclusive)
     * @return la cantidad de asesores distintos con actividad desde fecha hasta hoy
     */
    @Query(value = """
            SELECT COUNT(DISTINCT usuario_id)
            FROM auditorias
            WHERE fecha_accion >= :fecha 
            AND fecha_accion < DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY)
        """, nativeQuery = true)
    Long contarAsesoresActividadUltimosDias(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene la actividad reciente de un usuario, categorizada por tipo de cambio.
     * 
     * Retorna los últimos 6 cambios realizados en cualquier tabla del sistema,
     * agrupados como USUARIO, POLIZA, CLIENTE u OTRO. Utilizado en el perfil del asesor
     * para mostrar sus acciones recientes.
     * 
     * @return lista de hasta 6 DTO con actividades recientes ordenadas por fecha descendente
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
