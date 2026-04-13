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

    /**
     * Factory method que registra una nueva póliza de seguro en el sistema.
     * 
     * Se valida que el código de póliza sea único, que las fechas sean válidas (mínimo 1 año de vigencia),
     * que las primas sean coherentes (prima total > prima neta > 0), y que el ramo AUTOMOVIL tenga placa.
     * La póliza se crea con estado ACTIVA. El estado de pago se establece por defecto a PENDIENTE si no se especifica.
     * 
     * @param codigoPoliza el código único de la póliza (letras, números y guiones, sin espacios)
     * @param fechaInicio la fecha de inicio de vigencia de la póliza
     * @param fechaFin la fecha de fin de vigencia (mínimo 1 año después de fechaInicio)
     * @param primaNeta la prima neta sin comisiones (debe ser menor a primaTotal)
     * @param primaTotal la prima total incluyendo comisiones
     * @param estadoPago el estado de pago de la póliza {@link EstadoPagoPoliza} (PENDIENTE si es null)
     * @param placa la placa del vehículo (obligatorio si es ramo AUTOMOVIL, opcional para otros)
     * @param ramo el ramo de seguro {@link Ramo} de la póliza
     * @param aseguradora la aseguradora responsable {@link Aseguradora}
     * @return una nueva instancia de Poliza con estado ACTIVA
     * @throws IllegalArgumentException si el código de póliza no cumple formato
     * @throws ValidacionDatosExcepcion si fechas, primas, o ramo incumplen las reglas de negocio
     * @throws NegocioExcepcion si el ramo AUTOMOVIL falta placa
     */
    public static Poliza registrar(String codigoPoliza, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal primaNeta, BigDecimal primaTotal, EstadoPagoPoliza estadoPago, String placa, String descripcion, Ramo ramo, Aseguradora aseguradora) {

        validarCodigoPoliza(codigoPoliza);
        validarFechas(fechaInicio, fechaFin);
        validarPrimas(primaNeta, primaTotal);

        Poliza poliza = new Poliza(codigoPoliza, fechaInicio, fechaFin, primaNeta, primaTotal, EstadoPoliza.ACTIVA, validarEstadoPago(estadoPago), ramo, aseguradora);
        poliza.placa = placa;
        poliza.descripcion = descripcion;
        validarRamo(ramo, poliza.placa);

        return poliza;
    }

    /**
     * Factory method que reconstruye una póliza desde los datos persistidos en base de datos.
     * 
     * Este método es utilizado por la capa de infraestructura para instanciar objetos Poliza
     * desde registros existentes. Valida que el ID sea válido y que todos los parámetros cumplan
     * las reglas de negocio, incluyendo la validación especial para ramo AUTOMOVIL.
     * 
     * @param id el identificador único de la póliza en base de datos
     * @param codigoPoliza el código de la póliza
     * @param fechaInicio la fecha de inicio de vigencia
     * @param fechaFin la fecha de fin de vigencia
     * @param primaNeta la prima neta de la póliza
     * @param primaTotal la prima total de la póliza
     * @param estado el estado actual de la póliza {@link EstadoPoliza}
     * @param estadoPago el estado de pago actual {@link EstadoPagoPoliza}
     * @param placa la placa del vehículo (si aplica)
     * @param ramo el ramo de seguro {@link Ramo}
     * @param aseguradora la aseguradora responsable {@link Aseguradora}
     * @return una instancia de Poliza reconstruida con todos los parámetros proporcionados
     * @throws IllegalArgumentException si el ID es nulo o no positivo, o código de póliza inválido
     * @throws ValidacionDatosExcepcion si fechas, primas, o ramo incumplen las reglas
     */
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


    /**
     * Comando que cancela la póliza de seguro.
     * 
     * Solo es posible cancelar pólizas que no estén vencidas y que no hayan sido canceladas previamente.
     * Se registra un motivo de cancelación que se añade al historial de descripción de la póliza.
     * Una vez cancelada, la póliza cambia a estado {@link EstadoPoliza#CANCELADA}.
     * 
     * @param motivo la razón de la cancelación (mínimo 4 caracteres, obligatorio)
     * @throws NegocioExcepcion si la póliza ya está vencida, ya está cancelada, o el motivo es inválido
     */
    public void cancelarPoliza(String motivo) {

        if (esVencida(fechaFin)) {
            throw new NegocioExcepcion("La póliza se encuentra VENCIDA");
        }
        if (getEstado() == EstadoPoliza.CANCELADA) {
            throw new NegocioExcepcion("La poliza ya esta CANCELADA");
        }

        validarMotivoCancelacion(motivo);

        agregarMotivoCancelacion(motivo);
        this.estado = EstadoPoliza.CANCELADA;
    }


    /**
     * Consulta que verifica si la póliza se encuentra vencida.
     * 
     * Una póliza está vencida si su fecha de fin es anterior a la fecha actual del sistema.
     * 
     * @param fechaFin la fecha de fin de vigencia de la póliza
     * @return {@code true} si la póliza está vencida, {@code false} en caso contrario
     */
    private static boolean esVencida(LocalDate fechaFin) {
        if (fechaFin.isBefore(LocalDate.now())) {
            return true;
        }
        return false;
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


    private static EstadoPagoPoliza validarEstadoPago(EstadoPagoPoliza estadoPago) {
        if (estadoPago == null) {
            return EstadoPagoPoliza.PENDIENTE;
        }
        return estadoPago;
    }

    /**
     * Validación que verifica si el ramo es AUTOMOVIL y requiere placa obligatoria.
     *
     * Para ramos de tipo AUTOMOVIL, la placa del vehículo es un campo obligatorio.
     * Los otros ramos de seguro no requieren placa.
     *
     * @param ramo el ramo de seguro a validar {@link Ramo}
     * @param placa la placa del vehículo (requerida si ramo es AUTOMOVIL)
     * @throws ValidacionDatosExcepcion si el ramo es AUTOMOVIL y la placa es nula o vacía
     */
    private static void validarRamo (Ramo ramo, String placa) {
        if (ramo.esRamoAutomovil() && (placa == null || placa.isBlank())) {
            ArrayList<ValidacionDatosExcepcion.ErrorCampo> errores = new ArrayList<>();
            errores.add(new ValidacionDatosExcepcion.ErrorCampo("placa","El ramo AUTOMOVIL debe contener una placa"));
            throw new ValidacionDatosExcepcion("Ramo automovil falta placa", errores);
        }
    }

    private void validarMotivoCancelacion(String motivo) {
        if (motivo == null || motivo.isBlank() || motivo.length() < 4) {
            throw new NegocioExcepcion("Debes agregar un motivo de cancelación");
        }
    }

    private void agregarMotivoCancelacion(String motivo) {
        if (this.descripcion == null) {
            this.descripcion = "";
        }
        this.descripcion += "\n CANCELADA EL DIA "+LocalDate.now()+" POR EL MOTIVO: " + motivo;
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
