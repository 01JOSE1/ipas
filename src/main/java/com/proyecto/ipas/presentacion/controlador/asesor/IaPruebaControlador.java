package com.proyecto.ipas.presentacion.controlador.asesor;

import com.proyecto.ipas.infraestructura.externo.ia.IaServicio;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia.PeticionIaDTO;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.ia.PruebaRespuestaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/test")
public class IaPruebaControlador {

    private final IaServicio iaServicio;

    public IaPruebaControlador(IaServicio iaServicio) {
        this.iaServicio = iaServicio;
    }

    @GetMapping
    public ResponseEntity<PruebaRespuestaDTO> test() {

        PeticionIaDTO peticionIaDTO = PeticionIaDTO.conRespuesta(
                "Eres un asistente de prueba.",
                "Dime que modelo de ia gemini especifico estoy utilizando en este momento",
                PruebaRespuestaDTO.class
        );

        PruebaRespuestaDTO pruebaRespuestaDTO = iaServicio.procesar(peticionIaDTO, PruebaRespuestaDTO.class, "TEST");
        return ResponseEntity.ok(pruebaRespuestaDTO);
    }
}
