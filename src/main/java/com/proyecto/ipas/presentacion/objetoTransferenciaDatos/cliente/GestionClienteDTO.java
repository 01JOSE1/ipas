package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.cliente;

import com.proyecto.ipas.datos.entidad.ClienteEntidad;
import com.proyecto.ipas.infraestructura.utilidades.EnCreacion;
import com.proyecto.ipas.negocio.dominio.enums.EstadoCivilCliente;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoCliente;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class GestionClienteDTO {

    private Long idCliente;

    @Length(min = 2, max = 40, message = "El nombre no debe ser menor a 2 y mayor a 40 caracteres")
    @NotBlank(message = "El nombre no debe estar vacio", groups = EnCreacion.class)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Solo se permiten letras y espacios")
    private String nombre;

    @Length(min = 2, max = 40, message = "El apellido no debe ser menor a 2 y mayor a 40 caracteres")
    @NotBlank(message = "El apellido no debe estar vacio", groups = EnCreacion.class)
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Solo se permiten letras y espacios")
    private String apellido;

    @NotNull(message = "El tipo de documento es obligatorio", groups = EnCreacion.class)
    private TipoDocumentoCliente tipoDocumento;

    @Length(min = 10, max = 15, message = "El numero de documento no debe ser menor a 10 y mayor a 15 caracteres")
    @NotBlank(message = "El numero de documento es obligatorio", groups = EnCreacion.class)
    private String numeroDocumento;

    @Past(message = "selecciona una fecha del pasado")
    @NotNull(message = "La fecha de nacimiento es obligatoria", groups = EnCreacion.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Formato correcto de fecha para trabajar Thymleaf
    //    @JsonFormat(pattern = "dd-MM-yyyy") // El formato que verás en el JSON de la API
    private LocalDate fechaNacimiento;

    @NotNull(message = "El estado civil es obligatorio", groups = EnCreacion.class)
    private EstadoCivilCliente estadoCivil;

    @Length(min = 10, max = 10, message = "El numero de telefono debe ser de 10 numeros")
    @NotBlank(message = "El telefono no debe estar vacio", groups = EnCreacion.class)
    @Pattern(regexp = "^\\d+$", message = "Solo se permiten numeros")
    private String telefono;

    @Length(max = 100, message = "El correo no debe ser mayor a 100 caracteres")
    @NotBlank(message = "El correo no debe estar vacio",  groups = EnCreacion.class)
    @Email(message = "Email invalido")
    private String correo;

    @Length(max = 100, message = "La direccion no debe ser mayor a 100 caracteres")
    private String direccion;

    @Length(max = 60, message = "La ciudad no debe ser mayor a 60 caracteres")
    private String ciudad;


    public void actualizarCliente(ClienteEntidad clienteEntidad) {
        if (nombre != null && !nombre.isBlank()) {
            clienteEntidad.setNombre(nombre);
        }
        if (apellido != null && !apellido.isBlank()) {
            clienteEntidad.setApellido(apellido);
        }
        if (tipoDocumento != null) {
            clienteEntidad.setTipoDocumento(tipoDocumento);
        }
        if (numeroDocumento != null && !numeroDocumento.isBlank()) {
            clienteEntidad.setNumeroDocumento(numeroDocumento);
        }
        if (fechaNacimiento != null) {
            clienteEntidad.setFechaNacimiento(fechaNacimiento);
        }
        if (estadoCivil != null) {
            clienteEntidad.setEstadoCivil(estadoCivil);
        }
        if (telefono != null && !telefono.isBlank()) {
            clienteEntidad.setTelefono(telefono);
        }
        if (correo != null && !correo.isBlank()) {
            clienteEntidad.setCorreo(correo);
        }
        if (direccion != null && !direccion.isBlank()) {
            clienteEntidad.setDireccion(direccion);
        }
        if (ciudad != null && !ciudad.isBlank()) {
            clienteEntidad.setCiudad(ciudad);
        }
    }
}
