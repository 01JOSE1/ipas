package com.proyecto.ipas.datos.repositorio;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<ClienteEntidad, Long> {

    boolean existsByNumeroDocumento(String documento);

    boolean existsByCorreo(String correo);

    boolean existsByTelefono(String telefono);

    boolean existsByNumeroDocumentoAndIdClienteNot(String documento, Long idCliente);

    boolean existsByCorreoAndIdClienteNot(String correo, Long idCliente);

    boolean existsByTelefonoAndIdClienteNot(String telefono, Long idCliente);


}
