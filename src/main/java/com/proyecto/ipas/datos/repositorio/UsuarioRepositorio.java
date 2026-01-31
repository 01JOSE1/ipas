package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntidad, Long> {

    boolean existsByCorreo(String correo);

    boolean existsBytelefonoAndIdUsuarioNot(String telefono, Long idUsuario);

    boolean existsByNumeroDocumentoAndIdUsuarioNot(String numeroDocumento, Long idUsuario);

    Optional<UsuarioEntidad> findByCorreo(String correo);

}
