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

    /**
     * Factory method que registra un nuevo cliente en el sistema.
     * 
     * Se validan todos los parámetros de entrada según el tipo de documento. Para documentos de personas 
     * (CC, CE, PASAPORTE) se requieren nombres y apellidos. Para NIT (empresas) solo se requiere nombre.
     * Se verifica que el cliente sea mayor de edad (mínimo 18 años) al momento del registro.
     * 
     * @param nombre el nombre o razón social del cliente (2-40 caracteres, solo letras y espacios para personas)
     * @param apellido el apellido del cliente (2-100 caracteres, solo para personas; nullable para NIT)
     * @param tipoDocumento el tipo de documento {@link TipoDocumentoCliente} (CC, CE, PASAPORTE, NIT)
     * @param numeroDocumento el número del documento de identidad
     * @param fechaNacimiento la fecha de nacimiento del cliente (debe ser mayor de edad)
     * @return una nueva instancia de Cliente validada
     * @throws IllegalArgumentException si nombres, apellidos o documento incumplen restricciones
     * @throws NegocioExcepcion si el cliente no es mayor de edad
     */
    public static Cliente registrarNuevo (String nombre, String apellido, TipoDocumentoCliente tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento) {
        validarNombres(nombre, tipoDocumento);
        validarApellidos(apellido, tipoDocumento);

        Cliente cliente = new Cliente(nombre, apellido, tipoDocumento, numeroDocumento, fechaNacimiento);

        cliente.esMayorDeEdad();

        return cliente;
    }

    /**
     * Factory method que reconstruye un cliente desde los datos persistidos en base de datos.
     * 
     * Este método es utilizado por la capa de infraestructura para instanciar objetos Cliente 
     * desde registros existentes. Valida que el ID sea válido y verifica la edad del cliente 
     * en base a la fecha de nacimiento registrada.
     * 
     * @param idCliente el identificador único del cliente en base de datos
     * @param nombre el nombre del cliente
     * @param apellido el apellido del cliente
     * @param tipoDocumento el tipo de documento {@link TipoDocumentoCliente}
     * @param numeroDocumento el número del documento de identidad
     * @param fechaNacimiento la fecha de nacimiento del cliente
     * @return una instancia de Cliente reconstruida con todos los parámetros proporcionados
     * @throws IllegalArgumentException si el ID es nulo o no positivo, o si los datos no pasan validación
     * @throws NegocioExcepcion si el cliente no es mayor de edad
     */
    public static Cliente reconstruir (Long idCliente, String nombre, String apellido, TipoDocumentoCliente tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento) {
        validarNombres(nombre, tipoDocumento);
        validarApellidos(apellido, tipoDocumento);
        validarIdCliente(idCliente);
        Cliente cliente = new Cliente(nombre, apellido, tipoDocumento, numeroDocumento, fechaNacimiento);
        cliente.esMayorDeEdad();
        cliente.id = idCliente;

        return cliente;
    }


    /**
     * Consulta que valida si el cliente es mayor de edad.
     * 
     * Verifica que la fecha de nacimiento sea al menos 18 años anterior a la fecha actual.
     * Este método es invocado durante el registro y reconstrucción de clientes para garantizar
     * que solo clientes mayores de edad pueden ser registrados en el sistema.
     * 
     * @throws NegocioExcepcion si el cliente es menor de 18 años
     */
    public void esMayorDeEdad() {
        if (fechaNacimiento == null) {return;}

        Long edad = fechaNacimiento.until(LocalDate.now(), ChronoUnit.YEARS);

        if (edad < 18) {
            throw new NegocioExcepcion("Debes ser mayor de edad");
        }

    }


    private void validarInvariantes() {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if ((apellido == null || apellido.isBlank()) && tipoDocumento != TipoDocumentoCliente.NUMERO_IDENTIFICACION_TRIBUTARIA) {
            throw new IllegalArgumentException("Los apellidos son obligatorios 1");
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

    private static void validarNombres(String nombres, TipoDocumentoCliente tipoDocumento) {

        if (tipoDocumento == TipoDocumentoCliente.NUMERO_IDENTIFICACION_TRIBUTARIA) {
            return;
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

    private static void validarApellidos(String apellidos, TipoDocumentoCliente tipoDocumento) {
        if (tipoDocumento == TipoDocumentoCliente.NUMERO_IDENTIFICACION_TRIBUTARIA) {
            return;
        }

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
                    "Los apellidos no pueden exceder 100 caracteres"
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
