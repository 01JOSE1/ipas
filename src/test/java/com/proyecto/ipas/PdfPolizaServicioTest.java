package com.proyecto.ipas;

import com.proyecto.ipas.infraestructura.externo.almacenamiento.ArchivoAlmacenamientoServicio;
import com.proyecto.ipas.negocio.servicio.poliza.PdfPolizaServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ActiveProfiles("test")
class PdfPolizaServicioTest {

    @Autowired
    private PdfPolizaServicio pdfServicio;

    @Autowired
    private ArchivoAlmacenamientoServicio archivoAlmacenamientoServicio;

    private MockMultipartFile cargarPdf(String nombreArchivo) throws Exception {
        Resource recurso = archivoAlmacenamientoServicio.cargarRecurso(nombreArchivo);
        return new MockMultipartFile(
                "archivo",
                nombreArchivo,
                "application/pdf",
                recurso.getInputStream()
        );
    }

    private static final String NOMBRE_ARCHIVO = "2c7d5abb-d19b-4676-81da-50d437971fe6_9000014871741234.pdf";

    // ── TEST 1: Extraer texto plano ────────────────────────────────────────
    @Test
    void probarExtraccion() throws Exception {
        MockMultipartFile mockFile = cargarPdf(NOMBRE_ARCHIVO);

        String resultado = pdfServicio.prepararParaIa(mockFile);

        System.out.println("\n==========================================");
        System.out.println("--- CONTENIDO EXTRAÍDO DEL PDF ---");
        System.out.println(resultado);
        System.out.println("==========================================\n");

        assertNotNull(resultado);
        assertFalse(resultado.isBlank(), "El texto extraído está vacío");
    }

}