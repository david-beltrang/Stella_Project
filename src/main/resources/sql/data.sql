-- ============================================
-- DATOS DE EJEMPLO PARA STELLA
-- Curso completo de Introducción a Java
-- ============================================

-- 1. USUARIOS Y ESTADÍSTICAS
-- ============================================

-- Usuario de prueba principal (ID 1)
-- CORRECCIÓN: Se ajusta el formato para que el ID sea numérico (1) y coincida con las 6 columnas.
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo_usuario) VALUES
('JuanPa', 'test@estudio.com', 'Juan Pérez', 'pass123', 'ESTUDIANTE');

-- Stats del usuario (usa usuario_id = 1)
INSERT INTO "usuario_stats" (usuario_id, pescaditos, objetivo_sesiones, racha_dias, tiempo_total_estudio_segundos) VALUES
(1, 100, 3, 5, 3600);

-- Usuario de prueba para el foro (Obtendrá ID 2 si la tabla usa AUTO_INCREMENT, o se le asigna 2 si se controla externamente)
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo_usuario)
VALUES ('testestudio1', 'test@estudio1.com', 'Usuario Estudio1', 'pass123', 'ESTUDIANTE');

-- ============================================
-- 2. CURSO
-- ============================================

-- CURSO: Introducción a Java (ID 1)
INSERT INTO "curso" (id, titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones) VALUES
(1, 'Introducción a Java', 'Aprende los fundamentos de programación en Java desde cero', 'INTERMEDIO', 'Programación', 240, 3);

-- ============================================
-- 3. SECCIONES, LECCIONES Y EJERCICIOS
-- ============================================

-- SECCIÓN 1: Fundamentos de Java (ID 1)
INSERT INTO "seccion" (id, curso_id, titulo, numero_orden) VALUES
(1, 1, 'Fundamentos de Java', 1);

-- Lección 1.1: Introducción a Java (Teoría con HTML) (ID 1)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(1, 1, '¿Qué es Java?', 1, 'TEORIA',
'Java es un lenguaje de programación orientado a objetos, robusto y multiplataforma.',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        .highlight { background: #ffeb3b; padding: 2px 5px; }
        .code { background: #263238; color: #aed581; padding: 10px; border-radius: 5px; font-family: monospace; }
    </style>
</head>
<body>
    <h1>¿Qué es Java?</h1>
    <p>Java es un lenguaje de programación <span class="highlight">orientado a objetos</span>, creado por Sun Microsystems en 1995.</p>
    <h2>Características principales:</h2>
    <ul>
        <li><strong>Multiplataforma:</strong> "Write Once, Run Anywhere"</li>
        <li><strong>Orientado a Objetos:</strong> Todo es un objeto</li>
        <li><strong>Robusto:</strong> Manejo automático de memoria</li>
        <li><strong>Seguro:</strong> Sin punteros, sandbox security</li>
    </ul>
    <div class="code">
        public class HolaMundo {<br>
        &nbsp;&nbsp;public static void main(String[] args) {<br>
        &nbsp;&nbsp;&nbsp;&nbsp;System.out.println("¡Hola, Mundo!");<br>
        &nbsp;&nbsp;}<br>
        }
    </div>
</body>
</html>');

-- Lección 1.2: Instalación de Java (Video) (ID 2)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido) VALUES
(2, 1, 'Instalando Java JDK', 2, 'VIDEO',
'https://www.youtube.com/watch?v=QekeJBShCy4',
'En este video aprenderás a instalar Java JDK en tu sistema operativo.');

-- Lección 1.3: Primer programa (Ejercicio) (ID 3)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(3, 1, 'Tu primer programa en Java', 3, 'PRACTICA',
'Escribe un programa que imprima tu nombre en la consola.',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h2 { color: #0066cc; }
        .task { background: #e3f2fd; padding: 15px; border-left: 4px solid #2196f3; margin: 20px 0; }
        pre { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h2>¡Escribe tu primer programa!</h2>
    <div class="task">
        <strong>Tarea:</strong> Crea una clase llamada Saludo que imprima "Hola, [tu nombre]" en la consola.
    </div>
    <h3>Ejemplo de salida esperada:</h3>
    <pre>Hola, Juan</pre>
</body>
</html>');

-- Ejercicio 1 para la lección 3
INSERT INTO "ejercicio" (id, leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(1, 3, 'Programa Hola Mundo',
'Completa el código para que imprima "Hola, Mundo" en la consola.',
'public class HolaMundo {
    public static void main(String[] args) {
        // Escribe aquí tu código
    }
}',
'Hola, Mundo',
10, 1);

-- SECCIÓN 2: Variables y Tipos de Datos (ID 2)
INSERT INTO "seccion" (id, curso_id, titulo, numero_orden) VALUES
(2, 1, 'Variables y Tipos de Datos', 2);

-- Lección 2.1: Variables en Java (Teoría con HTML) (ID 4)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(4, 2, 'Variables en Java', 1, 'TEORIA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; }
        th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }
        th { background: #2196f3; color: white; }
        .code { background: #263238; color: #aed581; padding: 10px; border-radius: 5px; margin: 10px 0; }
    </style>
</head>
<body>
    <h1>Variables en Java</h1>
    <p>Una variable es un contenedor para almacenar datos. En Java, necesitas declarar el tipo de dato.</p>

    <h2>Tipos de datos primitivos:</h2>
    <table>
        <tr><th>Tipo</th><th>Tamaño</th><th>Ejemplo</th></tr>
        <tr><td>int</td><td>4 bytes</td><td>int edad = 25;</td></tr>
        <tr><td>double</td><td>8 bytes</td><td>double precio = 19.99;</td></tr>
        <tr><td>boolean</td><td>1 bit</td><td>boolean activo = true;</td></tr>
        <tr><td>char</td><td>2 bytes</td><td>char letra = ''A'';</td></tr>
        <tr><td>String</td><td>variable</td><td>String nombre = "Juan";</td></tr>
    </table>

    <h2>Ejemplo:</h2>
    <div class="code">
        int edad = 20;<br>
        String nombre = "María";<br>
        double promedio = 8.5;<br>
        System.out.println(nombre + " tiene " + edad + " años");
    </div>
</body>
</html>');

-- Lección 2.2: Operaciones con variables (Ejercicio) (ID 5)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(5, 2, 'Operaciones con variables', 2, 'PRACTICA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h2 { color: #0066cc; }
        .task { background: #fff9c4; padding: 15px; border-left: 4px solid #fbc02d; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Practica con variables</h2>
    <div class="task">
        <strong>Ejercicio:</strong> Crea un programa que calcule el área de un rectángulo.
        <br>Declara dos variables: base = 5 y altura = 10
        <br>Calcula el área e imprímela.
    </div>
</body>
</html>');

-- Ejercicio 2 para la lección 5
INSERT INTO "ejercicio" (id, leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(2, 5, 'Calcular área de rectángulo',
'Calcula el área de un rectángulo con base 5 y altura 10. Imprime solo el número.',
'public class AreaRectangulo {
    public static void main(String[] args) {
        int base = 5;
        int altura = 10;
        // Calcula el área e imprímela
    }
}',
'50',
15, 1);

-- SECCIÓN 3: Control de Flujo (ID 3)
INSERT INTO "seccion" (id, curso_id, titulo, numero_orden) VALUES
(3, 1, 'Control de Flujo', 3);

-- Lección 3.1: Condicionales if-else (ID 6)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(6, 3, 'Condicionales if-else', 1, 'TEORIA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        .code { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; margin: 15px 0; font-family: monospace; }
        .note { background: #e8f5e9; padding: 15px; border-left: 4px solid #4caf50; margin: 20px 0; }
    </style>
</head>
<body>
    <h1>Estructuras Condicionales</h1>
    <p>Las estructuras condicionales permiten ejecutar código basado en condiciones.</p>

    <h2>Sintaxis básica:</h2>
    <div class="code">
        if (condicion) {<br>
        &nbsp;&nbsp;// código si es verdadero<br>
        } else {<br>
        &nbsp;&nbsp;// código si es falso<br>
        }
    </div>

    <div class="note">
        <strong>Ejemplo práctico:</strong><br>
        Determinar si un número es par o impar.
    </div>

    <div class="code">
        int numero = 10;<br>
        if (numero % 2 == 0) {<br>
        &nbsp;&nbsp;System.out.println("Es par");<br>
        } else {<br>
        &nbsp;&nbsp;System.out.println("Es impar");<br>
        }
    </div>
</body>
</html>');

-- Lección 3.2: Ejercicio de condicionales (ID 7)
INSERT INTO "leccion" (id, seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(7, 3, 'Practica con condicionales', 2, 'PRACTICA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        .task { background: #ffebee; padding: 15px; border-left: 4px solid #f44336; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Ejercicio: Verificar edad</h2>
    <div class="task">
        Crea un programa que determine si una persona es mayor de edad (18 años o más).
        <br>Si edad >= 18, imprime "Mayor de edad"
        <br>Si no, imprime "Menor de edad"
    </div>
</body>
</html>');

-- Ejercicio 3 para la lección 7
INSERT INTO "ejercicio" (id, leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(3, 7, 'Verificar mayoría de edad',
'Escribe un programa que determine si edad = 20 es mayor de edad. Imprime el resultado.',
'public class VerificarEdad {
    public static void main(String[] args) {
        int edad = 20;
        // Tu código aquí
    }
}',
'Mayor de edad',
20, 1);

-- ============================================
-- 4. QUIZ FINAL (PRUEBA ID 1)
-- ============================================
INSERT INTO "prueba" (id, curso_id, seccion_id, titulo, tipo) VALUES
(1, 1, 3, 'Quiz Final - Fundamentos de Java', 'FINAL');

-- Pregunta 1
INSERT INTO "pregunta" (id, enunciado, prueba_id) VALUES (1, '¿Cuál es la palabra clave para declarar una variable entera en Java?', 1);
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES
(1, 1, 'integer', false), (2, 1, 'int', true), (3, 1, 'Integer', false), (4, 1, 'num', false);

-- Pregunta 2
INSERT INTO "pregunta" (id, enunciado, prueba_id) VALUES (2, '¿Qué imprime System.out.println("Hola" + " " + "Mundo");?', 1);
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES
(5, 2, 'Hola Mundo', true), (6, 2, 'Hola+Mundo', false), (7, 2, 'HolaMundo', false), (8, 2, 'Error de compilación', false);

-- Pregunta 3
INSERT INTO "pregunta" (id, enunciado, prueba_id) VALUES (3, '¿Cuál es el resultado de 10 % 3 en Java?', 1);
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES
(9, 3, '3', false), (10, 3, '1', true), (11, 3, '0', false), (12, 3, '10', false);

-- Pregunta 4
INSERT INTO "pregunta" (id, enunciado, prueba_id) VALUES (4, '¿Qué estructura se usa para tomar decisiones en Java?', 1);
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES
(13, 4, 'loop', false), (14, 4, 'if-else', true), (15, 4, 'switch', false), (16, 4, 'while', false);

-- Pregunta 5
INSERT INTO "pregunta" (id, enunciado, prueba_id) VALUES (5, '¿Cuál es el método principal en una aplicación Java?', 1);
INSERT INTO "opcion" (id, pregunta_id, texto, es_correcta) VALUES
(17, 5, 'start()', false), (18, 5, 'main()', true), (19, 5, 'run()', false), (20, 5, 'init()', false);

-- ============================================
-- 5. ITEMS DE LA TIENDA Y STELLA
-- ============================================

-- ITEMS DE LA TIENDA Y STELLA CON EL OBJETO

-- 👕 HOODIE GITHUB (ID 1)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Hoodie GitHub','Sudadera con capucha inspirada en GitHub, ideal para desarrolladores que quieren vestir con estilo tech.',
     1150, '/Image/TiendaStella/hoodieGithub.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (1, '/Image/stellas/stellaHoodieGithub.png');

-- 👕 CAMISETA APPLE (ID 2)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Apple','Camiseta minimalista con logo de Apple, ideal para los amantes de la tecnología y el diseño limpio.',
     950, '/Image/TiendaStella/camisetaApple.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (2, '/Image/stellas/stellaCamisetaApple.png');

-- 🏫 CAMISETA FIS (ID 3)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta FIS','Camiseta deportiva con diseño institucional FIS, ideal para eventos y actividades académicas.',
     800, '/Image/TiendaStella/camisetaFIS.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (3, '/Image/stellas/stellaFIS.png');

-- 🇨🇴 CAMISETA COLOMBIA (ID 4)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Colombia','Camiseta de la selección Colombia, fabricada con tela transpirable y cómoda para mostrar el orgullo nacional.',
     1050, '/Image/TiendaStella/camisetaColombia.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (4, '/Image/stellas/stellaCamisetaColombia.png');

-- 🧢 BALACA DISNEY (ID 5)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Balaca Disney','Balaca con diseño inspirado en personajes clásicos de Disney, cómoda y divertida para toda ocasión.',
     450, '/Image/TiendaStella/balacaDisney.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (5, '/Image/stellas/stellaDisney.png');

-- 🍹 JUGO HIT (ID 6)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Jugo Hit','Bebida refrescante de frutas naturales, ideal para acompañar tus comidas o hidratarte en cualquier momento.',
     350, '/Image/TiendaStella/jugoHit.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (6, '/Image/stellas/stellaJugoHit.png');

-- ⚽ CAMISETA MILLOS (ID 7)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Millos','Camiseta oficial del equipo Millonarios, perfecta para hinchas que quieren lucir sus colores con pasión.',
     1000, '/Image/TiendaStella/camisetaMillos.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (7, '/Image/stellas/stellaCamisetaMillos.png');

-- 👒 SOMBRERO VUELTEAO (ID 8)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Sombrero Vuelteao','Sombrero típico colombiano elaborado con caña flecha, símbolo de tradición y estilo.',
     900, '/Image/TiendaStella/sombreroVuelteao.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (8, '/Image/stellas/stellaSombreroVuelteao.png');

-- 👚 CROPTOP (ID 9)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Croptop','Croptop moderno y cómodo, ideal para climas cálidos y estilos urbanos.',
     700, '/Image/TiendaStella/croptop.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (9, '/Image/stellas/stellaCroptop.png');

-- 🧣 BUFANDA (ID 10)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Bufanda','Bufanda tejida con materiales suaves y cálidos, perfecta para protegerte del frío con estilo.',
     850, '/Image/TiendaStella/bufanda.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (10, '/Image/stellas/stellaBufanda.png');

-- 🕶️ GAFAS DE SOL (ID 11)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas de Sol','Gafas elegantes con protección UV400, perfectas para un look moderno y proteger tus ojos del sol.',
     600, '/Image/TiendaStella/gafasDeSol.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (11, '/Image/stellas/stellaGafas.png');

-- 🕶️ GAFAS DE SKI (ID 12)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas de Ski','Gafas diseñadas para nieve y deporte extremo, resistentes al viento y la neblina.',
     1100, '/Image/TiendaStella/gafasSki.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (12, '/Image/stellas/stellaSki.png');

-- 🧢 GORRA (ID 13)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra','Gorra clásica ajustable, con visera curva y diseño casual ideal para cualquier día.',
     500, '/Image/TiendaStella/gorra.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (13, '/Image/stellas/stellaGorra.png');

-- 🧢 GORRA ROJA (ID 14)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra Roja','Gorra de color rojo intenso, ajustable y cómoda, perfecta para destacar en cualquier ocasión.',
     520, '/Image/TiendaStella/gorraRoja.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (14, '/Image/stellas/stellaGorraRoja.png');

-- 🧢 GORRA ROSA (ID 15)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra Rosa','Gorra color rosa con diseño moderno, perfecta para un look casual y alegre.',
     520, '/Image/TiendaStella/gorraRosa.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (15, '/Image/stellas/stellaGorraRosa.png');

-- 7. COMPRA INICIAL del Usuario 1
INSERT INTO "usuario_item" (usuario_id, item_id) VALUES (1, 4);

-- ============================================
-- 6. DATOS DEL FORO
-- ============================================

-- Post 1 (Creado por Usuario 1)
INSERT INTO "post" (usuario_id, contenido_texto, fecha, etiqueta) VALUES
(1, '¿Alguien sabe cómo usar JavaFX para un foro?', '2025-11-07 18:00:00', 'JavaFX');

-- Comentario 1 y 2 (Creados por Usuario 2, que es el que se insertó sin ID al final de la sección 1 y tendrá ID 2)
INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(1, 2, 'Sí, usa FXML para la interfaz.', '2025-11-07 18:05:00');

INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(1, 2, 'También puedes agregar un ListView para los posts.', '2025-11-07 18:10:00');

-- Post 2 (Creado por Usuario 2)
INSERT INTO "post" (usuario_id, contenido_texto, fecha, etiqueta) VALUES
(2, '¿Alguien sabe cómo utilizar la memoria dinámica en c++?', '2025-11-07 18:20:00', 'C++');

-- Comentario 3 (Creado por Usuario 1)
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