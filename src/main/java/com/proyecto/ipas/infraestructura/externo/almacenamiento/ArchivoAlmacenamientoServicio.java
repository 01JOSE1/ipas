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
 * Implementación LOCAL para el almacenamiento de archivos PDF de las polizas
 */
@Service
public class ArchivoAlmacenamientoServicio implements ArchivoAlmacenamiento {

    private final Path rutaLocal;
    private final ArchivoAlmacenamientoPropiedades propiedades;

    public ArchivoAlmacenamientoServicio(ArchivoAlmacenamientoPropiedades propiedades) {
        this.propiedades = propiedades;
        this.rutaLocal = Paths.get(propiedades.getDestino());
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rutaLocal);
        } catch (IOException e) {
            throw new AlmacenamientoExcepcion("No se pudo inicializar el almacenamiento: " + e);
        }
    }


    @Override
    public String almacenar(MultipartFile archivo, String codigoPoliza) {
        validarArchivo(archivo);

        try {
            String nombreArchivo = generarNombreArchivo(codigoPoliza, archivo.getOriginalFilename());

            Path destinoArchivo = rutaLocal.resolve(nombreArchivo).normalize(); // Con normalize(): Limpia la ruta y previene ataques tipo Path Traversal

            if (!destinoArchivo.getParent().equals(rutaLocal)) {
                throw new AlmacenamientoExcepcion("No se puede almacenar fuera del directorio permitido");
            }

            try (InputStream archivoOrigen = archivo.getInputStream()) {
                Files.copy(archivoOrigen, destinoArchivo, StandardCopyOption.REPLACE_EXISTING);
            }

            return nombreArchivo;

        } catch (IOException e) {
            throw new AlmacenamientoExcepcion("Error al almacenar el archivo: " + e);
        }

    }

    @Override
    public Resource cargarRecurso(String numeroPdf) {
        try {
            Path archivo = rutaLocal.resolve(numeroPdf);

            Resource recurso = new UrlResource(archivo.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new RecursoNOEncontradoException("archivo pdf", "nombre del archivo: ", numeroPdf);
            }
        } catch (MalformedURLException e) {
            throw new RecursoNOEncontradoException("archivo pdf", "nombre del archivo: ", numeroPdf);
        }
    }

    @Override
    public String getRutaArchivo(String numeroPdf) {
        return rutaLocal.resolve(numeroPdf).toString();
    }

    private String generarNombreArchivo(String codigoPoliza, String nombreArchivoOriginal) {

        if (codigoPoliza.isBlank()) {
            throw new ArchivoInvalidoExcepcion("Faltan datos (idPoliza) para generar nombre del archivo.");
        }

        String extension = getExtensionArchivo(nombreArchivoOriginal);

        return UUID.randomUUID().toString() + "_" + codigoPoliza + extension;
    }

    private void validarArchivo(MultipartFile archivo) {

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

    private String getExtensionArchivo(String nombreArchivoOriginal) {
        if (nombreArchivoOriginal == null || !nombreArchivoOriginal.contains(".")) {
            return "";
        }
        return nombreArchivoOriginal.substring(nombreArchivoOriginal.lastIndexOf("."));
    }
}
