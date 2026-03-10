package com.proyecto.ipas.negocio.dominio.modelo;

import java.util.Objects;

public class Aseguradora {
    private Long idAseguradora;
    private String nombre;
    private String numeroDocumento;
    private String telefono;
    private String clave;

    private Aseguradora(String nombre, String numeroDocumento, String telefono, String clave) {
        this.nombre = nombre;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.clave = clave;

        validarInvariantes();
    }

    /**
     * Factory method que registra una nueva aseguradora en el sistema.
     * 
     * Se valida que la aseguradora tenga todos los datos requeridos: nombre, número de documento,
     * teléfono de contacto y credenciales de acceso. Los datos se validan al momento de creación
     * mediante los invariantes del dominio.
     * 
     * @param nombre el nombre o razón social de la aseguradora
     * @param numeroDocumento el número de identificación tributaria (NIT u equivalente)
     * @param telefono el número de teléfono de contacto de la aseguradora
     * @param clave la credencial de acceso del sistema para la aseguradora
     * @return una nueva instancia de Aseguradora con ID null (a ser asignado por base de datos)
     * @throws IllegalArgumentException si alguno de los parámetros obligatorios es nulo o vacío
     */
    public static Aseguradora registrar (String nombre, String numeroDocumento, String telefono, String clave) {

        return new Aseguradora(nombre, numeroDocumento, telefono, clave);
    }

    /**
     * Factory method que reconstruye una aseguradora desde los datos persistidos en base de datos.
     * 
     * Este método es utilizado por la capa de infraestructura para instanciar objetos Aseguradora
     * desde registros existentes. Valida que todos los datos obligatorios sean válidos.
     * 
     * @param idAseguradora el identificador único de la aseguradora en base de datos
     * @param nombre el nombre de la aseguradora
     * @param numeroDocumento el número de documento de la aseguradora
     * @param telefono el teléfono de contacto
     * @param clave la credencial de acceso del sistema
     * @return una instancia de Aseguradora reconstruida con los parámetros proporcionados
     * @throws IllegalArgumentException si any mandatory field is null or blank
     */
    public static Aseguradora reconstruir (Long idAseguradora, String nombre, String numeroDocumento, String telefono, String clave) {

        Aseguradora aseguradora = new Aseguradora(nombre, numeroDocumento, telefono, clave);

        aseguradora.idAseguradora = idAseguradora;

        return aseguradora;
    }

    private void validarInvariantes() {
        if (numeroDocumento == null || numeroDocumento.isBlank()) {
            throw new IllegalArgumentException("El numero de documento es obligatorio");
        }

        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El numero de telefono es obligatorio");
        }

        if (clave == null || clave.isBlank()) {
            throw new IllegalArgumentException("La clave es obligatoria");
        }
    }

    private static void validarIdAseguradora(Long idAseguradora) {
        if (idAseguradora == null || idAseguradora <= 0) {
            throw new IllegalArgumentException("ID de la aseguradora debe ser positivo");
        }
    }

    private static void validarNombre (String nombres) {
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

    }

    public String getNombre() {
        return nombre;
    }

    public Long getIdAseguradora() {
        return idAseguradora;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getClave() {
        return clave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aseguradora that = (Aseguradora) o;
        return Objects.equals(idAseguradora, that.idAseguradora);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idAseguradora);
    }

    @Override
    public String toString() {
        return "Aseguradora{" +
                "idAseguradora=" + idAseguradora +
                ", nombre='" + nombre + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
