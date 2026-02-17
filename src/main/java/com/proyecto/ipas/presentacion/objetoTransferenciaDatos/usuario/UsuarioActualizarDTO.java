package com.proyecto.ipas.presentacion.objetoTransferenciaDatos.usuario;

import com.proyecto.ipas.datos.entidad.UsuarioEntidad;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UsuarioActualizarDTO {

    @Length(min = 2, max = 40, message = "El nombre no debe ser a 2 y mayor a 40 caracteres")
    @NotBlank(message = "El nombre no debe estar vacio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Solo se permiten letras y espacios")
    private String nombre;

    @Length(min = 2, max = 40, message = "El apellido no debe ser menor a 2 y mayor a 40 caracteres")
    @NotBlank(message = "El apellido no debe estar vacio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "Solo se permiten letras y espacios")
    private String apellido;

    private TipoDocumentoUsuario tipoDocumento;

    @Length(min = 10, max = 15, message = "El numero de documento no debe ser menor a 10 y mayor a 15 caracteres")
    private String numeroDocumento;

    @Length(min = 10, max = 15, message = "El numero de telefono no debe ser menor a 10 y mayor a 15 caracteres")
    private String telefono;

    @Length(min = 5, max = 200, message = "La direccion no debe ser menor a 5 y mayor a 200 caracteres")
    private String direccion;


    public void actualizarUsuario(UsuarioEntidad usuarioEntidad) {
        if (nombre != null && !nombre.isEmpty() && !nombre.isBlank()) {
            usuarioEntidad.setNombre(nombre);
        }
        if (apellido != null && !apellido.isEmpty() && !apellido.isBlank()) {
            usuarioEntidad.setApellido(apellido);
        }
        if (tipoDocumento != null ) {
            usuarioEntidad.setTipoDocumento(tipoDocumento);
        }
        if (numeroDocumento != null && !numeroDocumento.isBlank()) {
            usuarioEntidad.setNumeroDocumento(numeroDocumento);
        }
        if (telefono != null && !telefono.isBlank()) {
            usuarioEntidad.setTelefono(telefono);
        }
        if (direccion != null && !direccion.isBlank()) {
            usuarioEntidad.setDireccion(direccion);
        }
    }

}
