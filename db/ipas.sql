-- ==========================================
-- BASE DE DATOS: IPAS
-- Sistema de gestión de pólizas de seguros
-- ==========================================


-- ==========================================
-- 1. ELIMINACION DE BASE DE DATOS (Opcional)
-- ==========================================
DROP DATABASE IF EXISTS ipas;

-- ==========================================
-- 2. CREACION DE BASE DE DATOS
-- ==========================================
CREATE DATABASE IF NOT EXISTS ipas
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ipas;

-- ==========================================
-- 3. ELIMINACION DE TABLAS (En orden inverso)
-- ==========================================
-- Orden: De la mas dependiente a la mas independiente

-- NIVEL 3: Tabla hijo de multiples padres
DROP TABLE IF EXISTS polizas;
-- NIVEL 2: Tablas con Foreign Keys o dependencias
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS auditorias;
DROP TABLE IF EXISTS usuarios;
-- NIVEL 1: Tablas padre (sin Foreign Keys desde otras tablas)
DROP TABLE IF EXISTS ramos;
DROP TABLE IF EXISTS aseguradoras;
DROP TABLE IF EXISTS roles;


-- ==========================================
-- 4. CREACION DE TABLAS (En orden correcto)
-- ==========================================
-- PRIMERO: Tablas independientes (sin FK)

-- ------------------------------------------
-- Tabla: roles
-- Almacena los roles del sistema (ADMINISTRADOR, ASESOR)
-- ------------------------------------------
CREATE TABLE roles (
    -- BIGINT para consistencia con el resto del esquema y escalabilidad futura
    id_role BIGINT AUTO_INCREMENT,
    nombre_rol VARCHAR(20) NOT NULL,
    descripcion TEXT,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_role PRIMARY KEY (id_role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ------------------------------------------
-- Tabla: aseguradoras
-- Empresas aseguradoras con las que opera el sistema
-- ------------------------------------------
CREATE TABLE aseguradoras (
    id_aseguradora BIGINT AUTO_INCREMENT,
    nombre_aseguradora VARCHAR(40) NOT NULL,
    numero_documento VARCHAR(15) NOT NULL,
    direccion VARCHAR(100),
    ciudad VARCHAR(40),
    telefono VARCHAR(15) NOT NULL,
    -- Clave de acceso o código de integración de la aseguradora
    clave VARCHAR(30) NOT NULL,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_aseguradoras PRIMARY KEY (id_aseguradora),
    CONSTRAINT uk_aseguradoras_telefono UNIQUE (telefono),
    CONSTRAINT uk_aseguradoras_clave UNIQUE (clave),
    CONSTRAINT uk_aseguradoras_numero_documento UNIQUE (numero_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ------------------------------------------
-- Tabla: ramos
-- Categorías de seguros que maneja la agencia
-- ------------------------------------------
CREATE TABLE ramos (
    id_ramo BIGINT AUTO_INCREMENT,
    nombre_ramo VARCHAR(40) NOT NULL,
    -- Porcentaje de comisión del asesor por este ramo (0-30%)
    comision DECIMAL(4,2) NOT NULL,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_ramos PRIMARY KEY (id_ramo),
    CONSTRAINT chk_ramos_comision CHECK (comision >= 0 AND comision <= 30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- SEGUNDO: Tablas con FK a una tabla o dependencias a otras tablas

-- ------------------------------------------
-- Tabla: usuarios
-- Asesores y administradores del sistema IPAS
-- ------------------------------------------
CREATE TABLE usuarios (
    id_usuario BIGINT AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) NOT NULL,
    tipo_documento VARCHAR(40) DEFAULT NULL,
    numero_documento VARCHAR(15) DEFAULT NULL,
    telefono VARCHAR(15) DEFAULT NULL,
    direccion VARCHAR(200) DEFAULT NULL,
    correo VARCHAR(100) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    estado VARCHAR(40) NOT NULL,

    -- Llaves foraneas
    role_id BIGINT NOT NULL,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuarios_telefono UNIQUE (telefono),
    CONSTRAINT uk_usuarios_numero_documento UNIQUE (numero_documento),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo),
    CONSTRAINT chk_usuarios_correo CHECK (correo REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),

    -- Constraints a nivel de tabla llaves foraneas
    CONSTRAINT fk_roles_usuarios FOREIGN KEY (role_id)
    REFERENCES roles(id_role)
    ON DELETE RESTRICT
    ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ------------------------------------------
-- Tabla: auditorias
-- Registro automático de cambios críticos en el sistema
-- Se alimenta exclusivamente mediante triggers (no inserción directa)
-- ------------------------------------------
CREATE TABLE auditorias (
    id_auditoria BIGINT AUTO_INCREMENT,
    tabla_afectada VARCHAR(50) NOT NULL,
    -- ID del registro modificado dentro de la tabla afectada
    id_registro INT NOT NULL,
    -- Tipo de operación: INSERT, UPDATE, DELETE
    accion VARCHAR(15) NOT NULL,
    fecha_accion DATETIME NOT NULL,
    -- Detalle legible de los cambios realizados (campo: valor_anterior -> valor_nuevo)
    detalles TEXT,

    -- Llaves foraneas
    usuario_id BIGINT NOT NULL,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_auditorias PRIMARY KEY (id_auditoria),

    -- Constraints a nivel de tabla llaves foraneas
    CONSTRAINT fk_usuarios_auditorias FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id_usuario)
    ON DELETE RESTRICT
    ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ------------------------------------------
-- Tabla: clientes
-- Personas naturales o jurídicas titulares de pólizas
-- ------------------------------------------
CREATE TABLE clientes (
    id_cliente BIGINT AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) DEFAULT NULL,
    tipo_documento VARCHAR(40) NOT NULL,
    numero_documento VARCHAR(15) NOT NULL,
    fecha_nacimiento DATE DEFAULT NULL,
    estado_civil VARCHAR(40) DEFAULT NULL,
    telefono VARCHAR(10) NOT NULL,
    correo VARCHAR(100) DEFAULT NULL,
    direccion VARCHAR(100) DEFAULT NULL,
    ciudad VARCHAR(60) DEFAULT NULL,

    -- Llaves foraneas
    -- Asesor responsable del cliente
    usuario_id BIGINT NOT NULL,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_clientes PRIMARY KEY (id_cliente),
    CONSTRAINT uk_clientes_telefono UNIQUE (telefono),
    CONSTRAINT uk_clientes_numero_documento UNIQUE (numero_documento),
    CONSTRAINT uk_clientes_correo UNIQUE (correo),
    CONSTRAINT chk_clientes_correo CHECK (correo REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),

    -- Constraints a nivel de tabla llaves foraneas
    CONSTRAINT fk_usuarios_clientes FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id_usuario)
    ON DELETE RESTRICT
    ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- TERCERO: Tablas con FK a múltiples tablas

-- ------------------------------------------
-- Tabla: polizas
-- Contratos de seguro emitidos por la agencia
-- ------------------------------------------
CREATE TABLE polizas (
    id_poliza BIGINT AUTO_INCREMENT,
    -- Código legible de la póliza (ej: POL-2024-SAL-1001)
    codigo_poliza VARCHAR(20) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    -- Prima sin impuestos ni recargos
    prima_neta DECIMAL(15,2) NOT NULL,
    -- Prima final facturada al cliente
    prima_total DECIMAL(15,2) NOT NULL,
    estado VARCHAR(40) DEFAULT NULL,
    estado_pago VARCHAR(40) NOT NULL DEFAULT 'PENDIENTE',
    -- Referencia al documento PDF de la póliza
    numero_pdf VARCHAR(150) NOT NULL,
    -- Placa del vehículo (obligatoria solo para ramo AUTOMOVIL, ver trigger validar_placa_insert)
    placa VARCHAR(15) DEFAULT NULL,
    -- Observaciones adicionales sobre la póliza
    descripcion VARCHAR(255) DEFAULT NULL,

    -- Llaves foraneas
    cliente_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    ramo_id BIGINT NOT NULL,
    aseguradora_id BIGINT NOT NULL,

    -- Constraints a nivel de tabla
    CONSTRAINT pk_polizas PRIMARY KEY (id_poliza),
    CONSTRAINT uk_polizas_codigo UNIQUE (codigo_poliza),
    CONSTRAINT uk_polizas_pdf UNIQUE (numero_pdf),
    CONSTRAINT chk_polizas_fechas CHECK (fecha_vencimiento > fecha_inicio),
    CONSTRAINT chk_polizas_prima_neta CHECK (prima_neta >= 0),
    CONSTRAINT chk_polizas_prima_total CHECK (prima_total >= prima_neta),

    -- Constraints a nivel de tabla llaves foraneas
    CONSTRAINT fk_clientes_polizas FOREIGN KEY (cliente_id)
    REFERENCES clientes(id_cliente)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

    CONSTRAINT fk_usuarios_polizas FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id_usuario)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

    CONSTRAINT fk_ramos_polizas FOREIGN KEY (ramo_id)
    REFERENCES ramos(id_ramo)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,

    CONSTRAINT fk_aseguradoras_polizas FOREIGN KEY (aseguradora_id)
    REFERENCES aseguradoras(id_aseguradora)
    ON DELETE RESTRICT
    ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ==========================================
-- 5. INDICES ADICIONALES
-- ==========================================
-- Mejoramos las busquedas en los puntos criticos como clientes y polizas

CREATE INDEX idx_clientes_documento ON clientes(numero_documento);
CREATE INDEX idx_clientes_apellido ON clientes(apellido);
CREATE INDEX idx_clientes_telefono ON clientes(telefono);

CREATE INDEX idx_polizas_codigo ON polizas(codigo_poliza);
CREATE INDEX idx_polizas_placa ON polizas(placa);
CREATE INDEX idx_polizas_estado ON polizas(estado);
-- Para consultas por cliente + estado:
CREATE INDEX idx_polizas_cliente_estado ON polizas(cliente_id, estado);
-- Para consultas por fecha:
CREATE INDEX idx_polizas_fecha_inicio ON polizas(fecha_inicio);
CREATE INDEX idx_polizas_fecha_vencimiento ON polizas(fecha_vencimiento);


-- ==========================================
-- 6. TRIGGERS
-- ==========================================
-- Una parte de código que se ejecuta automáticamente en la DB cuando hay un evento específico.
-- Cambia el delimitador de ; a // temporalmente, porque dentro del trigger se usa ;


-- ------------------------------------------
-- Trigger: validar_edad_cliente
-- Valida que el cliente sea mayor de edad antes de insertarlo
-- ------------------------------------------
DELIMITER //

CREATE TRIGGER validar_edad_cliente
BEFORE INSERT ON clientes
FOR EACH ROW
BEGIN
    IF TIMESTAMPDIFF(YEAR, NEW.fecha_nacimiento, CURDATE()) < 18 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El cliente debe ser mayor de edad (mínimo 18 años)';
    END IF;
END //

DELIMITER ;


-- ------------------------------------------
-- Trigger: validar_edad_cliente_update
-- Evita que se establezca una fecha de nacimiento que haga menor al cliente al actualizarlo
-- ------------------------------------------
DELIMITER //

CREATE TRIGGER validar_edad_cliente_update
BEFORE UPDATE ON clientes
FOR EACH ROW
BEGIN
    IF TIMESTAMPDIFF(YEAR, NEW.fecha_nacimiento, CURDATE()) < 18 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede establecer una fecha de nacimiento que haga al cliente menor de edad';
    END IF;
END //

DELIMITER ;


-- ------------------------------------------
-- Trigger: tr_auditoria_crear_cliente
-- Registra en auditorias cada vez que se crea un nuevo cliente
-- ------------------------------------------
DELIMITER //

CREATE TRIGGER tr_auditoria_crear_cliente
AFTER INSERT ON clientes
FOR EACH ROW
BEGIN
    INSERT INTO auditorias (
        tabla_afectada,
        id_registro,
        accion,
        fecha_accion,
        detalles,
        usuario_id
    )
    VALUES (
        'clientes',
        NEW.id_cliente,
        'INSERT',
        NOW(),
        CONCAT('Se creó el cliente: ', NEW.nombre, ' ', NEW.apellido, ' con documento: ', NEW.numero_documento),
        NEW.usuario_id
    );
END //

DELIMITER ;


-- ------------------------------------------
-- Trigger: auditoria_clientes
-- Cada vez que se actualiza un registro en la tabla clientes este trigger se activa.
-- Registra solo los campos que efectivamente cambiaron.
-- ------------------------------------------
DELIMITER //

-- Se ejecuta una vez por registro (cliente) afectado por el UPDATE
CREATE TRIGGER auditoria_clientes
AFTER UPDATE ON clientes
FOR EACH ROW
-- Inicia el bloque de código
BEGIN
    -- Crea una variable interna (cambios) donde se irá guardando una cadena con los detalles de los cambios
    DECLARE cambios TEXT DEFAULT '';

    -- Verificamos campo por campo y construimos la cadena solo con los cambios
    IF OLD.nombre <> NEW.nombre THEN
        SET cambios = CONCAT(cambios, 'Nombre: ', OLD.nombre, ' -> ', NEW.nombre, '; ');
    END IF;

    IF OLD.apellido <> NEW.apellido THEN
        SET cambios = CONCAT(cambios, 'Apellido: ', OLD.apellido, ' -> ', NEW.apellido, '; ');
    END IF;

    IF OLD.tipo_documento <> NEW.tipo_documento THEN
        SET cambios = CONCAT(cambios, 'Tipo documento: ', OLD.tipo_documento, ' -> ', NEW.tipo_documento, '; ');
    END IF;

    IF OLD.numero_documento <> NEW.numero_documento THEN
        SET cambios = CONCAT(cambios, 'Numero documento: ', OLD.numero_documento, ' -> ', NEW.numero_documento, '; ');
    END IF;

    IF OLD.estado_civil <> NEW.estado_civil THEN
        SET cambios = CONCAT(cambios, 'Estado civil: ', OLD.estado_civil, ' -> ', NEW.estado_civil, '; ');
    END IF;

    IF OLD.telefono <> NEW.telefono THEN
        SET cambios = CONCAT(cambios, 'Telefono: ', OLD.telefono, ' -> ', NEW.telefono, '; ');
    END IF;

    IF OLD.correo <> NEW.correo THEN
        SET cambios = CONCAT(cambios, 'Correo: ', OLD.correo, ' -> ', NEW.correo, '; ');
    END IF;

    IF OLD.direccion <> NEW.direccion THEN
        SET cambios = CONCAT(cambios, 'Direccion: ', OLD.direccion, ' -> ', NEW.direccion, '; ');
    END IF;

    IF OLD.ciudad <> NEW.ciudad THEN
        SET cambios = CONCAT(cambios, 'Ciudad: ', OLD.ciudad, ' -> ', NEW.ciudad, '; ');
    END IF;

    -- Insertamos en la tabla de auditoria solo si hay cambios
    IF cambios <> '' THEN
        INSERT INTO auditorias(
            tabla_afectada,
            id_registro,
            accion,
            fecha_accion,
            detalles,
            usuario_id
        )
        VALUES (
            'clientes',
            OLD.id_cliente,
            'UPDATE',
            NOW(),
            cambios,

            -- Este dato se envía desde la aplicación mediante SET @usuario_actual = id
            @usuario_actual
        );
    END IF;
-- Cierra el bloque del trigger
END //

DELIMITER ;


-- ------------------------------------------
-- Trigger: validar_placa_insert
-- Valida antes de insertar una póliza que, si el ramo es AUTOMOVIL,
-- el campo placa no puede quedar vacío ni nulo.
-- Para los demás ramos la placa es opcional.
-- ------------------------------------------
DELIMITER //

CREATE TRIGGER validar_placa_insert
BEFORE INSERT ON polizas
FOR EACH ROW
BEGIN
    DECLARE v_nombre_ramo VARCHAR(40);

    -- Consultar el nombre del ramo de la póliza
    SELECT nombre_ramo INTO v_nombre_ramo
    FROM ramos
    WHERE id_ramo = NEW.ramo_id;

    -- Validar que si es AUTOMOVIL, la placa no puede ser vacía ni nula
    IF v_nombre_ramo = 'AUTOMOVIL' AND (NEW.placa IS NULL OR NEW.placa = '') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Debe ingresar la placa para las pólizas del ramo AUTOMOVIL';
    END IF;
END //

DELIMITER ;


-- ------------------------------------------
-- Trigger: validar_placa_update
-- Evita que se elimine la placa de una póliza del ramo AUTOMOVIL al actualizar
-- ------------------------------------------
DELIMITER //

CREATE TRIGGER validar_placa_update
BEFORE UPDATE ON polizas
FOR EACH ROW
BEGIN
    DECLARE v_nombre_ramo VARCHAR(40);

    SELECT nombre_ramo INTO v_nombre_ramo
    FROM ramos
    WHERE id_ramo = NEW.ramo_id;

    IF v_nombre_ramo = 'AUTOMOVIL' AND (NEW.placa IS NULL OR NEW.placa = '') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No puede eliminar la placa de una póliza de AUTOMOVIL';
    END IF;
END //

DELIMITER ;


-- ------------------------------------------
-- Trigger: auditoria_polizas
-- Registra en auditorias cada cambio relevante realizado sobre una póliza.
-- Usa el operador <=> (NULL-safe equals) para comparar correctamente campos opcionales.
-- ------------------------------------------
DELIMITER //

CREATE TRIGGER auditoria_polizas
AFTER UPDATE ON polizas
FOR EACH ROW
BEGIN
    DECLARE cambios TEXT DEFAULT '';

    -- Comparación NULL-safe: detecta cambios incluso cuando el valor anterior o nuevo es NULL
    IF NOT (OLD.fecha_inicio <=> NEW.fecha_inicio) THEN
        SET cambios = CONCAT(cambios,
            'Fecha de inicio: ',
            IFNULL(OLD.fecha_inicio, 'NULL'),
            ' -> ',
            IFNULL(NEW.fecha_inicio, 'NULL'),
            '; ');
    END IF;

    IF NOT (OLD.fecha_vencimiento <=> NEW.fecha_vencimiento) THEN
        SET cambios = CONCAT(cambios,
            'Fecha de vencimiento: ',
            IFNULL(OLD.fecha_vencimiento, 'NULL'),
            ' -> ',
            IFNULL(NEW.fecha_vencimiento, 'NULL'),
            '; ');
    END IF;

    IF NOT (OLD.prima_neta <=> NEW.prima_neta) THEN
        SET cambios = CONCAT(cambios,
            'Prima neta: ',
            IFNULL(OLD.prima_neta, 'NULL'),
            ' -> ',
            IFNULL(NEW.prima_neta, 'NULL'),
            '; ');
    END IF;

    IF NOT (OLD.prima_total <=> NEW.prima_total) THEN
        SET cambios = CONCAT(cambios,
            'Prima total: ',
            IFNULL(OLD.prima_total, 'NULL'),
            ' -> ',
            IFNULL(NEW.prima_total, 'NULL'),
            '; ');
    END IF;

    IF NOT (OLD.estado <=> NEW.estado) THEN
        SET cambios = CONCAT(cambios,
            'Estado: ',
            IFNULL(OLD.estado, 'NULL'),
            ' -> ',
            IFNULL(NEW.estado, 'NULL'),
            '; ');
    END IF;

    IF NOT (OLD.numero_pdf <=> NEW.numero_pdf) THEN
        SET cambios = CONCAT(cambios,
            'Numero del PDF: ',
            IFNULL(OLD.numero_pdf, 'NULL'),
            ' -> ',
            IFNULL(NEW.numero_pdf, 'NULL'),
            '; ');
    END IF;

    IF NOT (OLD.placa <=> NEW.placa) THEN
        SET cambios = CONCAT(cambios,
            'Placa: ',
            IFNULL(OLD.placa, 'NULL'),
            ' -> ',
            IFNULL(NEW.placa, 'NULL'),
            '; ');
    END IF;

    IF cambios <> '' THEN
        INSERT INTO auditorias(
            tabla_afectada,
            id_registro,
            accion,
            fecha_accion,
            detalles,
            usuario_id
        )
        VALUES (
            'polizas',
            OLD.id_poliza,
            'UPDATE',
            NOW(),
            cambios,
            -- Este dato se envía desde la aplicación mediante SET @usuario_actual = id
            @usuario_actual
        );
    END IF;

END //

DELIMITER ;


-- ==========================================
-- 7. FUNCIONES
-- ==========================================

-- ------------------------------------------
-- Función: levenshtein
-- Calcula la Distancia de Levenshtein entre dos cadenas de texto.
-- Mide cuántas operaciones mínimas (inserción, eliminación, sustitución)
-- se necesitan para transformar s1 en s2.
-- Uso principal: tolerancia a errores tipográficos al buscar aseguradoras,
-- clientes o pólizas. Ej: "Seguros de Estado" ≈ "Seguros del Estado".
-- ------------------------------------------
DELIMITER $$

CREATE FUNCTION levenshtein(s1 VARCHAR(255), s2 VARCHAR(255))
RETURNS INT
DETERMINISTIC
BEGIN
    -- Declaración de variables para longitudes y manejo de la matriz
    DECLARE s1_len, s2_len, i, j, c, c_temp, cost INT;
    DECLARE s1_char CHAR;
    -- cv0 y cv1 actúan como los vectores (filas) de la matriz de edición
    DECLARE cv0, cv1 VARBINARY(256);

    SET s1_len = CHAR_LENGTH(s1),
        s2_len = CHAR_LENGTH(s2),
        cv1 = 0x00,
        j = 1,
        i = 1,
        c = 0;

    -- Caso base: Si los textos son idénticos, la distancia es 0
    IF s1 = s2 THEN
        RETURN 0;
    -- Si una de las cadenas está vacía, la distancia es el largo de la otra
    ELSEIF s1_len = 0 THEN
        RETURN s2_len;
    ELSEIF s2_len = 0 THEN
        RETURN s1_len;
    ELSE
        -- Inicialización del primer vector de la matriz
        WHILE j <= s2_len DO
            SET cv1 = CONCAT(cv1, CHAR(j)), j = j + 1;
        END WHILE;

        -- Inicio del ciclo principal por cada carácter de la primera cadena
        WHILE i <= s1_len DO
            SET s1_char = SUBSTRING(s1, i, 1),
                c = i, -- Representa el costo de inserción
                cv0 = CHAR(i),
                j = 1;

            -- Ciclo interno: Compara el carácter de s1 con cada carácter de s2
            WHILE j <= s2_len DO
                SET c = c + 1;
                -- Si los caracteres son iguales, el costo es 0, si no, es 1 (sustitución)
                SET cost = IF(s1_char = SUBSTRING(s2, j, 1), 0, 1);

                -- Cálculo del valor mínimo entre:
                -- 1. Eliminación (celda de arriba + 1)
                -- 2. Inserción (celda de la izquierda + 1)
                -- 3. Sustitución (celda diagonal + costo)
                SET c_temp = ORD(SUBSTRING(cv1, j, 1)) + cost;
                IF c > c_temp THEN SET c = c_temp; END IF;

                SET c_temp = ORD(SUBSTRING(cv1, j+1, 1)) + 1;
                IF c > c_temp THEN SET c = c_temp; END IF;

                -- Guardar el resultado en el vector actual
                SET cv0 = CONCAT(cv0, CHAR(c)), j = j + 1;
            END WHILE;

            -- Pasar al siguiente vector (fila siguiente de la matriz)
            SET cv1 = cv0, i = i + 1;
        END WHILE;
    END IF;

    -- El último valor calculado en la matriz es la Distancia de Levenshtein
    RETURN c;
END $$

DELIMITER ;


-- ==========================================
-- DATOS INICIALES - SISTEMA IPAS
-- ==========================================


-- ==========================================
-- 1. ROLES
-- ==========================================
INSERT INTO roles (nombre_rol, descripcion) VALUES
('ADMINISTRADOR', 'Acceso total al sistema, gestión de usuarios y configuración'),
('ASESOR',        'Gestión de clientes y pólizas, ventas');


-- ==========================================
-- 2. RAMOS
-- ==========================================
INSERT INTO ramos (nombre_ramo, comision) VALUES
('SALUD',                    12.50),
('VIDA',                     15.00),
('AUTOMOVIL',                10.00),
('SOAT',                      8.50),
('HOGAR',                    11.00),
('CUMPLIMIENTO',             10.00),
('RESPONSABILIDAD_CIVIL',     8.00),
('ARL',                      11.00),
('TRANSPORTE',               13.00),
('MULTIRIESGO_EMPRESARIAL',  10.00),
('ARRENDAMIENTO',             6.00);


-- ==========================================
-- 3. ASEGURADORAS EN COLOMBIA
-- ==========================================
INSERT INTO aseguradoras (nombre_aseguradora, numero_documento, direccion, ciudad, telefono, clave) VALUES
('Suramericana S.A.',              '890903407-9', 'Calle 49 #63-50',        'Medellín', '6045108000', 'SUR#2024$SEC'),
('Seguros Bolívar S.A.',           '860002503',   'Carrera 7 #26-20',       'Bogotá',   '6013077000', 'BOL*SEC@2024'),
('Mapfre Seguros',                 '890903476',   'Calle 72 #10-07',        'Bogotá',   '6014239090', 'MAP#KEY$2024'),
('Allianz Seguros S.A.',           '860002400',   'Carrera 11 #90-20',      'Bogotá',   '6016586000', 'ALZ@2024*KEY'),
('Liberty Seguros S.A.',           '860002534',   'Avenida 19 #109A-30',    'Bogotá',   '6016580000', 'LIB#2024@SEC'),
('AXA Colpatria Seguros S.A.',     '860002184-6', 'Carrera 13 #26-45',      'Bogotá',   '6013077900', 'AXA$KEY#2024'),
('Seguros Generales Suramericana', '890903401',   'Calle 49 #63-50',        'Medellín', '6045108020', 'SGS@2024#SEC'),
('HDI Seguros S.A.',               '860002500',   'Calle 72 #10-51',        'Bogotá',   '6015948000', 'HDI*2024$KEY'),
('Seguros del Estado S.A.',        '860009578-6', 'Carrera 7 #27-18',       'Bogotá',   '6013077400', 'EST#SEC@2024'),
('Equidad Seguros S.A.',           '860028415',   'Avenida 13 #91-46',      'Bogotá',   '6014232000', 'EQU$2024*SEC'),
('Nacional de Seguros S.A.',       '860002527-9', 'CALLE 94 N° 11-30 PISO 4','Bogotá', '6017463219', 'GDH24HSA642');


-- ==========================================
-- 4. USUARIOS
-- ==========================================
-- Nota: Contraseña hasheada con bcrypt correspondiente a "1234qwer"
INSERT INTO usuarios (nombre, apellido, tipo_documento, numero_documento, telefono, direccion, correo, clave, estado, role_id) VALUES
-- Administradores
('Carlos',    'Rodríguez', 'CEDULA_CIUDADANIA', '1098765432', '3001234562', 'Calle 45 #23-10',        'carlos.rodriguez@ipas.com.co', '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'ACTIVO',     1),
('María',     'Gómez',     'CEDULA_CIUDADANIA', '1098765433', '3001234568', 'Carrera 27 #45-67',      'maria.gomez@ipas.com.co',      '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'ACTIVO',     1),

-- Asesores
('Juan',      'Martínez',  'CEDULA_CIUDADANIA', '1098765434', '3001234569', 'Calle 10 #15-20',        'juan.martinez@ipas.com.co',     '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'ACTIVO',     2),
('Ana',       'López',     'CEDULA_CIUDADANIA', '1098765435', '3001234570', 'Carrera 33 #28-45',      'ana.lopez@ipas.com.co',         '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'INACTIVO',   2),
('Pedro',     'Sánchez',   'CEDULA_CIUDADANIA', '1098765436', '3001234571', 'Calle 55 #12-34',        'pedro.sanchez@ipas.com.co',     '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'ACTIVO',     1),
('Laura',     'Torres',    'CEDULA_CIUDADANIA', '1098765437', '3001234572', 'Avenida 15 #45-23',      'laura.torres@ipas.com.co',      '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'ACTIVO',     2),
('Diego',     'Ramírez',   'CEDULA_CIUDADANIA', '1098765438', '3001234573', 'Carrera 18 #34-56',      'diego.ramirez@ipas.com.co',     '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'ACTIVO',     2),
('Sofía',     'Vargas',    'CEDULA_CIUDADANIA', '1098765439', '3001234574', 'Calle 89 #23-12',        'sofia.vargas@ipas.com.co',      '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'SUSPENDIDO', 2),
('Andrés',    'Moreno',    'CEDULA_CIUDADANIA', '1098765440', '3001234575', 'Carrera 50 #67-89',      'andres.moreno@ipas.com.co',     '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'INACTIVO',   2),
('Valentina', 'Castro',    'CEDULA_CIUDADANIA', '1098765441', '3001234576', 'Calle 120 #15-30',       'valentina.castro@ipas.com.co',  '$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq', 'SUSPENDIDO', 2),
('Mario',     'Gomez',     'CEDULA_CIUDADANIA', '1098765421', '3001234171', 'Calle 120 #15-38',       'Mario.Gomez@ipas.com.co',       '$2a$10$IJ.Klz.DyXVAMkXvT/4R.e/LDjFsfLqgRrZeKQoetSp7tMhER214C', 'ACTIVO',    2);


-- ==========================================
-- 5. CLIENTES
-- ==========================================
INSERT INTO clientes (
    nombre, apellido, tipo_documento, numero_documento, fecha_nacimiento,
    estado_civil, telefono, correo, direccion, ciudad, usuario_id
) VALUES
-- clientes del usuario id=1 (Carlos Rodríguez - ADMINISTRADOR)
('Roberto',  'Pérez García',    'CEDULA_CIUDADANIA',  '3784562178',   '1985-03-15', 'SOLTERO',    '3112345676', 'roberto.perez@gmail.com',       'Calle 45 #12-34',       'Bucaramanga', 1),
('Claudia',  'Ruiz Mendoza',    'CEDULA_CIUDADANIA',  '37845622',     '1990-07-22', 'VIUDO',      '3112345679', 'claudia.ruiz@hotmail.com',      'Carrera 27 #56-78',     'Bucaramanga', 1),
('Fernando', 'Silva Ortiz',     'CEDULA_CIUDADANIA',  '91234567',     '1978-11-30', 'SOLTERO',    '3112345680', 'fernando.silva@yahoo.com',      'Calle 67 #23-45',       'Bogotá',      1),
('Patricia', 'Jiménez Cruz',    'PASAPORTE',          'PA123456',     '1988-05-18', 'DIVORCIADO', '3112345681', 'patricia.jimenez@outlook.com',  'Avenida 9 #45-67',      'Medellín',    1),

-- clientes del usuario id=2 (María Gómez - ADMINISTRADOR)
('Miguel',   'Hernández Rojas', 'CEDULA_EXTRANJERA',  '1075234567',   '1982-09-10', 'CASADO',     '3123456789', 'miguel.hernandez@gmail.com',    'Carrera 33 #78-90',     'Bogotá',      2),
('Diana',    'Gutiérrez Pardo', 'PASAPORTE',          '5234567823',   '1995-02-14', 'SOLTERO',    '3123456790', 'diana.gutierrez@hotmail.com',   'Calle 100 #23-45',      'Bogotá',      2),
('Javier',   'Morales Vega',    'CEDULA_EXTRANJERA',  '8023456723',   '1975-12-05', 'VIUDO',      '3123456791', 'javier.morales@yahoo.com',      'Carrera 15 #34-56',     'Barranquilla',2),
('Karen',    'Ramírez Díaz',    'PASAPORTE',          '3785678945',   '1992-08-20', 'UNION_LIBRE','3123456792', 'carolina.ramirez@gmail.com',    'Calle 85 #12-34',       'Bucaramanga', 2),

-- clientes del usuario id=3 (Juan Martínez - ASESOR)
('Lucas',    'Torres Medina',   'CEDULA_EXTRANJERA',  '37856734',     '1980-04-25', 'CASADO',     '3134567890', 'luis.torres@outlook.com',       'Avenida 19 #67-89',     'Medellín',    3),
('Maria',    'López Castillo',  'PASAPORTE',          '52456789233',  '1987-10-12', 'SOLTERO',    '3134567891', 'sandra.lopez@gmail.com',        'Carrera 7 #45-67',      'Bogotá',      3),
('Oscar',    'García Soto',     'LICENCIA_CONDUCCION','9134567823',   '1983-06-08', 'DIVORCIADO', '3134567892', 'oscar.garcia@hotmail.com',      'Calle 50 #23-45',       'Bucaramanga', 3),
('Melissa',  'Martínez Luna',   'CEDULA_EXTRANJERA',  '37867890',     '1993-01-30', 'VIUDO',      '3134567893', 'melissa.martinez@yahoo.com',    'Carrera 28 #56-78',     'Bucaramanga', 3),

-- clientes del usuario id=4 (Ana López - ASESOR)
('Andrés',   'Rodríguez Villa', 'LICENCIA_CONDUCCION','1075345678',   '1979-11-15', 'CASADO',     '3145678901', 'andres.rodriguez@gmail.com',    'Calle 72 #34-56',       'Bogotá',      4),
('Natalia',  'Vargas Rojas',    'PASAPORTE',          '52567890',     '1991-03-22', 'SOLTERO',    '3145678902', 'natalia.vargas@hotmail.com',    'Carrera 40 #67-89',     'Medellín',    4),
('Camilo',   'Suárez Pinto',    'CEDULA_EXTRANJERA',  '80345678',     '1986-07-18', 'CASADO',     '3145678903', 'camilo.suarez@outlook.com',     'Avenida 30 #45-67',     'Cali',        4),

-- clientes del usuario id=5 (Pedro Sánchez - ADMINISTRADOR)
('Paola',    'Gómez Reyes',     'PASAPORTE',          '37878901',     '1994-12-10', 'UNION_LIBRE','3156789012', 'paola.gomez@gmail.com',         'Calle 15 #23-45',       'Bucaramanga', 5),
('Ricardo',  'Mendoza Cruz',    'LICENCIA_CONDUCCION','1098345678',   '1981-05-28', 'CASADO',     '3156789013', 'ricardo.mendoza@yahoo.com',     'Carrera 11 #56-78',     'Barranquilla',5),
('Juliana',  'Castro Ríos',     'LICENCIA_CONDUCCION','52678901',     '1989-09-16', 'VIUDO',      '3156789014', 'juliana.castro@hotmail.com',    'Calle 95 #34-56',       'Bogotá',      5),

-- clientes del usuario id=6 (Laura Torres - ASESOR)
('Daniel',   'Ortiz Paredes',   'LICENCIA_CONDUCCION','91456789',     '1984-02-20', 'CASADO',     '3167890123', 'daniel.ortiz@gmail.com',        'Carrera 50 #78-90',     'Medellín',    6),
('Marcela',  'Ríos Gómez',      'CEDULA_EXTRANJERA',  '37889012',     '1996-06-14', 'UNION_LIBRE','3167890124', 'marcela.rios@outlook.com',      'Calle 120 #23-45',      'Bucaramanga', 6),

-- clientes del usuario id=11 (Mario Gomez - ASESOR)
('José',          'Ordoñez Díaz',  'CEDULA_CIUDADANIA',               '1098765341', '2005-01-06', 'SOLTERO',    '3002375112', 'oljd2002@gmail.com',                   'CALLE 104 E # 12-05',  'Bucaramanga',   11),
('Juana',         'diaz',          'CEDULA_CIUDADANIA',               '234234234',  '2008-02-08', 'SOLTERO',    '3002375231', 'cara.ramirez@gmail.com',               'CALLE 104 E # 12-05',  'Bucaramanga',   11),
('Ramiro',        'Perez',         'CEDULA_EXTRANJERA',               '1098761141', '1998-04-08', 'UNION_LIBRE','3002372112', 'leon@gmail.com',                       'CALLE 104 E # 12-05',  'Bucaramanga',   11),
('MARIANA',       'RAMIREZ',       'CEDULA_CIUDADANIA',               '1234567890', NULL,         'CASADO',     '1234567890', 'MARIANA@GMAIL.COM',                    'Carrera 7 #45-67',     'Bucaramanga',   11),
('mario',         'Gonzales',      'CEDULA_CIUDADANIA',               '121342781731',NULL,         'CASADO',    '7126324121', '002@gmail.com',                        'Carrera 7 #45-67',     'Bucaramanga',   11),
('PEAR SOLUTIONS S.A.S.', NULL,    'NUMERO_IDENTIFICACION_TRIBUTARIA','9001481776', NULL,         NULL,         '6015082339', 'laura.castro@pearsolutarsolutions.com.co','CL 99 # 49 53 P 4', 'BOGOTÁ D.C.',   11),
('JOSEPH STEPHEN','SOSA CARDENAS', 'CEDULA_CIUDADANIA',               '1022437272', NULL,         NULL,         '3195267913', NULL,                                   'TV 35 BIS 29B 63 SUR', 'BOGOTA',        11);


-- ==========================================
-- 6. PÓLIZAS
-- ==========================================
-- Pólizas de SALUD (ramo_id = 1)
INSERT INTO polizas (id_poliza, codigo_poliza, fecha_inicio, fecha_vencimiento, prima_neta, prima_total, estado, estado_pago, numero_pdf, placa, descripcion, cliente_id, usuario_id, ramo_id, aseguradora_id) VALUES
(1001, 'POL-2024-SAL-1001', '2024-01-15', '2025-01-15', 450000.00, 520000.00, 'CANCELADA', 'PAGADA',    'PDF-1001', '526HTS', NULL, 1,  2, 3,  1),
(1002, 'POL-2024-SAL-1002', '2024-02-20', '2025-02-20', 380000.00, 440000.00, 'ACTIVA',    'PENDIENTE', 'PDF-1002', NULL,     NULL, 5,  1, 1,  2),
(1003, 'POL-2024-SAL-1003', '2024-03-10', '2025-03-10', 520000.00, 600000.00, 'CANCELADA', 'PENDIENTE', 'PDF-1003', NULL,     NULL, 9,  3, 1,  3),
(1004, 'POL-2023-SAL-1004', '2023-06-15', '2024-06-15', 400000.00, 460000.00, 'CANCELADA', 'PENDIENTE', 'PDF-1004', NULL,     NULL, 3,  3, 1,  1),

-- Pólizas de VIDA (ramo_id = 2)
(2001, 'POL-2024-VID-2001', '2025-11-20', '2026-12-20', 280000.00, 340000.00, 'ACTIVA',    'PENDIENTE', 'PDF-2001', NULL,     NULL, 12, 2, 2,  4),
(2002, 'POL-2024-VID-2002', '2024-02-15', '2025-02-15', 320000.00, 390000.00, 'CANCELADA', 'PENDIENTE', 'PDF-2002', NULL,     NULL, 11, 6, 2,  5),
(2003, 'POL-2024-VID-2003', '2024-04-05', '2025-04-05', 450000.00, 550000.00, 'ACTIVA',    'PENDIENTE', 'PDF-2003', NULL,     NULL, 11, 5, 2,  6),
(2004, 'POL-2023-VID-2004', '2023-12-01', '2024-12-01', 300000.00, 365000.00, 'CANCELADA', 'PENDIENTE', 'PDF-2004', NULL,     NULL, 14, 6, 2,  4),

-- Pólizas de AUTOMOVIL (ramo_id = 3) — placa obligatoria
(3001, 'POL-2024-ACC-3001', '2024-03-15', '2025-03-15', 180000.00, 220000.00, 'ACTIVA',    'PENDIENTE', 'PDF-3001', NULL,     NULL, 13, 8, 2,  7),
(3002, 'POL-2024-ACC-302',  '2024-04-20', '2025-04-20', 150000.00, 185000.00, 'ACTIVA',    'PENDIENTE', 'PDF-3002', '243gfd', NULL, 17, 7, 3,  8),
(3003, 'POL-2024-ACC-3003', '2024-05-10', '2025-05-10', 200000.00, 245000.00, 'ACTIVA',    'PENDIENTE', 'PDF-3003', NULL,     NULL, 11, 5, 2,  9),
(3004, 'POL-2023-ACC-3004', '2023-08-15', '2024-08-15', 175000.00, 215000.00, 'CANCELADA', 'PENDIENTE', 'PDF-3004', NULL,     NULL, 16, 5, 2,  7),

-- Pólizas de HOGAR (ramo_id = 5)
(4001, 'POL-2024-HOG-4001', '2024-02-01', '2025-02-01', 650000.00, 750000.00, 'ACTIVA',    'PENDIENTE', 'PDF-4001', NULL,     NULL, 14, 8, 4,  1),
(4002, 'POL-2024-HOG-4002', '2024-03-15', '2025-03-15', 580000.00, 670000.00, 'ACTIVA',    'PENDIENTE', 'PDF-4002', NULL,     NULL, 18, 9, 4,  2),
(4003, 'POL-2024-HOG-4003', '2024-04-20', '2025-04-20', 720000.00, 830000.00, 'ACTIVA',    'PENDIENTE', 'PDF-4003', NULL,     NULL, 12, 2, 4,  3),
(4004, 'POL-2023-HOG-4004', '2023-11-10', '2024-11-10', 600000.00, 690000.00, 'ACTIVA',    'PENDIENTE', 'PDF-4004', NULL,     NULL, 15, 2, 4,  1),

-- Pólizas de AUTOMOVIL (ramo_id = 3) — placa obligatoria
(5001, 'POL-2024-AUT-5001', '2024-01-10', '2025-01-10', 1200000.00, 1380000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5001', 'ABC123', NULL, 11, 3, 3, 1),
(5002, 'POL-2024-AUT-5002', '2024-02-05', '2025-02-05',  950000.00, 1090000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5002', 'DEF456', NULL, 12, 3, 3, 2),
(5003, 'POL-2024-AUT-5003', '2024-03-12', '2025-03-12', 1400000.00, 1610000.00, 'CANCELADA','PENDIENTE', 'PDF-5003', 'GHI789', NULL, 15, 4, 3, 3),
(5004, 'POL-2024-AUT-5004', '2024-04-18', '2025-04-18', 1100000.00, 1265000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5004', 'JKL012', NULL, 6,  4, 3, 4),
(5005, 'POL-2024-AUT-5005', '2024-05-22', '2025-05-22', 1350000.00, 1550000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5005', 'MNO345', NULL, 9,  5, 3, 5),
(5006, 'POL-2024-AUT-5006', '2024-06-15', '2025-06-15', 1050000.00, 1210000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5006', 'PQR678', NULL, 10, 5, 3, 6),
(5007, 'POL-2024-AUT-5007', '2024-07-08', '2025-07-08', 1280000.00, 1470000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5007', 'STU901', NULL, 13, 6, 3, 1),
(5008, 'POL-2024-AUT-5008', '2024-08-20', '2025-08-20',  980000.00, 1130000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5008', 'VWX234', NULL, 14, 6, 3, 2),
(5009, 'POL-2024-AUT-5009', '2024-09-10', '2025-09-10', 1150000.00, 1320000.00, 'CANCELADA','PENDIENTE', 'PDF-5009', 'YZA567', NULL, 16, 7, 3, 3),
(5010, 'POL-2023-AUT-5010', '2023-10-05', '2024-10-05', 1300000.00, 1495000.00, 'ACTIVA',   'PAGADA',    'PDF-5010', 'BCD890', NULL, 21, 7, 3, 4),
(5011, 'POL-2024-AUT-5011', '2024-10-15', '2025-10-15', 1420000.00, 1630000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5011', 'EFG123', NULL, 18, 8, 3, 5),
(5012, 'POL-2024-AUT-5012', '2025-03-01', '2026-03-09', 1080000.00, 1240000.00, 'ACTIVA',   'PENDIENTE', 'PDF-5012', 'HIJ456', NULL, 19, 8, 3, 6);

