-- ClassConnect — esquema de la base de datos (MySQL 8)
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS sistema_escolar CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_escolar;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `pass` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rol` enum('admin','profesor','padre','estudiante') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`),
  KEY `idx_correo` (`correo`),
  KEY `idx_rol` (`rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `cursos`;
CREATE TABLE `cursos` (
  `id_curso` int NOT NULL AUTO_INCREMENT,
  `grado` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `anio_escolar` int NOT NULL,
  `seccion` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_curso`),
  KEY `idx_anio_grado` (`anio_escolar`,`grado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `profesores`;
CREATE TABLE `profesores` (
  `id_profesor` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `especialidad` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_contratacion` date DEFAULT NULL,
  PRIMARY KEY (`id_profesor`),
  KEY `idx_profesores_usuario` (`id_usuario`),
  CONSTRAINT `profesores_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `padres`;
CREATE TABLE `padres` (
  `id_padre` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `telefono` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ocupacion` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nota` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_padre`),
  KEY `idx_padres_usuario` (`id_usuario`),
  CONSTRAINT `padres_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `alumnos`;
CREATE TABLE `alumnos` (
  `id_estudiante` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_curso` int NOT NULL,
  `matricula` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `direccion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `id_padre` int DEFAULT NULL,
  PRIMARY KEY (`id_estudiante`),
  UNIQUE KEY `matricula` (`matricula`),
  KEY `idx_alumnos_usuario` (`id_usuario`),
  KEY `idx_alumnos_curso` (`id_curso`),
  KEY `idx_matricula` (`matricula`),
  CONSTRAINT `alumnos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `alumnos_ibfk_2` FOREIGN KEY (`id_curso`) REFERENCES `cursos` (`id_curso`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `padre_estudiante`;
CREATE TABLE `padre_estudiante` (
  `id_padre_estudiante` int NOT NULL AUTO_INCREMENT,
  `id_padre` int NOT NULL,
  `id_estudiante` int NOT NULL,
  `parentesco` enum('padre','madre','tutor','abuelo','otro') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_padre_estudiante`),
  UNIQUE KEY `unique_padre_estudiante` (`id_padre`,`id_estudiante`),
  KEY `idx_padre` (`id_padre`),
  KEY `idx_estudiante` (`id_estudiante`),
  CONSTRAINT `padre_estudiante_ibfk_1` FOREIGN KEY (`id_padre`) REFERENCES `padres` (`id_padre`) ON DELETE CASCADE,
  CONSTRAINT `padre_estudiante_ibfk_2` FOREIGN KEY (`id_estudiante`) REFERENCES `alumnos` (`id_estudiante`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `asignaturas`;
CREATE TABLE `asignaturas` (
  `id_asignatura` int NOT NULL AUTO_INCREMENT,
  `nombre_asignatura` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_profesor` int NOT NULL,
  `id_curso` int NOT NULL,
  `creditos` int DEFAULT '1',
  PRIMARY KEY (`id_asignatura`),
  KEY `idx_asignaturas_profesor` (`id_profesor`),
  KEY `idx_asignaturas_curso` (`id_curso`),
  CONSTRAINT `asignaturas_ibfk_1` FOREIGN KEY (`id_profesor`) REFERENCES `profesores` (`id_profesor`) ON DELETE RESTRICT,
  CONSTRAINT `asignaturas_ibfk_2` FOREIGN KEY (`id_curso`) REFERENCES `cursos` (`id_curso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `notas`;
CREATE TABLE `notas` (
  `id_nota` int NOT NULL AUTO_INCREMENT,
  `id_estudiante` int NOT NULL,
  `id_asignatura` int NOT NULL,
  `calificacion` double DEFAULT '0',
  `calificacion2` double DEFAULT '0',
  `calificacion3` double DEFAULT '0',
  `calificacion4` double DEFAULT '0',
  `observacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_nota`),
  UNIQUE KEY `uk_nota_unica` (`id_estudiante`,`id_asignatura`),
  KEY `fk_nota_asignatura` (`id_asignatura`),
  CONSTRAINT `fk_nota_asignatura` FOREIGN KEY (`id_asignatura`) REFERENCES `asignaturas` (`id_asignatura`),
  CONSTRAINT `fk_nota_estudiante` FOREIGN KEY (`id_estudiante`) REFERENCES `alumnos` (`id_estudiante`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `asistencia`;
CREATE TABLE `asistencia` (
  `id_asistencia` int NOT NULL AUTO_INCREMENT,
  `id_estudiante` int NOT NULL,
  `fecha` date NOT NULL DEFAULT (curdate()),
  `estado` enum('presente','ausente','tarde','justificado') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `observacion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_asistencia`),
  UNIQUE KEY `unique_estudiante_fecha` (`id_estudiante`,`fecha`),
  KEY `idx_asistencia_estudiante` (`id_estudiante`),
  KEY `idx_asistencia_fecha` (`fecha`),
  CONSTRAINT `asistencia_ibfk_1` FOREIGN KEY (`id_estudiante`) REFERENCES `alumnos` (`id_estudiante`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `asistencias`;
CREATE TABLE `asistencias` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_estudiante` int NOT NULL,
  `fecha` date NOT NULL,
  `estado` varchar(20) NOT NULL,
  `observaciones` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `avisos_eventos`;
CREATE TABLE `avisos_eventos` (
  `id_evento` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `fecha_evento` date NOT NULL,
  `tipo` enum('reunión','actividad','aviso','feriado','examen') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_creador` int NOT NULL,
  `id_curso` int DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_evento`),
  KEY `id_creador` (`id_creador`),
  KEY `id_curso` (`id_curso`),
  KEY `idx_fecha_evento` (`fecha_evento`),
  KEY `idx_tipo_evento` (`tipo`),
  CONSTRAINT `avisos_eventos_ibfk_1` FOREIGN KEY (`id_creador`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `avisos_eventos_ibfk_2` FOREIGN KEY (`id_curso`) REFERENCES `cursos` (`id_curso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `evento_visto`;
CREATE TABLE `evento_visto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_evento` int NOT NULL,
  `id_usuario` int NOT NULL,
  `fecha_visto` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_evento_usuario` (`id_evento`,`id_usuario`),
  KEY `idx_evento` (`id_evento`),
  KEY `idx_usuario_visto` (`id_usuario`),
  CONSTRAINT `evento_visto_ibfk_1` FOREIGN KEY (`id_evento`) REFERENCES `avisos_eventos` (`id_evento`) ON DELETE CASCADE,
  CONSTRAINT `evento_visto_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `mensajes_comunicaciones`;
CREATE TABLE `mensajes_comunicaciones` (
  `id_mensaje` int NOT NULL AUTO_INCREMENT,
  `id_emisor` int NOT NULL,
  `id_receptor` int NOT NULL,
  `asunto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contenido` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_envio` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `leido` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id_mensaje`),
  KEY `idx_mensajes_receptor` (`id_receptor`),
  KEY `idx_mensajes_emisor` (`id_emisor`),
  KEY `idx_mensajes_leido` (`leido`),
  CONSTRAINT `mensajes_comunicaciones_ibfk_1` FOREIGN KEY (`id_emisor`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `mensajes_comunicaciones_ibfk_2` FOREIGN KEY (`id_receptor`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
