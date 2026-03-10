package com.proyecto.ipas.negocio.servicio.cliente;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.datos.mapeador.ClienteMapper;
import com.proyecto.ipas.datos.repositorio.ClienteRepositorio;
import com.proyecto.ipas.datos.repositorio.UsuarioRepositorio;
import com.proyecto.ipas.negocio.dominio.modelo.Cliente;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.RecursoNOEncontradoException;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.BusquedaClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.RespuestaClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteServicio {

    private static final Logger registro = LoggerFactory.getLogger(ClienteServicio.class);


    ClienteRepositorio clienteRepositorio;
    UsuarioRepositorio usuarioRepositorio;
    ClienteMapper clienteMapper;

    /**
     * JdbcTemplate es una clase de Spring que simplifica el acceso a la base de datos
     * usando JDBC.
     *
     * Permite ejecutar sentencias SQL directamente (SELECT, INSERT, UPDATE, DELETE)
     * sin tener que manejar manualmente conexiones, statements o resultsets.
     *
     * En este caso, se utiliza para ejecutar sentencias SQL nativas necesarias
     * para preparar el contexto de la conexión (por ejemplo, variables de sesión
     * que serán utilizadas por triggers en la base de datos).
     */
    private final JdbcTemplate jdbcTemplate;

    public ClienteServicio(ClienteRepositorio clienteRepositorio, UsuarioRepositorio usuarioRepositorio, ClienteMapper clienteMapper, JdbcTemplate jdbcTemplate) {
        this.clienteRepositorio = clienteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.clienteMapper = clienteMapper;
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Obtiene una página de clientes ordenados según la configuración de paginación.
     *
     * @param pagina número de la página (0-basado)
     * @param cantidad cantidad de registros por página
     * @return Page con los clientes paginados convertidos a RespuestaClienteDTO
     */
    @Transactional(readOnly = true)
    public Page<RespuestaClienteDTO> obtenerClientesPaginados(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad);

        return clienteRepositorio.findAll(pageable).map(RespuestaClienteDTO::new);
    }

    /**
     * Busca clientes por un término específico (nombre, documento, teléfono o correo).
     * Utiliza una búsqueda flexible que coincide parcialmente con los datos del cliente.
     *
     * @param termino término de búsqueda que se compara contra los datos del cliente
     * @return lista de BusquedaClienteDTO con los clientes que coinciden con el término
     */
    @Transactional(readOnly = true)
    public List<BusquedaClienteDTO> buscarClientes(String termino) {
        return clienteRepositorio.buscarClientes(termino);
    }


    /**
     * Registra un nuevo cliente en el sistema validando datos únicos.
     * Valida que el número de documento, correo y teléfono no estén duplicados.
     * El cliente es asignado al usuario especificado.
     *
     * @param idUsuario ID del usuario asesor que registra al cliente
     * @param gestionClienteDTO objeto DTO con los datos del cliente a registrar
     * @return objeto RespuestaClienteDTO con los datos del cliente creado
     * @throws ConflictoExcepcion si el número de documento, correo o teléfono ya existen
     * @throws RecursoNOEncontradoException si el ID del usuario no existe
     * @throws NegocioExcepcion si ocurre un error de integridad de datos durante la creación
     */
    @Transactional
    public RespuestaClienteDTO registrarCliente(Long idUsuario, GestionClienteDTO gestionClienteDTO){

        registro.debug("Registrando cliente con numero de documento: {}", gestionClienteDTO.getNumeroDocumento());

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();

        if (clienteRepositorio.existsByNumeroDocumento((gestionClienteDTO.getNumeroDocumento()))) {
            errores.add( new ConflictoExcepcion.ErrorCampo("numeroDocumento", "El numero de documento ya se encuentra registrado"));
        }

        if (gestionClienteDTO.getCorreo() != null && !gestionClienteDTO.getCorreo().isBlank()) {
            if (clienteRepositorio.existsByCorreo(gestionClienteDTO.getCorreo())) {
                errores.add( new ConflictoExcepcion.ErrorCampo("correo", "El correo ya se encuentra registrado"));
            }
        }

        if (clienteRepositorio.existsByTelefono(gestionClienteDTO.getTelefono())) {
            errores.add( new ConflictoExcepcion.ErrorCampo("telefono", "El numero de telefono ya se encuentra registrado"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos registrados", errores);
        }

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findById(idUsuario).orElseThrow( () -> new RecursoNOEncontradoException("usuario", "id", idUsuario));

        try {
            Cliente cliente = clienteMapper.toCliente(gestionClienteDTO);

            ClienteEntidad clienteEntidad = clienteMapper.toClienteEntidad(cliente);

            clienteEntidad.setEstadoCivil(gestionClienteDTO.getEstadoCivil());
            clienteEntidad.setTelefono(gestionClienteDTO.getTelefono());
            clienteEntidad.setCorreo(gestionClienteDTO.getCorreo());
            clienteEntidad.setDireccion(gestionClienteDTO.getDireccion());
            clienteEntidad.setCiudad(gestionClienteDTO.getCiudad());
            clienteEntidad.setUsuario(usuarioEntidad);

            ClienteEntidad clienteGuardado = clienteRepositorio.save(clienteEntidad);
    
            registro.debug("Cliente guardado: {}", clienteEntidad.getIdCliente());

            return clienteMapper.toRespuestaCliente(clienteGuardado);

        } catch (DataIntegrityViolationException ex) {

            registro.error("Error de integridad al crear usuario: {}", ex.getMessage());
            throw new NegocioExcepcion("No se pudo crear el cliente. Intenta nuevamente", "CREACION_CLIENTE_FALLIDA");
        }
    }

    /**
     * Obtiene los datos de un cliente específico para ser editados.
     *
     * @param idCliente ID del cliente que se desea consultar
     * @return objeto GestionClienteDTO con la información del cliente
     * @throws RecursoNOEncontradoException si el ID del cliente no existe en la base de datos
     */
    @Transactional(readOnly = true)
    public GestionClienteDTO obtenerCliente(Long idCliente) {
        registro.debug("Obtener cliente con ID: {}", idCliente);

        ClienteEntidad clienteEntidad = clienteRepositorio.findById(idCliente).orElseThrow( () -> new RecursoNOEncontradoException("Cliente", "id", idCliente));

        return clienteMapper.toGestionCliente(clienteEntidad);
    }


    /**
     * Actualiza los datos de un cliente existente validando datos únicos.
     * Valida que el número de documento, correo y teléfono no estén duplicados en otros clientes.
     * Establece una variable de sesión en la base de datos para que los triggers puedan identificar
     * quién realizó la actualización.
     *
     * @param idCliente ID del cliente a actualizar
     * @param gestionClienteDTO objeto DTO con los nuevos datos del cliente
     * @param idUsuarioActual ID del usuario autenticado que realiza la actualización
     * @throws RecursoNOEncontradoException si el ID del cliente no existe
     * @throws ConflictoExcepcion si el número de documento, correo o teléfono ya existen para otro cliente
     * @throws NegocioExcepcion si ocurre un error de integridad de datos durante la actualización
     */
    @Transactional
    public void actualizarCliente(Long idCliente, GestionClienteDTO gestionClienteDTO, Long idUsuarioActual) {

        registro.debug("Actualizando cliente con numero de documento: {}", gestionClienteDTO.getNumeroDocumento());

        ClienteEntidad clienteEntidad = clienteRepositorio.findById(idCliente).orElseThrow( () -> new RecursoNOEncontradoException("usuario", "id", idCliente));

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();

        if (clienteRepositorio.existsByNumeroDocumentoAndIdClienteNot(gestionClienteDTO.getNumeroDocumento(), idCliente)) {
            errores.add( new ConflictoExcepcion.ErrorCampo("numeroDocumento", "El numero de documento ya se encuentra registrado"));
        }

        if (clienteRepositorio.existsByCorreoAndIdClienteNot(gestionClienteDTO.getCorreo(), idCliente)) {
            errores.add( new ConflictoExcepcion.ErrorCampo("correo", "El correo ya se encuentra registrado"));
        }

        if (clienteRepositorio.existsByTelefonoAndIdClienteNot(gestionClienteDTO.getTelefono(), idCliente)) {
            errores.add( new ConflictoExcepcion.ErrorCampo("telefono", "El numero de telefono ya se encuentra registrado"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos registrados", errores);
        }


        try {
            clienteMapper.toCliente(clienteEntidad);

            gestionClienteDTO.actualizarCliente(clienteEntidad);

            /**
             * Establece una variable de sesión en la base de datos llamada @usuario_actual.
             *
             * Esta variable:
             * - Vive únicamente durante la conexión actual a la base de datos.
             * - Es leída posteriormente por un trigger de MySQL/MariaDB durante la ejecución de una operación UPDATE.
             *
             * - Esta sentencia debe ejecutarse en la misma transacción que el UPDATE, para garantizar que el trigger tenga acceso a la variable.
             * - El valor se envía desde la aplicación, el id del usuarioautenticado en el sistema).
             *
             * @param idUsuarioActual Identificador del usuario que realiza la operación.
             */
            jdbcTemplate.update(
                    "SET @usuario_actual = ?", idUsuarioActual
            );

            clienteRepositorio.save(clienteEntidad);
            clienteRepositorio.flush();

            registro.info("Cliente actualizado exitosamente con ID: {}", clienteEntidad.getIdCliente());


        } catch (DataIntegrityViolationException ex) {
            registro.error("Error de integridad al actualizar cliente: {}", ex.getMessage());
            throw new NegocioExcepcion("No se pudo actualizar el cliente. Intenta nuevamente", "ACTUALIZACION_CLIENTE_FALLIDA");
        }
    }
}
