package com.proyecto.ipas.negocio.servicio.poliza;

import com.proyecto.ipas.infraestructura.externo.almacenamiento.ArchivoAlmacenamientoServicio;
import com.proyecto.ipas.presentacion.excepcion.ArchivoInvalidoExcepcion;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfPolizaServicio {

    private final ArchivoAlmacenamientoServicio almacenamientoServicio;

    public PdfPolizaServicio(ArchivoAlmacenamientoServicio almacenamientoServicio) {
        this.almacenamientoServicio = almacenamientoServicio;
    }

    public String prepararParaIa(MultipartFile archivo) throws IOException {

        try (PDDocument documento = validarArchivo(archivo)) {

            String texto = extraerValidarTexto(documento);

            texto = limpiarTexto(texto);

            return texto;

        }
    }


    /**
     * Extraer texto del pdf de la poliza
     */
    private PDDocument validarArchivo(MultipartFile archivo) throws IOException {

        almacenamientoServicio.validarArchivo(archivo);

        PDDocument documento = Loader.loadPDF(archivo.getBytes());

        if (documento.isEncrypted()) {
            documento.close();
            throw new ArchivoInvalidoExcepcion(
                    "El PDF está protegido con contraseña."
            );
        }

        return documento;
    }

    private String extraerValidarTexto(PDDocument documento) throws IOException {

        PDFTextStripper stripper = new PDFTextStripper();

        stripper.setStartPage(1);
        stripper.setEndPage(1);

        stripper.setSortByPosition(true);
        stripper.setAddMoreFormatting(false);

        String texto = stripper.getText(documento);

        if (texto == null || texto.trim().length() < 200) {
            throw new ArchivoInvalidoExcepcion(
                    "El PDF no contiene texto extraíble. Puede ser escaneado.");
        }

        return texto;
    }


    private String limpiarTexto(String texto) {
        return texto
                // Normalizar saltos de línea
                .replaceAll("\\r\\n|\\r", "\n")

                // Eliminar caracteres de control invisibles Ascii 0 - 32
                // Excepto \n y \t que sí son útiles
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")

                // Estandarizar todas las variantes tipográficas de guiones a un guion común
                .replaceAll("[\\u2010\\u2011\\u2012\\u2013\\u2014\\u2015]", "-")

                // Convierte comillas decorativas o curvas en comillas estándar de teclado, evitando errores de comparación
                .replaceAll("[\\u201C\\u201D\\u201E]", "\"")
                .replaceAll("[\\u2018\\u2019]", "'")

                // Transforma el símbolo especial de puntos suspensivos en tres puntos estándar
                .replaceAll("\\u2026", "...")

                // Unifica todos los tipos de espacios invisibles y técnicos en un espacio simple
                .replaceAll("[\\u00A0\\u2002\\u2003\\u2009]", " ")

                // Limpia el texto de cualquier símbolo extraño o "basura" técnica, permitiendo exclusivamente letras (con tildes y eñes),
                // números, el signo de pesos, puntuación esencial y espacios
                .replaceAll("[^\\p{L}\\p{N}$.,;:\\-_%()\\s\\n\\t/@#*&+=]", " ")

                // Elimina el exceso de espacios en blanco y tabuladores, normalizando el texto para que cada
                // palabra esté separada por un solo espacio y eliminando el desorden visual típico de las tablas
                .replaceAll("[ \\t]{2,}", " ")

                // Detecta y elimina líneas con letras repetidas 4+ veces (artefactos del PDF)
                // Nunca elimina líneas por repetición de números
                .replaceAll("(?m)^.*([a-zA-ZáéíóúñÁÉÍÓÚÑ])\\1{4,}.*$", "")

                // Elimina los espacios en blanco innecesarios al principio y al final de cada renglón, dejando el texto perfectamente alineado
                .lines()
                .map(String::trim)
                .collect(java.util.stream.Collectors.joining("\n"))

                // Elimina líneas vacías excesivas (máx 2 seguidas)
                .replaceAll("\\n{3,}", "\n\n")

                // Elimina líneas que son solo números de página
                // Líneas como "1", "2", "- 3 -", "Página 1 de 5"
                .replaceAll("(?mi)^p[aá]gina\\s+\\d+\\s*(de\\s+\\d+)?$", "")
                .replaceAll("(?m)^\\s*\\d{1,2}\\s*$", "")

                // Elimina líneas largas sin espacios (logos, marcas de agua concatenadas)
                .replaceAll("(?m)^\\S{80,}$", "")

                // Elimina líneas que son solo símbolos decorativos
                // Líneas como "----", "====", "****", "....", "####"
                .replaceAll("(?m)^[\\-=\\*\\.#_~\\s]{3,}$", "")
                
                // Limpiar líneas vacías generadas por pasos anteriores
                .replaceAll("\\n{3,}", "\n\n")

                // Elimina los espacios y saltos de línea sobrantes al inicio y al final del texto
                .trim();
    }


}
