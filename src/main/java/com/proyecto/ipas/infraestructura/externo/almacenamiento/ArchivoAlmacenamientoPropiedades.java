package com.proyecto.ipas.infraestructura.externo.almacenamiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app.almacenamiento")
@Validated
public class ArchivoAlmacenamientoPropiedades {

    /**
     * Directorio raíz donde se almacenan los archivos
     */
    @NotBlank(message = "La ubicación de almacenamiento no puede estar vacía")
    private String destino = "almacenamientoPolizaPDF";

    /**
     * Tamaño máximo de archivo en MB
     */
    @Positive(message = "El tamaño máximo debe ser positivo")
    private long tamanoMaximoArchivo = 10;

    /**
     * Tamaño máximo de request en MB
     */
    @Positive
    private long TamanoMaximoPeticion = 10;

    /**
     * Tipos MIME permitidos
     */
    private String[] tipoContenidoPermitido = {"application/pdf"};

    /**
     * Extensiones de archivo permitidas
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
