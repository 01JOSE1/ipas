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
DROP TABLE IF EXISTS Polizas;
-- NIVEL 2: Tablas con Foreign Keys o dependencias
DROP TABLE IF EXISTS Clientes;
DROP TABLE IF EXISTS Auditorias;
DROP TABLE IF EXISTS Usuarios;
-- NIVEL 1: Tablas padre (sin Foreign Keys desde otras tablas)
DROP TABLE IF EXISTS Ramos;
DROP TABLE IF EXISTS Aseguradoras;
DROP TABLE IF EXISTS Roles;


-- ==========================================
-- 4. CREACION DE TABLAS (En orden correcto)
-- ==========================================
-- PRIMERO: Tablas independientes (sin FK)
CREATE TABLE Roles (
    id_role INT AUTO_INCREMENT,
    nombre_rol VARCHAR(20) NOT NULL,
    descripcion TEXT,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_role PRIMARY KEY (id_role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Aseguradoras (
    id_aseguradora INT AUTO_INCREMENT,
    nombre_aseguradora VARCHAR(40) NOT NULL,
    numero_documento VARCHAR(15) NOT NULL,
    direccion VARCHAR(100),
    ciudad VARCHAR(40),
    telefono VARCHAR(15) NOT NULL,
    clave VARCHAR(30) NOT NULL,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_aseguradoras PRIMARY KEY (id_aseguradora),
    CONSTRAINT uk_aseguradoras_telefono UNIQUE (telefono),
    CONSTRAINT uk_aseguradoras_clave UNIQUE (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Ramos (
    id_ramo INT AUTO_INCREMENT,
    nombre_ramo VARCHAR(40) NOT NULL,
    comision DECIMAL(4,2) NOT NULL,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_ramos PRIMARY KEY (id_ramo),
    CONSTRAINT chk_ramos_comision CHECK (comision >= 0 AND comision <= 30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- SEGUNDO: Tablas con FK a una tabla o dependencias a otras tablas
CREATE TABLE Usuarios (
    id_usuario INT AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) NOT NULL,
    tipo_documento ENUM('CEDULA DE CIUDADANIA', 'PASAPORTE', 'LICENCIA DE CONDUCIR') ,
    numero_documento VARCHAR(15),
    telefono VARCHAR(15),
    direccion VARCHAR(100),
    correo VARCHAR(100) NOT NULL,
    clave VARCHAR(100) NOT NULL,
    estado ENUM('ACTIVO', 'INACTIVO', 'SUSPENDIDO') DEFAULT 'INACTIVO',
    
    -- Llaves foraneas
    role_id INT NOT NULL,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuarios_telefono UNIQUE (telefono),
    CONSTRAINT uk_usuarios_numero_documento UNIQUE (numero_documento),
    CONSTRAINT uk_usuarios_correo UNIQUE (correo),
    CONSTRAINT chk_usuarios_correo CHECK (correo REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),
    
    -- Constraints a nivel de tabla llaves foraneas
	CONSTRAINT fk_roles_usuarios FOREIGN KEY (role_id) 
	REFERENCES Roles(id_role)
	ON DELETE RESTRICT
	ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE Auditorias (
    id_auditoria INT AUTO_INCREMENT,
    tabla_afectada VARCHAR(50) NOT NULL,
    id_registro INT NOT NULL,
    accion VARCHAR(15) NOT NULL,
    fecha_accion DATETIME NOT NULL,
    detalles TEXT,
    
    -- Llaves foraneas
    usuario_id INT NOT NULL,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_auditorias PRIMARY KEY (id_auditoria),
    
    -- Constraints a nivel de tabla llaves foraneas
	CONSTRAINT fk_usuarios_auditorias FOREIGN KEY (usuario_id) 
	REFERENCES Usuarios(id_usuario)
	ON DELETE RESTRICT
	ON UPDATE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE Clientes (
    id_cliente INT AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    apellido VARCHAR(40) NOT NULL,
    tipo_documento ENUM('CEDULA DE CIUDADANIA', 'PASAPORTE', 'LICENCIA DE CONDUCIR') NOT NULL,
    numero_documento VARCHAR(15) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    estado_civil ENUM('SOLTERO', 'CASADO', 'VIUDO', 'DIVORCIADO', 'SEPARADO'),
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    direccion VARCHAR(100),
    ciudad VARCHAR(60),
    
	-- Llaves foraneas
    usuario_id INT NOT NULL,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_clientes PRIMARY KEY (id_cliente),
    CONSTRAINT uk_clientes_telefono UNIQUE (telefono),
    CONSTRAINT uk_clientes_numero_documento UNIQUE (numero_documento),
	CONSTRAINT uk_clientes_correo UNIQUE (correo),
    CONSTRAINT chk_clientes_correo CHECK (correo REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),
    
	-- Constraints a nivel de tabla llaves foraneas
	CONSTRAINT fk_usuarios_clientes FOREIGN KEY (usuario_id) 
	REFERENCES Usuarios(id_usuario)
	ON DELETE RESTRICT
	ON UPDATE CASCADE
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- TERCERO: Tablas con FK a múltiples tablas
CREATE TABLE Polizas (
    numero_poliza INT,
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    prima_neta DECIMAL(15,2) NOT NULL,
    prima_total DECIMAL(15,2) NOT NULL,
    estado ENUM('VIGENTE', 'VENCIDA', 'CANCELADA', 'PENDIENTE PAGO', 'FINANCIADA', 'PAGADA') NOT NULL,
    numero_pdf VARCHAR(150) NOT NULL,
    placa VARCHAR(15),
    
    -- Llaves foraneas
    cliente_id INT NOT NULL,
    usuario_id INT NOT NULL,
    ramo_id INT NOT NULL,
    aseguradora_id INT NOT NULL,
    
    -- Constraints a nivel de tabla
    CONSTRAINT pk_polizas PRIMARY KEY (numero_poliza),
    CONSTRAINT uk_polizas_pdf UNIQUE (numero_pdf),
    CONSTRAINT uk_polizas_placa UNIQUE (placa),
    CONSTRAINT chk_polizas_fechas CHECK (fecha_vencimiento > fecha_inicio),
    CONSTRAINT chk_polizas_prima_neta CHECK (prima_neta >= 0),
    CONSTRAINT chk_polizas_prima_total CHECK (prima_total >= prima_neta),
    
    -- Constraints a nivel de tabla llaves foraneas
	CONSTRAINT fk_clientes_polizas FOREIGN KEY (cliente_id) 
	REFERENCES Clientes(id_cliente)
	ON DELETE RESTRICT
	ON UPDATE CASCADE,
    
	-- Constraints a nivel de tabla llaves foraneas
	CONSTRAINT fk_usuarios_polizas FOREIGN KEY (usuario_id) 
	REFERENCES Usuarios(id_usuario)
	ON DELETE RESTRICT
	ON UPDATE CASCADE,
    
	CONSTRAINT fk_ramos_polizas FOREIGN KEY (ramo_id) 
	REFERENCES Ramos(id_ramo)
	ON DELETE RESTRICT
	ON UPDATE CASCADE,
    
	CONSTRAINT fk_aseguradoras_polizas FOREIGN KEY (aseguradora_id) 
	REFERENCES Aseguradoras(id_aseguradora)
	ON DELETE RESTRICT
	ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- ==========================================
-- 5. INDICES ADICIONALES
-- ==========================================
-- Mejoramos las busquedas en los puntos criticos como clientes y polizas
CREATE INDEX idx_clientes_documento ON Clientes(numero_documento);
CREATE INDEX idx_clientes_apellido ON Clientes(apellido);
CREATE INDEX idx_clientes_telefono ON Clientes(telefono);
CREATE INDEX idx_polizas_placa ON Polizas(placa);
CREATE INDEX idx_polizas_estado ON Polizas(estado);
-- Para consultas por cliente + estado:
CREATE INDEX idx_polizas_cliente_estado ON Polizas(cliente_id, estado);
-- Para consultas por fecha:
CREATE INDEX idx_polizas_fecha_inicio ON Polizas(fecha_inicio);
CREATE INDEX idx_polizas_fecha_vencimiento ON Polizas(fecha_vencimiento);



-- ==========================================
-- 6. TRIGGERS 
-- ==========================================
-- Una parte de codigo que se ejecuta automaticamente en la db cuando hay un evento especifico.

-- Cambia el delimitador de ; a // temporalmente, porque dentro del trigger se usa ; .

DELIMITER //

CREATE TRIGGER validar_edad_cliente
BEFORE INSERT ON Clientes
FOR EACH ROW
BEGIN
    IF TIMESTAMPDIFF(YEAR, NEW.fecha_nacimiento, CURDATE()) < 18 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El cliente debe ser mayor de edad (mínimo 18 años)';
    END IF;
END//

DELIMITER ;

DELIMITER //

CREATE TRIGGER validar_edad_cliente_update
BEFORE UPDATE ON Clientes
FOR EACH ROW
BEGIN
    IF TIMESTAMPDIFF(YEAR, NEW.fecha_nacimiento, CURDATE()) < 18 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede establecer una fecha de nacimiento que haga al cliente menor de edad';
    END IF;
END//

DELIMITER ;

DELIMITER //
-- Cada vez que se actualiza un registro en la tabla Clientes este trigger se activa.
CREATE TRIGGER auditoria_clientes
AFTER UPDATE ON Clientes
-- Se ejecuta una vez por registro (cliente)
FOR EACH ROW

-- inicia el bloque de código.
BEGIN
	-- crea una variable interna (cambios) donde se ira guardando una cadena con los detalles de los cambios.
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
        INSERT INTO Auditorias(
            tabla_afectada,
            id_registro,
            accion,
            fecha_accion,
            detalles,
            usuario_id
        )
        VALUES (
            'Clientes',
            OLD.id_cliente,
            'UPDATE',
            NOW(),
            cambios,
            
            -- Este dato se envia desde la aplicacion
            @usuario_actual
        );
    END IF;
-- Cierra el bloque del trigger.
END;
-- Restaura el delimitador normal.
//
DELIMITER ;


DELIMITER //
-- Cada vez que se actualiza un registro en la tabla Polizas este trigger se activa.
CREATE TRIGGER auditoria_polizas
AFTER UPDATE ON Polizas
FOR EACH ROW
BEGIN
	-- crea una variable interna (cambios) donde se ira guardando una cadena con los detalles de los cambios.
    DECLARE cambios TEXT DEFAULT '';

    -- Verificamos campo por campo y construimos la cadena solo con los cambios
    IF OLD.fecha_inicio <> NEW.fecha_inicio THEN
        SET cambios = CONCAT(cambios, 'Fecha de inicio: ', OLD.fecha_inicio, ' -> ', NEW.fecha_inicio, '; ');
    END IF;

    IF OLD.fecha_vencimiento <> NEW.fecha_vencimiento THEN
        SET cambios = CONCAT(cambios, 'Fecha de vencimiento: ', OLD.fecha_vencimiento, ' -> ', NEW.fecha_vencimiento, '; ');
    END IF;

    IF OLD.prima_neta <> NEW.prima_neta THEN
        SET cambios = CONCAT(cambios, 'Prima neta: ', OLD.prima_neta, ' -> ', NEW.prima_neta, '; ');
    END IF;

    IF OLD.prima_total <> NEW.prima_total THEN
        SET cambios = CONCAT(cambios, 'Prima total: ', OLD.prima_total, ' -> ', NEW.prima_total, '; ');
    END IF;

    IF OLD.estado <> NEW.estado THEN
        SET cambios = CONCAT(cambios, 'Estado: ', OLD.estado, ' -> ', NEW.estado, '; ');
    END IF;

    IF OLD.numero_pdf <> NEW.numero_pdf THEN
        SET cambios = CONCAT(cambios, 'Numero del PDF: ', OLD.numero_pdf, ' -> ', NEW.numero_pdf, '; ');
    END IF;

    IF OLD.placa <> NEW.placa THEN
        SET cambios = CONCAT(cambios, 'Placa: ', OLD.placa, ' -> ', NEW.placa, '; ');
    END IF;

    -- Insertamos en la tabla de auditoria solo si hay cambios
    IF cambios <> '' THEN
        INSERT INTO Auditorias(
            tabla_afectada,
            id_registro,
            accion,
            fecha_accion,
            detalles,
            usuario_id
        )
        VALUES (
            'Polizas',
            OLD.numero_poliza,
            'UPDATE',
            NOW(),
            cambios,
            
            -- Este dato se envia desde la aplicacion
            @usuario_actual
        );
    END IF;
END;
//
DELIMITER ;


DELIMITER //
-- Este trigger valida antes de insertar una poliza que, si el ramo asociado es "Automovil",
-- el campo "placa" no puede quedar vacío ni nulo. 
-- Para los demás ramos, la placa puede ser opcional.
CREATE TRIGGER validar_placa_insert
BEFORE INSERT ON Polizas
FOR EACH ROW
BEGIN
    DECLARE v_nombre_ramo VARCHAR(40);

    -- Consultar el nombre del ramo de la póliza
    SELECT nombre_ramo INTO v_nombre_ramo
    FROM Ramos
    WHERE id_ramo = NEW.ramo_id;

    -- Validar que si es Automovil, la placa no puede ser vacia ni nula
    IF v_nombre_ramo = 'Automovil' AND (NEW.placa IS NULL OR NEW.placa = '') THEN  
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Debe ingresar la placa para las pólizas del ramo Automovil';
    END IF;
END//

DELIMITER ;

DELIMITER //
CREATE TRIGGER validar_placa_update
BEFORE UPDATE ON Polizas
FOR EACH ROW
BEGIN
    DECLARE v_nombre_ramo VARCHAR(40);
    
    SELECT nombre_ramo INTO v_nombre_ramo
    FROM Ramos
    WHERE id_ramo = NEW.ramo_id;
    
    IF v_nombre_ramo = 'Automovil' AND (NEW.placa IS NULL OR NEW.placa = '') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No puede eliminar la placa de una póliza de Automovil';
    END IF;
END//
DELIMITER ;




-- ==========================================
-- DATOS INICIALES - SISTEMA IPAS
-- ==========================================

-- ==========================================
-- 1. ROLES
-- ==========================================
INSERT INTO Roles (nombre_rol, descripcion) VALUES
('ADMINISTRADOR', 'Acceso total al sistema, gestión de usuarios y configuración'),
('ASESOR', 'Gestión de clientes y pólizas, ventas');


-- ==========================================
-- 2. RAMOS
-- ==========================================
INSERT INTO Ramos (nombre_ramo, comision) VALUES
('Salud', 12.50),
('Vida', 15.00),
('Accidentes', 10.00),
('Hogar', 8.50),
('Automovil', 11.00);


-- ==========================================
-- 3. ASEGURADORAS EN COLOMBIA
-- ==========================================
INSERT INTO Aseguradoras (nombre_aseguradora, numero_documento, direccion, ciudad, telefono, clave) VALUES
('Suramericana S.A.', '890903407', 'Calle 49 #63-50', 'Medellín', '6045108000', 'SUR#2024$SEC'),
('Seguros Bolívar S.A.', '860002503', 'Carrera 7 #26-20', 'Bogotá', '6013077000', 'BOL*SEC@2024'),
('Mapfre Seguros', '890903476', 'Calle 72 #10-07', 'Bogotá', '6014239090', 'MAP#KEY$2024'),
('Allianz Seguros S.A.', '860002400', 'Carrera 11 #90-20', 'Bogotá', '6016586000', 'ALZ@2024*KEY'),
('Liberty Seguros S.A.', '860002534', 'Avenida 19 #109A-30', 'Bogotá', '6016580000', 'LIB#2024@SEC'),
('AXA Colpatria Seguros', '860002400', 'Carrera 13 #26-45', 'Bogotá', '6013077900', 'AXA$KEY#2024'),
('Seguros Generales Suramericana', '890903407', 'Calle 49 #63-50', 'Medellín', '6045108020', 'SGS@2024#SEC'),
('HDI Seguros S.A.', '860002500', 'Calle 72 #10-51', 'Bogotá', '6015948000', 'HDI*2024$KEY'),
('Seguros del Estado S.A.', '899999061', 'Carrera 7 #27-18', 'Bogotá', '6013077400', 'EST#SEC@2024'),
('Equidad Seguros S.A.', '860026506', 'Avenida 13 #91-46', 'Bogotá', '6014232000', 'EQU$2024*SEC');


-- ==========================================
-- 4. INSERTAR USUARIOS
-- ==========================================
-- Nota: Contraseña hasheada con bcrypt = "1234qwer"


INSERT INTO Usuarios (nombre, apellido, tipo_documento, numero_documento, telefono, direccion, correo, clave, estado, role_id) VALUES
-- Administradores
('Carlos', 'Rodríguez', 'CEDULA_CIUDADANIA', '1098765432', '3001234567', 'Calle 45 #23-10', 'carlos.rodriguez@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 1),
('María', 'Gómez', 'CEDULA_CIUDADANIA', '1098765433', '3001234568', 'Carrera 27 #45-67', 'maria.gomez@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 1),


-- Asesores
('Juan', 'Martínez', 'CEDULA_CIUDADANIA', '1098765434', '3001234569', 'Calle 10 #15-20', 'juan.martinez@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2),
('Ana', 'López', 'CEDULA_CIUDADANIA', '1098765435', '3001234570', 'Carrera 33 #28-45', 'ana.lopez@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2),
('Pedro', 'Sánchez', 'CEDULA_CIUDADANIA', '1098765436', '3001234571', 'Calle 55 #12-34', 'pedro.sanchez@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2),
('Laura', 'Torres', 'CEDULA_CIUDADANIA', '1098765437', '3001234572', 'Avenida 15 #45-23', 'laura.torres@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2),
('Diego', 'Ramírez', 'CEDULA_CIUDADANIA', '1098765438', '3001234573', 'Carrera 18 #34-56', 'diego.ramirez@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2),
('Sofía', 'Vargas', 'CEDULA_CIUDADANIA', '1098765439', '3001234574', 'Calle 89 #23-12', 'sofia.vargas@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2),
('Andrés', 'Moreno', 'CEDULA_CIUDADANIA', '1098765440', '3001234575', 'Carrera 50 #67-89', 'andres.moreno@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'INACTIVO', 2),
('Valentina', 'Castro', 'CEDULA_CIUDADANIA', '1098765441', '3001234576', 'Calle 120 #15-30', 'valentina.castro@ipas.com.co', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ACTIVO', 2);


-- ==========================================
-- 5. CLIENTES
-- ==========================================

INSERT INTO Clientes (
    nombre, apellido, tipo_documento, numero_documento, fecha_nacimiento,
    estado_civil, telefono, correo, direccion, ciudad, usuario_id
) VALUES

-- Clientes del asesor Juan Martínez (usuario_id: 11)
('Roberto', 'Pérez García', 'CEDULA DE CIUDADANIA', '37845621', '1985-03-15', 'CASADO', '3112345678', 'roberto.perez@gmail.com', 'Calle 45 #12-34', 'Bucaramanga', 11),
('Claudia', 'Ruiz Mendoza', 'CEDULA DE CIUDADANIA', '37845622', '1990-07-22', 'SOLTERA', '3112345679', 'claudia.ruiz@hotmail.com', 'Carrera 27 #56-78', 'Bucaramanga', 11),
('Fernando', 'Silva Ortiz', 'CEDULA DE CIUDADANIA', '91234567', '1978-11-30', 'CASADO', '3112345680', 'fernando.silva@yahoo.com', 'Calle 67 #23-45', 'Bogotá', 11),
('Patricia', 'Jiménez Cruz', 'PASAPORTE', 'PA123456', '1988-05-18', 'DIVORCIADA', '3112345681', 'patricia.jimenez@outlook.com', 'Avenida 9 #45-67', 'Medellín', 11),

-- Clientes de la asesora Ana López (usuario_id: 12)
('Miguel', 'Hernández Rojas', 'CEDULA DE CIUDADANIA', '1075234567', '1982-09-10', 'CASADO', '3123456789', 'miguel.hernandez@gmail.com', 'Carrera 33 #78-90', 'Cali', 12),
('Diana', 'Gutiérrez Pardo', 'CEDULA DE CIUDADANIA', '52345678', '1995-02-14', 'SOLTERA', '3123456790', 'diana.gutierrez@hotmail.com', 'Calle 100 #23-45', 'Bogotá', 12),
('Javier', 'Morales Vega', 'CEDULA DE CIUDADANIA', '80234567', '1975-12-05', 'VIUDO', '3123456791', 'javier.morales@yahoo.com', 'Carrera 15 #34-56', 'Barranquilla', 12),
('Carolina', 'Ramírez Díaz', 'CEDULA DE CIUDADANIA', '37856789', '1992-08-20', 'CASADA', '3123456792', 'carolina.ramirez@gmail.com', 'Calle 85 #12-34', 'Bucaramanga', 12),

-- Clientes del asesor Pedro Sánchez (usuario_id: 13)
('Luis', 'Torres Medina', 'CEDULA DE CIUDADANIA', '1098234567', '1980-04-25', 'CASADO', '3134567890', 'luis.torres@outlook.com', 'Avenida 19 #67-89', 'Medellín', 13),
('Sandra', 'López Castillo', 'CEDULA DE CIUDADANIA', '52456789', '1987-10-12', 'SOLTERA', '3134567891', 'sandra.lopez@gmail.com', 'Carrera 7 #45-67', 'Bogotá', 13),
('Oscar', 'García Soto', 'CEDULA DE CIUDADANIA', '91345678', '1983-06-08', 'DIVORCIADO', '3134567892', 'oscar.garcia@hotmail.com', 'Calle 50 #23-45', 'Cali', 13),
('Melissa', 'Martínez Luna', 'CEDULA DE CIUDADANIA', '37867890', '1993-01-30', 'CASADA', '3134567893', 'melissa.martinez@yahoo.com', 'Carrera 28 #56-78', 'Bucaramanga', 13),

-- Clientes de la asesora Laura Torres (usuario_id: 14)
('Andrés', 'Rodríguez Villa', 'CEDULA DE CIUDADANIA', '1075345678', '1979-11-15', 'CASADO', '3145678901', 'andres.rodriguez@gmail.com', 'Calle 72 #34-56', 'Bogotá', 14),
('Natalia', 'Vargas Rojas', 'CEDULA DE CIUDADANIA', '52567890', '1991-03-22', 'SOLTERA', '3145678902', 'natalia.vargas@hotmail.com', 'Carrera 40 #67-89', 'Medellín', 14),
('Camilo', 'Suárez Pinto', 'CEDULA DE CIUDADANIA', '80345678', '1986-07-18', 'CASADO', '3145678903', 'camilo.suarez@outlook.com', 'Avenida 30 #45-67', 'Cali', 14),

-- Clientes del asesor Diego Ramírez (usuario_id: 15)
('Paola', 'Gómez Reyes', 'CEDULA DE CIUDADANIA', '37878901', '1994-12-10', 'SOLTERA', '3156789012', 'paola.gomez@gmail.com', 'Calle 15 #23-45', 'Bucaramanga', 15),
('Ricardo', 'Mendoza Cruz', 'CEDULA DE CIUDADANIA', '1098345678', '1981-05-28', 'CASADO', '3156789013', 'ricardo.mendoza@yahoo.com', 'Carrera 11 #56-78', 'Barranquilla', 15),
('Juliana', 'Castro Ríos', 'CEDULA DE CIUDADANIA', '52678901', '1989-09-16', 'DIVORCIADA', '3156789014', 'juliana.castro@hotmail.com', 'Calle 95 #34-56', 'Bogotá', 15),

-- Clientes de la asesora Sofía Vargas (usuario_id: 16)
('Daniel', 'Ortiz Paredes', 'CEDULA DE CIUDADANIA', '91456789', '1984-02-20', 'CASADO', '3167890123', 'daniel.ortiz@gmail.com', 'Carrera 50 #78-90', 'Medellín', 16),
('Marcela', 'Ríos Gómez', 'CEDULA DE CIUDADANIA', '37889012', '1996-06-14', 'SOLTERA', '3167890124', 'marcela.rios@outlook.com', 'Calle 120 #23-45', 'Bucaramanga', 16);


-- ==========================================
-- 6. PÓLIZAS
-- ==========================================
-- Nota: numero_poliza se auto-incrementa, pero aquí lo especificamos para control

-- Pólizas de Salud
INSERT INTO Polizas (numero_poliza, fecha_inicio, fecha_vencimiento, prima_neta, prima_total, estado, numero_pdf, placa, cliente_id, usuario_id, ramo_id, aseguradora_id) VALUES
(1001, '2024-01-15', '2025-01-15', 450000.00, 520000.00, 'VIGENTE', 'POL-2024-SAL-1001', NULL, 101, 12, 1, 1),
(1002, '2024-02-20', '2025-02-20', 380000.00, 440000.00, 'VIGENTE', 'POL-2024-SAL-1002', NULL, 105, 11, 1, 2),
(1003, '2024-03-10', '2025-03-10', 520000.00, 600000.00, 'VIGENTE', 'POL-2024-SAL-1003', NULL, 109, 13, 1, 3),
(1004, '2023-06-15', '2024-06-15', 400000.00, 460000.00, 'VENCIDA', 'POL-2023-SAL-1004', NULL, 113, 13, 1, 1),

-- Pólizas de Vida
(2001, '2024-01-20', '2025-01-20', 280000.00, 340000.00, 'VIGENTE', 'POL-2024-VID-2001', NULL, 120, 12, 2, 4),
(2002, '2024-02-15', '2025-02-15', 320000.00, 390000.00, 'VIGENTE', 'POL-2024-VID-2002', NULL, 116, 16, 2, 5),
(2003, '2024-04-05', '2025-04-05', 450000.00, 550000.00, 'VIGENTE', 'POL-2024-VID-2003', NULL, 110, 15, 2, 6),
(2004, '2023-12-01', '2024-12-01', 300000.00, 365000.00, 'PENDIENTE PAGO', 'POL-2023-VID-2004', NULL, 114, 16, 2, 4),

-- Pólizas de Accidentes
(3001, '2024-03-15', '2025-03-15', 180000.00, 220000.00, 'VIGENTE', 'POL-2024-ACC-3001', NULL, 113, 18, 3, 7),
(3002, '2024-04-20', '2025-04-20', 150000.00, 185000.00, 'VIGENTE', 'POL-2024-ACC-3002', NULL, 117, 17, 3, 8),
(3003, '2024-05-10', '2025-05-10', 200000.00, 245000.00, 'VIGENTE', 'POL-2024-ACC-3003', NULL, 111, 15, 3, 9),
(3004, '2023-08-15', '2024-08-15', 175000.00, 215000.00, 'CANCELADA', 'POL-2023-ACC-3004', NULL, 116, 15, 3, 7),

-- Pólizas de Hogar
(4001, '2024-02-01', '2025-02-01', 650000.00, 750000.00, 'VIGENTE', 'POL-2024-HOG-4001', NULL, 114, 18, 4, 1),
(4002, '2024-03-15', '2025-03-15', 580000.00, 670000.00, 'VIGENTE', 'POL-2024-HOG-4002', NULL, 118, 19, 4, 2),
(4003, '2024-04-20', '2025-04-20', 720000.00, 830000.00, 'VIGENTE', 'POL-2024-HOG-4003', NULL, 112, 20, 4, 3),
(4004, '2023-11-10', '2024-11-10', 600000.00, 690000.00, 'FINANCIADA', 'POL-2023-HOG-4004', NULL, 115, 20, 4, 1),

-- Pólizas de Automóvil (con placa obligatoria)
(5001, '2024-01-10', '2025-01-10', 1200000.00, 1380000.00, 'VIGENTE', 'POL-2024-AUT-5001', 'ABC123', 111, 13, 5, 1),
(5002, '2024-02-05', '2025-02-05', 950000.00, 1090000.00, 'VIGENTE', 'POL-2024-AUT-5002', 'DEF456', 112, 13, 5, 2),
(5003, '2024-03-12', '2025-03-12', 1400000.00, 1610000.00, 'VIGENTE', 'POL-2024-AUT-5003', 'GHI789', 115, 14, 5, 3),
(5004, '2024-04-18', '2025-04-18', 1100000.00, 1265000.00, 'VIGENTE', 'POL-2024-AUT-5004', 'JKL012', 116, 14, 5, 4),
(5005, '2024-05-22', '2025-05-22', 1350000.00, 1550000.00, 'VIGENTE', 'POL-2024-AUT-5005', 'MNO345', 119, 15, 5, 5),
(5006, '2024-06-15', '2025-06-15', 1050000.00, 1210000.00, 'VIGENTE', 'POL-2024-AUT-5006', 'PQR678', 110, 15, 5, 6),
(5007, '2024-07-08', '2025-07-08', 1280000.00, 1470000.00, 'VIGENTE', 'POL-2024-AUT-5007', 'STU901', 113, 16, 5, 1),
(5008, '2024-08-20', '2025-08-20', 980000.00, 1130000.00, 'VIGENTE', 'POL-2024-AUT-5008', 'VWX234', 114, 16, 5, 2),
(5009, '2024-09-10', '2025-09-10', 1150000.00, 1320000.00, 'VIGENTE', 'POL-2024-AUT-5009', 'YZA567', 116, 17, 5, 3),
(5010, '2023-10-05', '2024-10-05', 1300000.00, 1495000.00, 'VENCIDA', 'POL-2023-AUT-5010', 'BCD890', 117, 17, 5, 4),
(5011, '2024-10-15', '2025-10-15', 1420000.00, 1630000.00, 'PAGADA', 'POL-2024-AUT-5011', 'EFG123', 118, 18, 5, 5),
(5012, '2024-11-01', '2025-11-01', 1080000.00, 1240000.00, 'VIGENTE', 'POL-2024-AUT-5012', 'HIJ456', 119, 18, 5, 6);

