-- ClassConnect — datos de demostración
SET NAMES utf8mb4;
-- Usuarios de prueba:
--   Docente:      profesor@escuela.com / pass123
--   Padre/Madre:  padre@correo.com     / pass123
USE sistema_escolar;

SET @anio = YEAR(CURDATE());

INSERT INTO usuarios (id, nombre, apellido, correo, pass, rol) VALUES
 (1, 'Juan',   'Pérez',     'profesor@escuela.com', 'pass123',  'profesor'),
 (2, 'José',   'García',    'padre@correo.com',     'pass123',  'padre'),
 (3, 'Admin',  'Sistema',   'admin@escuela.com',    'admin123', 'admin');

INSERT INTO profesores (id_profesor, id_usuario, especialidad, telefono, fecha_contratacion)
VALUES (1, 1, 'Educación Primaria', '809-555-0101', '2020-08-15');

INSERT INTO padres (id_padre, id_usuario, telefono, ocupacion)
VALUES (1, 2, '809-555-0202', 'Ingeniero');

INSERT INTO cursos (id_curso, grado, anio_escolar, seccion) VALUES
 (1, '3ro Primaria', @anio, 'A'),
 (2, '3ro Primaria', @anio, 'B');

INSERT INTO asignaturas (nombre_asignatura, id_profesor, id_curso, creditos)
SELECT m.nombre, 1, c.id_curso, 1
FROM (SELECT 'Matemáticas' AS nombre UNION ALL SELECT 'Lengua Española' UNION ALL SELECT 'Ciencias Naturales'
      UNION ALL SELECT 'Ciencias Sociales' UNION ALL SELECT 'Inglés' UNION ALL SELECT 'Educación Física'
      UNION ALL SELECT 'Arte y Cultura') m
CROSS JOIN cursos c;

-- Estudiantes (usuario + alumno)
INSERT INTO usuarios (id, nombre, apellido, correo, pass, rol) VALUES
 (11, 'María',     'García López',     '2024001@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (12, 'Carlos',    'Rodríguez Pérez',  '2024002@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (13, 'Ana',       'Martínez Díaz',    '2024003@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (14, 'Luis',      'Fernández Santos', '2024004@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (15, 'Sofía',     'Morales Ortiz',    '2024005@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (16, 'Diego',     'Gómez Ruiz',       '2024006@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (17, 'Isabella',  'Torres Ramírez',   '2024007@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (18, 'Mateo',     'Sánchez Vázquez',  '2024008@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (19, 'Valentina', 'Flores Castro',    '2024009@estudiantes.classconnect.edu', NULL, 'estudiante'),
 (20, 'Samuel',    'Reyes Peña',       '2024010@estudiantes.classconnect.edu', NULL, 'estudiante');

INSERT INTO alumnos (id_estudiante, id_usuario, id_curso, matricula, fecha_nacimiento, direccion) VALUES
 (1,  11, 1, '2024001', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'Av. Independencia #12, Santo Domingo'),
 (2,  12, 1, '2024002', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'C/ El Sol #45, Santiago'),
 (3,  13, 1, '2024003', DATE_SUB(CURDATE(), INTERVAL 9 YEAR),  'C/ Duarte #8, La Vega'),
 (4,  14, 1, '2024004', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'Av. Las Américas #101, Santo Domingo Este'),
 (5,  15, 1, '2024005', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'C/ Mella #23, San Cristóbal'),
 (6,  16, 2, '2024006', DATE_SUB(CURDATE(), INTERVAL 9 YEAR),  'C/ Restauración #5, Puerto Plata'),
 (7,  17, 2, '2024007', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'Av. 27 de Febrero #300, Santo Domingo'),
 (8,  18, 2, '2024008', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'C/ Sánchez #14, Baní'),
 (9,  19, 2, '2024009', DATE_SUB(CURDATE(), INTERVAL 9 YEAR),  'C/ Luperón #77, Moca'),
 (10, 20, 2, '2024010', DATE_SUB(CURDATE(), INTERVAL 8 YEAR),  'Av. Máximo Gómez #9, Santo Domingo');

INSERT INTO padre_estudiante (id_padre, id_estudiante, parentesco) VALUES (1, 1, 'padre');

-- Notas: tres trimestres con valores entre 70 y 100; el examen final queda pendiente (0) en algunas materias
INSERT INTO notas (id_estudiante, id_asignatura, calificacion, calificacion2, calificacion3, calificacion4, observacion)
SELECT a.id_estudiante, asig.id_asignatura,
       70 + MOD(a.id_estudiante * 7 + asig.id_asignatura * 3, 30),
       70 + MOD(a.id_estudiante * 5 + asig.id_asignatura * 11, 30),
       70 + MOD(a.id_estudiante * 3 + asig.id_asignatura * 13, 30),
       IF(MOD(a.id_estudiante + asig.id_asignatura, 4) = 0, 0, 70 + MOD(a.id_estudiante * 11 + asig.id_asignatura * 7, 30)),
       NULL
FROM alumnos a JOIN asignaturas asig ON asig.id_curso = a.id_curso;

-- Asistencia de los últimos 30 días laborables
INSERT INTO asistencia (id_estudiante, fecha, estado, observacion)
WITH RECURSIVE dias AS (
    SELECT DATE_SUB(CURDATE(), INTERVAL 1 DAY) AS fecha, 1 AS n
    UNION ALL SELECT DATE_SUB(fecha, INTERVAL 1 DAY), n + 1 FROM dias WHERE n < 45
)
SELECT a.id_estudiante, d.fecha,
       CASE WHEN MOD(a.id_estudiante * 31 + DAYOFYEAR(d.fecha), 23) = 0 THEN 'ausente'
            WHEN MOD(a.id_estudiante * 17 + DAYOFYEAR(d.fecha), 19) = 0 THEN 'tarde'
            ELSE 'presente' END,
       NULL
FROM alumnos a CROSS JOIN dias d
WHERE DAYOFWEEK(d.fecha) BETWEEN 2 AND 6;

INSERT INTO avisos_eventos (titulo, descripcion, fecha_evento, tipo, id_creador, id_curso) VALUES
 ('Reunión de padres', 'Entrega de calificaciones del trimestre', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'reunión', 1, 1),
 ('Feria científica', 'Presentación de proyectos de Ciencias Naturales', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'actividad', 1, 1);
