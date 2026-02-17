package com.proyecto.ipas.infraestructura.externo.almacenamiento;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


/**
 * Contrato para el almacenamiento de archivos de las polizas
 */
public interface ArchivoAlmacenamiento {

    /**
     * Almacena el archivo y retorna su codigo de identificacion
     */
    String almacenar(MultipartFile archivo, String codigoPoliza);


    /**
     * Carga el archivo como recurso
     */
    Resource cargarRecurso(String numeroPdf);


    /**
     * Obtener la ruta completa del archivo
     */
    String getRutaArchivo(String numeroPdf);


}
