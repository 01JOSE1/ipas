package com.proyecto.ipas.negocio.dominio.enums;

public enum TipoRamo {

    /** Pólizas de salud privada y medicina prepagada. */
    SALUD,

    /** Seguros de vida individual, grupo, accidentes personales y exequias. */
    VIDA,

    /** Seguros voluntarios de vehículos (Pérdidas totales, parciales, responsabilidad civil extracontractual). */
    AUTOMOVIL,

    /** Seguro Obligatorio de Accidentes de Tránsito. Se separa de Automóvil por tener regulación y tarifas de ley. */
    SOAT,

    /** Protección de vivienda frente a incendio, terremoto, inundación y hurto. */
    HOGAR,

    /** Garantiza el cumplimiento de contratos privados o estatales (Seriedad de oferta, buen manejo de anticipo). */
    CUMPLIMIENTO,

    /** Responsabilidad Civil Extracontractual. Protege el patrimonio frente a daños causados a terceros. */
    RESPONSABILIDAD_CIVIL,

    /** Riesgos Laborales. Seguro obligatorio para trabajadores dependientes e independientes. */
    ARL,

    /** Seguros para mercancías en tránsito (importaciones, exportaciones y transporte nacional). */
    TRANSPORTE,

    /** Ramos técnicos para empresas (Incendio, Terremoto, Rotura de Maquinaria, Equipo Electrónico). */
    MULTIRIESGO_EMPRESARIAL,

    /** Pólizas que garantizan el pago de cánones de arrendamiento y servicios públicos. */
    ARRENDAMIENTO
}
