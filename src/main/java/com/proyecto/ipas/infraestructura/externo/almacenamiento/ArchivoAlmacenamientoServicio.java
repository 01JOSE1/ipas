package com.proyecto.ipas.infraestructura.externo.almacenamiento;

import com.proyecto.ipas.presentacion.excepcion.AlmacenamientoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.ArchivoInvalidoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.RecursoNOEncontradoException;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

/**
 * Implementación LOCAL para el almacenamiento de archivos PDF de las pólizas.
 * 
 * Gestiona dos directorios: uno temporal para archivos en procesamiento y otro 
 * permanente para archivos finales. Incluye validaciones de seguridad contra 
 * ataques path traversal y control de tipos/tamaños de archivo.
 */
@Service
public class ArchivoAlmacenamientoServicio implements ArchivoAlmacenamiento {

    private final Path rutaLocal;
    private final Path rutaTemporal;
    private final ArchivoAlmacenamientoPropiedades propiedades;

    /**
     * Constructor que inicializa las rutas del almacenamiento desde las propiedades.
     * 
     * @param propiedades configuración de almacenamiento inyectada desde application.properties
     */
    public ArchivoAlmacenamientoServicio(ArchivoAlmacenamientoPropiedades propiedades) {
        this.propiedades = propiedades;
        this.rutaLocal = Paths.get(propiedades.getDestino());
        this.rutaTemporal = Paths.get(propiedades.getDestinoTemporal());
    }

    /**
     * Inicialización posterior a la construcción que crea los directorios si no existen.
     * 
     * @throws AlmacenamientoExcepcion si no se pueden crear los directorios
     */
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rutaLocal);
            Files.createDirectories(rutaTemporal);
        } catch (IOException e) {
            throw new AlmacenamientoExcepcion("No se pudo inicializar el almacenamiento: " + e);
        }
    }

    /**
     * Almacena un archivo en la ruta especificada con medidas de seguridad.
     * 
     * Valida el archivo, normaliza la ruta de destino para prevenir path traversal,
     * y copia el contenido del archivo de entrada al destino especificado.
     * 
     * @param archivo el archivo a almacenar (MultipartFile desde la petición)
     * @param destino la ruta del directorio destino
     * @param nombreArchivo el nombre del archivo generado
     * @throws ArchivoInvalidoExcepcion si el archivo no pasa las validaciones
     * @throws AlmacenamientoExcepcion si hay error de I/O
     */
    @Override
    public void almacenar(MultipartFile archivo, Path destino, String nombreArchivo) {
        validarArchivo(archivo);

        try {

            Path destinoArchivo = destino.resolve(nombreArchivo).normalize(); // Con normalize(): Limpia la ruta y previene ataques tipo Path Traversal

            if (!destinoArchivo.startsWith(destino.normalize())) {
                throw new AlmacenamientoExcepcion("No se puede almacenar fuera del directorio permitido");
            }

            try (InputStream archivoOrigen = archivo.getInputStream()) {
                Files.copy(archivoOrigen, destinoArchivo, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            throw new AlmacenamientoExcepcion("Error al almacenar el archivo: " + e);
        }

    }


    /**
     * Mueve un archivo desde el directorio temporal al directorio permanente.
     * 
     * Realiza validaciones de seguridad en ambas rutas para prevenir path traversal.
     * Se utiliza después de procesar un PDF para confirmar su almacenamiento definitivo.
     * 
     * @param numeroPdf el nombre del archivo (generado con UUID y código de póliza)
     * @throws SecurityException si las rutas especificadas son inválidas
     * @throws RecursoNOEncontradoException si el archivo temporal no existe
     * @throws AlmacenamientoExcepcion si hay error al mover el archivo
     */
    @Override
    public void moverRecurso(String numeroPdf) {
        try {
            Path origen = rutaTemporal.resolve(numeroPdf).normalize();
            Path destino = rutaLocal.resolve(numeroPdf).normalize();

            if (!origen.startsWith(rutaTemporal)) {
                throw new SecurityException("Ruta origen inválida");
            }

            if (!destino.startsWith(rutaLocal)) {
                throw new SecurityException("Ruta destino inválida");
            }

            if (!Files.exists(origen) || !Files.isRegularFile(origen)) {
                throw new RecursoNOEncontradoException(
                        "archivo pdf",
                        "nombre del archivo: ",
                        numeroPdf
                );
            }

            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new AlmacenamientoExcepcion("Error al mover el archivo: " + e);
        }
    }


    /**
     * Carga un archivo PDF del almacenamiento permanente como recurso descargable.
     * 
     * Prepara el archivo para ser enviado al cliente HTTP con headers apropiados.
     * Utiliza Spring Resource para servir archivos existentes en el sistema de archivos.
     * 
     * @param numeroPdf el nombre del archivo a descargar
     * @param destino la ruta del directorio donde se busca el archivo (generalmente la ruta permanente)
     * @return Resource del archivo listo para descargar
     * @throws RecursoNOEncontradoException si el archivo no existe en la ruta especificada
     * @throws AlmacenamientoExcepcion si hay error al acceder al archivo
     */
    @Override
    public Resource cargarRecurso(String numeroPdf, Path destino) {
        try {
            Path archivo = destino.resolve(numeroPdf);

            Resource recurso = new UrlResource(archivo.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                return recurso;
            } else {
                throw new RecursoNOEncontradoException("archivo pdf", "nombre del archivo: ", numeroPdf);
            }
        } catch (MalformedURLException e) {
            throw new RecursoNOEncontradoException("archivo pdf", "nombre del archivo: ", numeroPdf);
        }
    }

    public Path getRutaTemporal() {
        return rutaTemporal;
    }

    public Path getRutaLocal() {
        return rutaLocal;
    }

    /**
     * Obtiene la ruta completa del archivo en el almacenamiento permanente.
     * 
     * @param numeroPdf el nombre del archivo generado
     * @return la ruta absoluta del archivo como String
     */
    @Override
    public String getRutaArchivo(String numeroPdf) {
        return rutaLocal.resolve(numeroPdf).toString();
    }

    /**
     * Obtiene la ruta completa del archivo en el directorio temporal.
     * 
     * @param numeroPdf el nombre del archivo generado
     * @return la ruta absoluta del archivo temporal como String
     */
    @Override
    public String getRutaArchivoTemporal(String numeroPdf) {
        return rutaTemporal.resolve(numeroPdf).toString();
    }

    /**
     * Genera un nombre de archivo único combinando UUID, código de póliza y extensión.
     * 
     * Utiliza UUID para garantizar unicidad global del nombre, asegurando que no haya
     * conflictos entre archivos almacenados. El nombre incluye el código de póliza para
     * facilitar trazabilidad y búsqueda de archivos relacionados.
     * 
     * @param codigoPoliza el código identificador de la póliza (no puede estar en blanco)
     * @param nombreArchivoOriginal el nombre del archivo original para extraer su extensión
     * @return un nombre único con formato: UUID_codigoPoliza.extension
     * @throws ArchivoInvalidoExcepcion si codigoPoliza está en blanco
     */
    public String generarNombreArchivo(String codigoPoliza, String nombreArchivoOriginal) {

        if (codigoPoliza.isBlank()) {
            throw new ArchivoInvalidoExcepcion("Faltan datos (codigoPoliza) para generar nombre del archivo.");
        }

        String extension = getExtensionArchivo(nombreArchivoOriginal);

        return UUID.randomUUID().toString() + "_" + codigoPoliza + extension;
    }

    /**
     * Valida que un archivo cumple con los requisitos de almacenamiento.
     * 
     * Realiza múltiples validaciones:
     * - Verifica que el archivo no sea nulo ni esté vacío
     * - Valida que el tipo MIME sea "application/pdf"
     * - Verifica que la extensión sea ".pdf"
     * - Comprueba que el tamaño no exceda el límite configurado (default 10MB)
     * 
     * @param archivo el archivo a validar desde el formulario multipart
     * @throws ArchivoInvalidoExcepcion si alguna validación falla con mensaje descriptivo
     */
    public void validarArchivo(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoInvalidoExcepcion("El archivo es nulo o vacio.");
        }

        String tipoArchivo = archivo.getContentType();
        boolean esTipoPermitido = Arrays.asList(propiedades.getTipoContenidoPermitido()).contains(tipoArchivo);

        if (!esTipoPermitido) {
            throw new ArchivoInvalidoExcepcion("Tipo de archivo no permitido: " + tipoArchivo);
        }

        String extension = getExtensionArchivo(archivo.getOriginalFilename());
        boolean esExtensionPermitida = Arrays.asList(propiedades.getExtensionesPermitidas()).contains(extension.toLowerCase());

        if (!esExtensionPermitida) {
            throw new ArchivoInvalidoExcepcion("Extension de archivo no permitida: " + extension);
        }

        if (archivo.getSize() > propiedades.getTamanoMaximoBytes()) {

            System.out.println("SI SE VALIDO EL TAMAÑO DEL ARCHIVO: " + archivo.getSize());
            throw new ArchivoInvalidoExcepcion(String.format("El archivo escede el tamaño maximo de %d MB", propiedades.getTamanoMaximoArchivo()));
        }

    }

    /**
     * Extrae la extensión de un nombre de archivo.
     * 
     * Método auxiliar privado que localiza el último punto en el nombre y extrae
     * la extensión incluyendo el punto (ej: ".pdf"). Si el archivo no tiene extensión
     * o el nombre es nulo, devuelve una cadena vacía.
     * 
     * @param nombreArchivoOriginal el nombre del archivo del que extraer extensión
     * @return la extensión con punto (ej: ".pdf") o cadena vacía si no hay extensión
     */
    private String getExtensionArchivo(String nombreArchivoOriginal) {
        if (nombreArchivoOriginal == null || !nombreArchivoOriginal.contains(".")) {
            return "";
        }
        return nombreArchivoOriginal.substring(nombreArchivoOriginal.lastIndexOf("."));
    }
}
