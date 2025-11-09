-- Eliminar tablas existentes para asegurar un estado limpio.
DROP TABLE IF EXISTS "respuesta";
DROP TABLE IF EXISTS "intento";
DROP TABLE IF EXISTS "progreso_leccion";
DROP TABLE IF EXISTS "opcion";
DROP TABLE IF EXISTS "pregunta";
DROP TABLE IF EXISTS "prueba";
DROP TABLE IF EXISTS "leccion";
DROP TABLE IF EXISTS "seccion";
DROP TABLE IF EXISTS "usuario_curso";
DROP TABLE IF EXISTS "sesion_estudio";
DROP TABLE IF EXISTS "usuario_stats";
DROP TABLE IF EXISTS "usuario_item";
DROP TABLE IF EXISTS "stella_item";
DROP TABLE IF EXISTS "item";
DROP TABLE IF EXISTS "curso";
DROP TABLE IF EXISTS "comentario";
DROP TABLE IF EXISTS "post";
DROP TABLE IF EXISTS "usuario";

-- CREACIÓN DE TABLAS (con Claves Foráneas y ON DELETE CASCADE)
-- =========================================================================

-- TABLA: "usuario"
CREATE TABLE "usuario" (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           username VARCHAR(50) UNIQUE NOT NULL,
                           correo VARCHAR(100) UNIQUE NOT NULL,
                           nombre VARCHAR(100) NOT NULL,
                           contrasena VARCHAR(255) NOT NULL,
                           tipo VARCHAR(20) NOT NULL -- ESTUDIANTE, PROFESOR, ADMIN
);

-- TABLA: "usuario_stats" (Estadísticas y gamificación)
CREATE TABLE "usuario_stats" (
                                 usuario_id INT PRIMARY KEY,
                                 pescaditos INT NOT NULL DEFAULT 0,
                                 racha_dias INT NOT NULL DEFAULT 0,
                                 tiempo_total_estudio_segundos INT NOT NULL DEFAULT 0,
                                 FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

-- TABLA: "curso"
CREATE TABLE "curso" (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         titulo VARCHAR(255) NOT NULL,
                         descripcion VARCHAR(2000),
                         nivel VARCHAR(50),
                         categoria VARCHAR(100),
                         duracion_minutos INT,
                         numero_secciones INT
);

-- TABLA: "usuario_curso"
CREATE TABLE "usuario_curso" (
                                 usuario_id INT NOT NULL,
                                 curso_id INT NOT NULL,
                                 fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (usuario_id, curso_id),
                                 FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
                                 FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE
);

-- TABLA: "seccion"
CREATE TABLE "seccion" (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           curso_id INT NOT NULL,
                           titulo VARCHAR(255) NOT NULL,
                           numero_orden INT NOT NULL,
                           FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE,
                           UNIQUE (curso_id, numero_orden)
);

-- TABLA: "leccion" (Contenido individual: Teoría, Video, Práctica, Pregunta)
CREATE TABLE "leccion" (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           seccion_id INT NOT NULL,
                           titulo VARCHAR(255) NOT NULL,
                           numero_orden INT NOT NULL,
                           tipo_contenido VARCHAR(20) NOT NULL,
                           url_video VARCHAR(255),
                           contenido CLOB,
                           FOREIGN KEY (seccion_id) REFERENCES "seccion"(id) ON DELETE CASCADE,
                           UNIQUE (seccion_id, numero_orden)
);

-- TABLA: "prueba" (Quizzes seccionales o examen final)
CREATE TABLE "prueba" (
                          id INT PRIMARY KEY,
                          curso_id INT NOT NULL,
                          seccion_id INT NOT NULL,
                          titulo VARCHAR(255) NOT NULL,
                          tipo VARCHAR(20) NOT NULL,
                          FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE,
                          FOREIGN KEY (seccion_id) REFERENCES "seccion"(id) ON DELETE CASCADE
);

-- TABLA: "pregunta"
CREATE TABLE "pregunta" (
                            id INT PRIMARY KEY,
                            enunciado VARCHAR(500) NOT NULL,
                            leccion_id INT,
                            prueba_id INT,
                            FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
                            FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE,
                            CHECK (leccion_id IS NOT NULL OR prueba_id IS NOT NULL)
);

-- TABLA: "opcion"
CREATE TABLE "opcion" (
                          id INT PRIMARY KEY,
                          pregunta_id INT NOT NULL,
                          texto VARCHAR(500) NOT NULL,
                          es_correcta BOOLEAN NOT NULL,
                          FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE
);

-- TABLA: "progreso_leccion"
CREATE TABLE "progreso_leccion" (
                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                    usuario_id INT NOT NULL,
                                    leccion_id INT NOT NULL,
                                    estado VARCHAR(20) NOT NULL,
                                    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
                                    FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
                                    UNIQUE (usuario_id, leccion_id)
);

-- TABLA: "intento" (Registro de una prueba realizada)
CREATE TABLE "intento" (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           usuario_id INT NOT NULL,
                           prueba_id INT NOT NULL,
                           puntaje DOUBLE NOT NULL,
                           fecha_intento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
                           FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE
);

-- TABLA: "respuesta" (Respuestas específicas dentro de un Intento)
CREATE TABLE "respuesta" (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             intento_id INT NOT NULL,
                             pregunta_id INT NOT NULL,
                             opcion_seleccionada_id INT NOT NULL,
                             FOREIGN KEY (intento_id) REFERENCES "intento"(id) ON DELETE CASCADE,
                             FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE,
                             FOREIGN KEY (opcion_seleccionada_id) REFERENCES "opcion"(id) ON DELETE CASCADE
);

-- TABLA: "sesion_estudio" (Guardar todas las sesiones de estudio)
CREATE TABLE "sesion_estudio" (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  usuario_id INT NOT NULL,
                                  tiempo_estudio INT NOT NULL,
                                  tiempo_descanso INT NOT NULL,
                                  fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  fecha_final TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

-- TABLA: "post"
CREATE TABLE "post" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    contenido_texto TEXT NOT NULL,
    likes INT DEFAULT 0,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etiqueta TEXT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

-- TABLA "comentario"
CREATE TABLE "comentario" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    usuario_id INT NOT NULL,
    contenido_texto TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    likes INT DEFAULT 0,
    FOREIGN KEY (post_id) REFERENCES "post"(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);


-- TABLA: "item"
CREATE TABLE "item" (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nombre VARCHAR(255) NOT NULL,
                        descripcion VARCHAR(500) NOT NULL,
                        precio INT NOT NULL,
                        image_path VARCHAR(500)
);

-- TABLA: "stella_item"
CREATE TABLE "stella_item" (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               item_id INT NOT NULL,
                               image_path VARCHAR(500),
                               FOREIGN KEY (item_id) REFERENCES "item"(id) ON DELETE CASCADE
);

-- TABLA: "usuario_item"
CREATE TABLE "usuario_item" (
                                usuario_id INT NOT NULL,
                                item_id INT NOT NULL,
                                fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                es_activo BOOLEAN DEFAULT TRUE NOT NULL,
                                PRIMARY KEY (usuario_id, item_id),
                                FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
                                FOREIGN KEY (item_id) REFERENCES "item"(id) ON DELETE CASCADE
);

-- DATOS INICIALES PARA TESTING
-- =========================================================================

-- 1. USUARIO DE PRUEBA
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo)
VALUES ('testestudio', 'test@estudio.com', 'Usuario Estudio', 'pass123', 'ESTUDIANTE');

-- 2. ESTADÍSTICAS (100 pescaditos)
INSERT INTO "usuario_stats" (usuario_id, pescaditos, racha_dias, tiempo_total_estudio_segundos)
VALUES (1, 100, 0, 0);

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

-- 5. ITEMS DE ROPA EN LA TIENDA
-- Sombrero Vuelteado
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Sombrero Vuelteado','Un sombrero elegante y sofisticado, con un diseño único que añade misterio y estilo a cualquier atuendo. Perfecto para quienes buscan destacar con un toque Colombiano.',
     800, '/Image/TiendaStella/sombreroVuelteado.png');

-- Gorra
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra','Gorra casual con elementos bordados que aporta un toque de estilo moderno. Ideal para el día a día, tanto para una caminata por la ciudad como para un día de descanso.',
     40, '/Image/TiendaStella/gorra.png');

-- Gafas
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas', 'Gafas de sol con protección UV, diseñadas para ofrecer una protección total frente a los rayos solares sin sacrificar el estilo. Perfectas para cualquier ocasión al aire libre.',
     60, '/Image/TiendaStella/gafasDeSol.png');

-- Camiseta FIS
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta FIS', 'Camiseta oficial de Fundamentos en Ingeniería de Software, con un diseño moderno y cómodo. Ideal para los estudiantes y exalumnos que quieren lucir su orgullo académico con estilo.',
     50, '/Image/TiendaStella/sombreroVuelteado.png');

-- Hoodie
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Hoodie', 'Sudadera de alta calidad, cómoda y abrigada, ideal para mantenerte caliente durante esas largas horas de estudio o para disfrutar de un día relajado con amigos. Su diseño versátil la hace perfecta para cualquier ocasión.',
     120, '/Image/TiendaStella/HoodieGit.png');

-- Crop Top
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Crop Top', 'Un crop top fresco y moderno, perfecto para los días soleados o cálidos. Su corte y estilo te aseguran comodidad y libertad de movimiento, ideal para lucir relajada y con estilo.',
     45, '/Image/TiendaStella/sombreroVuelteado.png');

-- 6. STELLA USANDO ITEMS
INSERT INTO "stella_item" (item_id, image_path) VALUES (1, '/Image/stellas/stellaSombrero.png');

INSERT INTO "stella_item" (item_id, image_path) VALUES (2, '/Image/stellas/stellaGorra.png');

INSERT INTO "stella_item" (item_id, image_path) VALUES (3, '/images/stellas/stellaGafas.png');

INSERT INTO "stella_item" (item_id, image_path) VALUES (4, '/images/stellas/stellaCamiseta.png');

INSERT INTO "stella_item" (item_id, image_path) VALUES (5, '/images/stellas/stellaHoodie.png');

INSERT INTO "stella_item" (item_id, image_path) VALUES (6, '/images/stellas/stellaCrop.png');

-- 7. COMPRA INICIAL
INSERT INTO "usuario_item" (usuario_id, item_id) VALUES (1, 4);

--PARA EL FORO

--Insertar usuario de prueba para crear comentario en el post, el usuario tendrá el ID 2
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo)
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
(2, 1, 'Sí, debes asignar y liberar memoria así: int *arr = new int[10]; y delete[] arr;', '2025-11-07 18:25:00')


