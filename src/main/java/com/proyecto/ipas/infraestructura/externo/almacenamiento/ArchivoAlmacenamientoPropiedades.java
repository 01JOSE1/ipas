package com.proyecto.ipas.infraestructura.externo.almacenamiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades de configuración para el almacenamiento de archivos.
 * 
 * Se carga desde el archivo application.properties con el prefijo "app.almacenamiento".
 * Define rutas, límites de tamaño y tipos MIME permitidos para archivos PDF.
 * 
 * Ejemplo en application.properties:
 * {@code
 * app.almacenamiento.destino=almacenamientoPolizaPDF
 * app.almacenamiento.destino-temporal=almacenamientoPolizaPDFTemporal
 * app.almacenamiento.tamano-maximo-archivo=10
 * }
 */
@Configuration
@ConfigurationProperties(prefix = "app.almacenamiento")
@Validated
public class ArchivoAlmacenamientoPropiedades {

    /**
     * Directorio raíz donde se almacenan los archivos PDF finales permanentemente.
     */
    @NotBlank(message = "La ubicación de almacenamiento no puede estar vacía")
    private String destino = "almacenamientoPolizaPDF";

    /**
     * Directorio raíz donde se almacenan temporalmente los archivos durante el procesamiento.
     */
    @NotBlank(message = "La ubicación de almacenamiento temporal no puede estar vacía")
    private String destinoTemporal = "almacenamientoPolizaPDFTemporal";

    /**
     * Tamaño máximo permitido para un archivo individual en MB.
     */
    @Positive(message = "El tamaño máximo debe ser positivo")
    private long tamanoMaximoArchivo = 10;

    /**
     * Tamaño máximo permitido para una petición HTTP completa en MB.
     */
    @Positive
    private long TamanoMaximoPeticion = 10;

    /**
     * Tipos MIME permitidos para subir archivos (por defecto solo PDF).
     */
    private String[] tipoContenidoPermitido = {"application/pdf"};

    /**
     * Extensiones de archivo permitidas para subir (por defecto solo .pdf).
     */
    private String[] extensionesPermitidas = {".pdf"};

    /**
     * Si se debe crear el directorio automáticamente
     */
    private boolean crearDirectorioAutomatico = true;


    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDestinoTemporal() {
        return destinoTemporal;
    }

    public void setDestinoTemporal(String destinoTemporal) {
        this.destinoTemporal = destinoTemporal;
    }

    public long getTamanoMaximoArchivo() {
        return tamanoMaximoArchivo;
    }

    public void setTamanoMaximoArchivo(long tamanoMaximoArchivo) {
        this.tamanoMaximoArchivo = tamanoMaximoArchivo;
    }

    public long getTamanoMaximoPeticion() {
        return TamanoMaximoPeticion;
    }

    public void setTamanoMaximoPeticion(long tamanoMaximoPeticion) {
        TamanoMaximoPeticion = tamanoMaximoPeticion;
    }

    public String[] getExtensionesPermitidas() {
        return extensionesPermitidas;
    }

    public void setExtensionesPermitidas(String[] extensionesPermitidas) {
        this.extensionesPermitidas = extensionesPermitidas;
    }

    public String[] getTipoContenidoPermitido() {
        return tipoContenidoPermitido;
    }

    public void setTipoContenidoPermitido(String[] tipoContenidoPermitido) {
        this.tipoContenidoPermitido = tipoContenidoPermitido;
    }

    public boolean isCrearDirectorioAutomatico() {
        return crearDirectorioAutomatico;
    }

    public void setCrearDirectorioAutomatico(boolean crearDirectorioAutomatico) {
        this.crearDirectorioAutomatico = crearDirectorioAutomatico;
    }

    public long getTamanoMaximoBytes(){
        return tamanoMaximoArchivo * 1024 * 1024;
    }
}
