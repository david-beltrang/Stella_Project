-- CONFIGURACIÓN INICIAL Y LIMPIEZA
-- =========================================================================

-- Eliminar tablas existentes para asegurar un estado limpio.
DROP TABLE IF EXISTS respuesta;
DROP TABLE IF EXISTS intento;
DROP TABLE IF EXISTS progreso_leccion;
DROP TABLE IF EXISTS usuario_stats;
DROP TABLE IF EXISTS opcion;
DROP TABLE IF EXISTS pregunta;
DROP TABLE IF EXISTS prueba;
DROP TABLE IF EXISTS leccion;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS usuario;

-- CREACIÓN DE TABLAS (con Claves Foráneas y ON DELETE CASCADE)
-- =========================================================================

-- TABLA: usuario
CREATE TABLE usuario (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(50) UNIQUE NOT NULL,
                         correo VARCHAR(100) UNIQUE NOT NULL,
                         nombre VARCHAR(100) NOT NULL,
                         contrasena VARCHAR(255) NOT NULL,
                         tipo VARCHAR(20) NOT NULL -- ESTUDIANTE, PROFESOR, ADMIN
);

-- TABLA: usuario_stats (Estadísticas y gamificación)
CREATE TABLE usuario_stats (
                               usuario_id INT PRIMARY KEY,
                               pescaditos INT NOT NULL DEFAULT 0,
                               racha_dias INT NOT NULL DEFAULT 0,
                               tiempo_total_estudio_segundos INT NOT NULL DEFAULT 0,

                               FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- TABLA: curso
CREATE TABLE curso (
                       id INT PRIMARY KEY,
                       titulo VARCHAR(255) NOT NULL,
                       numero_secciones INT NOT NULL
);

-- TABLA: leccion (Contenido individual: Teoría, Video, Práctica, Pregunta)
CREATE TABLE leccion (
                         id INT PRIMARY KEY,
                         curso_id INT NOT NULL,
                         titulo VARCHAR(255) NOT NULL,
                         numero_seccion INT NOT NULL,
                         numero_orden INT NOT NULL, -- Orden dentro de la sección
                         tipo_contenido VARCHAR(20) NOT NULL, -- TEORIA, VIDEO, PRACTICA, PREGUNTA, QUIZ, QUIZ_FINAL
                         contenido_html CLOB, -- Texto (Teoría/Práctica) o URL/Título (Video)
                         solucion CLOB, -- Contenido de la solución para ejercicios de tipo PRACTICA
                         prueba_id INT, -- Campo que enlaza a una PRUEBA (si es lección de tipo QUIZ)

                         FOREIGN KEY (curso_id) REFERENCES curso(id) ON DELETE CASCADE,
                         UNIQUE (curso_id, numero_seccion, numero_orden)
);

-- TABLA: prueba (Quizzes seccionales o examen final)
CREATE TABLE prueba (
                        id INT PRIMARY KEY,
                        curso_id INT NOT NULL,
                        titulo VARCHAR(255) NOT NULL,
                        tipo VARCHAR(20) NOT NULL, -- SECCIONAL, FINAL
                        numero_seccion INT, -- Null si es prueba FINAL

                        FOREIGN KEY (curso_id) REFERENCES curso(id) ON DELETE CASCADE
);

-- TABLA: pregunta
CREATE TABLE pregunta (
                          id INT PRIMARY KEY,
                          enunciado VARCHAR(500) NOT NULL,

    -- Claves foráneas mutuamente excluyentes
                          leccion_id INT, -- Si es una pregunta ligada a una Lección (tipo PREGUNTA)
                          prueba_id INT,  -- Si es una pregunta ligada a una Prueba (Quiz)

                          FOREIGN KEY (leccion_id) REFERENCES leccion(id) ON DELETE CASCADE,
                          FOREIGN KEY (prueba_id) REFERENCES prueba(id) ON DELETE CASCADE,

    -- Restricción para asegurar que solo esté asociada a leccion O a prueba, no a ambas.
                          CHECK (leccion_id IS NOT NULL OR prueba_id IS NOT NULL)
);

-- TABLA: opcion
CREATE TABLE opcion (
                        id INT PRIMARY KEY,
                        pregunta_id INT NOT NULL,
                        texto VARCHAR(500) NOT NULL,
                        es_correcta BOOLEAN NOT NULL,

                        FOREIGN KEY (pregunta_id) REFERENCES pregunta(id) ON DELETE CASCADE
);

-- TABLA: progreso_leccion
-- Se añade AUTO_INCREMENT para que la BD gestione el ID, solucionando el error NULL not allowed
CREATE TABLE progreso_leccion (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  usuario_id INT NOT NULL,
                                  leccion_id INT NOT NULL,
                                  estado VARCHAR(20) NOT NULL, -- PENDIENTE, EN_PROGRESO, COMPLETADA
                                  fecha_inicio TIMESTAMP,
                                  fecha_completado TIMESTAMP,

                                  FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
                                  FOREIGN KEY (leccion_id) REFERENCES leccion(id) ON DELETE CASCADE,
                                  UNIQUE (usuario_id, leccion_id)
);

-- TABLA: intento (Registro de una prueba realizada)
CREATE TABLE intento (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         usuario_id INT NOT NULL,
                         prueba_id INT NOT NULL,
                         puntaje DOUBLE NOT NULL,
                         fecha_intento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
                         FOREIGN KEY (prueba_id) REFERENCES prueba(id) ON DELETE CASCADE
);

-- TABLA: respuesta (Respuestas específicas dentro de un Intento)
CREATE TABLE respuesta (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           intento_id INT NOT NULL,
                           pregunta_id INT NOT NULL,
                           opcion_seleccionada_id INT NOT NULL,

                           FOREIGN KEY (intento_id) REFERENCES intento(id) ON DELETE CASCADE,
                           FOREIGN KEY (pregunta_id) REFERENCES pregunta(id) ON DELETE CASCADE,
                           FOREIGN KEY (opcion_seleccionada_id) REFERENCES opcion(id) ON DELETE CASCADE
);


-- DATOS INICIALES PARA TESTING
-- =========================================================================

-- 1. USUARIO DE PRUEBA (ID 42 usado en TestSesionEstudioService)
INSERT INTO usuario (id, username, correo, nombre, contrasena, tipo)
VALUES (42, 'testestudio', 'test@estudio.com', 'Usuario Estudio', 'pass123', 'ESTUDIANTE');

-- 2. ESTADÍSTICAS DEL USUARIO
INSERT INTO usuario_stats (usuario_id, pescaditos, racha_dias)
VALUES (42, 50, 2); -- Datos iniciales para pruebas

-- 3. CURSO PRINCIPAL
INSERT INTO curso (id, titulo, numero_secciones)
VALUES (1, 'C++ Básico', 4);

-- 4. LECCIONES DEL CURSO "C++ Básico"
-- ------------------------------------

INSERT INTO leccion (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES
                                                                                                                                  (1, 1, 'Historia y uso de C++', 1, 1, 'TEORIA', 'C++ es un lenguaje de programación creado por Bjarne Stroustrup en 1979 en los laboratorios Bell. Originalmente se llamó "C con clases"... Características principales: Lenguaje compilado, Multiparadigma, Alto rendimiento... Ámbitos de uso: Sistemas operativos, Videojuegos, etc.', NULL, NULL),
                                                                                                                                  (2, 1, 'Primeros pasos en C++', 1, 2, 'VIDEO', '{"titulo": "Introducción a C++ - Tu primer programa", "enlace": "https://www.youtube.com/watch?v=ej_FcRlqo4A"}', NULL, NULL),
                                                                                                                                  (3, 1, 'Pregunta sobre el video', 1, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 3
                                                                                                                                  (4, 1, 'Sintaxis básica y estructura', 1, 4, 'TEORIA', 'La estructura básica de un programa C++ incluye: #include <iostream>, int main()...', NULL, NULL),
                                                                                                                                  (5, 1, 'Ejercicio práctico', 1, 5, 'PRACTICA', 'Ejercicio: Crea un programa que muestre el mensaje "¡Hola Mundo! Bienvenido a C++" en la pantalla.', '#include <iostream>\nusing namespace std;\n\nint main() {\n\tcout << "¡Hola Mundo! Bienvenido a C++" << endl;\n\treturn 0;\n}', NULL);

-- SECCIÓN 2: Control de flujo
INSERT INTO leccion (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES
                                                                                                                                  (6, 1, 'Condicionales if-else', 2, 1, 'TEORIA', 'Los condicionales permiten que tu programa tome decisiones basadas en condiciones. La estructura if-else evalúa una condición...', NULL, NULL),
                                                                                                                                  (7, 1, 'Estructuras de control', 2, 2, 'VIDEO', '{"titulo": "Condicionales y Bucles en C++ - Control de Flujo", "enlace": "https://www.youtube.com/watch?v=Q_4WghBJFT8"}', NULL, NULL),
                                                                                                                                  (8, 1, 'Pregunta sobre el video', 2, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 8
                                                                                                                                  (9, 1, 'Bucles y operadores lógicos', 2, 4, 'TEORIA', 'Bucles en C++: for, while, do-while. Operadores lógicos: && (AND), || (OR), ! (NOT).', NULL, NULL),
                                                                                                                                  (10, 1, 'Ejercicio práctico', 2, 5, 'PRACTICA', 'Ejercicio: Crea un programa que pida números al usuario hasta que ingrese 0, luego muestre la suma de todos los números ingresados.', '#include <iostream>\nusing namespace std;\n\nint main() {\n\tint numero, suma = 0;\n\tdo {\n\t\tcout << "Ingresa un número (0 para terminar): ";\n\t\tcin >> numero;\n\t\tsuma += numero;\n\t} while (numero != 0);\n\tcout << "La suma total es: " << suma << endl;\n\treturn 0;\n}', NULL);

-- SECCIÓN 3: Funciones y estructuras
INSERT INTO leccion (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES
                                                                                                                                  (11, 1, 'Introducción a funciones', 3, 1, 'TEORIA', 'Las funciones son bloques de código que realizan una tarea específica y pueden ser reutilizados. Partes: Declaración, Definición, Llamada.', NULL, NULL),
                                                                                                                                  (12, 1, 'Funciones en profundidad', 3, 2, 'VIDEO', '{"titulo": "Funciones en C++ - Modularización del Código", "enlace": "https://www.youtube.com/watch?v=GQp1zzTwrIg"}', NULL, NULL),
                                                                                                                                  (13, 1, 'Pregunta sobre el video', 3, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 13
                                                                                                                                  (14, 1, 'Ámbito y estructuras', 3, 4, 'TEORIA', 'Ámbito de variables (Local/Global). Estructuras (struct) para agrupar variables relacionadas.', NULL, NULL),
                                                                                                                                  (15, 1, 'Ejercicio práctico', 3, 5, 'PRACTICA', 'Ejercicio: Crea una función que calcule el área de un rectángulo y otra función que calcule el área de un círculo.', 'Solución: Funciones areaRectangulo y areaCirculo con lógica condicional en main().', NULL);

-- SECCIÓN 4: Arreglos y punteros
INSERT INTO leccion (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES
                                                                                                                                  (16, 1, 'Arreglos unidimensionales', 4, 1, 'TEORIA', 'Los arreglos almacenan múltiples valores del mismo tipo. Declaración: tipo nombre[tamaño]; Acceso por índice (inicia en 0).', NULL, NULL),
                                                                                                                                  (17, 1, 'Trabajando con arreglos', 4, 2, 'VIDEO', '{"titulo": "Arreglos y Punteros en C++ - Estructuras de Datos Básicas", "enlace": "https://www.youtube.com/watch?v=Z_hPJ_EhceI"}', NULL, NULL),
                                                                                                                                  (18, 1, 'Pregunta sobre el video', 4, 3, 'PREGUNTA', NULL, NULL, NULL), -- Pregunta en la lección 18
                                                                                                                                  (19, 1, 'Punteros básicos', 4, 4, 'TEORIA', 'Los punteros almacenan direcciones de memoria. Operadores: & (dirección de) y * (desreferencia).', NULL, NULL),
                                                                                                                                  (20, 1, 'Ejercicio práctico', 4, 5, 'PRACTICA', 'Ejercicio: Crea un programa que pida 5 números al usuario, los almacene en un arreglo, luego encuentre y muestre el número mayor usando punteros.', 'Solución: Uso de puntero int* ptr = numeros; para recorrer y encontrar el mayor.', NULL);


-- 5. PRUEBAS (QUIZZES) DEL CURSO
-- ------------------------------------
-- IDs de Pruebas: 101, 102, 103, 104 (Seccionales) y 105 (Final)
INSERT INTO prueba (id, curso_id, titulo, tipo, numero_seccion) VALUES
                                                                    (101, 1, 'Quiz Sección 1: Fundamentos', 'SECCIONAL', 1),
                                                                    (102, 1, 'Quiz Sección 2: Control de flujo', 'SECCIONAL', 2),
                                                                    (103, 1, 'Quiz Sección 3: Funciones y estructuras', 'SECCIONAL', 3),
                                                                    (104, 1, 'Quiz Sección 4: Arreglos y punteros', 'SECCIONAL', 4),
                                                                    (105, 1, 'Evaluación Final C++', 'FINAL', NULL);

-- Lecciones asociadas a los Quizzes (Tipo QUIZ)
INSERT INTO leccion (id, curso_id, titulo, numero_seccion, numero_orden, tipo_contenido, contenido_html, solucion, prueba_id) VALUES
                                                                                                                                  (21, 1, 'Quiz Final de Sección 1', 1, 6, 'QUIZ', 'Realiza el quiz para desbloquear la siguiente sección.', NULL, 101),
                                                                                                                                  (22, 1, 'Quiz Final de Sección 2', 2, 6, 'QUIZ', 'Realiza el quiz para desbloquear la siguiente sección.', NULL, 102),
                                                                                                                                  (23, 1, 'Quiz Final de Sección 3', 3, 6, 'QUIZ', 'Realiza el quiz para desbloquear la siguiente sección.', NULL, 103),
                                                                                                                                  (24, 1, 'Quiz Final de Sección 4', 4, 6, 'QUIZ', 'Realiza el quiz para el examen final.', NULL, 104),
                                                                                                                                  (25, 1, 'Evaluación Final del Curso C++', 4, 7, 'QUIZ_FINAL', 'Realiza el examen final para completar el curso.', NULL, 105);

-- 6. PREGUNTAS Y OPCIONES (LECCIONES DE TIPO PREGUNTA)
-- ------------------------------------------------------------------------

-- Lección 3: Pregunta sobre el video (ID 3)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (1, '¿Cuál es la función principal que debe estar presente en todo programa C++?', 3, NULL);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (10, 1, 'start()', FALSE),
                                                             (11, 1, 'main()', TRUE), -- Respuesta correcta para la prueba funcional (Lec 3)
                                                             (12, 1, 'begin()', FALSE),
                                                             (13, 1, 'run()', FALSE);

-- Lección 8: Pregunta sobre el video (ID 8)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (2, '¿Qué bucle se ejecuta al menos una vez, incluso si la condición es falsa inicialmente?', 8, NULL);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (14, 2, 'for', FALSE),
                                                             (15, 2, 'while', FALSE),
                                                             (16, 2, 'do-while', TRUE),
                                                             (17, 2, 'if-else', FALSE);

-- Lección 13: Pregunta sobre el video (ID 13)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (3, '¿Qué palabra clave se usa para devolver un valor desde una función?', 13, NULL);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (18, 3, 'break', FALSE),
                                                             (19, 3, 'return', TRUE),
                                                             (20, 3, 'exit', FALSE),
                                                             (21, 3, 'end', FALSE);

-- Lección 18: Pregunta sobre el video (ID 18)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (4, '¿En qué índice comienzan los arreglos en C++?', 18, NULL);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (22, 4, '0', TRUE),
                                                             (23, 4, '1', FALSE),
                                                             (24, 4, 'Depende del compilador', FALSE),
                                                             (25, 4, 'Se puede configurar', FALSE);


-- 7. PREGUNTAS Y OPCIONES (QUIZZES SECCIONALES Y FINAL)
-- ------------------------------------------------------------------------

-- Q1 (ID 5): ¿En qué año fue creado C++?
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (5, '¿En qué año fue creado C++?', NULL, 101);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (26, 5, '1985', FALSE), (27, 5, '1979', TRUE), (28, 5, '1995', FALSE), (29, 5, '2000', FALSE);

-- Q2 (ID 6): ¿Qué línea de código se usa para incluir la biblioteca de entrada/salida?
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (6, '¿Qué línea de código se usa para incluir la biblioteca de entrada/salida?', NULL, 101);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (30, 6, '#include <iostream>', TRUE), (31, 6, 'import iostream', FALSE), (32, 6, 'using iostream', FALSE), (33, 6, 'include <iostream>', FALSE);

-- Q3 (ID 7): ¿Qué función es el punto de entrada de un programa C++?
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (7, '¿Qué función es el punto de entrada de un programa C++?', NULL, 101);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (34, 7, 'start()', FALSE), (35, 7, 'init()', FALSE), (36, 7, 'main()', TRUE), (37, 7, 'begin()', FALSE);

-- Q4 (ID 8): ¿Qué símbolo se usa para comentarios de una sola línea?
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (8, '¿Qué símbolo se usa para comentarios de una sola línea?', NULL, 101);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (38, 8, '//', TRUE), (39, 8, '/*', FALSE), (40, 8, '#', FALSE), (41, 8, '--', FALSE);

-- Q5 (ID 9): ¿Qué comando muestra texto en la consola?
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (9, '¿Qué comando muestra texto en la consola?', NULL, 101);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (42, 9, 'print', FALSE), (43, 9, 'cout', TRUE), (44, 9, 'display', FALSE), (45, 9, 'write', FALSE);

-- QUIZ SECCIÓN 2 (ID Prueba 102)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (10, '¿Qué palabra clave se usa para condiciones alternativas al if?', NULL, 102);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (46, 10, 'else', TRUE), (47, 10, 'elseif', FALSE), (48, 10, 'otherwise', FALSE), (49, 10, 'alternative', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (11, '¿Cuál es la estructura correcta de un bucle for?', NULL, 102);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (50, 11, 'for (condición; inicio; incremento)', FALSE), (51, 11, 'for (inicio; condición; incremento)', TRUE), (52, 11, 'for (inicio; incremento; condición)', FALSE), (53, 11, 'for (condición; incremento; inicio)', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (12, '¿Qué operador lógico representa "AND"?', NULL, 102);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (54, 12, '||', FALSE), (55, 12, '&&', TRUE), (56, 12, '!', FALSE), (57, 12, '&', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (13, '¿Cuántas veces se ejecuta este bucle: for(int i=1; i<=3; i++)?', NULL, 102);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (58, 13, '1', FALSE), (59, 13, '2', FALSE), (60, 13, '3', TRUE), (61, 13, '4', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (14, '¿Qué bucle garantiza al menos una ejecución?', NULL, 102);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (62, 14, 'for', FALSE), (63, 14, 'while', FALSE), (64, 14, 'do-while', TRUE), (65, 14, 'if', FALSE);


-- QUIZ SECCIÓN 3 (ID Prueba 103)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (15, '¿Qué palabra clave define una estructura?', NULL, 103);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (66, 15, 'struct', TRUE), (67, 15, 'class', FALSE), (68, 15, 'object', FALSE), (69, 15, 'type', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (16, '¿Dónde es visible una variable local?', NULL, 103);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (70, 16, 'En todo el programa', FALSE), (71, 16, 'Solo dentro de la función o bloque donde fue declarada', TRUE), (72, 16, 'Solo en el archivo actual', FALSE), (73, 16, 'En cualquier función que la llame', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (17, '¿Cuál es el tipo de retorno de una función que no devuelve ningún valor?', NULL, 103);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (74, 17, 'int', FALSE), (75, 17, 'null', FALSE), (76, 17, 'void', TRUE), (77, 17, 'none', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (18, '¿Qué es un prototipo de función?', NULL, 103);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (78, 18, 'La definición completa de la función', FALSE), (79, 18, 'Una llamada a la función', FALSE), (80, 18, 'Una declaración de la función antes de su uso', TRUE), (81, 18, 'El valor que devuelve la función', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (19, '¿Cómo se accede a un miembro de una estructura llamada "Punto" con un miembro "x"?', NULL, 103);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (82, 19, 'Punto.x', TRUE), (83, 19, 'Punto->x', FALSE), (84, 19, 'Punto::x', FALSE), (85, 19, 'Punto[x]', FALSE);


-- QUIZ SECCIÓN 4 (ID Prueba 104)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (20, '¿Cuál es el índice del quinto elemento de un arreglo?', NULL, 104);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (86, 20, '5', FALSE), (87, 20, '4', TRUE), (88, 20, '1', FALSE), (89, 20, '0', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (21, '¿Qué almacena una variable de tipo puntero?', NULL, 104);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (90, 21, 'Un valor entero', FALSE), (91, 21, 'La dirección de memoria de otra variable', TRUE), (92, 21, 'Una cadena de texto', FALSE), (93, 21, 'Una referencia a un objeto', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (22, '¿Qué operador se usa para obtener la dirección de memoria de una variable?', NULL, 104);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (94, 22, '*', FALSE), (95, 22, '&', TRUE), (96, 22, '->', FALSE), (97, 22, '.', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (23, '¿Cómo se declara un arreglo de 10 enteros?', NULL, 104);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (98, 23, 'int[10] array;', FALSE), (99, 23, 'array int[10];', FALSE), (100, 23, 'int array[10];', TRUE), (101, 23, 'int array = new int[10];', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (24, '¿Cuál es el nombre técnico del operador * cuando se usa con un puntero?', NULL, 104);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (102, 24, 'Operador de asignación', FALSE), (103, 24, 'Operador de dirección', FALSE), (104, 24, 'Operador de desreferencia', TRUE), (105, 24, 'Operador de multiplicación', FALSE);


-- QUIZ FINAL (ID Prueba 105)
INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (25, 'Pregunta 1 Final: ¿Cuál es el significado de "Multiparadigma" en C++?', NULL, 105);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (106, 25, 'Solo orientado a objetos', FALSE), (107, 25, 'Permite diferentes estilos de programación', TRUE), (108, 25, 'Funciona en múltiples sistemas operativos', FALSE), (109, 25, 'Puede manejar múltiples hilos', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (26, 'Pregunta 2 Final: ¿Qué operador se usa para la entrada de datos?', NULL, 105);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (110, 26, 'cout <<', FALSE), (111, 26, 'cin >>', TRUE), (112, 26, 'gets()', FALSE), (113, 26, 'input()', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (27, 'Pregunta 3 Final: ¿Cuál es el rango de valores de una variable `int`?', NULL, 105);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (114, 27, 'Depende del sistema', TRUE), (115, 27, 'De 0 a 255', FALSE), (116, 27, 'De -32768 a 32767', FALSE), (117, 27, 'De 0 a 65535', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (28, 'Pregunta 4 Final: ¿Qué tipo de dato se usa para almacenar números con decimales?', NULL, 105);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (118, 28, 'int', FALSE), (119, 28, 'char', FALSE), (120, 28, 'float o double', TRUE), (121, 28, 'bool', FALSE);

INSERT INTO pregunta (id, enunciado, leccion_id, prueba_id) VALUES (29, 'Pregunta 5 Final: ¿Qué hace la función `endl`?', NULL, 105);
INSERT INTO opcion (id, pregunta_id, texto, es_correcta) VALUES
                                                             (122, 29, 'Finaliza el programa', FALSE), (123, 29, 'Inserta una nueva línea y vacía el buffer', TRUE), (124, 29, 'Borra la consola', FALSE), (125, 29, 'Es un comentario', FALSE);