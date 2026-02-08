package com.proyecto.ipas.datos.entidad;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proyecto.ipas.negocio.dominio.enums.EstadoCivilCliente;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoCliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class ClienteEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String apellido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 40)
    private TipoDocumentoCliente tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 15)
    private String numeroDocumento;

    @Column(name = "fecha_nacimiento", nullable = false)
//    @JsonFormat(pattern = "dd-MM-yyyy") // El formato que verás en el JSON de la API
    @Past
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_civil", nullable = false, length = 40)
    private EstadoCivilCliente estadoCivil;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Email
    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = true, length = 100)
    private String direccion;

    @Column(nullable = true, length = 60)
    private String ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntidad usuario;

    public ClienteEntidad() {
    }

    public ClienteEntidad(String nombre, String apellido, TipoDocumentoCliente tipoDocumento, String numeroDocumento, LocalDate fechaNacimiento, EstadoCivilCliente estadoCivil, String telefono, String correo, UsuarioEntidad usuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.estadoCivil = estadoCivil;
        this.telefono = telefono;
        this.correo = correo;
        this.usuario = usuario;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
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

    public TipoDocumentoCliente getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoCliente tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public @Past LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(@Past LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public EstadoCivilCliente getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivilCliente estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public @Email String getCorreo() {
        return correo;
    }

    public void setCorreo(@Email String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public UsuarioEntidad getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntidad usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        ClienteEntidad that = (ClienteEntidad) obj;

        if (idCliente == null || that.idCliente == null) return false;

        return Objects.equals(idCliente, that.idCliente);

    }

    @Override
    public String toString() {
        return "ClienteEntidad{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", estadoCivil=" + estadoCivil +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", usuario=" + usuario +
                '}';
    }
}




//    public static class Builder {
//        private String nombre;
//        private String apellido;
//        private TipoDocumentoCliente tipoDocumento;
//        private String numeroDocumento;
//        private LocalDate fechaNacimiento;
//        private EstadoCivilCliente estadoCivil;
//        private String telefono;
//        private String correo;
//        private UsuarioEntidad usuario;
//
//
//        public Builder nombre(String nombre) {
//            this.nombre = nombre;
//            return this;
//        }
//
//        public Builder apellido(String apellido) {
//            this.apellido = apellido;
//            return this;
//        }
//
//        public Builder tipoDocumento(TipoDocumentoCliente tipoDocumento) {
//            this.tipoDocumento = tipoDocumento;
//            return this;
//        }
//
//        public Builder numeroDocumento(String numeroDocumento) {
//            this.numeroDocumento = numeroDocumento;
//            return this;
//        }
//
//        public Builder fechaNacimiento(LocalDate fechaNacimiento) {
//            this.fechaNacimiento = fechaNacimiento;
//            return this;
//        }
//
//        public Builder estadoCivil(EstadoCivilCliente estadoCivil) {
//            this.estadoCivil = estadoCivil;
//            return this;
//        }
//
//        public Builder telefono(String telefono) {
//            this.telefono = telefono;
//            return this;
//        }
//
//        public Builder correo(String correo) {
//            this.correo = correo;
//            return this;
//        }
//
//        public Builder usuarioEntidad(UsuarioEntidad usuarioEntidad) {
//            this.usuario = usuarioEntidad;
//            return this;
//        }
//
//        public ClienteEntidad build() {
//            return new ClienteEntidad(this);
//        }
//    }
//
//    public static Builder builder() {
//        return new Builder();
//    }
