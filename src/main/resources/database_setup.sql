-- CONFIGURACIÓN INICIAL Y LIMPIEZA
-- =========================================================================

-- Eliminar tablas existentes para asegurar un estado limpio.
DROP TABLE IF EXISTS "respuesta";
DROP TABLE IF EXISTS "intento";
DROP TABLE IF EXISTS "progreso_leccion";
DROP TABLE IF EXISTS "usuario_stats";
DROP TABLE IF EXISTS "opcion";
DROP TABLE IF EXISTS "pregunta";
DROP TABLE IF EXISTS "prueba";
DROP TABLE IF EXISTS "leccion";
DROP TABLE IF EXISTS "curso";
DROP TABLE IF EXISTS "usuario"; -- <-- Ajustado
DROP TABLE IF EXISTS "sesion_estudio";

-- CREACIÓN DE TABLAS (con Claves Foráneas y ON DELETE CASCADE)
-- =========================================================================

-- TABLA: "usuario"
CREATE TABLE "usuario" ( -- <-- Usamos comillas dobles en el nombre de la tabla
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           username VARCHAR(50) UNIQUE NOT NULL,
                           correo VARCHAR(100) UNIQUE NOT NULL,
                           nombre VARCHAR(100) NOT NULL,
                           contrasena VARCHAR(255) NOT NULL,
                           tipo VARCHAR(20) NOT NULL -- ESTUDIANTE, PROFESOR, ADMIN
);

-- TABLA: "usuario_stats" (Estadísticas y gamificación)
CREATE TABLE "usuario_stats" ( -- <-- Usamos comillas dobles
                                 usuario_id INT PRIMARY KEY,
                                 pescaditos INT NOT NULL DEFAULT 0,
                                 racha_dias INT NOT NULL DEFAULT 0,
                                 tiempo_total_estudio_segundos INT NOT NULL DEFAULT 0,

                                 FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE -- <-- Referencia ajustada con comillas
);

-- TABLA: "curso"
CREATE TABLE "curso" ( -- <-- Usamos comillas dobles
                         id INT PRIMARY KEY,
                         titulo VARCHAR(255) NOT NULL,
                         numero_secciones INT NOT NULL
);

-- TABLA: "leccion" (Contenido individual: Teoría, Video, Práctica, Pregunta)
CREATE TABLE "leccion" ( -- <-- Usamos comillas dobles
                           id INT PRIMARY KEY,
                           curso_id INT NOT NULL,
                           titulo VARCHAR(255) NOT NULL,
                           numero_seccion INT NOT NULL,
                           numero_orden INT NOT NULL, -- Orden dentro de la sección
                           tipo_contenido VARCHAR(20) NOT NULL, -- TEORIA, VIDEO, PRACTICA, PREGUNTA, QUIZ, QUIZ_FINAL
                           contenido_html CLOB, -- Texto (Teoría/Práctica) o URL/Título (Video)
                           solucion CLOB, -- Contenido de la solución para ejercicios de tipo PRACTICA
                           prueba_id INT, -- Campo que enlaza a una PRUEBA (si es lección de tipo QUIZ)

                           FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                           UNIQUE (curso_id, numero_seccion, numero_orden)
);

-- TABLA: "prueba" (Quizzes seccionales o examen final)
CREATE TABLE "prueba" ( -- <-- Usamos comillas dobles
                          id INT PRIMARY KEY,
                          curso_id INT NOT NULL,
                          titulo VARCHAR(255) NOT NULL,
                          tipo VARCHAR(20) NOT NULL, -- SECCIONAL, FINAL
                          numero_seccion INT, -- Null si es prueba FINAL

                          FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE -- <-- Referencia ajustada con comillas
);

-- TABLA: "pregunta"
CREATE TABLE "pregunta" ( -- <-- Usamos comillas dobles
                            id INT PRIMARY KEY,
                            enunciado VARCHAR(500) NOT NULL,

    -- Claves foráneas mutuamente excluyentes
                            leccion_id INT, -- Si es una pregunta ligada a una Lección (tipo PREGUNTA)
                            prueba_id INT,  -- Si es una pregunta ligada a una Prueba (Quiz)

                            FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                            FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas

    -- Restricción para asegurar que solo esté asociada a leccion O a prueba, no a ambas.
                            CHECK (leccion_id IS NOT NULL OR prueba_id IS NOT NULL)
);

-- TABLA: "opcion"
CREATE TABLE "opcion" ( -- <-- Usamos comillas dobles
                          id INT PRIMARY KEY,
                          pregunta_id INT NOT NULL,
                          texto VARCHAR(500) NOT NULL,
                          es_correcta BOOLEAN NOT NULL,

                          FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE -- <-- Referencia ajustada con comillas
);

-- TABLA: "progreso_leccion"
-- Se añade AUTO_INCREMENT para que la BD gestione el ID, solucionando el error NULL not allowed
CREATE TABLE "progreso_leccion" ( -- <-- Usamos comillas dobles
                                    id INT PRIMARY KEY AUTO_INCREMENT,
                                    usuario_id INT NOT NULL,
                                    leccion_id INT NOT NULL,
                                    estado VARCHAR(20) NOT NULL, -- PENDIENTE, EN_PROGRESO, COMPLETADA
                                    fecha_inicio TIMESTAMP,
                                    fecha_completado TIMESTAMP,

                                    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                                    FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                                    UNIQUE (usuario_id, leccion_id)
);

-- TABLA: "intento" (Registro de una prueba realizada)
CREATE TABLE "intento" ( -- <-- Usamos comillas dobles
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           usuario_id INT NOT NULL,
                           prueba_id INT NOT NULL,
                           puntaje DOUBLE NOT NULL,
                           fecha_intento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                           FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE -- <-- Referencia ajustada con comillas
);

-- TABLA: "respuesta" (Respuestas específicas dentro de un Intento)
CREATE TABLE "respuesta" ( -- <-- Usamos comillas dobles
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             intento_id INT NOT NULL,
                             pregunta_id INT NOT NULL,
                             opcion_seleccionada_id INT NOT NULL,

                             FOREIGN KEY (intento_id) REFERENCES "intento"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                             FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE, -- <-- Referencia ajustada con comillas
                             FOREIGN KEY (opcion_seleccionada_id) REFERENCES "opcion"(id) ON DELETE CASCADE -- <-- Referencia ajustada con comillas
);

-- TABLA: "sesion_estudio" (Guardar todas las sesiones de estudio )
CREATE TABLE "sesion_estudio" ( -- <-- Usamos comillas dobles
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             usuario_id INT NOT NULL,
                             tiempo_estudio INT NOT NULL,
                             tiempo_descanso INT NOT NULL,
                             fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             fecha_final TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                             FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE -- <-- Referencia ajustada con comillas

);


-- DATOS INICIALES PARA TESTING
-- =========================================================================

-- 1. USUARIO DE PRUEBA (ID 42 usado en TestSesionEstudioService)
INSERT INTO "usuario" (id, username, correo, nombre, contrasena, tipo) -- <-- Usamos comillas dobles
VALUES (42, 'testestudio', 'test@estudio.com', 'Usuario Estudio', 'pass123', 'ESTUDIANTE');

-- 2. ESTADÍSTICAS DEL USUARIO
INSERT INTO "usuario_stats" (usuario_id, pescaditos, racha_dias) -- <-- Usamos comillas dobles
VALUES (42, 50, 2); -- Datos iniciales para pruebas

-- 3. CURSO PRINCIPAL
INSERT INTO "curso" (id, titulo, numero_secciones) -- <-- Usamos comillas dobles
VALUES (1, 'C++ Básico', 4);

-- 4. LECCIONES DEL CURSO "C++ Básico"
-- ------------------------------------

INSERT INTO "leccion" (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES -- <-- Usamos comillas dobles
                                                                                                                                       (1, 1, 'Historia y uso de C++', 1, 1, 'TEORIA', 'C++ es un lenguaje de programación creado por Bjarne Stroustrup en 1979 en los laboratorios Bell. Originalmente se llamó "C con clases"... Características principales: Lenguaje compilado, Multiparadigma, Alto rendimiento... Ámbitos de uso: Sistemas operativos, Videojuegos, etc.', NULL, NULL),
                                                                                                                                       (2, 1, 'Primeros pasos en C++', 1, 2, 'VIDEO', '{"titulo": "Introducción a C++ - Tu primer programa", "enlace": "https://www.youtube.com/watch?v=ej_FcRlqo4A"}', NULL, NULL),
                                                                                                                                       (3, 1, 'Pregunta sobre el video', 1, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 3
                                                                                                                                       (4, 1, 'Sintaxis básica y estructura', 1, 4, 'TEORIA', 'La estructura básica de un programa C++ incluye: #include <iostream>, int main()...', NULL, NULL),
                                                                                                                                       (5, 1, 'Ejercicio práctico', 1, 5, 'PRACTICA', 'Ejercicio: Crea un programa que muestre el mensaje "¡Hola Mundo! Bienvenido a C++" en la pantalla.', '#include <iostream>\nusing namespace std;\n\nint main() {\n\tcout << "¡Hola Mundo! Bienvenido a C++" << endl;\n\treturn 0;\n}', NULL);

-- SECCIÓN 2: Control de flujo
INSERT INTO "leccion" (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES -- <-- Usamos comillas dobles
                                                                                                                                       (6, 1, 'Condicionales if-else', 2, 1, 'TEORIA', 'Los condicionales permiten que tu programa tome decisiones basadas en condiciones. La estructura if-else evalúa una condición...', NULL, NULL),
                                                                                                                                       (7, 1, 'Estructuras de control', 2, 2, 'VIDEO', '{"titulo": "Condicionales y Bucles en C++ - Control de Flujo", "enlace": "https://www.youtube.com/watch?v=Q_4WghBJFT8"}', NULL, NULL),
                                                                                                                                       (8, 1, 'Pregunta sobre el video', 2, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 8
                                                                                                                                       (9, 1, 'Bucles y operadores lógicos', 2, 4, 'TEORIA', 'Bucles en C++: for, while, do-while. Operadores lógicos: && (AND), || (OR), ! (NOT).', NULL, NULL),
                                                                                                                                       (10, 1, 'Ejercicio práctico', 2, 5, 'PRACTICA', 'Ejercicio: Crea un programa que pida números al usuario hasta que ingrese 0, luego muestre la suma de todos los números ingresados.', '#include <iostream>\nusing namespace std;\n\nint main() {\n\tint numero, suma = 0;\n\tdo {\n\t\tcout << "Ingresa un número (0 para terminar): ";\n\t\tcin >> numero;\n\t\tsuma += numero;\n\t} while (numero != 0);\n\tcout << "La suma total es: " << suma << endl;\n\treturn 0;\n}', NULL);

-- SECCIÓN 3: Funciones y estructuras
INSERT INTO "leccion" (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES -- <-- Usamos comillas dobles
                                                                                                                                       (11, 1, 'Introducción a funciones', 3, 1, 'TEORIA', 'Las funciones son bloques de código que realizan una tarea específica y pueden ser reutilizados. Partes: Declaración, Definición, Llamada.', NULL, NULL),
                                                                                                                                       (12, 1, 'Funciones en profundidad', 3, 2, 'VIDEO', '{"titulo": "Funciones en C++ - Modularización del Código", "enlace": "https://www.youtube.com/watch?v=GQp1zzTwrIg"}', NULL, NULL),
                                                                                                                                       (13, 1, 'Pregunta sobre el video', 3, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 13
                                                                                                                                       (14, 1, 'Ámbito y estructuras', 3, 4, 'TEORIA', 'Ámbito de variables (Local/Global). Estructuras (struct) para agrupar variables relacionadas.', NULL, NULL),
                                                                                                                                       (15, 1, 'Ejercicio práctico', 3, 5, 'PRACTICA', 'Ejercicio: Crea una función que calcule el área de un rectángulo y otra función que calcule el área de un círculo.', 'Solución: Funciones areaRectangulo y areaCirculo con lógica condicional en main().', NULL);

-- SECCIÓN 4: Arreglos y punteros
INSERT INTO "leccion" (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES -- <-- Usamos comillas dobles
                                                                                                                                       (16, 1, 'Arreglos unidimensionales', 4, 1, 'TEORIA', 'Los arreglos almacenan múltiples valores del mismo tipo. Declaración: tipo nombre[tamaño]; Acceso por índice (inicia en 0).', NULL, NULL),
                                                                                                                                       (17, 1, 'Trabajando con arreglos', 4, 2, 'VIDEO', '{"titulo": "Arreglos y Punteros en C++ - Estructuras de Datos Básicas", "enlace": "https://www.youtube.com/watch?v=Z_hPJ_EhceI"}', NULL, NULL),
                                                                                                                                       (18, 1, 'Pregunta sobre el video', 4, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 18
                                                                                                                                       (19, 1, 'Punteros básicos', 4, 4, 'TEORIA', 'Los punteros almacenan direcciones de memoria. Operadores: & (dirección de) y * (desreferencia).', NULL, NULL),
                                                                                                                                       (20, 1, 'Ejercicio práctico', 4, 5, 'PRACTICA', 'Ejercicio: Crea un programa que pida 5 números al usuario, los almacene en un arreglo, luego encuentre y muestre el número mayor usando punteros.', 'Solución: Uso de puntero int* ptr = numeros; para recorrer y encontrar el mayor.', NULL);


-- 5. PRUEBAS (QUIZZES) DEL CURSO
-- ------------------------------------
-- IDs de Pruebas: 101, 102, 103, 104 (Seccionales) y 105 (Final)
INSERT INTO "prueba" (id, curso_id, titulo, tipo, numero_seccion) VALUES -- <-- Usamos comillas dobles
                                                                         (101, 1, 'Quiz Sección 1: Fundamentos', 'SECCIONAL', 1),
                                                                         (102, 1, 'Quiz Sección 2: Control de flujo', 'SECCIONAL', 2),
                                                                         (103, 1, 'Quiz Sección 3: Funciones y estructuras', 'SECCIONAL', 3),
                                                                         (104, 1, 'Quiz Sección 4: Arreglos y punteros', 'SECCIONAL', 4),
                                                                         (105, 1, 'Evaluación Final C++', 'FINAL', NULL);

-- Lecciones asociadas a los Quizzes (Tipo QUIZ)
INSERT INTO "leccion" (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES -- <-- Usamos comillas dobles
                                                                                                                                       (21, 1, 'Quiz Final de Sección 1', 1, 6, 'QUIZ', 'Realiza el quiz para desbloquear la siguiente sección.', NULL, 101),
                                                                                                                                       (22, 1, 'Quiz Final de Sección 2', 2, 6, 'QUIZ', 'Realiza el quiz para desbloquear la siguiente sección.', NULL, 102),
                                                                                                                                       (23, 1, 'Quiz Final de Sección 3', 3, 6, 'QUIZ', 'Realiza el quiz para desbloquear la siguiente sección.', NULL, 103),
                                                                                                                                       (24, 1, 'Quiz Final de Sección 4', 4, 6, 'QUIZ', 'Realiza el quiz para el examen final.', NULL, 104),
                                                                                                                                       (25, 1, 'Evaluación Final del Curso C++', 4, 7, 'QUIZ_FINAL', 'Realiza el examen final para completar el curso.', NULL, 105);

-- 6. PREGUNTAS Y OPCIONES (LECCIONES DE TIPO PREGUNTA)
-- ------------------------------------------------------------------------

-- Lección 3: Pregunta sobre el video (ID 3)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (1, '¿Cuál es la función principal que debe estar presente en todo programa C++?', 3, NULL); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (10, 1, 'start()', FALSE),
                                                                  (11, 1, 'main()', TRUE), -- Respuesta correcta para la prueba funcional (Lec 3)
                                                                  (12, 1, 'begin()', FALSE),
                                                                  (13, 1, 'run()', FALSE);

-- Lección 8: Pregunta sobre el video (ID 8)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (2, '¿Qué bucle se ejecuta al menos una vez, incluso si la condición es falsa inicialmente?', 8, NULL); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (14, 2, 'for', FALSE),
                                                                  (15, 2, 'while', FALSE),
                                                                  (16, 2, 'do-while', TRUE),
                                                                  (17, 2, 'if-else', FALSE);

-- Lección 13: Pregunta sobre el video (ID 13)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (3, '¿Qué palabra clave se usa para devolver un valor desde una función?', 13, NULL); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (18, 3, 'break', FALSE),
                                                                  (19, 3, 'return', TRUE),
                                                                  (20, 3, 'exit', FALSE),
                                                                  (21, 3, 'end', FALSE);

-- Lección 18: Pregunta sobre el video (ID 18)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (4, '¿En qué índice comienzan los arreglos en C++?', 18, NULL); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (22, 4, '0', TRUE),
                                                                  (23, 4, '1', FALSE),
                                                                  (24, 4, 'Depende del compilador', FALSE),
                                                                  (25, 4, 'Se puede configurar', FALSE);


-- 7. PREGUNTAS Y OPCIONES (QUIZZES SECCIONALES Y FINAL)
-- ... (Se omiten por brevedad, asumiendo que son correctas, pero se mantienen en el script)
-- Q1 (ID 5): ¿En qué año fue creado C++?
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (5, '¿En qué año fue creado C++?', NULL, 101); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (26, 5, '1985', FALSE), (27, 5, '1979', TRUE), (28, 5, '1995', FALSE), (29, 5, '2000', FALSE);

-- Q2 (ID 6): ¿Qué línea de código se usa para incluir la biblioteca de entrada/salida?
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (6, '¿Qué línea de código se usa para incluir la biblioteca de entrada/salida?', NULL, 101); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (30, 6, '#include <iostream>', TRUE), (31, 6, 'import iostream', FALSE), (32, 6, 'using iostream', FALSE), (33, 6, 'include <iostream>', FALSE);

-- Q3 (ID 7): ¿Qué función es el punto de entrada de un programa C++?
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (7, '¿Qué función es el punto de entrada de un programa C++?', NULL, 101); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (34, 7, 'start()', FALSE), (35, 7, 'init()', FALSE), (36, 7, 'main()', TRUE), (37, 7, 'begin()', FALSE);

-- Q4 (ID 8): ¿Qué símbolo se usa para comentarios de una sola línea?
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (8, '¿Qué símbolo se usa para comentarios de una sola línea?', NULL, 101); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (38, 8, '//', TRUE), (39, 8, '/*', FALSE), (40, 8, '#', FALSE), (41, 8, '--', FALSE);

-- Q5 (ID 9): ¿Qué comando muestra texto en la consola?
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (9, '¿Qué comando muestra texto en la consola?', NULL, 101); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (42, 9, 'print', FALSE), (43, 9, 'cout', TRUE), (44, 9, 'display', FALSE), (45, 9, 'write', FALSE);

-- QUIZ SECCIÓN 2 (ID Prueba 102)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (10, '¿Qué palabra clave se usa para condiciones alternativas al if?', NULL, 102); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (46, 10, 'else', TRUE), (47, 10, 'elseif', FALSE), (48, 10, 'otherwise', FALSE), (49, 10, 'alternative', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (11, '¿Cuál es la estructura correcta de un bucle for?', NULL, 102); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (50, 11, 'for (condición; inicio; incremento)', FALSE), (51, 11, 'for (inicio; condición; incremento)', TRUE), (52, 11, 'for (inicio; incremento; condición)', FALSE), (53, 11, 'for (condición; incremento; inicio)', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (12, '¿Qué operador lógico representa "AND"?', NULL, 102); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (54, 12, '||', FALSE), (55, 12, '&&', TRUE), (56, 12, '!', FALSE), (57, 12, '&', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (13, '¿Cuántas veces se ejecuta este bucle: for(int i=1; i<=3; i++)?', NULL, 102); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (58, 13, '1', FALSE), (59, 13, '2', FALSE), (60, 13, '3', TRUE), (61, 13, '4', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (14, '¿Qué bucle garantiza al menos una ejecución?', NULL, 102); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (62, 14, 'for', FALSE), (63, 14, 'while', FALSE), (64, 14, 'do-while', TRUE), (65, 14, 'if', FALSE);


-- QUIZ SECCIÓN 3 (ID Prueba 103)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (15, '¿Qué palabra clave define una estructura?', NULL, 103); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (66, 15, 'struct', TRUE), (67, 15, 'class', FALSE), (68, 15, 'object', FALSE), (69, 15, 'type', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (16, '¿Dónde es visible una variable local?', NULL, 103); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (70, 16, 'En todo el programa', FALSE), (71, 16, 'Solo en la función donde se declara', TRUE), (72, 16, 'En todas las funciones del archivo', FALSE), (73, 16, 'En funciones del mismo tipo', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (17, '¿Qué hace la palabra clave "return"?', NULL, 103); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (74, 17, 'Termina el programa', FALSE), (75, 17, 'Devuelve un valor y sale de la función', TRUE), (76, 17, 'Continúa con la siguiente línea', FALSE), (77, 17, 'Llama a otra función', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (18, '¿Cómo se accede a los miembros de una estructura?', NULL, 103); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (78, 18, '->', FALSE), (79, 18, '.', TRUE), (80, 18, '::', FALSE), (81, 18, ':', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (19, '¿Qué tipo de parámetro modifica la variable original?', NULL, 103); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (82, 19, 'Por valor', FALSE), (83, 19, 'Por copia', FALSE), (84, 19, 'Por referencia', TRUE), (85, 19, 'Por nombre', FALSE);


-- QUIZ SECCIÓN 4 (ID Prueba 104)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (20, '¿Cómo se declara un arreglo de 10 enteros?', NULL, 104); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (86, 20, 'int array[10];', TRUE), (87, 20, 'array int[10];', FALSE), (88, 20, 'int[10] array;', FALSE), (89, 20, 'integer array(10);', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (21, '¿Qué operador obtiene la dirección de una variable?', NULL, 104); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (90, 21, '*', FALSE), (91, 21, '&', TRUE), (92, 21, '->', FALSE), (93, 21, '@', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (22, '¿Qué hace el operador * con punteros?', NULL, 104); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (94, 22, 'Obtiene la dirección', FALSE), (95, 22, 'Accede al valor apuntado', TRUE), (96, 22, 'Suma valores', FALSE), (97, 22, 'Multiplica punteros', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (23, '¿Cuál es el primer índice de un arreglo?', NULL, 104); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (98, 23, '0', TRUE), (99, 23, '1', FALSE), (100, 23, '-1', FALSE), (101, 23, '10', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (24, '¿El nombre de un arreglo es...?', NULL, 104); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (102, 24, 'Una función', FALSE), (103, 24, 'Un puntero al primer elemento', TRUE), (104, 24, 'Una variable especial', FALSE), (105, 24, 'Un objeto', FALSE);


-- EVALUACIÓN FINAL (ID Prueba 105)
INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (25, '¿Qué incluye necesario para usar cout y cin?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (106, 25, '#include <iostream>', TRUE), (107, 25, '#include <string>', FALSE), (108, 25, '#include <math>', FALSE), (109, 25, '#include <input>', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (26, '¿Cuál es la salida de: for(int i=0; i<3; i++) cout << i;', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (110, 26, '012', TRUE), (111, 26, '123', FALSE), (112, 26, '0123', FALSE), (113, 26, '111', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (27, '¿Qué devuelve una función void?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (114, 27, '0', FALSE), (115, 27, '1', FALSE), (116, 27, 'Nada', TRUE), (117, 27, 'Un valor aleatorio', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (28, '¿Cómo se declara un puntero a entero?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (118, 28, 'int ptr;', FALSE), (119, 28, 'int* ptr;', TRUE), (120, 28, 'pointer int;', FALSE), (121, 28, 'ptr int*;', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (29, '¿Qué hace break en un switch?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (122, 29, 'Continúa al siguiente case', FALSE), (123, 29, 'Termina el switch', TRUE), (124, 29, 'Reinicia el switch', FALSE), (125, 29, 'Ejecuta todos los cases', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (30, '¿Cómo se accede al tercer elemento de un arreglo?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (126, 30, 'array[2]', TRUE), (127, 30, 'array[3]', FALSE), (128, 30, 'array(2)', FALSE), (129, 30, 'array(3)', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (31, '¿Qué operador representa "OR" lógico?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (130, 31, '&&', FALSE), (131, 31, '||', TRUE), (132, 31, '!', FALSE), (133, 31, '&', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (32, '¿Cuál es el tipo de retorno de main()?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (134, 32, 'void', FALSE), (135, 32, 'int', TRUE), (136, 32, 'float', FALSE), (137, 32, 'string', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (33, '¿Qué struct es correcto?', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (138, 33, 'struct {int x; int y;} Punto;', TRUE), (139, 33, 'struct Punto int x, y;', FALSE), (140, 33, 'struct Punto {x, y};', FALSE), (141, 33, 'Punto struct {int x; int y;}', FALSE);

INSERT INTO "pregunta" (id, enunciado, leccion_id, prueba_id) VALUES (34, '¿Qué imprime: int x=5; cout << ++x;', NULL, 105); -- <-- Usamos comillas dobles
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES -- <-- Usamos comillas dobles
                                                                  (142, 34, '5', FALSE), (143, 34, '6', TRUE), (144, 34, '4', FALSE), (145, 34, 'Error', FALSE);

-- ESTABLECER ID DE SECUENCIA PARA EVITAR CONFLICTOS CON LAS INSERCIONES MANUALES
ALTER TABLE "intento" ALTER COLUMN id RESTART WITH 1000;
ALTER TABLE "respuesta" ALTER COLUMN id RESTART WITH 1000;