package com.proyecto.ipas.infraestructura.utilidades;

/**
 * Interfaz marcadora (marker interface) para validaciones en grupos de Bean Validation.
 * 
 * Se utiliza con la anotación {@code @Validated(EnCreacion.class)} en controladores
 * para indicar que solo deben ejecutarse validaciones anotadas con 
 * {@code @NotNull(groups = EnCreacion.class)} u similar.
 * 
 * Permite separar reglas de validación depending en diferentes casos de uso
 * (creación vs actualización de entidades).
 * 
 * Ejemplo en una entidad:
 * {@code
 * @NotNull(groups = EnCreacion.class, message = "El ID es requerido en creación")
 * private Long id;
 * }
 */
public interface EnCreacion {
}
