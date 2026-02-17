package com.proyecto.ipas.negocio.dominio.modelo;

import com.proyecto.ipas.negocio.dominio.enums.EstadoUsuario;
import com.proyecto.ipas.negocio.dominio.enums.TipoDocumentoUsuario;
import com.proyecto.ipas.presentacion.excepcion.NegocioExcepcion;
import com.proyecto.ipas.presentacion.excepcion.PermisoInsuficienteExcepcion;

import java.util.Objects;

public class Usuario {

    /**
     * Atributos necesarios para el modelo
     */
    private Long id;
    private final String nombre;
    private final String apellido;
    private final String email;
    private EstadoUsuario estado;
    private Rol rol ;


    // CONSTRUCTOR PRIVADO
    /**
     * El constructor privado fuerza que los objetos solo se creen a través de factory methods,
     * garantizando que SIEMPRE se apliquen las validaciones de negocio correctas según cada escenario de creación.
     */
    private Usuario(String nombre, String apellido, String email, EstadoUsuario estado, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.estado = estado;
        this.rol = rol;

        validarInvariantes();
    }


    // METODOS DE FABRICA
    /**
     * Registra un nuevo usuario en el sistema con un estado INACTIVO hasta que el administrador active su cuenta
     * Este usuario se crea desde la app
     */
    public static Usuario registrarNuevo(String nombres, String apellidos, String email, Rol rol) {
        validarNombres(nombres);
        validarApellidos(apellidos);
        return new Usuario(nombres, apellidos, email, EstadoUsuario.INACTIVO, rol);
    }

    /**
     * Reconstruye un usuario desde la base de datos
     * NO aplica validaciones de negocio (el objeto ya existe)
     * Este usuario se reconstruye ya que es de la base de datos
     * Este metodo no modela una accion de negocio y es interno por eso no sigue la regla de los 7 parámetros
     */
    public static Usuario reconstruir(Long id, String nombres, String apellidos, String email, EstadoUsuario estado, Rol rol) {
        validarIdUsuario(id);
        validarNombres(nombres);
        validarApellidos(apellidos);
        validarEstado(estado);
        Usuario usuario = new Usuario(nombres, apellidos, email, estado, rol);

        usuario.id = id;

        return usuario;
    }


    // METODOS DE NEGOCIO / COMANDOS

    /**
     * Fucnionalidad que tiene permitida solo el administrador para acticar los usuarios del sistema
     */
    public void activar(Usuario usuarioObjetivo) {
        if (this.estado == EstadoUsuario.ACTIVO) {
            throw new NegocioExcepcion("El usuario ya se encuentra ACTIVADO");
        }

        if (!usuarioObjetivo.rol.esAdministrador()) {
            throw new PermisoInsuficienteExcepcion("Accion no permitida");
        }

        this.estado = EstadoUsuario.ACTIVO;
    }

    /**
     * Fucnionalidad que tiene permitida solo el administrador para desacticar los usuarios del sistema
     */
    public void desactivar(Usuario usuarioObjetivo) {
        if (this.estado == EstadoUsuario.INACTIVO) {
            throw new NegocioExcepcion("El usuario ya se encuentra INACTIVO");
        }
        if (!usuarioObjetivo.rol.esAdministrador()) {
            throw new PermisoInsuficienteExcepcion("Accion no permitida");
        }
        this.estado = EstadoUsuario.INACTIVO;
    }

    /**
     * Fucnionalidad que tiene permitida solo el administrador para suspender los usuarios del sistema
     */
    public void suspender(Usuario usuarioObjetivo) {
        if (this.estado == EstadoUsuario.SUSPENDIDO) {
            throw new NegocioExcepcion("El usuario ya se encuentra SUSPENDIDO", "USUARIO_SUSPENDIDO");
        }
        if (!usuarioObjetivo.rol.esAdministrador()) {
            throw new PermisoInsuficienteExcepcion("Accion no permitida");
        }
        this.estado = EstadoUsuario.SUSPENDIDO;

    }

    /**
     * Cambia el rol del usuario
     * Solo administradores pueden hacer esto
     */
    public void cambiarRol(Rol nuevoRol, Usuario administrador) {
        if (!administrador.esAdministrador()) {
            throw new PermisoInsuficienteExcepcion("Solo administradores pueden cambiar roles");
        }

        if (this.rol.equals(nuevoRol)) {
            throw new NegocioExcepcion("El usuario ya tiene ese rol");
        }

        this.rol = nuevoRol;
    }


    // METODOS DE NEGOCIO / CONSULTAS
    /**
     * Verifica si el usuario esta activo
     */
    public boolean esAactivo() {
        return this.estado == EstadoUsuario.ACTIVO;
    }

    /**
     * Verifica si el usuario está bloqueado
     */
    public boolean esInactivo() {
        return this.estado == EstadoUsuario.INACTIVO;
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean esAdministrador() {
        return this.rol.esAdministrador();
    }

    /**
     * Verifica si el usuario es un asesor
     */
    public boolean esAsesor() {
        return this.rol.esAsesor();
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }


    // VALIDACIONES PRIVADAS

    /**
     * Valida si alguno de los campos obligatorios para el modelo es inconsistente
     */
    private void validarInvariantes() {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        if (email == null) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }

    private static void validarIdUsuario(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario debe ser positivo");
        }
    }

    /**
     * Valida el formato correcto que debe cumplir el nombre del modelo
     * No menor a dos caracteres
     * No mayor a 100 caracteres
     * Solo contiene letras y espacios
     */
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
                    "Los nombres no pueden exceder 40 caracteres"
            );
        }

        if (!nombres.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException(
                    "Los nombres solo pueden contener letras y espacios"
            );
        }
    }

    /**
     * Valida el formato correcto que debe cumplir el apellido del modelo
     * No menor a 2 caracteres
     * No mayor a 100 caracteres
     */
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

    private static void validarEstado(EstadoUsuario estado) {
        if (estado == EstadoUsuario.INACTIVO) {
            throw new NegocioExcepcion("Estado del usuario INVALIDO. Contacta un administrador!");
        } else if (estado == EstadoUsuario.SUSPENDIDO) {
            throw new NegocioExcepcion("Estado del usuario SUSPENDIDO. Contacta un administrador!");
        }

    }




    // GETTERS - SOLO LECTURA
    public Long getId() { return id; }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public Rol getRol() {
        return rol;
    }


    // EQUALS, HASHCODE, TOSTRING

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        return Objects.equals(id, usuario.id);
    }

    /**
     * Usa el mismo identificador que el Objects.equals
     */
    @Override
    public int hashCode() {

        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre=" + nombre +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", estadoUsuario=" + estado +
                ", rol=" + rol +
                '}';
    }
}


