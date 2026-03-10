package com.proyecto.ipas.infraestructura.externo.almacenamiento;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;


/**
 * Contrato abstrato para el almacenamiento y gestión de archivos de pólizas.
 * 
 * Define la interfaz genérica que permite distintas implementaciones de almacenamiento
 * (local, nube, etc.). Actualmente implementada por {@link ArchivoAlmacenamientoServicio}
 * para almacenamiento LOCAL en el sistema de archivos.
 * 
 * Las operaciones incluyen: almacenamiento temporal, movimiento a permanente, carga para descarga,
 * y consulta de rutas de almacenamiento. Todos los métodos deben validar seguridad de rutas
 * para prevenir path traversal attacks.
 */
public interface ArchivoAlmacenamiento {

    /**
     * Almacena un archivo PDF en el directorio especificado con validaciones de seguridad.
     * 
     * Utilizado para guardar archivos cargados por usuarios. El archivo se valida antes de almacenarse,
     * verificando tipo, extensión y tamaño. Se normaliza la ruta de destino para prevenir path traversal.
     * 
     * @param archivo el MultipartFile que se desea almacenar (típicamente de un formulario web)
     * @param destino la ruta del directorio donde se almacenará el archivo
     * @param nombreArchivo el nombre con el que se guardará el archivo (generado con UUID + codigoPoliza)
     * @throws ArchivoInvalidoExcepcion si el archivo no cumple validaciones
     * @throws AlmacenamientoExcepcion si hay error de I/O o seguridad en la ruta
     */
    void almacenar(MultipartFile archivo, Path destino, String nombreArchivo);

    /**
     * Mueve un archivo del directorio temporal al almacenamiento permanente.
     * 
     * Se invoca después de procesar y validar completamente un PDF. Realiza verificaciones de seguridad
     * para ambas rutas y confirma que el archivo temporal existe antes de moverlo.
     * 
     * @param numeroPdf el nombre del archivo generado (UUID_codigoPoliza.pdf)
     * @throws SecurityException si las rutas no pasan validación de seguridad
     * @throws RecursoNOEncontradoException si el archivo temporal no existe
     * @throws AlmacenamientoExcepcion si hay error al mover el archivo
     */
    void moverRecurso(String numeroPdf);

    /**
     * Carga un archivo PDF del almacenamiento como recurso descargable para el cliente.
     * 
     * Prepara un archivo existente en el almacenamiento permanente para ser descargado a través
     * de HTTP. Verifica que el archivo exista y sea legible antes de devolverlo como Resource.
     * 
     * @param numeroPdf el nombre del archivo a descargar
     * @param destino la ruta del directorio donde se busca el archivo (generalmente almacenamiento permanente)
     * @return un objeto Spring Resource listo para servir al cliente
     * @throws RecursoNOEncontradoException si el archivo no existe o no es legible
     * @throws AlmacenamientoExcepcion si hay error al acceder al archivo
     */
    Resource cargarRecurso(String numeroPdf, Path destino);

    /**
     * Obtiene la ruta completa de un archivo en el almacenamiento permanente.
     * 
     * Devuelve como String la ubicación absoluta del archivo para casos donde se necesita
     * registrar la ruta en la base de datos o para auditoría.
     * 
     * @param numeroPdf el nombre del archivo generado
     * @return la ruta absoluta completa del archivo como String
     */
    String getRutaArchivo(String numeroPdf);

    /**
     * Obtiene la ruta completa de un archivo en el almacenamiento temporal.
     * 
     * Devuelve como String la ubicación en el directorio temporal para casos donde se necesita
     * referenciar archivos en procesamiento o validación.
     * 
     * @param numeroPdf el nombre del archivo generado
     * @return la ruta absoluta completa del archivo temporal como String
     */
    String getRutaArchivoTemporal(String numeroPdf);





}
