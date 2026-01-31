package com.proyecto.ipas.datos.entidad;

import com.proyecto.ipas.negocio.dominio.enums.EstadoUsuario;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class UsuarioEntidad {

    @Id
//    @GeneratedValue → valor automático
//    IDENTITY → lo genera la BD
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 40)
    private String nombre;

    @Column(nullable = false, length = 40)
    private String apellido;

    @Enumerated(EnumType.STRING)
    private TipoDocumentoUsuario tipoDocumento;

    @Column(name = "numero_documento", length = 40)
    private String numeroDocumento;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String direccion;

    @Column(nullable = false, unique = true, length = 100)
    @Email
    private String correo;

    @Column(nullable = false)
    private String clave;

//    EnumType.ORDINAL
//    EnumType.STRING => RECOMENDADO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado;

//    FK de la tabla usuario
//    @ManyToOne(fetch = FetchType.LAZY) // carga la entidad relacionada solo cuando la accedes, no cuando cargas la entidad principal
    @ManyToOne(fetch = FetchType.EAGER) // Evita problemas con spring security (LazyInitializationException) y no tiene mucho inpacto ya que es un solo rol
    @JoinColumn(name = "role_id" , nullable = false) // nombre de la columna FK en la BD
    private RolEntidad rol;

    public UsuarioEntidad() {
    }

    public UsuarioEntidad(String nombre, String apellido, String correo, String clave, RolEntidad rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.clave = clave;
        this.estado = EstadoUsuario.INACTIVO;
        this.rol = rol;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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

    public TipoDocumentoUsuario getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoUsuario tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public EstadoUsuario getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public boolean esActivo() {
        return estado == EstadoUsuario.ACTIVO;
    }

    public boolean esSuspendido() {
        return estado == EstadoUsuario.SUSPENDIDO;
    }

    public RolEntidad getRol() {
        return rol;
    }

    public void setRol(RolEntidad rolEntidad) {
        this.rol = rolEntidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsuarioEntidad that = (UsuarioEntidad) o;
        // Para entidades JPA
        // Si idUsuario es null (objeto nuevo no persistido), comparar por identidad de memoria
        if (idUsuario == null || that.idUsuario == null) {
            return false; // Objetos nuevos nunca son iguales
        }
        return Objects.equals(idUsuario, that.idUsuario);
    }

    @Override
    public int hashCode() {
        // IMPORTANTE: Para entidades JPA
        // No usar id porque puede cambiar (null → valor al persistir)
        // Usar un valor constante
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UsuarioEntidad{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", correo='" + correo + '\'' +
                ", estado=" + estado +
                '}';
    }
}
