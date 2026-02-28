package com.proyecto.ipas.negocio.dominio.modelo;

import com.proyecto.ipas.negocio.dominio.enums.EstadoPagoPoliza;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import com.proyecto.ipas.presentacion.excepcion.ConflictoExcepcion;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.ValidacionDatosExcepcion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class Poliza {

    private Long id;
    private String codigoPoliza;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal primaNeta;
    private BigDecimal primaTotal;
    private EstadoPoliza estado;
    private EstadoPagoPoliza estadoPago;
    private String placa;
    private String descripcion;
    private Ramo ramo;
    private Aseguradora aseguradora;

    private Poliza(String codigoPoliza, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal primaNeta, BigDecimal primaTotal, EstadoPoliza estado, EstadoPagoPoliza estadoPago, Ramo ramo, Aseguradora aseguradora) {
        this.codigoPoliza = codigoPoliza;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.primaNeta = primaNeta;
        this.primaTotal = primaTotal;
        this.estado = estado;
        this.estadoPago = estadoPago;
        this.ramo = ramo;
        this.aseguradora = aseguradora;
    }

    public static Poliza registrar(String codigoPoliza, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal primaNeta, BigDecimal primaTotal, EstadoPoliza estado, EstadoPagoPoliza estadoPago, String placa, Ramo ramo, Aseguradora aseguradora) {

        validarCodigoPoliza(codigoPoliza);
        validarFechas(fechaInicio, fechaFin);
        validarPrimas(primaNeta, primaTotal);

        Poliza poliza = new Poliza(codigoPoliza, fechaInicio, fechaFin, primaNeta, primaTotal, validarEstado(estado), validarEstadoPago(estadoPago), ramo, aseguradora);

        poliza.placa = placa;
        validarRamo(ramo, poliza.placa);

        return poliza;
    }

    public static Poliza reconstruir(Long id, String codigoPoliza, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal primaNeta, BigDecimal primaTotal, EstadoPoliza estado, EstadoPagoPoliza estadoPago, String placa, Ramo ramo, Aseguradora aseguradora) {

        validarIdPoliza(id);
        validarCodigoPoliza(codigoPoliza);
        validarFechas(fechaInicio, fechaFin);
        validarPrimas(primaNeta, primaTotal);

        Poliza poliza = new Poliza(codigoPoliza, fechaInicio, fechaFin, primaNeta, primaTotal, estado, estadoPago, ramo, aseguradora);
        poliza.id = id;
        poliza.placa = placa;
        validarRamo(ramo, poliza.placa);
        return poliza;
    }


    public void cancelarPoliza(String motivo) {

        validarMotivoCancelacion(motivo);

        if (this.estado != EstadoPoliza.VIGENTE) {
            throw new NegocioExcepcion("La póliza no puede ser cancelada");
        }

        agregarMotivoCancelacion(motivo);
        this.estado = EstadoPoliza.CANCELADA;
    }
    

    private static void validarIdPoliza(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de poliza debe ser positivo");
        }
    }

    public static void validarCodigoPoliza(String codigoPoliza) {
        // Usamos \w para letras, números y guiones bajos.
        // El "!" al principio evalúa si el código NO coincide con el patrón.
        if (codigoPoliza == null || !codigoPoliza.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("El código de póliza solo puede contener letras, números y guiones, sin espacios.");
        }
    }

    private static void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio) || fechaInicio.until(fechaFin, ChronoUnit.YEARS) < 1) {
            ArrayList<ValidacionDatosExcepcion.ErrorCampo> errores = new ArrayList<>();
            errores.add(new ValidacionDatosExcepcion.ErrorCampo("fechaFin","Fecha invalida"));
            throw new ValidacionDatosExcepcion("Fechas invalidas", errores);
        }
    }

    private static void validarPrimas(BigDecimal primaNeta, BigDecimal primaTotal) {
        ArrayList<ValidacionDatosExcepcion.ErrorCampo> errores = new ArrayList<>();
        if (primaNeta.compareTo(primaTotal) >= 0 || primaNeta.compareTo(BigDecimal.ZERO) < 0 || primaNeta.compareTo(BigDecimal.ZERO) <= 0) {
            errores.add(new ValidacionDatosExcepcion.ErrorCampo("primaNeta","prima invalida"));
            throw new ValidacionDatosExcepcion("Primas invalidas", errores);
        }
    }

    private static EstadoPoliza validarEstado(EstadoPoliza estado) {
        if (estado == null) {
            return EstadoPoliza.VIGENTE;
        }
        return estado;
    }

    private static EstadoPagoPoliza validarEstadoPago(EstadoPagoPoliza estadoPago) {
        if (estadoPago == null) {
            return EstadoPagoPoliza.PENDIENTE;
        }
        return estadoPago;
    }


    private static void validarRamo (Ramo ramo, String placa) {
        if (ramo.esRamoAutomovil() && (placa == null || placa.isBlank())) {
            ArrayList<ValidacionDatosExcepcion.ErrorCampo> errores = new ArrayList<>();
            errores.add(new ValidacionDatosExcepcion.ErrorCampo("placa","El ramo AUTOMOVIL debe contener una placa"));
            throw new ValidacionDatosExcepcion("Ramo automovil falta placa", errores);
        }
    }

    private void validarMotivoCancelacion(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new NegocioExcepcion("Debes agregar un motivo de cancelación");
        }
    }

    private void agregarMotivoCancelacion(String motivo) {
        if (this.descripcion == null) {
            this.descripcion = "";
        }
        this.descripcion += "\nMOTIVO CANCELACIÓN: " + motivo;
    }

    public boolean esRamoAutomovil () {
        return ramo.esRamoAutomovil();
    }

    public String getCodigoPoliza() {
        return codigoPoliza;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public BigDecimal getPrimaNeta() {
        return primaNeta;
    }

    public BigDecimal getPrimaTotal() {
        return primaTotal;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public String getPlaca() {
        return placa;
    }

    public EstadoPagoPoliza getEstadoPago() {
        return estadoPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Ramo getRamo() {
        return ramo;
    }

    public Aseguradora getSeguradora() {
        return aseguradora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poliza poliza = (Poliza) o;
        return Objects.equals(id, poliza.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Poliza{" +
                "id=" + id +
                ", codigoPoliza='" + codigoPoliza + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", primaNeta=" + primaNeta +
                ", primaTotal=" + primaTotal +
                ", estado=" + estado +
                ", estadoPago=" + estadoPago +
                ", placa='" + placa + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ramo=" + ramo +
                ", aseguradora=" + aseguradora +
                '}';
    }
}
