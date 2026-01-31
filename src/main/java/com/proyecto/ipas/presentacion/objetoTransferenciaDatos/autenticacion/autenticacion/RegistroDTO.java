package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.autenticacion.autenticacion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class RegistroDTO {
    @Length(min = 2, max = 40, message = "El nombre no debe ser a 2 y mayor a 40 caracteres")
    @NotBlank(message = "El nombre no debe estar vacio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Solo se permiten letras y espacios")
    private String nombre;

    @Length(min = 2, max = 40, message = "El apellido no debe ser menor a 2 y mayor a 40 caracteres")
    @NotBlank(message = "El apellido no debe estar vacio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Solo se permiten letras y espacios")
    private String apellido;

    @Length(max = 100, message = "El correo no debe ser mayor a 100 caracteres")
    @NotBlank(message = "El correo no debe estar vacio")
    @Email(message = "Email invalido")
    private String correo;

    @NotBlank(message = "La contraseña no debe estar vacia")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Clave debe tener mínimo 8 caracteres, una letra y un número")
    private String clave;

    public RegistroDTO() {
    }

    public RegistroDTO(String nombre, String apellido, String correo, String clave) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}



