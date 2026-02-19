package com.proyecto.ipas.negocio.dominio.modelo;

import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoCliente;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Cliente {

    private Long id;
    private final String nombre;
    private final String apellido;
    private TipoDocumentoCliente tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;

    private Cliente(String nombre, String apellido, TipoDocumentoCliente tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;

        validarInvariantes();
    }

    public static Cliente registrarNuevo (String nombre, String apellido, TipoDocumentoCliente tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento) {
        validarNombres(nombre);
        validarApellidos(apellido);

        Cliente cliente = new Cliente(nombre, apellido, tipoDocumento, numeroDocumento, fechaNacimiento);
        cliente.esMayorDeEdad();

        return cliente;
    }

    public static Cliente reconstruir (Long idCliente, String nombre, String apellido, TipoDocumentoCliente tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento) {
        validarNombres(nombre);
        validarApellidos(apellido);
        validarIdCliente(idCliente);
        Cliente cliente = new Cliente(nombre, apellido, tipoDocumento, numeroDocumento, fechaNacimiento);
        cliente.esMayorDeEdad();
        cliente.id = idCliente;

        return cliente;
    }


    public void esMayorDeEdad() {

        Long edad = fechaNacimiento.until(LocalDate.now(), ChronoUnit.YEARS);

        if (edad < 18) {
            throw new NegocioExcepcion("Debes ser mayor de edad");
        }

    }


    private void validarInvariantes() {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("El tipo de documento es obligatorio");
        }
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("El numero de documento es obligatorio");
        }
    }

    private static void validarIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente debe ser positivo");
        }
    }

    private static void validarNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        nombres = nombres.trim();

        if (nombres.length() < 2) {
            throw new IllegalArgumentException(
                    "Los nombres deben tener al menos 2 caracteres"
            );
        }

        if (nombres.length() > 40) {
            throw new IllegalArgumentException(
                    "Los nombres no pueden exceder 100 caracteres"
            );
        }

        if (!nombres.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException(
                    "Los nombres solo pueden contener letras y espacios"
            );
        }
    }

    private static void validarApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }

        apellidos = apellidos.trim();

        if (apellidos.length() < 2) {
            throw new IllegalArgumentException(
                    "Los apellidos deben tener al menos 2 caracteres"
            );
        }

        if (apellidos.length() > 100) {
            throw new IllegalArgumentException(
                    "Los apellidos no pueden exceder 40 caracteres"
            );
        }

    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public TipoDocumentoCliente getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }
}
