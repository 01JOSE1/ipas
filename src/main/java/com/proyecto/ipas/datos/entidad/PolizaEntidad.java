package com.proyecto.ipas.datos.entidad;

import com.proyecto.ipas.negocio.dominio.enums.EstadoPagoPoliza;
import com.proyecto.ipas.negocio.dominio.enums.EstadoPoliza;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "polizas")
public class PolizaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_poliza")
    private Long idPoliza;

    @Column(name = "codigo_poliza",  nullable = false, unique = true, length = 20)
    private String codigoPoliza;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "prima_neta", nullable = false, precision = 15, scale = 2)
    private BigDecimal primaNeta;

    @Column(name = "prima_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal primaTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPoliza estado;

    @Column(name = "estado_pago", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPagoPoliza estadoPago;

    @Column(name = "numero_pdf", nullable = false, length = 150, unique = true)
    private String numeroPdf;

    @Column(length = 15)
    private String placa;

    @Column(length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntidad cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntidad usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ramo_id", nullable = false)
    private RamoEntidad ramo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aseguradora_id", nullable = false)
    private AseguradoraEntidad aseguradora;

    public PolizaEntidad() {
    }

    public PolizaEntidad(String codigoPoliza, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal primaNeta, BigDecimal primaTotal, EstadoPoliza estado, EstadoPagoPoliza estadoPago, String numeroPdf, ClienteEntidad cliente, UsuarioEntidad usuario, RamoEntidad ramo, AseguradoraEntidad aseguradora) {
        this.codigoPoliza = codigoPoliza;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.primaNeta = primaNeta;
        this.primaTotal = primaTotal;
        this.estado = estado;
        this.estadoPago = estadoPago;
        this.numeroPdf = numeroPdf;
        this.cliente = cliente;
        this.usuario = usuario;
        this.ramo = ramo;
        this.aseguradora = aseguradora;
    }

    public Long getIdPoliza() {
        return idPoliza;
    }

    public void setIdPoliza(Long idPoliza) {
        this.idPoliza = idPoliza;
    }

    public String getCodigoPoliza() {
        return codigoPoliza;
    }

    public void setCodigoPoliza(String codigoPoliza) {
        this.codigoPoliza = codigoPoliza;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPrimaNeta() {
        return primaNeta;
    }

    public void setPrimaNeta(BigDecimal primaNeta) {
        this.primaNeta = primaNeta;
    }

    public BigDecimal getPrimaTotal() {
        return primaTotal;
    }

    public void setPrimaTotal(BigDecimal primaTotal) {
        this.primaTotal = primaTotal;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public void setEstado(EstadoPoliza estado) {
        this.estado = estado;
    }

    public EstadoPagoPoliza getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPagoPoliza estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getNumeroPdf() {
        return numeroPdf;
    }

    public void setNumeroPdf(String numeroPdf) {
        this.numeroPdf = numeroPdf;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ClienteEntidad getCliente() {
        return cliente;
    }

    public void setCliente(ClienteEntidad cliente) {
        this.cliente = cliente;
    }

    public UsuarioEntidad getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntidad usuario) {
        this.usuario = usuario;
    }

    public RamoEntidad getRamo() {
        return ramo;
    }

    public void setRamo(RamoEntidad ramo) {
        this.ramo = ramo;
    }

    public AseguradoraEntidad getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(AseguradoraEntidad aseguradora) {
        this.aseguradora = aseguradora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolizaEntidad that = (PolizaEntidad) o;
        if (idPoliza != that.idPoliza) return false;
        return Objects.equals(idPoliza, that.idPoliza);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "PolizaEntidad{" +
                "idPoliza=" + idPoliza +
                ", codigoPoliza='" + codigoPoliza + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", primaNeta=" + primaNeta +
                ", primaTotal=" + primaTotal +
                ", estado=" + estado +
                ", estadoPago=" + estadoPago +
                ", numeroPdf='" + numeroPdf + '\'' +
                ", placa='" + placa + '\'' +
                ", cliente=" + cliente +
                ", usuario=" + usuario +
                ", ramo=" + ramo +
                ", aseguradora=" + aseguradora +
                '}';
    }
}
