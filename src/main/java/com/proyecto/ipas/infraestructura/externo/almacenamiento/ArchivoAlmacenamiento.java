package com.proyecto.ipas.infraestructura.externo.almacenamiento;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;


/**
 * Contrato para el almacenamiento de archivos de las polizas
 */
public interface ArchivoAlmacenamiento {

    /**
     * Almacena el archivo y retorna su codigo de identificacion
     */
    void almacenar(MultipartFile archivo, Path destino, String nombreArchivo);

    /**
     * Eliminar archivos temporales
     */
    void moverRecurso(String numeroPdf);

    /**
     * Carga el archivo como recurso
     */
    Resource cargarRecurso(String numeroPdf, Path destino);

    /**
     * Obtener la ruta completa del archivo
     */
    String getRutaArchivo(String numeroPdf);

    /**
     * Obtener la ruta temporal completa del archivo
     */
    String getRutaArchivoTemporal(String numeroPdf);





}
