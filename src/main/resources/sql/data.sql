-- DATOS INICIALES
-- =========================================================================

-- 1. USUARIO DE PRUEBA
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo_usuario)
VALUES ('testestudio', 'test@estudio.com', 'Usuario Estudio', 'pass123', 'ESTUDIANTE');

-- 2. CURSOS
INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones)
VALUES ('Curso de C++ basico', 'En este curso aprenderas a manejar variables', 'BÁSICO', 'categoria c++', 0, 0);

INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones)
VALUES ('Curso de java basico', 'En este curso aprenderas a manejar variables', 'BÁSICO', 'categoria java', 0, 0);

INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones)
VALUES ('Curso de python basico', 'En este curso aprenderas a manejar variables', 'BÁSICO', 'categoria python', 0, 0);

INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones)
VALUES ('Curso de GO basico', 'En este curso aprenderas a manejar variables', 'BÁSICO', 'categoria GO', 0, 0);

-- 3. SECCIONES

-- SECCIONES CURSO 1
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES (1, 'Introducción al lenguaje y entorno', 1);
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES (1, 'Variables, tipos de datos y operadores', 2);
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES (1, 'Estructuras de control', 3);
-- SECCIONES CURSO 2
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES (2, 'Introducción a Java y su entorno', 1);
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES (2, 'Variables, tipos de datos y operadores', 2);
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES (2, 'Estructuras de control', 3);

-- 4. LECCIONES

-- CURSO 1
-- LECCIONES SECCION 1
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (1, 'Historia y características de C++', 1, 'TEORIA', NULL, 'Origen y evolución del lenguaje C++. Principales características y usos actuales.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (1, 'Instalación del entorno (IDE)', 2, 'TEORIA', 'https://www.youtube.com/watch?v=OOeO2984nJI', 'Instrucciones para instalar Code::Blocks o Visual Studio y configurar el compilador.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (1, 'Estructura básica de un programa C++', 3, 'VIDEO', 'https://www.youtube.com/watch?v=Rub-JsjMhWY', 'Análisis de la estructura base de un programa: directivas, función main y sintaxis básica.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (1, 'Primer programa: Hola Mundo', 4, 'TEORIA', NULL, 'Creación, compilación y ejecución de un programa sencillo que imprime Hola Mundo en consola.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (1, 'Comentarios y buenas prácticas', 5, 'TEORIA', NULL, 'Uso de comentarios en C++ y recomendaciones para escribir código limpio y legible.');

-- LECCIONES SECCION 2
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (2, 'Tipos de datos primitivos', 1, 'TEORIA', NULL, 'Explicación de los tipos de datos: int, float, char, bool y double.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (2, 'Declaración y asignación de variables', 2, 'TEORIA', NULL, 'Cómo declarar, inicializar y modificar variables en C++.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (2, 'Operadores aritméticos y relacionales', 3, 'VIDEO', 'https://www.youtube.com/watch?v=1JbmAml1nT4', 'Uso de operadores básicos para cálculos y comparaciones.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (2, 'Entrada y salida estándar', 4, 'TEORIA', NULL, 'Lectura y escritura en consola usando cin y cout.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (2, 'Ejercicios prácticos', 5, 'TEORIA', NULL, 'Ejercicios de aplicación: operaciones matemáticas, promedio y área de figuras.');

-- LECCIONES SECCION 3
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (3, 'Condicionales if, else if, else', 1, 'TEORIA', NULL, 'Uso de estructuras condicionales simples en C++.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (3, 'Estructura switch', 2, 'TEORIA', NULL, 'Uso del switch para manejar múltiples condiciones.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (3, 'Ciclo for', 3, 'VIDEO', 'https://www.youtube.com/watch?v=K4lnvU7hG6c', 'Cómo usar el ciclo for para repetir instrucciones controladamente.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (3, 'Ciclos while y do-while', 4, 'TEORIA', NULL, 'Repeticiones condicionales en C++ usando while y do-while.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (3, 'Proyecto final: Calculadora básica', 5, 'TEORIA', NULL, 'Desarrolla una calculadora que realice operaciones aritméticas básicas usando bucles y condicionales.');


-- CURSO 2
-- LECCIONES SECCION 1
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (4, 'Historia y filosofía de Java', 1, 'TEORIA', NULL, 'Origen y principios de Java, independencia de plataforma y portabilidad.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (4, 'Instalación de JDK y configuración del IDE', 2, 'TEORIA', NULL, 'Instalación del JDK y configuración del entorno de desarrollo (IntelliJ IDEA o Eclipse).');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (4, 'Estructura de un programa Java', 3, 'VIDEO', 'https://www.youtube.com/watch?v=GoXwIVyNvX0', 'Análisis de la estructura base de un programa Java: clases, método main y sintaxis.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (4, 'Primer programa: Hola Mundo', 4, 'TEORIA', NULL, 'Creación y ejecución de un programa simple que imprime Hola Mundo.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (4, 'Comentarios y convenciones de código', 5, 'TEORIA', NULL, 'Uso de comentarios y buenas prácticas de escritura en Java.');

-- LECCIONES SECCION 2
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (5, 'Tipos de datos primitivos', 1, 'TEORIA', NULL, 'Tipos de datos primitivos en Java: int, float, boolean, char, double.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (5, 'Variables y constantes', 2, 'TEORIA', NULL, 'Declaración, inicialización y uso de constantes con final.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (5, 'Operadores básicos y relacionales', 3, 'VIDEO', 'https://www.youtube.com/watch?v=cyuzt1Dp8X4', 'Uso de operadores aritméticos y relacionales en Java.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (5, 'Entrada y salida con Scanner', 4, 'TEORIA', NULL, 'Lectura de datos desde consola con la clase Scanner.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (5, 'Ejercicios prácticos', 5, 'TEORIA', NULL, 'Ejercicios simples de operaciones aritméticas y promedio.');

-- LECCIONES SECCION 3
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (6, 'Condicionales if y else if', 1, 'TEORIA', NULL, 'Uso de condicionales para ejecutar diferentes caminos de código.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (6, 'switch y uso con String', 2, 'TEORIA', NULL, 'Estructura switch en Java y ejemplos con cadenas de texto.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (6, 'Ciclo for', 3, 'VIDEO', 'https://www.youtube.com/watch?v=wUpPsEcGSGg', 'Uso del bucle for para repetir instrucciones en Java.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (6, 'Ciclos while y do-while', 4, 'TEORIA', NULL, 'Diferencias entre while y do-while. Ejemplos prácticos de repetición.');
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido)
VALUES (6, 'Proyecto final: Conversor de temperaturas', 5, 'TEORIA', NULL, 'Proyecto final para aplicar estructuras de control en un programa que convierte temperaturas.');

-- Después de insertar usuario y curso
INSERT INTO "usuario_curso" (usuario_id, curso_id) VALUES (1, 1);


--PARA LA TIENDA Y LOS ITEMS

-- 5. ITEMS DE LA TIENDA Y STELLA CON EL OBJETO
-- 👕 HOODIE GITHUB
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Hoodie GitHub','Sudadera con capucha inspirada en GitHub, ideal para desarrolladores que quieren vestir con estilo tech.',
     1150, '/Image/TiendaStella/hoodieGithub.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (1, '/Image/stellas/stellaHoodieGithub.png');

-- 👕 CAMISETA APPLE
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Apple','Camiseta minimalista con logo de Apple, ideal para los amantes de la tecnología y el diseño limpio.',
     950, '/Image/TiendaStella/camisetaApple.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (2, '/Image/stellas/stellaCamisetaApple.png');

-- 🏫 CAMISETA FIS
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta FIS','Camiseta deportiva con diseño institucional FIS, ideal para eventos y actividades académicas.',
     800, '/Image/TiendaStella/camisetaFIS.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (3, '/Image/stellas/stellaFIS.png');

-- 🇨🇴 CAMISETA COLOMBIA
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Colombia','Camiseta de la selección Colombia, fabricada con tela transpirable y cómoda para mostrar el orgullo nacional.',
     1050, '/Image/TiendaStella/camisetaColombia.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (4, '/Image/stellas/stellaCamisetaColombia.png');

-- 🧢 BALACA DISNEY
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Balaca Disney','Balaca con diseño inspirado en personajes clásicos de Disney, cómoda y divertida para toda ocasión.',
     450, '/Image/TiendaStella/balacaDisney.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (5, '/Image/stellas/stellaDisney.png');

-- 🍹 JUGO HIT
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Jugo Hit','Bebida refrescante de frutas naturales, ideal para acompañar tus comidas o hidratarte en cualquier momento.',
     350, '/Image/TiendaStella/jugoHit.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (6, '/Image/stellas/stellaJugoHit.png');

-- ⚽ CAMISETA MILLOS
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Millos','Camiseta oficial del equipo Millonarios, perfecta para hinchas que quieren lucir sus colores con pasión.',
     1000, '/Image/TiendaStella/camisetaMillos.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (7, '/Image/stellas/stellaCamisetaMillos.png');

-- 👒 SOMBRERO VUELTEAO
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Sombrero Vuelteao','Sombrero típico colombiano elaborado con caña flecha, símbolo de tradición y estilo.',
     900, '/Image/TiendaStella/sombreroVuelteao.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (8, '/Image/stellas/stellaSombreroVuelteao.png');

-- 👚 CROPTOP
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Croptop','Croptop moderno y cómodo, ideal para climas cálidos y estilos urbanos.',
     700, '/Image/TiendaStella/croptop.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (9, '/Image/stellas/stellaCroptop.png');

-- 🧣 BUFANDA
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Bufanda','Bufanda tejida con materiales suaves y cálidos, perfecta para protegerte del frío con estilo.',
     850, '/Image/TiendaStella/bufanda.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (10, '/Image/stellas/stellaBufanda.png');

-- 🕶️ GAFAS DE SOL
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas de Sol','Gafas elegantes con protección UV400, perfectas para un look moderno y proteger tus ojos del sol.',
     600, '/Image/TiendaStella/gafasDeSol.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (11, '/Image/stellas/stellaGafas.png');

-- 🕶️ GAFAS DE SKI
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas de Ski','Gafas diseñadas para nieve y deporte extremo, resistentes al viento y la neblina.',
     1100, '/Image/TiendaStella/gafasSki.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (12, '/Image/stellas/stellaSki.png');

-- 🧢 GORRA
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra','Gorra clásica ajustable, con visera curva y diseño casual ideal para cualquier día.',
     500, '/Image/TiendaStella/gorra.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (13, '/Image/stellas/stellaGorra.png');

-- 🧢 GORRA ROJA
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra Roja','Gorra de color rojo intenso, ajustable y cómoda, perfecta para destacar en cualquier ocasión.',
     520, '/Image/TiendaStella/gorraRoja.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (14, '/Image/stellas/stellaGorraRoja.png');

-- 🧢 GORRA ROSA
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra Rosa','Gorra color rosa con diseño moderno, perfecta para un look casual y alegre.',
     520, '/Image/TiendaStella/gorraRosa.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (15, '/Image/stellas/stellaGorraRosa.png');

-- 6. USUARIO_STATS INICIAL
INSERT INTO "usuario_stats" (usuario_id, pescaditos, objetivo_sesiones, racha_dias, tiempo_total_estudio_segundos)
VALUES (1, 5000, 1, 0, 0);

-- 7. COMPRA INICIAL
INSERT INTO "usuario_item" (usuario_id, item_id) VALUES (1, 4);

--PARA EL FORO

--Insertar usuario de prueba para crear comentario en el post, el usuario tendrá el ID 2
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo_usuario)
VALUES ('testestudio1', 'test@estudio1.com', 'Usuario Estudio1', 'pass123', 'ESTUDIANTE');

-- Insertar un post
INSERT INTO "post" (usuario_id, contenido_texto, fecha, etiqueta) VALUES
(1, '¿Alguien sabe cómo usar JavaFX para un foro?', '2025-11-07 18:00:00', 'JavaFX');

-- Insertar comentarios para el post
INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(1, 2, 'Sí, usa FXML para la interfaz.', '2025-11-07 18:05:00');

INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(1, 2, 'También puedes agregar un ListView para los posts.', '2025-11-07 18:10:00');

INSERT INTO "post" (usuario_id, contenido_texto, fecha, etiqueta) VALUES
(2, '¿Alguien sabe cómo utilizar la memoria dinámica en c++?', '2025-11-07 18:20:00', 'C++');

INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(2, 1, 'Sí, debes asignar y liberar memoria así: int *arr = new int[10]; y delete[] arr;', '2025-11-07 18:25:00');

--Prueba o quiz
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo)
VALUES (1, 1, 'Quiz: Tipos de datos en C++', 'SECCIONAL');

INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Cómo se declara una variable int en C++?', 1);

INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Qué tipo de dato se usa para texto?', 1);

-- Opciones para cada pregunta
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES (1, 'int x;', true);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES (1, 'variable x;', false);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES (1, 'x = int;', false);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES (2, 'string', true);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES (2, 'int', false);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES (2, 'char', false);


-- DATOS DE PRUEBA PARA PROGRESO (solo para tests - escenario realista)

-- Usuario id=1 (test@estudio.com) ya está inscrito en curso 1 (C++)
-- Nota: cuando un usuario se inscribe  en un curso se insertan un numero de registros
-- igual al numero de lecciones en el curso en el que se iscribió y ademas el campo "estado"
-- se marca como EN_PROGRESO

-- Simulamos que el usuario ya avanzó un poco:
-- Marcamos las primeras 7 lecciones como COMPLETADA
UPDATE "progreso_leccion"
SET estado = 'COMPLETADA'
WHERE usuario_id = 1
  AND leccion_id IN (1, 2, 3, 4, 5, 6, 7);

-- Lección 8 la dejamos EN_PROGRESO (para probar actualización)
-- Lecciones 9 a 15 quedan EN_PROGRESO (progreso actual = 7/15 ≈ 46.67%)