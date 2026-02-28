package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.poliza;

import com.proyecto.ipas.datos.entidad.PolizaEntidad;
import com.proyecto.ipas.infraestructura.utilidades.EnCreacion;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPagoPoliza;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import com.proyecto.ipas.presentacion.objetoTransferenciaDatos.cliente.GestionClienteDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GestionPolizaDTO {

    @Positive
    private Long idPoliza;

    @Length(min = 7, max = 20, message = "El codigo de la poliza debe contener entre 7 y 20 caracteres")
    @NotBlank(message = "El codigo de poliza no debe estar vacio", groups = EnCreacion.class)
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "El código de póliza solo puede contener letras, números y guiones, sin espacios.")
    private String codigoPoliza;

    @NotNull(message = "La fecha de registro es obligatoria", groups = EnCreacion.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de vencimiento es obligatoria", groups = EnCreacion.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    @Positive
    @Digits(integer = 15, fraction = 2)
    @NotNull(message = "La prima neta es obligatoria", groups = EnCreacion.class)
    private BigDecimal primaNeta;

    @Positive
    @Digits(integer = 15, fraction = 2)
    @NotNull(message = "La prima total es obligatoria", groups = EnCreacion.class)
    private BigDecimal primaTotal;

    private EstadoPoliza estado;

    private EstadoPagoPoliza estadoPago;

    @Size(min = 4, max = 15, message = "La placa debe contener entre 4 y 15 caracteres")
    private String placa;

    @Length(min = 10, max = 255, message = "La descripcion debe contener entre 10 y 255 caracteres")
    private String descripcion;

    @Positive
    @NotNull(message = "Debes agregar un cliente valido", groups = EnCreacion.class)
    private Long idCliente;
    private String nombreCliente;
    // Objeto para el proceso de la ia
    private GestionClienteDTO gestionClienteDTO;
    private boolean clienteExiste;

    @Positive
    @NotNull(message = "Debes agregar un ramo de poliza valido", groups = EnCreacion.class)
    private Long idRamo;

    @Positive
    @NotNull(message = "Debes agregar una aseguradora valida", groups = EnCreacion.class)
    private Long idAseguradora;
    private String numeroDocumentoAseguradora;
    private String nombreAseguradora;

    private MultipartFile archivoPoliza;
    private String numeroPdf;

    public GestionPolizaDTO() {
    }

    public GestionPolizaDTO(Long idPoliza, String codigoPoliza, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal primaNeta, BigDecimal primaTotal, EstadoPoliza estado, EstadoPagoPoliza estadoPago, String placa, String descripcion, Long idCliente, String nombreCliente, Long idRamo, Long idAseguradora, String numeroPdf) {
        this.idPoliza = idPoliza;
        this.codigoPoliza = codigoPoliza;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.primaNeta = primaNeta;
        this.primaTotal = primaTotal;
        this.estado = estado;
        this.estadoPago = estadoPago;
        this.placa = placa;
        this.descripcion = descripcion;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idRamo = idRamo;
        this.idAseguradora = idAseguradora;
        this.numeroPdf = numeroPdf;
    }

    public void actualizarPoliza(PolizaEntidad polizaEntidad ) {

        if (codigoPoliza != null && !codigoPoliza.isBlank()) {
            polizaEntidad.setCodigoPoliza(codigoPoliza);
        }
        if (fechaInicio != null) {
            polizaEntidad.setFechaInicio(fechaInicio);
        }
        if (fechaFin != null) {
            polizaEntidad.setFechaFin(fechaFin);
        }
        if (primaNeta != null) {
            polizaEntidad.setPrimaNeta(primaNeta);
        }
        if (primaTotal != null) {
            polizaEntidad.setPrimaTotal(primaTotal);
        }
        if (estado != null) {
            polizaEntidad.setEstado(estado);
        }
        if (estadoPago != null) {
            polizaEntidad.setEstadoPago(estadoPago);
        }
        if (estadoPago != null) {
            polizaEntidad.setEstadoPago(estadoPago);
        }
        if (placa != null && !placa.isBlank()) {
            polizaEntidad.setPlaca(placa);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            polizaEntidad.setDescripcion(descripcion);
        }
    }
}
