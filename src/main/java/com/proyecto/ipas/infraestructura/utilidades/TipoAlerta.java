package com.proyecto.ipas.infraestructura.utilidades;


/**
 * Define los niveles de severidad para las notificaciones del sistema.
 * Se utiliza en conjunto con AlertaDTO para estandarizar la respuesta al frontend.
 */
public enum TipoAlerta {

    /**
     * EXITO: Se utiliza cuando una acción del usuario finalizó correctamente.
     * Ejemplo: "Usuario registrado", "Datos guardados", "Correo enviado".
     * Color sugerido: Verde.
     */
    EXITO,

    /**
     * INFO: Se utiliza para mensajes informativos que no implican el éxito o fallo de una acción.
     * Ejemplo: "Hay una nueva actualización disponible", "Su sesión expirará en 5 minutos".
     * Color sugerido: Azul.
     */
    INFO,

    /**
     * ADVERTENCIA: Se utiliza cuando la acción se completó pero hubo algún detalle
     * que el usuario debe notar, o para prevenir una acción irreversible.
     * Ejemplo: "El archivo es muy pesado", "¿Está seguro de eliminar este registro?".
     * Color sugerido: Amarillo/Naranja.
     */
    ADVERTENCIA,

    /**
     * ERROR: Se utiliza cuando una acción no pudo completarse debido a un fallo
     * esperado o validación de negocio.
     * Ejemplo: "Contraseña incorrecta", "El usuario ya existe", "Saldo insuficiente".
     * Color sugerido: Rojo.
     */
    ERROR,

    /**
     * FATAL: Se utiliza para errores críticos del sistema o excepciones no controladas
     * que impiden el flujo normal de la aplicación.
     * Ejemplo: "Error de conexión con la base de datos", "Servidor no disponible".
     * Color sugerido: Rojo oscuro o púrpura. Normalmante requiere contacto con soporte.
     */
    FATAL
}