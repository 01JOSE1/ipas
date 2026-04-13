package com.proyecto.ipas.negocio.servicio.poliza;

import com.proyecto.ipas.datos.entidad.*;
import com.proyecto.ipas.datos.mapeador.PolizaMapper;
import com.proyecto.ipas.datos.repositorio.*;
import com.proyecto.ipas.infraestructura.externo.almacenamiento.ArchivoAlmacenamientoServicio;
import com.proyecto.ipas.infraestructura.externo.ia.IaServicio;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPagoPoliza;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoCliente;
import com.proyecto.ipas.negocio.dominio.enums.TipoRamo;
import com.proyecto.ipas.negocio.dominio.modelo.Poliza;
import com.proyecto.ipas.negocio.dominio.modelo.Ramo;
import com.proyecto.ipas.presentacion.excepcion.*;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.RespuestaClienteDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia.PeticionIaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.CancelarPolizaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.GestionPolizaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza.RespuestaPolizaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolizaServicio {

    private static final Logger registro = LoggerFactory.getLogger(PolizaServicio.class);

    PolizaRepositorio polizaRepositorio;
    ClienteRepositorio clienteRepositorio;
    UsuarioRepositorio usuarioRepositorio;
    RamoRepositorio ramoRepositorio;
    AseguradoraRepositorio aseguradoraRepositorio;

    PolizaMapper polizaMapper;

    ArchivoAlmacenamientoServicio archivoAlmacenamientoServicio;
    private final PdfPolizaServicio pdfPolizaServicio;

    private final IaServicio iaServicio;

    private final JdbcTemplate jdbcTemplate;

    public PolizaServicio(JdbcTemplate jdbcTemplate, PolizaRepositorio polizaRepositorio, ClienteRepositorio clienteRepositorio, UsuarioRepositorio usuarioRepositorio, RamoRepositorio ramoRepositorio, AseguradoraRepositorio aseguradoraRepositorio, PolizaMapper polizaMapper, ArchivoAlmacenamientoServicio archivoAlmacenamientoServicio, PdfPolizaServicio pdfPolizaServicio, IaServicio iaServicio) {
        this.jdbcTemplate = jdbcTemplate;
        this.polizaRepositorio = polizaRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.ramoRepositorio = ramoRepositorio;
        this.aseguradoraRepositorio = aseguradoraRepositorio;
        this.polizaMapper = polizaMapper;
        this.archivoAlmacenamientoServicio = archivoAlmacenamientoServicio;
        this.pdfPolizaServicio = pdfPolizaServicio;
        this.iaServicio = iaServicio;
    }

    /**
     * Obtiene una página de pólizas del sistema.
     *
     * @param pagina número de la página (0-basado)
     * @param cantidad cantidad de registros por página
     * @return Page con las pólizas paginadas convertidas a RespuestaPolizaDTO
     */
    @Transactional(readOnly = true)
    public Page<RespuestaPolizaDTO> obtenerPolizasPaginados(int pagina, int cantidad) {
        Pageable pageable = PageRequest.of(pagina, cantidad);

        return polizaRepositorio.findAll(pageable).map(RespuestaPolizaDTO::new);
    }


    /**
     * Obtiene todas las pólizas asociadas a un cliente específico.
     *
     * @param idCliente ID del cliente
     * @return lista de RespuestaPolizaDTO con todas las pólizas del cliente
     */

    @Transactional(readOnly = true)
    public List<RespuestaPolizaDTO> obtenerPolizasCliente(Long idCliente) {
        registro.debug("Mostrando los registros de polizas del cliente: " + idCliente);
        return polizaRepositorio.findAllByCliente_IdCliente(idCliente).stream().map(RespuestaPolizaDTO::new).collect(Collectors.toList());
    }

    /**
     * Obtiene un archivo PDF de póliza almacenado en la ruta local.
     *
     * @param nombreArchivo nombre del archivo PDF a cargar
     * @return Resource con el archivo PDF solicitado
     * @throws RecursoNOEncontradoException si el archivo no existe en la base de datos
     */
    @Transactional(readOnly = true)
    public Resource obtenerArchivo (String nombreArchivo) {
        if (!polizaRepositorio.existsByNumeroPdf(nombreArchivo)) {
            throw new RecursoNOEncontradoException("Archivo pdf", "numero", nombreArchivo);
        }
        return archivoAlmacenamientoServicio.cargarRecurso(nombreArchivo, archivoAlmacenamientoServicio.getRutaLocal());
    }

    /**
     * Obtiene un archivo PDF temporal (en proceso de carga).
     *
     * @param nombreArchivo nombre del archivo PDF temporal a cargar
     * @return Resource con el archivo temporal solicitado
     */
    @Transactional(readOnly = true)
    public Resource obtenerArchivoTemporal (String nombreArchivo) {
        return archivoAlmacenamientoServicio.cargarRecurso(nombreArchivo, archivoAlmacenamientoServicio.getRutaTemporal());
    }

    /**
     * Procesa un PDF de póliza utilizando IA para extraer datos automáticamente.
     * El método valida y enriquece los datos extraídos (aseguradora y cliente)
     * e intenta agrupar con clientes existentes si es posible.
     * Almacena el archivo en una ruta temporal durante el proceso.
     *
     * @param archivo archivo MultipartFile del PDF de la póliza a procesar
     * @param idCliente ID del cliente asociado a la póliza (puede ser null para detección automática)
     * @return GestionPolizaDTO con los datos extraídos y enriquecidos del PDF
     * @throws IOException si hay error al procesar el archivo
     * @throws ArchivoInvalidoExcepcion si el formato del archivo no es válido
     */
    @Transactional
    public GestionPolizaDTO procesarDatosPdfConIa(MultipartFile archivo, Long idCliente) throws IOException {

        try {
            registro.debug("Preparando documento pdf de la poliza a texto para el prompt: {}", archivo.getOriginalFilename());
            String texto = pdfPolizaServicio.prepararParaIa(archivo);

            List<RamoEntidad> ramos =ramoRepositorio.findAll();
            // Convertimos la lista de la DB en un String legible (ej: "1 - AUTOMOVIL, 2 - SALUD...")
            String listaRamosFormateada = ramos.stream()
                    .map(r -> "- ID %d: %s".formatted(r.getIdRamo(), r.getNombre()))
                    .collect(Collectors.joining("\n"));

            // Convertimos el Enum TipoDocumentoCliente a un String formateado
            String tiposDocumentoCliente = Arrays.stream(TipoDocumentoCliente.values())
                    .map(tipoDocumentoCliente -> "- " + tipoDocumentoCliente.name())
                    .collect(Collectors.joining("\n"));


            String rolProceso = """
                Eres un extractor de datos especializado en pólizas de seguros colombianas.
                
                    REGLAS ABSOLUTAS:
                    - Extrae ÚNICAMENTE información explícita en el texto. NUNCA inventes ni completes datos.
                    - Si un dato no está o no puedes identificarlo con certeza, devuelve null en ese campo.
                    - Responde EXCLUSIVAMENTE con JSON válido. Sin texto adicional, sin explicaciones, sin markdown.
                    - Los formatos de cada campo son obligatorios y no negociables.
                    - Los campos representan conceptos, no etiquetas textuales exactas. Búscalos por su SIGNIFICADO
                      usando el contexto del documento, ya que cada póliza puede usar terminología diferente.
                    - Los datos de contacto (teléfono, correo, dirección) en gestionClienteDTO corresponden
                      SIEMPRE al tomador. Nunca uses datos de contacto de la aseguradora, agente o intermediario.
                """;

            String datosPrompt = """
                Extrae los datos de la siguiente póliza y devuélvelos en el JSON indicado.

                    CAMPOS Y FORMATOS:
                    - codigoPoliza: String 10-20 caracteres, solo letras y números sin espacios. Tómalo tal como aparece.
                    - fechaInicio: Fecha en que inicia la vigencia del seguro. Formato: "yyyy-MM-dd".
                    - fechaFin: Fecha en que termina la vigencia del seguro. Formato: "yyyy-MM-dd".
                    - primaNeta: Valor base del seguro SIN impuestos, recargos ni descuentos adicionales.
                      Decimal hasta 15 enteros y 2 decimales, sin símbolos ni puntos de miles. Ej: 850000.00
                    - primaTotal: Valor FINAL a pagar incluyendo todos los cargos e impuestos. Mismo formato que primaNeta.
                    - placa: Placa del vehículo, entre 4 y 15 caracteres. null si no es póliza de vehículo.
                    - descripcion: Resumen corto y comprensible del objeto asegurado o cobertura principal.
                      Entre 10 y 150 caracteres. Redáctalo tú a partir del contexto si no hay una descripción explícita. 
                      null solo si no hay información suficiente para construirla.
                    - nombreCliente: Nombre completo del tomador, es decir la persona responsable del pago. Tómalo tal como aparece en el documento.
                    - idRamo: Elige el ID del ramo que mejor se adapte al tipo de póliza del documento.
                      Solo puedes elegir uno de los siguientes, devuelve únicamente el número ID:
                      %s
                    - numeroDocumentoAseguradora: Número de identificación fiscal/tributaria de la empresa aseguradora que emite la póliza.
                      Tómalo tal como aparece en el documento.
                    - nombreAseguradora: Extrae el nombre comercial o la razón social de la aseguradora.
                      Tómalo tal como aparece en el documento.
                      
                    DATOS DEL TOMADOR (gestionClienteDTO) - Determina si el tomador es persona o empresa analizando el nombre y el tipo de documento (NIT suele ser empresa). Si es empresa, respeta su puntuación legal. - Es la(razon social) o el responsable del pago de la póliza:
                    - nombre: Extrae el nombre completo; si es persona natural, usa solo los nombres propios; si es empresa, extrae la razón social íntegra incluyendo puntos y siglas u otros caracteres. 
                      En caso de ser empresa, el campo apellido debe ser obligatoriamente null.
                    - apellido: Solo los apellidos. Entre 2 y 40 caracteres, solo letras y espacios.
                      null si es una empresa o razón social.
                    - tipoDocumento: Tipo de documento de identidad. Debe ser exactamente uno de estos valores:
                      %s
                    - numeroDocumento: Número del documento de identidad del tomador. Solo números, sin puntos ni guiones ni espacios.
                    - telefono: Número de teléfono de contacto del TOMADOR (no de la aseguradora ni de intermediarios).
                      Debe ser una cadena de exactamente 10 dígitos numéricos. Si el número recibido tiene 7 dígitos,
                      anteponer el prefijo "601"; si ya tiene 10 dígitos, dejarlo tal cual. Solo números sin espacios.
                      null si no aparece en el documento.
                    - correo: Correo electrónico de contacto del TOMADOR (no de la aseguradora ni de intermediarios).
                      Máximo 100 caracteres. null si no aparece en el documento.
                    - direccion: Dirección de residencia o fiscal del tomador. Máximo 100 caracteres.
                    - ciudad: Ciudad de residencia o fiscal del tomador. Máximo 60 caracteres.

                    IMPORTANTE - telefono y correo:
                    - Ambos datos deben pertenecer exclusivamente al TOMADOR. Ignora cualquier correo o teléfono
                      que pertenezca a la aseguradora, al agente, al broker, al defensor del consumidor,
                      a la Superintendencia Financiera o a cualquier tercero.
                    - REGLA CRÍTICA: Un correo o teléfono que aparezca en frases como "puede contactar a...",
                      "podrá solicitar a...", "escríbanos a...", "comuníquese con..." NO es dato del tomador,
                      es dato de la entidad receptora. Ignóralo completamente.
                    - El correo del tomador es el que identifica al tomador como EMISOR o TITULAR del contacto,
                      no como destinatario de una comunicación.
                    - Si el documento no contiene teléfono ni correo propios del tomador, devuelve ambos en null.
                      No inventes ni tomes datos de contacto de otras entidades.
                    - Al menos uno de los dos (telefono o correo) debe tener valor SOLO si realmente pertenece
                      al tomador. Si no hay ninguno del tomador, ambos van en null.
                    
                Responde SOLO con este JSON:
                {
                  "codigoPoliza": null,
                  "fechaInicio": null,
                  "fechaFin": null,
                  "primaNeta": null,
                  "primaTotal": null,
                  "placa": null,
                  "descripcion": null,
                  "nombreCliente": null,
                  "idRamo": null,
                  "numeroDocumentoAseguradora": null,
                  "nombreAseguradora": null,
                  "gestionClienteDTO": {
                    "nombre": null,
                    "apellido": null,
                    "tipoDocumento": null,
                    "numeroDocumento": null,
                    "telefono": null,
                    "correo": null,
                    "direccion": null,
                    "ciudad": null
                  }
                }

                TEXTO DE LA PÓLIZA:
                ---
                %s
                ---
                """.formatted(listaRamosFormateada, tiposDocumentoCliente, texto);

            PeticionIaDTO peticion = PeticionIaDTO.conRespuesta(rolProceso, datosPrompt, GestionPolizaDTO.class);

            registro.debug("Procesamiento de los datos por la IA");

            GestionPolizaDTO datosExtraidos = iaServicio.procesar(peticion, GestionPolizaDTO.class, "EXTRACCION_POLIZA");

            registro.debug("Almacenando archivo temporal");
            String nombreArchivo = archivoAlmacenamientoServicio.generarNombreArchivo(datosExtraidos.getCodigoPoliza(), archivo.getOriginalFilename());
            archivoAlmacenamientoServicio.almacenar(archivo, archivoAlmacenamientoServicio.getRutaTemporal(), nombreArchivo);
            datosExtraidos.setNumeroPdf(nombreArchivo);
            registro.debug("Archivo temporal almacenado con exito: {}", nombreArchivo);

            if (datosExtraidos.getGestionClienteDTO().getTelefono() != null && datosExtraidos.getGestionClienteDTO().getTelefono().length() == 7) {
                datosExtraidos.getGestionClienteDTO().setTelefono("601" + datosExtraidos.getGestionClienteDTO().getTelefono());
            }
            datosExtraidos.setEstadoPago(EstadoPagoPoliza.PENDIENTE);


            if (datosExtraidos.getNumeroDocumentoAseguradora() != null && aseguradoraRepositorio.existsByIdAseguradora(datosExtraidos.getIdAseguradora())) {
                AseguradoraEntidad aseguradoraEntidad = aseguradoraRepositorio.findByNumeroDocumento(datosExtraidos.getNumeroDocumentoAseguradora());
                datosExtraidos.setIdAseguradora(aseguradoraEntidad.getIdAseguradora());
            } else if (datosExtraidos.getNombreAseguradora() != null) {
                // La consulta ahora devuelve un Optional (un solo resultado posible)
                Optional<AseguradoraEntidad> coincidencia = aseguradoraRepositorio.buscarPorSimilitud(datosExtraidos.getNombreAseguradora());

                if (coincidencia.isPresent()) {
                    AseguradoraEntidad aseguradora = coincidencia.get();
                    datosExtraidos.setIdAseguradora(aseguradora.getIdAseguradora());
                }
            }

            if (idCliente != null && clienteRepositorio.existsById(idCliente)) {
                datosExtraidos.setIdCliente(idCliente);
            } else if (idCliente == null && clienteRepositorio.existsByNumeroDocumento(datosExtraidos.getGestionClienteDTO().getNumeroDocumento())) {
                ClienteEntidad clienteExistente = clienteRepositorio.findByNumeroDocumento(datosExtraidos.getGestionClienteDTO().getNumeroDocumento());
                datosExtraidos.setIdCliente(clienteExistente.getIdCliente());
            } else {
                // true: NO EXISTE EL CLIENTE
                datosExtraidos.setClienteExiste(true);
            }

            return datosExtraidos;
        } catch (IOException ex) {
            throw new ArchivoInvalidoExcepcion(ex.getMessage());
        }
    }



    /**
     * Registra una nueva póliza en el sistema validando datos únicos.
     * Valida que el código de póliza no esté duplicado y que la placa (si existe)
     * no sea usada en otra póliza activa. Requiere un archivo PDF.
     * El archivo se almacena en la ruta permanente tras ser guardada lafóliza.
     *
     * @param gestionPolizaDTO objeto DTO con los datos de la póliza a registrar
     * @param idUsuario ID del usuario asesor que registra la póliza
     * @return RespuestaPolizaDTO con los datos de la póliza creada
     * @throws ConflictoExcepcion si el código de póliza existe o la placa está activa en otro cliente
     * @throws RecursoNOEncontradoException si alguno de los IDs (cliente, ramo, aseguradora) no existe
     * @throws NegocioExcepcion si ocurre un error de integridad de datos durante la creación
     */
    @Transactional
    public RespuestaPolizaDTO registrarPoliza(GestionPolizaDTO gestionPolizaDTO, Long idUsuario) {
        registro.debug("Registrando poliza con codigo: {}", gestionPolizaDTO.getCodigoPoliza());

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();


        if (polizaRepositorio.existsByCodigoPoliza(gestionPolizaDTO.getCodigoPoliza())) {
            errores.add(new ConflictoExcepcion.ErrorCampo("codigoPoliza", "El codigo de poliza ya existe"));
        }

        if ((gestionPolizaDTO.getPlaca() != null && !gestionPolizaDTO.getPlaca().isBlank()) && polizaRepositorio.existePlacaActivaEnOtroCliente(gestionPolizaDTO.getPlaca(), gestionPolizaDTO.getIdCliente(), EstadoPoliza.ACTIVA)) {
            errores.add(new ConflictoExcepcion.ErrorCampo("placa", "La placa se encuentra activa en otro cliente"));
        }

        if (gestionPolizaDTO.getNumeroPdf() == null && (gestionPolizaDTO.getArchivoPoliza() == null || gestionPolizaDTO.getArchivoPoliza().isEmpty())) {
            errores.add(new ConflictoExcepcion.ErrorCampo("archivoPoliza", "Debes agregar un documento pdf"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos registrados", errores);
        }

        ClienteEntidad clienteEntidad = clienteRepositorio.findById(gestionPolizaDTO.getIdCliente()).orElseThrow(() -> new RecursoNOEncontradoException("Cliente", "id", gestionPolizaDTO.getIdCliente()));

        UsuarioEntidad usuarioEntidad = usuarioRepositorio.findById(idUsuario).orElseThrow(() -> new RecursoNOEncontradoException("usuario", "id", idUsuario));

        RamoEntidad ramoEntidad = ramoRepositorio.findById(gestionPolizaDTO.getIdRamo()).orElseThrow(() -> new RecursoNOEncontradoException("ramo", "id", gestionPolizaDTO.getIdRamo()));

        AseguradoraEntidad aseguradoraEntidad = aseguradoraRepositorio.findById(gestionPolizaDTO.getIdAseguradora()).orElseThrow(() -> new RecursoNOEncontradoException("aseguradora", "id", gestionPolizaDTO.getIdAseguradora()));

        try {
            Poliza poliza = polizaMapper.toPoliza(gestionPolizaDTO, ramoEntidad, aseguradoraEntidad);

            PolizaEntidad polizaEntidad = polizaMapper.toPolizaEntidad(poliza);
            polizaEntidad.setCliente(clienteEntidad);
            polizaEntidad.setUsuario(usuarioEntidad);
            polizaEntidad.setRamo(ramoEntidad);
            polizaEntidad.setAseguradora(aseguradoraEntidad);

            registro.debug("Almacenando archivo pdf para la poliza : {}", polizaEntidad.getCodigoPoliza());

            /**
             * Si el usuario luego de completar el flujo de IA y llegar a crar la poliza ya el gestionPolizaDTO trae el numeroPdf por lo tanto no necesito asignarlo de nuevo.
             * Si el usuario completo la informacion manualmente de la poliza y envia el archivo en ese caso si se necesita de establecer un nombre para luego almacenarlo.
             * Si el usuario no envio el archivo el numeroPdf estara vacio y arrojara respectiva excepcion
             */
            if (gestionPolizaDTO.getArchivoPoliza() != null && !gestionPolizaDTO.getArchivoPoliza().isEmpty()) {
                String nombreArchivo = archivoAlmacenamientoServicio.generarNombreArchivo(gestionPolizaDTO.getCodigoPoliza(), gestionPolizaDTO.getArchivoPoliza().getOriginalFilename());
                polizaEntidad.setNumeroPdf(nombreArchivo);
            } else {
                polizaEntidad.setNumeroPdf(gestionPolizaDTO.getNumeroPdf());
            }


            PolizaEntidad polizaGuardada = polizaRepositorio.save(polizaEntidad);
            registro.debug("Poliza guardada: {}", polizaGuardada.getIdPoliza());

            /**
             * Para no romper el flujo de proceso de crear con ia y no me arroje excepcion de archivo nulo
             */
            // Validación para que detecte si es nulo O si está vacío
            if (gestionPolizaDTO.getArchivoPoliza() == null || gestionPolizaDTO.getArchivoPoliza().isEmpty()) {
                archivoAlmacenamientoServicio.moverRecurso(polizaGuardada.getNumeroPdf());
            } else {
                archivoAlmacenamientoServicio.almacenar(
                        gestionPolizaDTO.getArchivoPoliza(),
                        archivoAlmacenamientoServicio.getRutaLocal(),
                        polizaEntidad.getNumeroPdf()
                );
            }

            registro.debug("Archivo pdf almacenado con exito : {}", polizaEntidad.getNumeroPdf());

            return polizaMapper.toRespuestaPoliza(polizaGuardada);
        } catch (DataIntegrityViolationException ex) {
            registro.error("Error de integridad al crear poliza: {}", ex.getMessage());
            throw new NegocioExcepcion("No se pudo crear la poliza. Intenta nuevamente", "CREACION_POLIZA_FALLIDA");
        }
    }

    /**
     * Obtiene los datos de una póliza específica para ser editados.
     *
     * @param idPoliza ID de la póliza que se desea consultar
     * @return GestionPolizaDTO con la información de la póliza y su cliente
     * @throws RecursoNOEncontradoException si el ID de la póliza no existe en la base de datos
     */
    @Transactional(readOnly = true)
    public GestionPolizaDTO obtenerPoliza(Long idPoliza) {
        registro.debug("Obtener poliza con ID: {}", idPoliza);

        GestionPolizaDTO gestionPolizaDTO = polizaRepositorio.buscarDatosPoliza(idPoliza).orElseThrow( () -> new RecursoNOEncontradoException("poliza", "id", idPoliza) );
        System.out.println(gestionPolizaDTO.getNombreCliente());
        return gestionPolizaDTO;
    }

    /**
     * Actualiza los datos de una póliza existente validando el código único.
     * Permite actualizar el cliente, ramo, aseguradora y archivo PDF asociados.
     * Establece una variable de sesión para que los triggers de BD registren quién hizo el cambio.
     *
     * @param idPoliza ID de la póliza a actualizar
     * @param gestionPolizaDTO objeto DTO con los nuevos datos de la póliza
     * @param idUsuarioActual ID del usuario autenticado que realiza la actualización
     * @throws RecursoNOEncontradoException si el ID de la póliza o sus referencias no existen
     * @throws ConflictoExcepcion si el código de póliza ya existe para otra póliza
     * @throws NegocioExcepcion si ocurre un error de integridad de datos durante la actualización
     */
    @Transactional
    public void actualizarPoliza(Long idPoliza, GestionPolizaDTO gestionPolizaDTO, Long idUsuarioActual) {
        registro.debug("Actualizando poliza con numero de documento: {}", gestionPolizaDTO.getCodigoPoliza());

        PolizaEntidad polizaEntidad = polizaRepositorio.findById(idPoliza).orElseThrow(() -> new RecursoNOEncontradoException("Poliza", "id", idPoliza));

        ArrayList<ConflictoExcepcion.ErrorCampo> errores = new ArrayList<>();

        if (polizaRepositorio.existsByCodigoPolizaAndIdPolizaNot(gestionPolizaDTO.getCodigoPoliza(), idPoliza)) {
            errores.add( new ConflictoExcepcion.ErrorCampo("codigoPoliza", "El codigo de poliza ya se encuentra registrado"));
        }

        if (errores.size() > 0) {
            throw new ConflictoExcepcion("Conflicto de datos registrados", errores);
        }

        if (gestionPolizaDTO.getArchivoPoliza() != null && !gestionPolizaDTO.getArchivoPoliza().isEmpty() && polizaEntidad.getNumeroPdf() != null) {
            registro.debug("Actualizando archivo pdf para la poliza : {}", polizaEntidad.getIdPoliza());
            String nombreArchivo = archivoAlmacenamientoServicio.generarNombreArchivo(polizaEntidad.getCodigoPoliza(), gestionPolizaDTO.getArchivoPoliza().getOriginalFilename());
            polizaEntidad.setNumeroPdf(nombreArchivo);
        }

        if (gestionPolizaDTO.getIdCliente() != null) {
            ClienteEntidad clienteEntidad = clienteRepositorio.findById(gestionPolizaDTO.getIdCliente()).orElseThrow( () -> new RecursoNOEncontradoException("cliente", "id", gestionPolizaDTO.getIdCliente()));

            polizaEntidad.setCliente(clienteEntidad);
        }

        if (gestionPolizaDTO.getIdRamo() != null) {
            RamoEntidad ramoEntidad = ramoRepositorio.findById(gestionPolizaDTO.getIdRamo()).orElseThrow( () -> new RecursoNOEncontradoException("ramo", "id", gestionPolizaDTO.getIdRamo()));

            polizaEntidad.setRamo(ramoEntidad);
        }

        if (gestionPolizaDTO.getIdAseguradora() != null) {
            AseguradoraEntidad aseguradoraEntidad = aseguradoraRepositorio.findById(gestionPolizaDTO.getIdAseguradora()).orElseThrow( () -> new RecursoNOEncontradoException("aseguradora", "id", gestionPolizaDTO.getIdAseguradora()));

            polizaEntidad.setAseguradora(aseguradoraEntidad);
        }

        try {
            gestionPolizaDTO.actualizarPoliza(polizaEntidad);

            polizaMapper.toPoliza(polizaEntidad);

            jdbcTemplate.update("SET @usuario_actual = ?", idUsuarioActual);

            polizaRepositorio.saveAndFlush(polizaEntidad);
            registro.info("Poliza actualizada exitosamente con ID: {}", polizaEntidad.getCodigoPoliza());

            if (gestionPolizaDTO.getArchivoPoliza() != null && !gestionPolizaDTO.getArchivoPoliza().isEmpty() && polizaEntidad.getNumeroPdf() != null) {
                archivoAlmacenamientoServicio.almacenar(
                        gestionPolizaDTO.getArchivoPoliza(),
                        archivoAlmacenamientoServicio.getRutaLocal(),
                        polizaEntidad.getNumeroPdf()
                );
            }

            registro.debug("Archivo pdf actualizado con exito : {}", polizaEntidad.getNumeroPdf());
        } catch (DataIntegrityViolationException ex) {
            registro.error("Error de integridad al actualizar poliza: {}", ex.getMessage());
            throw new NegocioExcepcion("No se pudo actualizar la poliza. Intenta nuevamente", "ACTUALIZACION_POLIZA_FALLIDA");
        }
    }


    /**
     * Cancela una póliza existente registrando el motivo de la cancelación.
     * El cambio de estado es registrado indicando quién realizó la acción.
     * Utiliza un trigger de base de datos para auditar los cambios.
     *
     * @param cancelarPolizaDTO objeto DTO que contiene el ID de la póliza y el motivo de cancelación
     * @param idUsuarioActual ID del usuario autenticado que realiza la cancelación
     * @throws RecursoNOEncontradoException si el ID de la póliza no existe en la base de datos
     */
    @Transactional
    public void cancelarPoliza(CancelarPolizaDTO cancelarPolizaDTO, Long idUsuarioActual) {

        registro.error("Cancelando poliza: {}", cancelarPolizaDTO.getIdPoliza());

        PolizaEntidad polizaEntidad = polizaRepositorio.findById(cancelarPolizaDTO.getIdPoliza()).orElseThrow(() -> new RecursoNOEncontradoException("poliza", "id", cancelarPolizaDTO.getIdPoliza()));

        Poliza poliza = polizaMapper.toPoliza(polizaEntidad);

        poliza.cancelarPoliza(cancelarPolizaDTO.getMotivo());

        jdbcTemplate.update("SET @usuario_actual = ?", idUsuarioActual);

        // Sincronizamos los cambios del dominio sobre la entidad que Hibernate ya está trackeando — NO se crea objeto nuevo
        polizaMapper.sincronizar(poliza, polizaEntidad);


        // Es opcional — Hibernate detecta los cambios automáticamente al cerrar la transacción (dirty checking)
        polizaRepositorio.save(polizaEntidad);

        registro.error("Poliza cancelada con exito: {}", polizaEntidad.getIdPoliza());

    }

}