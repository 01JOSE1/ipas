-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ipas
-- ------------------------------------------------------
-- Server version	8.0.42
CREATE DATABASE IF NOT EXISTS `ipas`;
USE `ipas`;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aseguradoras`
--

DROP TABLE IF EXISTS `aseguradoras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aseguradoras` (
  `id_aseguradora` bigint NOT NULL AUTO_INCREMENT,
  `nombre_aseguradora` varchar(40) NOT NULL,
  `numero_documento` varchar(15) NOT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `ciudad` varchar(40) DEFAULT NULL,
  `telefono` varchar(15) NOT NULL,
  `clave` varchar(30) NOT NULL,
  PRIMARY KEY (`id_aseguradora`),
  UNIQUE KEY `uk_aseguradoras_telefono` (`telefono`),
  UNIQUE KEY `uk_aseguradoras_clave` (`clave`),
  UNIQUE KEY `numero_documento_UNIQUE` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aseguradoras`
--

LOCK TABLES `aseguradoras` WRITE;
/*!40000 ALTER TABLE `aseguradoras` DISABLE KEYS */;
INSERT INTO `aseguradoras` VALUES (1,'Suramericana S.A.','890903407-9','Calle 49 #63-50','Medellín','6045108000','SUR#2024$SEC'),(2,'Seguros Bolívar S.A.','860002503','Carrera 7 #26-20','Bogotá','6013077000','BOL*SEC@2024'),(3,'Mapfre Seguros','890903476','Calle 72 #10-07','Bogotá','6014239090','MAP#KEY$2024'),(4,'Allianz Seguros S.A.','860002400','Carrera 11 #90-20','Bogotá','6016586000','ALZ@2024*KEY'),(5,'Liberty Seguros S.A.','860002534','Avenida 19 #109A-30','Bogotá','6016580000','LIB#2024@SEC'),(6,'AXA Colpatria Seguros S.A.','860002184-6','Carrera 13 #26-45','Bogotá','6013077900','AXA$KEY#2024'),(7,'Seguros Generales Suramericana','890903401','Calle 49 #63-50','Medellín','6045108020','SGS@2024#SEC'),(8,'HDI Seguros S.A.','860002500','Calle 72 #10-51','Bogotá','6015948000','HDI*2024$KEY'),(9,'Seguros del Estado S.A.','860009578-6','Carrera 7 #27-18','Bogotá','6013077400','EST#SEC@2024'),(10,'Equidad Seguros S.A.','860028415','Avenida 13 #91-46','Bogotá','6014232000','EQU$2024*SEC'),(11,'Nacional de Seguros S.A.','860002527-9','CALLE 94 N° 11-30 PISO 4','Bogotá','6017463219','GDH24HSA642');
/*!40000 ALTER TABLE `aseguradoras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditorias`
--

DROP TABLE IF EXISTS `auditorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditorias` (
  `id_auditoria` bigint NOT NULL AUTO_INCREMENT,
  `tabla_afectada` varchar(50) NOT NULL,
  `id_registro` int NOT NULL,
  `accion` varchar(15) NOT NULL,
  `fecha_accion` datetime NOT NULL,
  `detalles` text,
  `usuario_id` bigint NOT NULL,
  PRIMARY KEY (`id_auditoria`),
  KEY `fk_usuarios_auditorias` (`usuario_id`),
  CONSTRAINT `fk_usuarios_auditorias` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id_usuario`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditorias`
--

LOCK TABLES `auditorias` WRITE;
/*!40000 ALTER TABLE `auditorias` DISABLE KEYS */;
INSERT INTO `auditorias` VALUES (1,'Clientes',1,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_CIUDADANIA; ',1),(2,'Clientes',2,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_CIUDADANIA; ',1),(3,'Clientes',3,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_CIUDADANIA; ',1),(4,'Clientes',5,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_EXTRANJERA; ',1),(5,'Clientes',7,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_EXTRANJERA; ',1),(6,'Clientes',9,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_EXTRANJERA; ',1),(7,'Clientes',12,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_EXTRANJERA; ',1),(8,'Clientes',15,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_EXTRANJERA; ',1),(9,'Clientes',20,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> CEDULA_EXTRANJERA; ',1),(10,'Clientes',19,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> LICENCIA_CONDUCCION; ',1),(11,'Clientes',18,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> LICENCIA_CONDUCCION; ',1),(12,'Clientes',17,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> LICENCIA_CONDUCCION; ',1),(13,'Clientes',13,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> LICENCIA_CONDUCCION; ',1),(14,'Clientes',11,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> LICENCIA_CONDUCCION; ',1),(15,'Clientes',14,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> PASAPORTE; ',1),(16,'Clientes',16,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> PASAPORTE; ',1),(17,'Clientes',10,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> PASAPORTE; ',1),(18,'Clientes',8,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> PASAPORTE; ',1),(19,'Clientes',6,'UPDATE','2026-02-06 16:04:14','Tipo documento: CEDULA DE CIUDADANIA -> PASAPORTE; ',1),(20,'Clientes',1,'UPDATE','2026-02-06 16:06:08','Estado civil: CASADO -> DIVORCIADO; ',1),(21,'Clientes',3,'UPDATE','2026-02-06 16:06:08','Estado civil: CASADO -> SOLTERO; ',1),(22,'Clientes',6,'UPDATE','2026-02-06 16:06:08','Estado civil: SOLTERA -> SOLTERO; ',1),(23,'Clientes',10,'UPDATE','2026-02-06 16:06:08','Estado civil: SOLTERA -> SOLTERO; ',1),(24,'Clientes',14,'UPDATE','2026-02-06 16:06:08','Estado civil: SOLTERA -> SOLTERO; ',1),(25,'Clientes',20,'UPDATE','2026-02-06 16:06:08','Estado civil: SOLTERA -> UNION_LIBRE; ',1),(26,'Clientes',16,'UPDATE','2026-02-06 16:06:08','Estado civil: SOLTERA -> UNION_LIBRE; ',1),(27,'Clientes',8,'UPDATE','2026-02-06 16:06:08','Estado civil: CASADA -> UNION_LIBRE; ',1),(28,'Clientes',4,'UPDATE','2026-02-06 16:06:08','Estado civil: DIVORCIADA -> DIVORCIADO; ',1),(29,'Clientes',2,'UPDATE','2026-02-06 16:06:08','Estado civil: SOLTERA -> VIUDO; ',1),(30,'Clientes',12,'UPDATE','2026-02-06 16:06:08','Estado civil: CASADA -> VIUDO; ',1),(31,'Clientes',18,'UPDATE','2026-02-06 16:06:08','Estado civil: DIVORCIADA -> VIUDO; ',1),(32,'Clientes',8,'UPDATE','2026-02-08 15:36:56','Nombre: Carolina -> Karen; ',29),(33,'Clientes',9,'UPDATE','2026-02-08 15:37:13','Nombre: Luis -> Lucas; ',29),(34,'Clientes',9,'UPDATE','2026-02-08 15:38:38','Numero documento: 1098234567 -> 37856734; ',29),(35,'Clientes',10,'UPDATE','2026-02-08 15:38:53','Nombre: Sandra -> M; ',29),(36,'Clientes',10,'UPDATE','2026-02-08 15:45:35','Nombre: M -> Maria; ',2),(37,'Clientes',10,'UPDATE','2026-02-08 15:46:02','Nombre: Maria -> M3434; ',29),(38,'Clientes',10,'UPDATE','2026-02-08 15:57:03','Nombre: M3434 -> Maria; ',2),(39,'Clientes',10,'UPDATE','2026-02-08 15:57:21','Nombre: Maria -> Maria2323; ',29),(40,'Clientes',10,'UPDATE','2026-02-08 16:01:44','Nombre: Maria2323 -> Maria; ',2),(41,'Clientes',10,'UPDATE','2026-02-08 16:11:55','Nombre: Maria -> Mar; Numero documento: 52456789 -> 5245678923; ',29),(42,'Clientes',10,'UPDATE','2026-02-08 16:29:08','Nombre: Mar -> Maria; Numero documento: 5245678923 -> 52456789233; ',29),(43,'Clientes',6,'UPDATE','2026-02-08 16:36:44','Numero documento: 52345678 -> 5234567823; ',29),(44,'Clientes',7,'UPDATE','2026-02-08 16:36:54','Numero documento: 80234567 -> 8023456723; ',29),(45,'Clientes',8,'UPDATE','2026-02-08 16:37:06','Numero documento: 37856789 -> 3785678945; ',29),(46,'Clientes',1,'UPDATE','2026-02-08 16:37:15','Numero documento: 37845621 -> 3784562178; ',29),(47,'Clientes',23,'UPDATE','2026-02-08 16:39:17','Apellido: Leon -> Perez; ',29),(48,'Polizas',2004,'UPDATE','2026-02-13 20:28:51','Estado: PENDIENTE PAGO -> VIGENTE; ',1),(49,'Polizas',4004,'UPDATE','2026-02-13 20:28:51','Estado: FINANCIADA -> VIGENTE; ',1),(50,'Polizas',5011,'UPDATE','2026-02-13 20:28:51','Estado: PAGADA -> VIGENTE; ',1),(51,'Clientes',11,'UPDATE','2026-02-14 15:48:26','Numero documento: 91345678 -> 9134567823; Ciudad: Cali -> Bucaramanga; ',29),(52,'Clientes',1,'UPDATE','2026-03-03 11:00:49','Estado civil: DIVORCIADO -> SOLTERO; ',29),(53,'Clientes',5,'UPDATE','2026-03-03 13:08:11','Ciudad: Cali -> Bogotá; ',29),(54,'Polizas',3002,'UPDATE','2026-03-03 13:09:33','Estado: VIGENTE -> CANCELADA; ',29),(55,'Clientes',1,'UPDATE','2026-03-03 19:58:47','Telefono: 3112345678 -> 3112345676; ',29),(56,'Polizas',3002,'UPDATE','2026-03-03 19:59:06','Estado: CANCELADA -> VENCIDA; ',29),(57,'Polizas',3002,'UPDATE','2026-03-03 20:05:26','Placa: NULL -> 243gfd; ',29),(58,'Polizas',5037,'UPDATE','2026-03-03 20:10:00','Estado: VIGENTE -> VENCIDA; ',29),(59,'Polizas',1001,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> CANCELADA; ',29),(60,'Polizas',1002,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> ACTIVA; ',29),(61,'Polizas',1003,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> CANCELADA; ',29),(62,'Polizas',1004,'UPDATE','2026-03-04 11:47:43','Estado: VENCIDA -> CANCELADA; ',29),(63,'Polizas',2001,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> ACTIVA; ',29),(64,'Polizas',2002,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> CANCELADA; ',29),(65,'Polizas',2003,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> ACTIVA; ',29),(66,'Polizas',2004,'UPDATE','2026-03-04 11:47:43','Estado: VIGENTE -> CANCELADA; ',29),(67,'Polizas',3001,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(68,'Polizas',3002,'UPDATE','2026-03-04 11:51:49','Estado: VENCIDA -> ACTIVA; ',29),(69,'Polizas',3003,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(70,'Polizas',4001,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(71,'Polizas',4002,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(72,'Polizas',4003,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(73,'Polizas',4004,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(74,'Polizas',5001,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(75,'Polizas',5002,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(76,'Polizas',5038,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(77,'Polizas',5037,'UPDATE','2026-03-04 11:51:49','Estado: VENCIDA -> CANCELADA; ',29),(78,'Polizas',5035,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(79,'Polizas',5017,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(80,'Polizas',5016,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(81,'Polizas',5015,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> CANCELADA; ',29),(82,'Polizas',5014,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(83,'Polizas',5012,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(84,'Polizas',5011,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(85,'Polizas',5010,'UPDATE','2026-03-04 11:51:49','Estado: VENCIDA -> ACTIVA; ',29),(86,'Polizas',5009,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> CANCELADA; ',29),(87,'Polizas',5008,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(88,'Polizas',5003,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> CANCELADA; ',29),(89,'Polizas',5004,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(90,'Polizas',5005,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(91,'Polizas',5006,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(92,'Polizas',5007,'UPDATE','2026-03-04 11:51:49','Estado: VIGENTE -> ACTIVA; ',29),(93,'Polizas',5035,'UPDATE','2026-03-05 00:47:18','Estado: ACTIVA -> CANCELADA; ',29),(94,'Polizas',5038,'UPDATE','2026-03-05 12:39:20','Estado: ACTIVA -> CANCELADA; ',29),(95,'Polizas',5012,'UPDATE','2026-03-05 22:31:22','Fecha de inicio: 2024-11-01 -> 2025-03-01; Fecha de vencimiento: 2025-11-01 -> 2026-03-09; ',29),(96,'Polizas',5038,'UPDATE','2026-03-09 20:27:41','Prima total: 2.00 -> 3.00; ',29),(97,'Clientes',36,'INSERT','2026-03-10 15:48:12','Se creó el cliente: JOSEPH STEPHEN SOSA CARDENAS con documento: 1022437272',29),(98,'Polizas',2001,'UPDATE','2026-03-10 15:50:11','Fecha de inicio: 2024-01-20 -> 2025-11-20; Fecha de vencimiento: 2025-01-20 -> 2026-12-20; ',29);
/*!40000 ALTER TABLE `auditorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(40) NOT NULL,
  `apellido` varchar(40) DEFAULT NULL,
  `tipo_documento` varchar(40) NOT NULL,
  `numero_documento` varchar(15) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `estado_civil` varchar(40) DEFAULT NULL,
  `telefono` varchar(10) NOT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `ciudad` varchar(60) DEFAULT NULL,
  `usuario_id` bigint NOT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `uk_clientes_telefono` (`telefono`),
  UNIQUE KEY `uk_clientes_numero_documento` (`numero_documento`),
  UNIQUE KEY `uk_clientes_correo` (`correo`),
  KEY `fk_usuarios_clientes` (`usuario_id`),
  KEY `idx_clientes_documento` (`numero_documento`),
  KEY `idx_clientes_apellido` (`apellido`),
  KEY `idx_clientes_telefono` (`telefono`),
  CONSTRAINT `fk_usuarios_clientes` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id_usuario`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_clientes_correo` CHECK (regexp_like(`correo`,_utf8mb4'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'))
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'Roberto','Pérez García','CEDULA_CIUDADANIA','3784562178','1985-03-15','SOLTERO','3112345676','roberto.perez@gmail.com','Calle 45 #12-34','Bucaramanga',1),(2,'Claudia','Ruiz Mendoza','CEDULA_CIUDADANIA','37845622','1990-07-22','VIUDO','3112345679','claudia.ruiz@hotmail.com','Carrera 27 #56-78','Bucaramanga',1),(3,'Fernando','Silva Ortiz','CEDULA_CIUDADANIA','91234567','1978-11-30','SOLTERO','3112345680','fernando.silva@yahoo.com','Calle 67 #23-45','Bogotá',1),(4,'Patricia','Jiménez Cruz','PASAPORTE','PA123456','1988-05-18','DIVORCIADO','3112345681','patricia.jimenez@outlook.com','Avenida 9 #45-67','Medellín',1),(5,'Miguel','Hernández Rojas','CEDULA_EXTRANJERA','1075234567','1982-09-10','CASADO','3123456789','miguel.hernandez@gmail.com','Carrera 33 #78-90','Bogotá',2),(6,'Diana','Gutiérrez Pardo','PASAPORTE','5234567823','1995-02-14','SOLTERO','3123456790','diana.gutierrez@hotmail.com','Calle 100 #23-45','Bogotá',2),(7,'Javier','Morales Vega','CEDULA_EXTRANJERA','8023456723','1975-12-05','VIUDO','3123456791','javier.morales@yahoo.com','Carrera 15 #34-56','Barranquilla',2),(8,'Karen','Ramírez Díaz','PASAPORTE','3785678945','1992-08-20','UNION_LIBRE','3123456792','carolina.ramirez@gmail.com','Calle 85 #12-34','Bucaramanga',2),(9,'Lucas','Torres Medina','CEDULA_EXTRANJERA','37856734','1980-04-25','CASADO','3134567890','luis.torres@outlook.com','Avenida 19 #67-89','Medellín',3),(10,'Maria','López Castillo','PASAPORTE','52456789233','1987-10-12','SOLTERO','3134567891','sandra.lopez@gmail.com','Carrera 7 #45-67','Bogotá',3),(11,'Oscar','García Soto','LICENCIA_CONDUCCION','9134567823','1983-06-08','DIVORCIADO','3134567892','oscar.garcia@hotmail.com','Calle 50 #23-45','Bucaramanga',3),(12,'Melissa','Martínez Luna','CEDULA_EXTRANJERA','37867890','1993-01-30','VIUDO','3134567893','melissa.martinez@yahoo.com','Carrera 28 #56-78','Bucaramanga',3),(13,'Andrés','Rodríguez Villa','LICENCIA_CONDUCCION','1075345678','1979-11-15','CASADO','3145678901','andres.rodriguez@gmail.com','Calle 72 #34-56','Bogotá',4),(14,'Natalia','Vargas Rojas','PASAPORTE','52567890','1991-03-22','SOLTERO','3145678902','natalia.vargas@hotmail.com','Carrera 40 #67-89','Medellín',4),(15,'Camilo','Suárez Pinto','CEDULA_EXTRANJERA','80345678','1986-07-18','CASADO','3145678903','camilo.suarez@outlook.com','Avenida 30 #45-67','Cali',4),(16,'Paola','Gómez Reyes','PASAPORTE','37878901','1994-12-10','UNION_LIBRE','3156789012','paola.gomez@gmail.com','Calle 15 #23-45','Bucaramanga',5),(17,'Ricardo','Mendoza Cruz','LICENCIA_CONDUCCION','1098345678','1981-05-28','CASADO','3156789013','ricardo.mendoza@yahoo.com','Carrera 11 #56-78','Barranquilla',5),(18,'Juliana','Castro Ríos','LICENCIA_CONDUCCION','52678901','1989-09-16','VIUDO','3156789014','juliana.castro@hotmail.com','Calle 95 #34-56','Bogotá',5),(19,'Daniel','Ortiz Paredes','LICENCIA_CONDUCCION','91456789','1984-02-20','CASADO','3167890123','daniel.ortiz@gmail.com','Carrera 50 #78-90','Medellín',6),(20,'Marcela','Ríos Gómez','CEDULA_EXTRANJERA','37889012','1996-06-14','UNION_LIBRE','3167890124','marcela.rios@outlook.com','Calle 120 #23-45','Bucaramanga',6),(21,'José','Ordoñez Díaz','CEDULA_CIUDADANIA','1098765341','2005-01-06','SOLTERO','3002375112','oljd2002@gmail.com','CALLE 104 E # 12-05','Bucaramanga',29),(22,'Juana','diaz','CEDULA_CIUDADANIA','234234234','2008-02-08','SOLTERO','3002375231','cara.ramirez@gmail.com','CALLE 104 E # 12-05','Bucaramanga',29),(23,'Ramiro','Perez','CEDULA_EXTRANJERA','1098761141','1998-04-08','UNION_LIBRE','3002372112','leon@gmail.com','CALLE 104 E # 12-05','Bucaramanga',29),(32,'MARIANA','RAMIREZ','CEDULA_CIUDADANIA','1234567890',NULL,'CASADO','1234567890','MARIANA@GMAIL.COM','Carrera 7 #45-67','Bucaramanga',29),(33,'mario','Gonzales','CEDULA_CIUDADANIA','121342781731',NULL,'CASADO','7126324121','002@gmail.com','Carrera 7 #45-67','Bucaramanga',29),(35,'PEAR SOLUTIONS S.A.S.',NULL,'NUMERO_IDENTIFICACION_TRIBUTARIA','9001481776',NULL,NULL,'6015082339','laura.castro@pearsolutarsolutions.com.co','CL 99 # 49 53 P 4','BOGOTÁ D.C.',29),(36,'JOSEPH STEPHEN','SOSA CARDENAS','CEDULA_CIUDADANIA','1022437272',NULL,NULL,'3195267913',NULL,'TV 35 BIS 29B 63 SUR','BOGOTA',29);
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `polizas`
--

DROP TABLE IF EXISTS `polizas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `polizas` (
  `id_poliza` bigint NOT NULL AUTO_INCREMENT,
  `codigo_poliza` varchar(20) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `prima_neta` decimal(15,2) NOT NULL,
  `prima_total` decimal(15,2) NOT NULL,
  `estado` varchar(40) DEFAULT NULL,
  `estado_pago` varchar(40) NOT NULL DEFAULT 'PENDIENTE',
  `numero_pdf` varchar(150) NOT NULL,
  `placa` varchar(15) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `cliente_id` bigint NOT NULL,
  `usuario_id` bigint NOT NULL,
  `ramo_id` bigint NOT NULL,
  `aseguradora_id` bigint NOT NULL,
  PRIMARY KEY (`id_poliza`),
  UNIQUE KEY `uk_polizas_codigo` (`codigo_poliza`),
  UNIQUE KEY `uk_polizas_pdf` (`numero_pdf`),
  KEY `fk_usuarios_polizas` (`usuario_id`),
  KEY `fk_ramos_polizas` (`ramo_id`),
  KEY `fk_aseguradoras_polizas` (`aseguradora_id`),
  KEY `idx_polizas_codigo` (`codigo_poliza`),
  KEY `idx_polizas_placa` (`placa`),
  KEY `idx_polizas_estado` (`estado`),
  KEY `idx_polizas_cliente_estado` (`cliente_id`,`estado`),
  KEY `idx_polizas_fecha_inicio` (`fecha_inicio`),
  KEY `idx_polizas_fecha_vencimiento` (`fecha_vencimiento`),
  CONSTRAINT `fk_aseguradoras_polizas` FOREIGN KEY (`aseguradora_id`) REFERENCES `aseguradoras` (`id_aseguradora`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_clientes_polizas` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id_cliente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_ramos_polizas` FOREIGN KEY (`ramo_id`) REFERENCES `ramos` (`id_ramo`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_usuarios_polizas` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id_usuario`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_polizas_fechas` CHECK ((`fecha_vencimiento` > `fecha_inicio`)),
  CONSTRAINT `chk_polizas_prima_neta` CHECK ((`prima_neta` >= 0)),
  CONSTRAINT `chk_polizas_prima_total` CHECK ((`prima_total` >= `prima_neta`))
) ENGINE=InnoDB AUTO_INCREMENT=5040 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `polizas`
--

LOCK TABLES `polizas` WRITE;
/*!40000 ALTER TABLE `polizas` DISABLE KEYS */;
INSERT INTO `polizas` VALUES (1001,'POL-2024-SAL-1001','2024-01-15','2025-01-15',450000.00,520000.00,'CANCELADA','PAGADA','PDF-1001','526HTS','holasdfbadsfb',1,2,3,1),(1002,'POL-2024-SAL-1002','2024-02-20','2025-02-20',380000.00,440000.00,'ACTIVA','PENDIENTE','PDF-1002',NULL,NULL,5,1,1,2),(1003,'POL-2024-SAL-1003','2024-03-10','2025-03-10',520000.00,600000.00,'CANCELADA','PENDIENTE','PDF-1003',NULL,NULL,9,3,1,3),(1004,'POL-2023-SAL-1004','2023-06-15','2024-06-15',400000.00,460000.00,'CANCELADA','PENDIENTE','PDF-1004',NULL,NULL,3,3,1,1),(2001,'POL-2024-VID-2001','2025-11-20','2026-12-20',280000.00,340000.00,'ACTIVA','PENDIENTE','PDF-2001',NULL,NULL,12,2,2,4),(2002,'POL-2024-VID-2002','2024-02-15','2025-02-15',320000.00,390000.00,'CANCELADA','PENDIENTE','PDF-2002',NULL,NULL,11,6,2,5),(2003,'POL-2024-VID-2003','2024-04-05','2025-04-05',450000.00,550000.00,'ACTIVA','PENDIENTE','PDF-2003',NULL,NULL,11,5,2,6),(2004,'POL-2023-VID-2004','2023-12-01','2024-12-01',300000.00,365000.00,'CANCELADA','PENDIENTE','PDF-2004',NULL,NULL,14,6,2,4),(3001,'POL-2024-ACC-3001','2024-03-15','2025-03-15',180000.00,220000.00,'ACTIVA','PENDIENTE','PDF-3001',NULL,NULL,13,8,2,7),(3002,'POL-2024-ACC-302','2024-04-20','2025-04-20',150000.00,185000.00,'ACTIVA','PENDIENTE','PDF-3002','243gfd','holas ascsdv',17,7,3,8),(3003,'POL-2024-ACC-3003','2024-05-10','2025-05-10',200000.00,245000.00,'ACTIVA','PENDIENTE','PDF-3003',NULL,NULL,11,5,2,9),(3004,'POL-2023-ACC-3004','2023-08-15','2024-08-15',175000.00,215000.00,'CANCELADA','PENDIENTE','PDF-3004',NULL,NULL,16,5,2,7),(4001,'POL-2024-HOG-4001','2024-02-01','2025-02-01',650000.00,750000.00,'ACTIVA','PENDIENTE','PDF-4001',NULL,NULL,14,8,4,1),(4002,'POL-2024-HOG-4002','2024-03-15','2025-03-15',580000.00,670000.00,'ACTIVA','PENDIENTE','PDF-4002',NULL,NULL,18,9,4,2),(4003,'POL-2024-HOG-4003','2024-04-20','2025-04-20',720000.00,830000.00,'ACTIVA','PENDIENTE','PDF-4003',NULL,NULL,12,2,4,3),(4004,'POL-2023-HOG-4004','2023-11-10','2024-11-10',600000.00,690000.00,'ACTIVA','PENDIENTE','PDF-4004',NULL,NULL,15,2,4,1),(5001,'POL-2024-AUT-5001','2024-01-10','2025-01-10',1200000.00,1380000.00,'ACTIVA','PENDIENTE','PDF-5001','ABC123',NULL,11,3,3,1),(5002,'POL-2024-AUT-5002','2024-02-05','2025-02-05',950000.00,1090000.00,'ACTIVA','PENDIENTE','PDF-5002','DEF456',NULL,12,3,3,2),(5003,'POL-2024-AUT-5003','2024-03-12','2025-03-12',1400000.00,1610000.00,'CANCELADA','PENDIENTE','PDF-5003','GHI789',NULL,15,4,3,3),(5004,'POL-2024-AUT-5004','2024-04-18','2025-04-18',1100000.00,1265000.00,'ACTIVA','PENDIENTE','PDF-5004','JKL012',NULL,6,4,3,4),(5005,'POL-2024-AUT-5005','2024-05-22','2025-05-22',1350000.00,1550000.00,'ACTIVA','PENDIENTE','PDF-5005','MNO345',NULL,9,5,3,5),(5006,'POL-2024-AUT-5006','2024-06-15','2025-06-15',1050000.00,1210000.00,'ACTIVA','PENDIENTE','PDF-5006','PQR678',NULL,10,5,3,6),(5007,'POL-2024-AUT-5007','2024-07-08','2025-07-08',1280000.00,1470000.00,'ACTIVA','PENDIENTE','PDF-5007','STU901',NULL,13,6,3,1),(5008,'POL-2024-AUT-5008','2024-08-20','2025-08-20',980000.00,1130000.00,'ACTIVA','PENDIENTE','PDF-5008','VWX234',NULL,14,6,3,2),(5009,'POL-2024-AUT-5009','2024-09-10','2025-09-10',1150000.00,1320000.00,'CANCELADA','PENDIENTE','PDF-5009','YZA567',NULL,16,7,3,3),(5010,'POL-2023-AUT-5010','2023-10-05','2024-10-05',1300000.00,1495000.00,'ACTIVA','PAGADA','PDF-5010','BCD890',NULL,21,7,3,4),(5011,'POL-2024-AUT-5011','2024-10-15','2025-10-15',1420000.00,1630000.00,'ACTIVA','PENDIENTE','PDF-5011','EFG123',NULL,18,8,3,5),(5012,'POL-2024-AUT-5012','2025-03-01','2026-03-09',1080000.00,1240000.00,'ACTIVA','PENDIENTE','PDF-5012','HIJ456',NULL,19,8,3,6),(5014,'9000014871741234','2026-02-14','2027-12-14',222.00,22232.00,'ACTIVA','PENDIENTE','2c7d5abb-d19b-4676-81da-50d437971fe6_9000014871741234.pdf','2342134234',NULL,21,29,3,1),(5015,'1234567890','2026-02-15','2027-02-16',1.00,2.00,'CANCELADA','PENDIENTE','9ba32569-fd3a-49e3-9f1b-81245a5c13ba_1234567890.pdf','1234',NULL,10,29,3,3),(5016,'0987654321','2026-02-15','2027-02-16',1.00,2.00,'ACTIVA','PENDIENTE','c71f4946-c858-460c-bbb8-99ad38417ab7_0987654321.pdf',NULL,NULL,10,29,1,5),(5017,'111111111122222','2026-02-16','2027-02-17',1.00,2.00,'ACTIVA','PENDIENTE','abcd4636-88a6-4afa-bbdb-fb64c8fa7fc2_111111111111111.pdf',NULL,'holaaaaaaaaaaaaaaaaaaaaaaaaa',10,29,1,3),(5035,'3425235423','2026-03-03','2027-03-04',1.00,2.00,'CANCELADA','PAGADA','990e1e33-d53a-4470-b180-77106263f195_3425235423.pdf',NULL,'\n CANCELADA EL DIA 2026-03-05 POR EL MOTIVO: Por fraude',9,29,2,2),(5037,'17378122','2026-03-03','2027-03-04',1.00,223.00,'CANCELADA','PAGADA','1517e44e-6bee-459d-afc5-8df8f62025c2_1737812245323423.pdf',NULL,NULL,33,29,5,9),(5038,'900001487175','2026-03-03','2027-03-03',1.00,3.00,'CANCELADA','PENDIENTE','8c030c52-ca8e-4fa9-ab85-388e2339204a_900001487174.pdf','JTR587','\n CANCELADA EL DIA 2026-03-05 POR EL MOTIVO: SDFBASBASBASBSBXZCB',35,29,3,7),(5039,'3056466','2025-12-14','2026-12-14',1828679.40,2199928.00,'ACTIVA','PENDIENTE','c2c3c11c-58c9-4fa2-b6ef-c8b0c46e79aa_3056466.pdf','XFJ06G',NULL,36,29,3,6);
/*!40000 ALTER TABLE `polizas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ramos`
--

DROP TABLE IF EXISTS `ramos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ramos` (
  `id_ramo` bigint NOT NULL AUTO_INCREMENT,
  `nombre_ramo` varchar(40) NOT NULL,
  `comision` decimal(4,2) NOT NULL,
  PRIMARY KEY (`id_ramo`),
  CONSTRAINT `chk_ramos_comision` CHECK (((`comision` >= 0) and (`comision` <= 30)))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ramos`
--

LOCK TABLES `ramos` WRITE;
/*!40000 ALTER TABLE `ramos` DISABLE KEYS */;
INSERT INTO `ramos` VALUES (1,'SALUD',12.50),(2,'VIDA',15.00),(3,'AUTOMOVIL',10.00),(4,'SOAT',8.50),(5,'HOGAR',11.00),(6,'CUMPLIMIENTO',10.00),(7,'RESPONSABILIDAD_CIVIL',8.00),(8,'ARL',11.00),(9,'TRANSPORTE',13.00),(10,'MULTIRIESGO_EMPRESARIAL',10.00),(11,'ARRENDAMIENTO',6.00);
/*!40000 ALTER TABLE `ramos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id_role` bigint NOT NULL AUTO_INCREMENT,
  `nombre_rol` varchar(20) NOT NULL,
  `descripcion` text,
  PRIMARY KEY (`id_role`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMINISTRADOR','Acceso total al sistema, gestión de usuarios y configuración'),(2,'ASESOR','Gestión de clientes y pólizas, ventas');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(40) NOT NULL,
  `apellido` varchar(40) NOT NULL,
  `tipo_documento` varchar(40) DEFAULT NULL,
  `numero_documento` varchar(15) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `correo` varchar(100) NOT NULL,
  `clave` varchar(100) NOT NULL,
  `estado` varchar(40) NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uk_usuarios_correo` (`correo`),
  UNIQUE KEY `uk_usuarios_numero_documento` (`numero_documento`),
  UNIQUE KEY `uk_usuarios_telefono` (`telefono`),
  KEY `fk_roles_usuarios` (`role_id`),
  CONSTRAINT `fk_roles_usuarios` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id_role`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_usuarios_correo` CHECK (regexp_like(`correo`,_utf8mb4'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'))
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Carlos','Rodríguez','CEDULA_CIUDADANIA','1098765432','3001234562','Calle 45 #23-10','carlos.rodriguez@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','ACTIVO',1),(2,'María','Gómez','CEDULA_CIUDADANIA','1098765433','3001234568','Carrera 27 #45-67','maria.gomez@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','ACTIVO',1),(3,'Juan','Martínez','CEDULA_CIUDADANIA','1098765434','3001234569','Calle 10 #15-20','juan.martinez@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','ACTIVO',2),(4,'Ana','López','CEDULA_CIUDADANIA','1098765435','3001234570','Carrera 33 #28-45','ana.lopez@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','INACTIVO',2),(5,'Pedro','Sánchez','CEDULA_CIUDADANIA','1098765436','3001234571','Calle 55 #12-34','pedro.sanchez@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','ACTIVO',1),(6,'Laura','Torres','CEDULA_CIUDADANIA','1098765437','3001234572','Avenida 15 #45-23','laura.torres@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','ACTIVO',2),(7,'Diego','Ramírez','CEDULA_CIUDADANIA','1098765438','3001234573','Carrera 18 #34-56','diego.ramirez@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','ACTIVO',2),(8,'Sofía','Vargas','CEDULA_CIUDADANIA','1098765439','3001234574','Calle 89 #23-12','sofia.vargas@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','SUSPENDIDO',2),(9,'Andrés','Moreno','CEDULA_CIUDADANIA','1098765440','3001234575','Carrera 50 #67-89','andres.moreno@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','INACTIVO',2),(10,'Valentina','Castro','CEDULA_CIUDADANIA','1098765441','3001234576','Calle 120 #15-30','valentina.castro@ipas.com.co','$2a$10$WvaF0TOXjHmSH.1hcAAB3.VT7A.OWPZxT3nJVKcur9uLTsa1dKZfq','SUSPENDIDO',2),(29,'Mario','gomez','CEDULA_CIUDADANIA','1098765421','3001234171','Calle 120 #15-38','bbbbb@gmail.com','$2a$10$IJ.Klz.DyXVAMkXvT/4R.e/LDjFsfLqgRrZeKQoetSp7tMhER214C','ACTIVO',2),(30,'Ramiro','Gonzales',NULL,NULL,NULL,NULL,'gonzales@gmail.com','$2a$10$PSOTmlKYICvU8PJ8wqMDXOEPpLvlv8qo40.1e5BeNoyMarLoxVHzC','INACTIVO',2),(31,'jose','luis','CEDULA_CIUDADANIA','12123451145',NULL,NULL,'luis@gmail.com','$2a$10$LodHecL1IaAA6suEx6z8Zu4jCbCatqAkUTp2tu5fKBSeUNmDOTLqy','ACTIVO',1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-10 23:06:18
