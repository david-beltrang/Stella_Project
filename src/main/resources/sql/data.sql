-- ============================================
-- DATOS DE EJEMPLO PARA STELLA
-- Curso completo de Introducción a Java
-- ============================================

-- 1. USUARIOS Y ESTADÍSTICAS
-- Nota: La columna ID se omite en todos los INSERTs con AUTO_INCREMENT.

-- Usuario de prueba principal (ID será 1)
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo_usuario) VALUES
('JuanPa', 'test@estudio.com', 'Juan Pérez', 'pass123', 'ESTUDIANTE');

-- Stats del usuario (usa usuario_id = 1)
INSERT INTO "usuario_stats" (usuario_id, pescaditos, objetivo_sesiones, racha_dias, tiempo_total_estudio_segundos) VALUES
(1, 1000, 3, 5, 3600);

-- Usuario de prueba para el foro (ID será 2)
INSERT INTO "usuario" (username, correo, nombre, contrasena, tipo_usuario)
VALUES ('testestudio1', 'test@estudio1.com', 'Usuario Estudio1', 'pass123', 'ESTUDIANTE');

-- ============================================
-- 2. CURSO 1
-- ============================================

-- CURSO: Introducción a Java (ID será 1)
INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones) VALUES
('Introducción a Java', 'Aprende los fundamentos de programación en Java desde cero', 'INTERMEDIO', 'Programación', 240, 3);

-- ============================================
-- 3. SECCIONES, LECCIONES Y EJERCICIOS CURSO 1
-- ============================================

-- SECCIÓN 1: Fundamentos de Java (ID será 1)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(1, 'Fundamentos de Java', 1);

-- Lección 1.1: Introducción a Java (ID será 1)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(1, '¿Qué es Java?', 1, 'TEORIA',
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

-- Lección 1.2: Instalación de Java (ID será 2)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido) VALUES
(1, 'Instalando Java JDK', 2, 'VIDEO',
'https://youtu.be/3rvdHvo9t9A?si=j6ANAhCpdlOHJ2wu',
'En este video aprenderás a instalar Java JDK en tu sistema operativo.');

-- Lección 1.3: Primer programa (ID será 3)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(1, 'Tu primer programa en Java', 3, 'PRACTICA',
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

-- Ejercicio 1 para la lección 3 (ID será 1)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(3, 'Programa Hola Mundo',
'Completa el código para que imprima "Hola, Mundo" en la consola.',
'public class HolaMundo {
    public static void main(String[] args) {
        // Escribe aquí tu código
    }
}',
'Hola, Mundo',
10, 1);

-- Lección 1.4: Estructura de un programa Java (ID será 4)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(1, 'Estructura de un programa Java', 4, 'TEORIA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        .code { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; margin: 15px 0; font-family: monospace; }
        .section { background: #e3f2fd; padding: 15px; margin: 15px 0; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Estructura de un programa Java</h1>
    <p>Todo programa Java sigue una estructura básica que debes conocer.</p>

    <div class="section">
        <h3>Componentes principales:</h3>
        <ul>
            <li><strong>Clase:</strong> Todo código está dentro de una clase</li>
            <li><strong>Método main:</strong> Punto de entrada del programa</li>
            <li><strong>Instrucciones:</strong> El código que se ejecuta</li>
        </ul>
    </div>

    <h2>Ejemplo básico:</h2>
    <div class="code">
        public class MiPrograma {<br>
        &nbsp;&nbsp;public static void main(String[] args) {<br>
        &nbsp;&nbsp;&nbsp;&nbsp;System.out.println("¡Hola!");<br>
        &nbsp;&nbsp;}<br>
        }
    </div>
</body>
</html>');

-- SECCIÓN 2: Variables y Tipos de Datos (ID será 2)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(1, 'Variables y Tipos de Datos', 2);

-- Lección 2.1: Variables en Java (ID será 5)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(2, 'Variables en Java', 1, 'TEORIA',
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

-- Lección 2.2: Operaciones con variables (ID será 6)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(2, 'Operaciones con variables', 2, 'PRACTICA',
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

-- Ejercicio 2 para la lección 6 (ID será 2)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(6, 'Calcular área de rectángulo',
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

-- Lección 2.3: Operadores (ID será 7)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(2, 'Operadores en Java', 3, 'TEORIA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        .code { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; margin: 15px 0; font-family: monospace; }
    </style>
</head>
<body>
    <h1>Operadores en Java</h1>
    <p>Los operadores permiten realizar operaciones con variables y valores.</p>

    <h2>Tipos de operadores:</h2>
    <ul>
        <li>Aritméticos: +, -, *, /, %</li>
        <li>Comparación: ==, !=, <, >, <=, >=</li>
        <li>Lógicos: &&, ||, !</li>
    </ul>

    <div class="code">
        int a = 10, b = 5;<br>
        int sum = a + b;  // 15<br>
        boolean mayor = a > b;  // true
    </div>
</body>
</html>');

-- Lección 2.4: Conversión de tipos (ID será 8)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(2, 'Conversión de tipos', 4, 'TEORIA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        .code { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Conversión de Tipos (Casting)</h1>
    <p>Puedes convertir un tipo de dato a otro.</p>

    <h2>Ejemplos:</h2>
    <div class="code">
        int num = 10;<br>
        double decimal = num;  // Automático<br>
        int truncado = (int) 3.14;  // Explícito
    </div>
</body>
</html>');

-- SECCIÓN 3: Control de Flujo (ID será 3)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(1, 'Control de Flujo', 3);

-- Lección 3.1: Condicionales if-else (ID será 9)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(3, 'Condicionales if-else', 1, 'TEORIA',
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

-- Lección 3.2: Ejercicio de condicionales (ID será 10)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(3, 'Practica con condicionales', 2, 'PRACTICA',
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

-- Ejercicio 3 para la lección 10 (ID será 3)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(10, 'Verificar mayoría de edad',
'Escribe un programa que determine si edad = 20 es mayor de edad. Imprime el resultado.',
'public class VerificarEdad {
    public static void main(String[] args) {
        int edad = 20;
        // Tu código aquí
    }
}',
'Mayor de edad',
20, 1);

-- Lección 3.3: Bucles (ID será 11)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(3, 'Bucles while y for', 3, 'TEORIA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        h1 { color: #0066cc; }
        .code { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Bucles en Java</h1>
    <p>Los bucles permiten repetir código múltiples veces.</p>

    <h2>Bucle for:</h2>
    <div class="code">
        for (int i = 0; i < 5; i++) {<br>
        &nbsp;&nbsp;System.out.println(i);<br>
        }
    </div>

    <h2>Bucle while:</h2>
    <div class="code">
        int i = 0;<br>
        while (i < 5) {<br>
        &nbsp;&nbsp;System.out.println(i);<br>
        &nbsp;&nbsp;i++;<br>
        }
    </div>
</body>
</html>');

-- Lección 3.4: Ejercicio bucles (ID será 12)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(3, 'Ejercicio: Tabla de multiplicar', 4, 'PRACTICA',
'<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        .task { background: #ffebee; padding: 15px; border-left: 4px solid #f44336; }
    </style>
</head>
<body>
    <h2>Ejercicio: Tabla de Multiplicar</h2>
    <div class="task">
        Crea un programa que imprima la tabla de multiplicar del 5.
    </div>
</body>
</html>');

-- ============================================
-- 4. QUIZZES Y PREGUNTAS CURSO 1
-- ============================================

-- QUIZ SECCIÓN 1: Fundamentos de Java (PRUEBA ID será 1)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(1, 1, 'Quiz - Fundamentos de Java', 'FINAL');

-- Pregunta 1.1 (ID será 1)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué significa JDK?', 1);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(1, 'Java Development Kit', true), (1, 'Java Design Kit', false), (1, 'Java Data Kit', false), (1, 'Java Download Kit', false);

-- Pregunta 1.2 (ID será 2)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál es la extensión de un archivo de código fuente Java?', 1);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(2, '.java', true), (2, '.class', false), (2, '.jar', false), (2, '.jav', false);

-- Pregunta 1.3 (ID será 3)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué método es el punto de entrada de una aplicación Java?', 1);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(3, 'main()', true), (3, 'start()', false), (3, 'run()', false), (3, 'init()', false);

-- QUIZ SECCIÓN 2: Variables y Tipos de Datos (PRUEBA ID será 2)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(1, 2, 'Quiz - Variables y Tipos de Datos', 'FINAL');

-- Pregunta 2.1 (ID será 4)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál es la palabra clave para declarar una variable entera en Java?', 2);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(4, 'integer', false), (4, 'int', true), (4, 'Integer', false), (4, 'num', false);

-- Pregunta 2.2 (ID será 5)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué tipo de dato se usa para almacenar números decimales?', 2);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(5, 'int', false), (5, 'double', true), (5, 'boolean', false), (5, 'char', false);

-- Pregunta 2.3 (ID será 6)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál es el resultado de 10 % 3 en Java?', 2);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(6, '3', false), (6, '1', true), (6, '0', false), (6, '10', false);

-- QUIZ SECCIÓN 3: Control de Flujo (PRUEBA ID será 3)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(1, 3, 'Quiz - Control de Flujo', 'FINAL');

-- Pregunta 3.1 (ID será 7)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué estructura se usa para tomar decisiones en Java?', 3);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(7, 'loop', false), (7, 'if-else', true), (7, 'switch', false), (7, 'while', false);

-- Pregunta 3.2 (ID será 8)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál bucle se ejecuta al menos una vez?', 3);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(8, 'for', false), (8, 'while', false), (8, 'do-while', true), (8, 'foreach', false);

-- Pregunta 3.3 (ID será 9)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué imprime System.out.println("Hola" + " " + "Mundo");?', 3);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(9, 'Hola Mundo', true), (9, 'Hola+Mundo', false), (9, 'HolaMundo', false), (9, 'Error de compilación', false);


-- ============================================
-- 5. CURSO DE PYTHON (ID será 2)
-- ============================================
INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones) VALUES
('Python para Principiantes', 'Aprende los fundamentos de programación con Python, el lenguaje más versátil de la actualidad.', 'BASICO', 'Programación', 280, 3);

-- ============================================
-- 6. SECCIONES, LECCIONES Y EJERCICIOS CURSO 2
-- ============================================

-- SECCIÓN 4: Introducción y Configuración (ID será 4)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(2, 'Introducción y Configuración', 1);

-- Lección 4.1: ¿Qué es Python? (ID será 13)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(4, '¿Qué es Python y por qué usarlo?', 1, 'TEORIA',
'Python es un lenguaje de programación de alto nivel, interpretado y multipropósito.',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        .highlight { background: #d9ead3; padding: 2px 5px; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; font-family: monospace; }
    </style>
</head>
<body>
    <h1>El poder de Python</h1>
    <p>Python se caracteriza por su <span class="highlight">sintaxis limpia y legible</span>, ideal para principiantes.</p>
    <h2>Usos comunes:</h2>
    <ul>
        <li>Desarrollo Web (Django, Flask)</li>
        <li>Ciencia de Datos y Machine Learning</li>
        <li>Automatización de tareas (Scripting)</li>
    </ul>
    <div class="code">
        print("¡Código simple y legible!")
    </div>
</body>
</html>');

-- Lección 4.2: Instalación de Python (ID será 14)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido) VALUES
(4, 'Instalación de Python y Entorno', 2, 'VIDEO',
'https://youtu.be/xd_0RN2SyfI?si=TfSSWuDPOBptiIeA',
'Aprende a instalar la versión más reciente de Python y a configurar tu editor de código (VS Code).');

-- Lección 4.3: Hola Mundo (ID será 15)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(4, 'Tu primer programa: Hola Mundo', 3, 'PRACTICA',
'Utiliza la función print() para imprimir un mensaje en la consola.',
'<html>
<head>
    <style>
        .task { background: #e3f2fd; padding: 15px; border-left: 4px solid #2196f3; margin: 20px 0; }
        pre { background: #263238; color: #aed581; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h2>Tarea: Primer Print</h2>
    <div class="task">
        <strong>Tarea:</strong> Escribe el código necesario para que la salida de tu programa sea "Hola, Pythonista!".
    </div>
    <h3>Ejemplo de salida esperada:</h3>
    <pre>Hola, Pythonista!</pre>
</body>
</html>');

-- Ejercicio 4 para la lección 15 (ID será 4)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(15, 'Función Print',
'Completa el código para que imprima el saludo esperado.',
'# Escribe tu código aquí
',
'Hola, Pythonista!',
10, 1);

-- Lección 4.4: Entrada de Datos (NUEVA - ID será 22)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(4, 'Entrada de Datos con input()', 4, 'TEORIA',
'Aprende a solicitar datos al usuario usando la función input().',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Recibiendo Input</h1>
    <p>La función `input()` lee una línea de texto de la entrada estándar y siempre devuelve un string.</p>
    <div class="code">
        nombre = input("Dime tu nombre: ")<br>
        print("Hola, " + nombre)
    </div>
</body>
</html>');

-- SECCIÓN 5: Variables y Tipos de Datos (ID será 5)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(2, 'Variables y Tipos de Datos', 2);

-- Lección 5.1: Declaración de Variables (ID será 16)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(5, 'Declaración y Asignación de Variables', 1, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Variables en Python</h1>
    <p>En Python, no necesitas declarar el tipo de dato. La asignación se hace con el signo `=`. </p>
    <div class="code">
        nombre = "Ana"     # String<br>
        edad = 30          # Integer<br>
        es_alto = False    # Boolean
    </div>
</body>
</html>');

-- Lección 5.2: Tipos de Datos Primarios (ID será 17)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(5, 'Tipos de Datos (int, float, str, bool)', 2, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; background: white; }
        th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }
        th { background: #38761d; color: white; }
    </style>
</head>
<body>
    <h1>Tipos de Datos</h1>
    <p>Conocer los tipos de datos es crucial para realizar operaciones correctas.</p>
    <table>
        <tr><th>Tipo</th><th>Descripción</th><th>Ejemplo</th></tr>
        <tr><td>int</td><td>Números enteros</td><td>100, -5</td></tr>
        <tr><td>float</td><td>Números decimales</td><td>3.14, 19.99</td></tr>
        <tr><td>str</td><td>Cadenas de texto</td><td>"Hola", "Mundo"</td>
        </tr>
        <tr><td>bool</td><td>Valores lógicos</td><td>True, False</td></tr>
    </table>
</body>
</html>');

-- Lección 5.3: Operadores Aritméticos (ID será 18)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(5, 'Operadores Aritméticos y Lógicos', 3, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #fff9c4; padding: 15px; border-left: 4px solid #fbc02d; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Ejercicio con Operadores</h2>
    <div class="task">
        <strong>Ejercicio:</strong> Calcula el residuo de la división de 17 entre 3, y almacénalo en una variable llamada `residuo`.
        <br>Utiliza el operador de módulo (`%`). Imprime el resultado.
    </div>
</body>
</html>');

-- Ejercicio 5 para la lección 18 (ID será 5)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(18, 'Cálculo de Módulo',
'Calcula y almacena el residuo de 17 / 3. Imprime solo el número.',
'a = 17
b = 3
residuo = # Tu código aquí
print(residuo)
',
'2',
15, 1);

-- Lección 5.4: F-strings y Formato de Cadenas (NUEVA - ID será 23)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(5, 'Formato de Cadenas (f-strings)', 4, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Video: F-strings</h1>
    <p>Aprende la forma moderna y legible de combinar variables y texto en Python: las f-strings.</p>
    <div class="code">
        nombre = "Juan"<br>
        edad = 25<br>
        mensaje = f"Hola, soy {nombre} y tengo {edad} años."<br>
        print(mensaje)
    </div>
</body>
</html>');


-- SECCIÓN 6: Estructuras de Control de Flujo (ID será 6)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(2, 'Estructuras de Control de Flujo', 3);

-- Lección 6.1: Condicionales if-elif-else (ID será 19)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(6, 'Sentencias Condicionales (if, elif, else)', 1, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
        .note { background: #e8f5e9; padding: 15px; border-left: 4px solid #4caf50; margin: 20px 0; }
    </style>
</head>
<body>
    <h1>El uso de if/else</h1>
    <p>Python utiliza la <span class="highlight">indentación</span> (espacios en blanco) para delimitar bloques de código, no llaves.</p>
    <div class="code">
        edad = 20<br>
        if edad >= 18:<br>
        &nbsp;&nbsp;print("Mayor de edad")<br>
        else:<br>
        &nbsp;&nbsp;print("Menor de edad")
    </div>
    <div class="note">
        <strong>Recuerda:</strong> Usa `elif` para condiciones intermedias.
    </div>
</body>
</html>');

-- Lección 6.2: Bucles While (ID será 20)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(6, 'Bucles While (Repetición)', 2, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #38761d; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Bucle While</h1>
    <p>El bucle `while` se ejecuta mientras una condición sea verdadera.</p>
    <div class="code">
        contador = 0<br>
        while contador < 3:<br>
        &nbsp;&nbsp;print(f"Contando... {contador}")<br>
        &nbsp;&nbsp;contador += 1
    </div>
    <p>¡Cuidado con los bucles infinitos! Asegúrate de que la condición cambie.</p>
</body>
</html>');

-- Lección 6.3: Bucles For (ID será 21)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(6, 'Bucles For e Iterables', 3, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #ffebee; padding: 15px; border-left: 4px solid #f44336; }
    </style>
</head>
<body>
    <h2>Ejercicio: Bucle For</h2>
    <div class="task">
        Crea un bucle `for` que itere 4 veces y, en cada iteración, imprima la palabra "Paso".
    </div>
    <pre>Salida esperada: Paso\nPaso\nPaso\nPaso</pre>
</body>
</html>');

-- Ejercicio 6 para la lección 21 (ID será 6)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(21, 'Bucle simple con For',
'Usa un bucle for y la función range() para imprimir "Paso" cuatro veces.',
'for i in range(4):
    # Tu código aquí
',
'Paso
Paso
Paso
Paso',
20, 1);

-- Lección 6.4: Control de Bucles (NUEVA - ID será 24)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(6, 'Control de Bucles: break y continue', 4, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #fff3e0; padding: 15px; border-left: 4px solid #ff9800; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Ejercicio: Usando Break</h2>
    <div class="task">
        <strong>Tarea:</strong> Utiliza un bucle `while` y la sentencia `break` para detener el bucle cuando el contador llegue a 3.
    </div>
</body>
</html>');

-- Ejercicio 7 para la lección 24 (ID será 7)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(24, 'Detener Bucle con Break',
'Completa el código para que el bucle se detenga cuando i sea igual a 3. Imprime solo los números.',
'i = 0
while True:
    print(i)
    if i == 3:
        break
    i += 1
',
'0
1
2
3',
25, 1);

-- ============================================
-- 7. QUIZZES Y PREGUNTAS CURSO 2
-- ============================================

-- QUIZ SECCIÓN 4: Introducción (PRUEBA ID será 4)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(2, 4, 'Quiz - Introducción a Python', 'FINAL');

-- Pregunta 4.1 (ID será 10)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué extensión de archivo se utiliza comúnmente para los scripts de Python?', 4);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(10, '.py', true), (10, '.java', false), (10, '.html', false), (10, '.pys', false);

-- Pregunta 4.2 (ID será 11)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál es la función en Python para mostrar información en la consola?', 4);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(11, 'System.out.println', false), (11, 'display', false), (11, 'print()', true), (11, 'show()', false);

-- Pregunta 4.3 (ID será 25)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué significa el concepto de "indentación" en Python?', 4);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(12, 'El espaciado al inicio de una línea para definir bloques de código.', true),
(12, 'El comentario al final de una línea.', false),
(12, 'La forma de nombrar variables.', false),
(12, 'La forma de importar módulos.', false);

-- QUIZ SECCIÓN 5: Variables y Tipos (PRUEBA ID será 5)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(2, 5, 'Quiz - Variables y Tipos de Datos', 'FINAL');

-- Pregunta 5.1 (ID será 12)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué tipo de dato almacena los valores `True` o `False`?', 5);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(13, 'int', false), (13, 'float', false), (13, 'bool', true), (13, 'str', false);

-- Pregunta 5.2 (ID será 13)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué operador se utiliza para obtener el residuo de una división en Python?', 5);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(14, '/', false), (14, '**', false), (14, '%', true), (14, '//', false);

-- Pregunta 5.3 (ID será 26)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('En Python, ¿cuál es el tipo de dato que almacena texto?', 5);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(15, 'char', false),
(15, 'string', false),
(15, 'str', true),
(15, 'text', false);

-- QUIZ SECCIÓN 6: Control de Flujo (PRUEBA ID será 6)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(2, 6, 'Quiz - Control de Flujo', 'FINAL');

-- Pregunta 6.1 (ID será 14)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué palabra clave se utiliza en Python para la condición "si no" (similar a else if)?', 6);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(16, 'else if', false), (16, 'elseif', false), (16, 'elif', true), (16, 'or if', false);

-- Pregunta 6.2 (ID será 15)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué palabra clave se usa para detener la ejecución de un bucle inmediatamente?', 6);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(17, 'continue', false), (17, 'stop', false), (17, 'break', true), (17, 'exit', false);

-- Pregunta 6.3 (ID será 27)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál es el bucle que se utiliza principalmente para iterar sobre una secuencia (lista, tupla, etc.)?', 6);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(18, 'do-while', false),
(18, 'while', false),
(18, 'loop', false),
(18, 'for', true);

-- ============================================
-- 8. CURSO DE SQL (ID será 3)
-- ============================================
INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones) VALUES
('Fundamentos de Bases de Datos (SQL)', 'Aprende a diseñar, consultar y manipular datos usando el lenguaje SQL estándar.', 'AVANZADO', 'Bases de Datos', 320, 3);

-- ============================================
-- 9. SECCIONES, LECCIONES Y EJERCICIOS (CURSO 3)
-- ============================================

-- SECCIÓN 7: Introducción y Diseño de Bases de Datos (ID será 7)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(3, 'Introducción y Diseño de Bases de Datos', 1);

-- Lección 7.1: ¿Qué es una Base de Datos? (ID será 22)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(7, 'Conceptos de Bases de Datos Relacionales', 1, 'TEORIA',
'Una base de datos relacional almacena datos en tablas vinculadas por relaciones.',
'<html>
<head>
    <style>
        h1 { color: #8e44ad; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; font-family: monospace; }
    </style>
</head>
<body>
    <h1>Modelos de Datos</h1>
    <p>El modelo relacional usa tablas (entidades) y filas (registros) para organizar la información.</p>
    <h2>SQL (Structured Query Language)</h2>
    <p>Es el lenguaje estándar para comunicarse con bases de datos relacionales.</p>
</body>
</html>');

-- Lección 7.2: Claves (VIDEO) (ID será 23)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido) VALUES
(7, 'Claves Primarias y Foráneas', 2, 'VIDEO',
'https://youtu.be/tyyhIsDmVM0?si=4IbfMcsengo9KgBd',
'Video que explica cómo las claves Primarias (PK) identifican registros y las Foráneas (FK) crean relaciones.');

-- Lección 7.3: Instalación de SGBD (ID será 24)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(7, 'Instalación de un SGBD (SQLite/MySQL)', 3, 'PRACTICA',
'Configura tu entorno para poder ejecutar consultas SQL.',
'<html>
<head>
    <style>
        .task { background: #e3f2fd; padding: 15px; border-left: 4px solid #2196f3; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Tarea de Configuración</h2>
    <div class="task"> -- Corregido: comillas dobles
        <strong>Tarea:</strong> Instala SQLite Studio o MySQL Workbench y crea una base de datos de prueba llamada `miscursos`.
    </div>
</body>
</html>');

-- Lección 7.4: Creación de Tablas (DDL) (ID será 31) <--- LECCIÓN AÑADIDA
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(7, 'Creación de Tablas (DDL)', 4, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #8e44ad; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; font-family: monospace; }
        .highlight { background: #d0e0e3; padding: 2px 5px; }
    </style>
</head>
<body>
    <h1>Data Definition Language (DDL)</h1>
    <p>DDL se usa para definir o modificar la estructura de la base de datos. La sentencia principal es <strong>CREATE TABLE</strong>.</p>
    <h2>Sintaxis básica:</h2>
    <div class="code"> -- Corregido: comillas dobles
        CREATE TABLE Productos (<br>
        &nbsp;&nbsp;id INT PRIMARY KEY,<br>
        &nbsp;&nbsp;nombre VARCHAR(255) NOT NULL,<br>
        &nbsp;&nbsp;precio DECIMAL(10, 2)<br>
        );
    </div>
    <p>Las <span class="highlight">restricciones</span> como PRIMARY KEY y NOT NULL aseguran la integridad de los datos.</p>
</body>
</html>');


-- SECCIÓN 8: Consultas Fundamentales (DML) (ID será 8)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(3, 'Consultas Fundamentales (SELECT y WHERE)', 2);

-- Lección 8.1: SELECT Básico (ID será 25)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html)
VALUES
(8, 'Recuperación de Datos: La sentencia SELECT', 1, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #8e44ad; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>SELECT Básico</h1>
    <p>La sentencia SELECT es la más usada. Permite elegir columnas o seleccionar todos los datos (*).</p>
    <div class="code"> -- Corregido: comillas dobles
        -- Seleccionar todas las columnas y filas de la tabla ''alumnos''<br>
        SELECT * FROM alumnos;
    </div>
    <div class="code"> -- Corregido: comillas dobles
        -- Seleccionar solo nombre y email<br>
        SELECT nombre, email FROM alumnos;
    </div>
</body>
</html>');

-- Lección 8.2: Filtrado con WHERE (ID será 26)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(8, 'Filtrado de Datos: Cláusula WHERE', 2, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #fff9c4; padding: 15px; border-left: 4px solid #fbc02d; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Práctica con WHERE</h2>
    <div class="task"> -- Corregido: comillas dobles
        <strong>Ejercicio:</strong> Escribe una consulta para obtener los nombres de todos los productos cuyo precio sea mayor a 50.
    </div>
    <pre>SELECT nombre FROM Productos WHERE precio > 50;</pre>
</body>
</html>');

-- Ejercicio 7 para la lección 26 (ID será 7)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(26, 'Filtrado por Precio',
'Escribe la consulta SQL para seleccionar el campo ''nombre'' de la tabla ''Productos'' donde el precio sea mayor a 100.',
'SELECT nombre FROM Productos WHERE', -- El usuario debe completar la condición
'SELECT nombre FROM Productos WHERE precio > 100',
20, 1);

-- Lección 8.3: ORDER BY y LIMIT (ID será 27)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(8, 'Ordenamiento y Limitación (ORDER BY, LIMIT)', 3, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #8e44ad; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Controlando la Salida</h1>
    <p>La cláusula ORDER BY ordena los resultados (ASC por defecto, o DESC para descendente). LIMIT restringe el número de filas.</p>
    <div class="code"> -- Corregido: comillas dobles
        -- 5 productos más caros<br>
        SELECT nombre, precio FROM Productos ORDER BY precio DESC LIMIT 5;
    </div>
</body>
</html>');

-- Lección 8.4: Funciones de Agregación y Agrupamiento (ID será 32) <--- LECCIÓN AÑADIDA
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(8, 'Funciones de Agregación y GROUP BY', 4, 'PRACTICA',
'Aprende a usar COUNT, SUM, AVG y a agrupar resultados con GROUP BY.',
'<html>
<head>
    <style>
        .task { background: #d4e6f1; padding: 15px; border-left: 4px solid #3498db; margin: 20px 0; }
        pre { background: #2c3e50; color: #ecf0f1; padding: 15px; border-radius: 5px; }
    </style>
</head>
<body>
    <h2>Ejercicio: Contar Registros</h2>
    <div class="task"> -- Corregido: comillas dobles
        <strong>Tarea:</strong> Escribe la consulta SQL para contar cuántos registros hay en la tabla `Clientes`.
    </div>
    <pre>Salida esperada (ejemplo): 15</pre>
</body>
</html>');

-- Ejercicio 9 para la lección 32 (ID será 9)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(32, 'Contar Clientes',
'Utiliza la función COUNT para obtener el número total de filas en la tabla ''Clientes''.',
'SELECT ',
'SELECT COUNT(*) FROM Clientes',
25, 1);

-- SECCIÓN 9: Manipulación de Datos (DML) (ID será 9)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(3, 'Manipulación de Datos (INSERT, UPDATE, DELETE)', 3);

-- Lección 9.1: Inserción de Datos (ID será 28)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(9, 'Inserción: La sentencia INSERT', 1, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #8e44ad; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>INSERT INTO</h1>
    <p>Para agregar nuevos registros a una tabla.</p>
    <div class="code"> -- Corregido: comillas dobles
        INSERT INTO Clientes (nombre, email) VALUES<br>
        (''Carlos'', ''carlos@ejemplo.com'');
    </div>
</body>
</html>');

-- Lección 9.2: Actualización (ID será 29)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(9, 'Actualización: La sentencia UPDATE', 2, 'TEORIA',
'<html>
<head>
    <style>
        .warning { background: #ffeb3b; padding: 15px; border-left: 4px solid #fbc02d; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>VIDEO: Cuidado con UPDATE y DELETE</h2>
    <p>La sentencia UPDATE modifica datos existentes. ¡Siempre usa WHERE!</p>
    <div class="warning"> -- Corregido: comillas dobles
        <strong>ADVERTENCIA:</strong> Si omites la cláusula WHERE, ¡actualizarás TODOS los registros de la tabla!
    </div>
</body>
</html>');

-- Lección 9.3: Eliminación (ID será 30)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(9, 'Eliminación: La sentencia DELETE', 3, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #ffebee; padding: 15px; border-left: 4px solid #f44336; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Práctica con DELETE</h2>
    <div class="task"> -- Corregido: comillas dobles
        <strong>Ejercicio:</strong> Escribe la consulta para eliminar de la tabla Log todos los registros anteriores al año 2024.
    </div>
</body>
</html>');

-- Ejercicio 8 para la lección 30 (ID será 8)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(30, 'Eliminar Datos Antiguos',
'Escribe la consulta SQL para eliminar de la tabla ''Pedidos'' aquellos registros con ''estado'' = ''cancelado''.',
'DELETE FROM Pedidos WHERE ',
'DELETE FROM Pedidos WHERE estado = ''cancelado''',
25, 1);

-- Lección 9.4: Control de Transacciones (ID será 33) <--- LECCIÓN AÑADIDA
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(9, 'Control de Transacciones (COMMIT, ROLLBACK)', 4, 'TEORIA',
'<html>
<head>
    <style>
        h1 { color: #8e44ad; }
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; font-family: monospace; }
        .tip { background: #fbe0e0; padding: 15px; border-left: 4px solid #c0392b; margin: 20px 0; }
    </style>
</head>
<body>
    <h1>ACID y Transacciones</h1>
    <p>Una transacción es un conjunto de operaciones que se ejecutan como una sola unidad.</p>
    <h2>Comandos principales:</h2>
    <ul>
        <li><strong>COMMIT:</strong> Guarda permanentemente los cambios en la BD.</li>
        <li><strong>ROLLBACK:</strong> Deshace todos los cambios realizados desde el último COMMIT.</li>
    </ul>
    <div class="tip"> -- Corregido: comillas dobles
        <strong>Tip:</strong> Si estás en modo de autocommit desactivado, debes ejecutar COMMIT para que los cambios de INSERT/UPDATE/DELETE sean definitivos.
    </div>
</body>
</html>');

-- ============================================
-- 10. QUIZZES Y PREGUNTAS (CURSO 3)
-- ============================================

-- QUIZ SECCIÓN 7: Introducción (PRUEBA ID será 7)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(3, 7, 'Quiz - Conceptos de Bases de Datos', 'FINAL');

-- Pregunta 7.1 (ID será 16)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué función tiene una Clave Foránea (FK)?', 7);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(19, 'Identificar un registro de forma única.', false),
(19, 'Acelerar las consultas.', false),
(19, 'Establecer una relación entre dos tablas.', true),
(19, 'Definir el tipo de dato de una columna.', false);

-- Pregunta 7.2 (ID será 17)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué significa SQL?', 7);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(20, 'Standard Query Language', false),
(20, 'Structured Query Language', true),
(20, 'Simple Question Logic', false),
(20, 'Sequential Query Logic', false);

-- Pregunta 7.3 (ID será 22)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Cuál de los siguientes no es un componente del modelo relacional?', 7);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(21, 'Tablas', false),
(21, 'Relaciones', false),
(21, 'Objetos de clase (Class Objects)', true),
(21, 'Claves primarias', false);

-- QUIZ SECCIÓN 8: Consultas Fundamentales (PRUEBA ID será 8)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(3, 8, 'Quiz - Consultas SELECT y WHERE', 'FINAL');

-- Pregunta 8.1 (ID será 18)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué símbolo se usa en SELECT para seleccionar todas las columnas de una tabla?', 8);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(22, '.', false),
(22, '#', false),
(22, '*', true),
(22, '$', false);

-- Pregunta 8.2 (ID será 19)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué cláusula se utiliza para filtrar los registros obtenidos?', 8);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(23, 'GROUP BY', false),
(23, 'ORDER BY', false),
(23, 'HAVING', false),
(23, 'WHERE', true);

-- Pregunta 8.3 (ID será 23)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué cláusula se utiliza para ordenar el resultado de una consulta?', 8);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(24, 'GROUP BY', false),
(24, 'SORT BY', false),
(24, 'ORDER BY', true),
(24, 'BY VALUE', false);

-- QUIZ SECCIÓN 9: Manipulación de Datos (PRUEBA ID será 9)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(3, 9, 'Quiz - Manipulación de Datos (DML)', 'FINAL');

-- Pregunta 9.1 (ID será 20)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué sentencia SQL se usa para modificar datos existentes en una tabla?', 9);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(25, 'INSERT INTO', false),
(25, 'ALTER TABLE', false),
(25, 'UPDATE', true),
(25, 'CREATE', false);

-- Pregunta 9.2 (ID será 21)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué ocurre si ejecutas una sentencia DELETE sin la cláusula WHERE?', 9);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(26, 'Se obtiene un error de sintaxis.', false),
(26, 'Se eliminan todos los registros de la tabla.', true),
(26, 'Solo se elimina el primer registro.', false),
(26, 'Se elimina la tabla completa.', false);

-- Pregunta 9.3 (ID será 24)
INSERT INTO "pregunta" (enunciado, prueba_id) VALUES ('¿Qué sentencia se usa para agregar nuevos registros a una tabla?', 9);
INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(26, 'ADD RECORD', false),
(26, 'CREATE ROW', false),
(26, 'INSERT INTO', true),
(26, 'UPDATE WITH', false);

-- ============================================
-- 11. CURSO DE C++ (ID será 4)
-- ============================================
INSERT INTO "curso" (titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones) VALUES
('Introducción a C++', 'Aprende los fundamentos de la programación orientada a objetos usando el lenguaje C++.', 'INTERMEDIO', 'Programación', 380, 3);

-- ============================================
-- 12. SECCIONES, LECCIONES Y EJERCICIOS (CURSO 4)
-- ============================================

-- SECCIÓN 10: Fundamentos y Sintaxis (ID 10)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(4, 'Fundamentos y Sintaxis', 1);

-- Lección 10.1: Entorno y "Hola Mundo"
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido, contenido_html) VALUES
(10, 'Configuración y "Hola Mundo"', 1, 'PRACTICA',
'Instalación del IDE (VS Code o g++) y compilación del primer programa.',
'<html>
<head>
    <style>
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; font-family: monospace; }
    </style>
</head>
<body>
    <h1>Primer programa</h1>
    <p>El programa clásico para empezar:</p>
    <div class="code">
        #include &lt;iostream&gt;<br>
        int main() {<br>
        &nbsp;&nbsp;std::cout &lt;&lt; "Hola Mundo!";<br>
        &nbsp;&nbsp;return 0;<br>
        }
    </div>
</body>
</html>');

-- Lección 10.2: Variables y Tipos de Datos
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(10, 'Variables, Tipos de Datos y Entrada/Salida', 2, 'TEORIA',
'<html>
<head>
    <style>
        .highlight { background: #e0f7fa; padding: 2px 5px; border-radius: 3px; }
    </style>
</head>
<body>
    <h2>Tipos Fundamentales</h2>
    <ul>
        <li><span class="highlight">int</span>: enteros</li>
        <li><span class="highlight">float</span> / <span class="highlight">double</span>: números decimales</li>
        <li><span class="highlight">char</span>: caracteres</li>
        <li><span class="highlight">bool</span>: verdadero/falso</li>
    </ul>
    <h2>Entrada de Usuario</h2>
    <p>Usamos <span class="highlight">std::cin</span> para leer datos desde la consola.</p>
</body>
</html>');

-- Lección 10.3: Operadores (VIDEO)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, url_video, contenido) VALUES
(10, 'Operadores Aritméticos y Lógicos', 3, 'VIDEO',
'https://youtu.be/EjemploOpCplus?si=CplVf7',
'Video que explica cómo funcionan los operadores de asignación, comparación y lógica booleana.');

-- Lección 10.4: Estructuras Condicionales
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(10, 'Control de Flujo: If, Else y Switch', 4, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #fbe0e0; padding: 15px; border-left: 4px solid #c0392b; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Ejercicio Práctico</h2>
    <div class="task">
        <strong>Tarea:</strong> Escribe un programa que pida una edad y use un `if/else` para indicar si la persona es mayor o menor de 18 años.
    </div>
</body>
</html>');

-- Ejercicio 10 (leccion_id 37)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(37, 'Control de Edad',
'Completa el código para verificar si la variable `edad` (int) es mayor o igual a 18.',
'int edad = 20; if (______) { return 1; } return 0;',
'int edad = 20; if (edad >= 18) { return 1; } return 0;',
25, 1);

-- SECCIÓN 11: Estructuras de Datos y Bucles (ID 11)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(4, 'Estructuras de Datos, Bucles y Arrays', 2);

-- Lección 11.1: Bucles While y For
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html)
VALUES
(11, 'Bucles: While, Do-While y For', 1, 'TEORIA',
'<html>
<head>
    <style>
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>El bucle For</h1>
    <p>Ideal para iteraciones con un número fijo de repeticiones.</p>
    <div class="code">
        for (int i = 0; i < 5; i++) {<br>
        &nbsp;&nbsp;std::cout &lt;&lt; i;<br>
        }
    </div>
</body>
</html>');

-- Lección 11.2: Arrays (Arreglos)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(11, 'Arreglos (Arrays) Unidimensionales', 2, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #e6f7e6; padding: 15px; border-left: 4px solid #4caf50; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Práctica de Arrays</h2>
    <div class="task">
        <strong>Ejercicio:</strong> Crea un array de 5 enteros e imprímelos usando un bucle `for`.
    </div>
    <pre>int numeros[5] = {1, 2, 3, 4, 5};</pre>
</body>
</html>');

-- Lección 11.3: Strings y Cadenas de Caracteres
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(11, 'Manejo de Cadenas de Caracteres (Strings)', 3, 'TEORIA',
'<html>
<head>
    <style>
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Tipos de Strings</h1>
    <p>C++ soporta strings de estilo C (char[]) y la clase <span style="background: #e0f7fa;">std::string</span> (preferida).</p>
    <div class="code">
        #include &lt;string&gt;<br>
        std::string nombre = "Curso C++";
    </div>
</body>
</html>');

-- Lección 11.4: Funciones Básicas
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(11, 'Creación y Llamada a Funciones', 4, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #fff8e1; padding: 15px; border-left: 4px solid #ffb300; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Ejercicio: Declaración de Funciones</h2>
    <div class="task">
        <strong>Tarea:</strong> Crea una función llamada `suma` que acepte dos argumentos enteros y devuelva su suma.
    </div>
</body>
</html>');

-- Ejercicio 11 (leccion_id 41)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(41, 'Función Suma',
'Escribe el cuerpo de la función `suma` para que devuelva la adición de `a` y `b`.',
'int suma(int a, int b) { ___ }',
'int suma(int a, int b) { return a + b; }',
30, 1);

-- SECCIÓN 12: Punteros e Introducción a Clases (ID 12)
INSERT INTO "seccion" (curso_id, titulo, numero_orden) VALUES
(4, 'Punteros y Conceptos POO', 3);

-- Lección 12.1: ¿Qué son los punteros?
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(12, 'Punteros, Referencias y Memoria', 1, 'TEORIA',
'<html>
<head>
    <style>
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h1>Punteros</h1>
    <p>Un puntero (`*`) almacena la dirección de memoria de una variable. Usamos `&` para obtener la dirección.</p>
    <div class="code">
        int var = 10;<br>
        int* ptr = &var; // ptr almacena la dirección de var
    </div>
</body>
</html>');

-- Lección 12.2: Estructuras (struct)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(12, 'Estructuras (struct) para Agrupar Datos', 2, 'PRACTICA',
'<html>
<head>
    <style>
        .task { background: #e3f2fd; padding: 15px; border-left: 4px solid #2196f3; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Ejercicio: Creando un struct</h2>
    <div class="task">
        <strong>Tarea:</strong> Define un `struct` llamado `Punto` con dos miembros enteros: `x` y `y`.
    </div>
    <pre>struct Punto { int x; int y; };</pre>
</body>
</html>');

-- Lección 12.3: Clases (Conceptos POO)
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(12, 'Introducción a Clases (Conceptos POO)', 3, 'TEORIA',
'<html>
<head>
    <style>
        .warning { background: #ffeb3b; padding: 15px; border-left: 4px solid #fbc02d; margin: 20px 0; }
    </style>
</head>
<body>
    <h2>Objetos y Clases</h2>
    <p>Una **clase** es un plano (blueprint) para crear **objetos** (instancias) que combinan datos y funciones.</p>
    <div class="warning">
        <strong>POO:</strong> Los pilares son Encapsulación, Herencia, Polimorfismo y Abstracción.
    </div>
</body>
</html>');

-- Lección 12.4: Constructores y Métodos
INSERT INTO "leccion" (seccion_id, titulo, numero_orden, tipo_contenido, contenido_html) VALUES
(12, 'Constructores y Métodos de Clase', 4, 'PRACTICA',
'<html>
<head>
    <style>
        .code { background: #333; color: #fff; padding: 10px; border-radius: 5px; }
    </style>
</head>
<body>
    <h2>Constructor</h2>
    <p>Función especial que se llama automáticamente al crear un objeto. Tiene el mismo nombre que la clase.</p>
    <div class="code">
        class Coche { <br>
        public: <br>
        &nbsp;&nbsp;Coche() { /* Código de inicialización */ } <br>
        };
    </div>
</body>
</html>');

-- Ejercicio 12 (leccion_id 45)
INSERT INTO "ejercicio" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES
(45, 'Clase Coche',
'Completa la clase `Coche` con un constructor que inicialice el miembro `velocidad` a 0.',
'class Coche { public: int velocidad; Coche() { ____ } };',
'class Coche { public: int velocidad; Coche() { velocidad = 0; } };',
40, 1);

-- ============================================
-- 13. QUIZZES Y PREGUNTAS
-- ============================================

-- QUIZ SECCIÓN 10: Fundamentos y Sintaxis (PRUEBA ID 10)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(1, 10, 'Quiz - Conceptos Básicos de C++', 'FINAL');
-- Pregunta 10.1
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Qué biblioteca se utiliza en C++ para realizar operaciones de entrada/salida (como cout y cin)?', 10);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(27, 'stdlib', false),
(27, 'iostream', true),
(27, 'stdio.h', false),
(27, 'math', false);

-- Pregunta 10.2
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Cuál es el tipo de dato que se utiliza para almacenar números enteros grandes?', 10);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(28, 'float', false),
(28, 'char', false),
(28, 'int', true),
(28, 'string', false);

-- Pregunta 10.3
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Qué operador se utiliza para evaluar la igualdad de dos valores?', 10);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(29, '=', false),
(29, ':=', false),
(29, '==', true),
(29, '!=', false);

-- QUIZ SECCIÓN 11: Estructuras de Datos y Bucles (PRUEBA ID 11)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(1, 11, 'Quiz - Estructuras de Datos y Bucles', 'FINAL');

-- Pregunta 11.1
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Qué tipo de bucle es el más adecuado si sabes exactamente cuántas veces debe ejecutarse?', 11);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(30, 'while', false),
(30, 'do-while', false),
(30, 'for', true),
(30, 'if', false);

-- Pregunta 11.2
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('En un array de tamaño 10, ¿cuál es el índice del último elemento?', 11);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(31, '10', false),
(31, '9', true),
(31, '11', false),
(31, '0', false);

-- Pregunta 11.3
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Cuál es la palabra clave que define una función que no devuelve ningún valor?', 11);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(32, 'null', false),
(32, 'void', true),
(32, 'none', false),
(32, 'return', false);

-- QUIZ SECCIÓN 12: Punteros e Introducción a Clases (PRUEBA ID 12)
INSERT INTO "prueba" (curso_id, seccion_id, titulo, tipo) VALUES
(1, 12, 'Quiz - Punteros e Introducción a Clases', 'FINAL');

-- Pregunta 12.1
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Qué almacena una variable de tipo puntero en C++?', 12);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(33, 'Un valor booleano.', false),
(33, 'El valor de la variable a la que apunta.', false),
(33, 'La dirección de memoria de otra variable.', true),
(33, 'Un array de caracteres.', false);

-- Pregunta 12.2
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('En el contexto de POO, ¿qué es un "objeto"?', 12);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(34, 'Una variable de tipo puntero.', false),
(34, 'Una función especial.', false),
(34, 'Una instancia (ejemplar) de una clase.', true),
(34, 'Un archivo de código fuente.', false);

-- Pregunta 12.3
INSERT INTO "pregunta" (enunciado, prueba_id)
VALUES ('¿Cómo se llama la función especial dentro de una clase que se invoca al crear un nuevo objeto?', 12);

INSERT INTO "opcion" (pregunta_id, texto, es_correcta) VALUES
(35, 'Inicializador', false),
(35, 'Destructor', false),
(35, 'Constructor', true),
(35, 'Creación', false);

-- ============================================
-- 5. ITEMS DE LA TIENDA Y STELLA (Sin IDs manuales)
-- ============================================

-- 👕 HOODIE GITHUB (ITEM ID será 1)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Hoodie GitHub','Sudadera con capucha inspirada en GitHub, ideal para desarrolladores que quieren vestir con estilo tech.',
     1150, '/Image/TiendaStella/hoodieGithub.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (1, '/Image/stellas/stellaHoodieGithub.png');

-- 👕 CAMISETA APPLE (ITEM ID será 2)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Apple','Camiseta minimalista con logo de Apple, ideal para los amantes de la tecnología y el diseño limpio.',
     950, '/Image/TiendaStella/camisetaApple.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (2, '/Image/stellas/stellaCamisetaApple.png');

-- TRAJE KERBAL (ITEM ID será 3)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Mistery','Traje espacial inspirado en Kerbal Space Program, diseñado para exploradores temerarios que sueñan con llegar más allá de la órbita.',
     5000, '/Image/TiendaStella/misteryBox.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (3, '/Image/stellas/stellaMistery.png');

-- 🏫 CAMISETA FIS (ITEM ID será 4)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta FIS','Camiseta deportiva con diseño institucional FIS, ideal para eventos y actividades académicas.',
     800, '/Image/TiendaStella/camisetaFIS.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (4, '/Image/stellas/stellaFIS.png');

-- 🇨🇴 CAMISETA COLOMBIA (ITEM ID será 5)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Colombia','Camiseta de la selección Colombia, fabricada con tela transpirable y cómoda para mostrar el orgullo nacional.',
     1050, '/Image/TiendaStella/camisetaColombia.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (5, '/Image/stellas/stellaCamisetaColombia.png');

-- 🧢 BALACA DISNEY (ITEM ID será 6)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Balaca Disney','Balaca con diseño inspirado en personajes clásicos de Disney, cómoda y divertida para toda ocasión.',
     450, '/Image/TiendaStella/balacaDisney.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (6, '/Image/stellas/stellaDisney.png');

-- 🍹 JUGO HIT (ITEM ID será 7)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Jugo Hit','Bebida refrescante de frutas naturales, ideal para acompañar tus comidas o hidratarte en cualquier momento.',
     350, '/Image/TiendaStella/jugoHit.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (7, '/Image/stellas/stellaJugoHit.png');

-- ⚽ CAMISETA MILLOS (ITEM ID será 8)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Camiseta Millos','Camiseta oficial del equipo Millonarios, perfecta para hinchas que quieren lucir sus colores con pasión.',
     1000, '/Image/TiendaStella/camisetaMillos.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (8, '/Image/stellas/stellaCamisetaMillos.png');

-- 👒 SOMBRERO VUELTEAO (ITEM ID será 9)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Sombrero Vuelteao','Sombrero típico colombiano elaborado con caña flecha, símbolo de tradición y estilo.',
     900, '/Image/TiendaStella/sombreroVuelteao.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (9, '/Image/stellas/stellaSombreroVuelteao.png');

-- 👚 CROPTOP (ITEM ID será 10)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Croptop','Croptop moderno y cómodo, ideal para climas cálidos y estilos urbanos.',
     700, '/Image/TiendaStella/croptop.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (10, '/Image/stellas/stellaCroptop.png');

-- 🧣 BUFANDA (ITEM ID será 11)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Bufanda','Bufanda tejida con materiales suaves y cálidos, perfecta para protegerte del frío con estilo.',
     850, '/Image/TiendaStella/bufanda.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (11, '/Image/stellas/stellaBufanda.png');

-- 🕶️ GAFAS DE SOL (ITEM ID será 12)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas de Sol','Gafas elegantes con protección UV400, perfectas para un look moderno y proteger tus ojos del sol.',
     600, '/Image/TiendaStella/gafasDeSol.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (12, '/Image/stellas/stellaGafas.png');

-- 🕶️ GAFAS DE SKI (ITEM ID será 13)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gafas de Ski','Gafas diseñadas para nieve y deporte extremo, resistentes al viento y la neblina.',
     1100, '/Image/TiendaStella/gafasSki.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (13, '/Image/stellas/stellaSki.png');

-- 🧢 GORRA (ITEM ID será 14)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra','Gorra clásica ajustable, con visera curva y diseño casual ideal para cualquier día.',
     500, '/Image/TiendaStella/gorra.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (14, '/Image/stellas/stellaGorra.png');

-- 🧢 GORRA ROJA (ITEM ID será 15)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra Roja','Gorra de color rojo intenso, ajustable y cómoda, perfecta para destacar en cualquier ocasión.',
     520, '/Image/TiendaStella/gorraRoja.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (15, '/Image/stellas/stellaGorraRoja.png');

-- 🧢 GORRA ROSA (ITEM ID será 16)
INSERT INTO "item" (nombre, descripcion, precio, image_path) VALUES
    ('Gorra Rosa','Gorra color rosa con diseño moderno, perfecta para un look casual y alegre.',
     520, '/Image/TiendaStella/gorraRosa.png');
INSERT INTO "stella_item" (item_id, image_path) VALUES
    (16, '/Image/stellas/stellaGorraRosa.png');

-- 7. COMPRA INICIAL del Usuario 1
INSERT INTO "usuario_item" (usuario_id, item_id) VALUES (1, 5);

-- ============================================
-- 6. DATOS DEL FORO (Los IDs ya estaban correctos)
-- ============================================

-- Post 1 (Creado por Usuario 1. ID será 1)
INSERT INTO "post" (usuario_id, contenido_texto, fecha, etiqueta) VALUES
(1, '¿Alguien sabe cómo usar JavaFX para un foro?', '2025-11-07 18:00:00', 'JavaFX');

-- Comentario 1 y 2 (Creados por Usuario 2. post_id = 1)
INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(1, 2, 'Sí, usa FXML para la interfaz.', '2025-11-07 18:05:00');

INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(1, 2, 'También puedes agregar un ListView para los posts.', '2025-11-07 18:10:00');

-- Post 2 (Creado por Usuario 2. ID será 2)
INSERT INTO "post" (usuario_id, contenido_texto, fecha, etiqueta) VALUES
(2, '¿Alguien sabe cómo utilizar la memoria dinámica en c++?', '2025-11-07 18:20:00', 'C++');

-- Comentario 3 (Creado por Usuario 1. post_id = 2)
INSERT INTO "comentario" (post_id, usuario_id, contenido_texto, fecha) VALUES
(2, 1, 'Sí, debes asignar y liberar memoria así: int *arr = new int[10]; y delete[] arr;', '2025-11-07 18:25:00');

-- DATOS DE PRUEBA PARA PROGRESO
-- Inscribir usuario 1 en curso 1
INSERT INTO "usuario_curso" (usuario_id, curso_id) VALUES (1, 1);

-- Crear registros de progreso para las 12 lecciones (IDs: 1 a 12)
INSERT INTO "progreso_leccion" (usuario_id, leccion_id, estado) VALUES
(1, 1, 'EN_PROGRESO'),
(1, 2, 'EN_PROGRESO'),
(1, 3, 'EN_PROGRESO'),
(1, 4, 'EN_PROGRESO'),
(1, 5, 'EN_PROGRESO'),
(1, 6, 'EN_PROGRESO'),
(1, 7, 'EN_PROGRESO'),
(1, 8, 'EN_PROGRESO'),
(1, 9, 'EN_PROGRESO'),
(1, 10, 'EN_PROGRESO'),
(1, 11, 'EN_PROGRESO'),
(1, 12, 'EN_PROGRESO');

-- Marcar las primeras 7 lecciones como COMPLETADAS (7/12 = 58.33%)
UPDATE "progreso_leccion"
SET estado = 'COMPLETADA'
WHERE usuario_id = 1
  AND leccion_id IN (1, 2, 3, 4, 5, 6, 7);