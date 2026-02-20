package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record PdfArchivoDTO(
        @NotNull(message = "Debe seleccionar un archivo")
        MultipartFile archivo
) {
}
