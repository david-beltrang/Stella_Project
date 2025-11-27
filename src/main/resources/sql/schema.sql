-- ============================================
-- 1. DROP TABLES (Orden inverso a la creación para respetar dependencias FK)
-- ============================================

DROP TABLE IF EXISTS "usuario_ejercicio";
DROP TABLE IF EXISTS "progreso_leccion";
DROP TABLE IF EXISTS "respuesta";
DROP TABLE IF EXISTS "intento";
DROP TABLE IF EXISTS "opcion";
DROP TABLE IF EXISTS "pregunta";
DROP TABLE IF EXISTS "comentario_foro";
DROP TABLE IF EXISTS "dislike_foro";
DROP TABLE IF EXISTS "like_foro";
DROP TABLE IF EXISTS "respuesta_foro";
DROP TABLE IF EXISTS "pregunta_foro";
DROP TABLE IF EXISTS "comentario";
DROP TABLE IF EXISTS "post";
DROP TABLE IF EXISTS "usuario_item";
DROP TABLE IF EXISTS "stella_item";
DROP TABLE IF EXISTS "item";
DROP TABLE IF EXISTS "ejercicio";
DROP TABLE IF EXISTS "leccion";
DROP TABLE IF EXISTS "prueba";
DROP TABLE IF EXISTS "seccion";
DROP TABLE IF EXISTS "usuario_curso";
DROP TABLE IF EXISTS "sesion_estudio";
DROP TABLE IF EXISTS "progreso_estudio";
DROP TABLE IF EXISTS "progreso_gamificacion";
DROP TABLE IF EXISTS "usuario_stats";
DROP TABLE IF EXISTS "curso";
DROP TABLE IF EXISTS "usuario";


-- ============================================
-- 2. CREATE TABLES (Todas las tablas, sin duplicados)
-- ============================================

-- [INICIO TABLAS PRINCIPALES]
CREATE TABLE "usuario" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    correo VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "usuario_stats" (
    usuario_id INT NOT NULL PRIMARY KEY,
    pescaditos INT NOT NULL DEFAULT 0,
    objetivo_sesiones INT NOT NULL DEFAULT 1,
    racha_dias INT NOT NULL DEFAULT 0,
    tiempo_total_estudio_segundos INT NOT NULL DEFAULT 0,
    ultima_leccion_fecha DATE,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "curso" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion VARCHAR(2000),
    nivel VARCHAR(50),
    categoria VARCHAR(100),
    duracion_minutos INT,
    numero_secciones INT
);

CREATE TABLE "usuario_curso" (
    usuario_id INT NOT NULL,
    curso_id INT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, curso_id),
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
    FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE
);

CREATE TABLE "seccion" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    curso_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    numero_orden INT NOT NULL,
    FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE,
    UNIQUE (curso_id, numero_orden)
);

CREATE TABLE "leccion" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seccion_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    numero_orden INT NOT NULL,
    tipo_contenido VARCHAR(20) NOT NULL,
    url_video VARCHAR(255),
    contenido CLOB,
    contenido_html CLOB,
    pdf_url VARCHAR(500),
    FOREIGN KEY (seccion_id) REFERENCES "seccion"(id) ON DELETE CASCADE,
    UNIQUE (seccion_id, numero_orden)
);

CREATE TABLE "ejercicio" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    leccion_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    instrucciones TEXT NOT NULL,
    codigo_plantilla TEXT,
    solucion_esperada TEXT NOT NULL,
    puntos INT NOT NULL DEFAULT 10,
    numero_orden INT NOT NULL,
    FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
    UNIQUE (leccion_id, numero_orden)
);

CREATE TABLE "usuario_ejercicio" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    ejercicio_id INT NOT NULL,
    codigo_enviado TEXT NOT NULL,
    es_correcto BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_intento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
    FOREIGN KEY (ejercicio_id) REFERENCES "ejercicio"(id) ON DELETE CASCADE
);

CREATE TABLE "prueba" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    curso_id INT NOT NULL,
    seccion_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE,
    FOREIGN KEY (seccion_id) REFERENCES "seccion"(id) ON DELETE CASCADE
);

CREATE TABLE "pregunta" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enunciado VARCHAR(500) NOT NULL,
    leccion_id INT,
    prueba_id INT,
    FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
    FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE,
    CHECK (leccion_id IS NOT NULL OR prueba_id IS NOT NULL)
);

CREATE TABLE "opcion" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pregunta_id INT NOT NULL,
    texto VARCHAR(500) NOT NULL,
    es_correcta BOOLEAN NOT NULL,
    FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE
);

CREATE TABLE "progreso_leccion" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    leccion_id INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
    FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
    UNIQUE (usuario_id, leccion_id)
);

CREATE TABLE "intento" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    prueba_id INT NOT NULL,
    puntaje DOUBLE NOT NULL,
    fecha_intento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
    FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE
);

CREATE TABLE "respuesta" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    intento_id INT NOT NULL,
    pregunta_id INT NOT NULL,
    opcion_seleccionada_id INT NOT NULL,
    FOREIGN KEY (intento_id) REFERENCES "intento"(id) ON DELETE CASCADE,
    FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE,
    FOREIGN KEY (opcion_seleccionada_id) REFERENCES "opcion"(id) ON DELETE CASCADE
);

CREATE TABLE "sesion_estudio" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    tiempo_estudio INT NOT NULL,
    tiempo_descanso INT NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_final TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "progreso_estudio" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    fecha DATE NOT NULL,
    estudio BOOLEAN NOT NULL,
    CONSTRAINT uk_progreso_estudio_usuario_fecha UNIQUE (usuario_id, fecha),
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "progreso_gamificacion" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL UNIQUE,
    puntos INT NOT NULL DEFAULT 0,
    nivel INT NOT NULL DEFAULT 1,
    racha_dias INT NOT NULL DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);
-- [FIN TABLAS PRINCIPALES]


-- ============================================
-- TABLAS DE COMUNIDAD Y FORO Q&A
-- ============================================

CREATE TABLE "post" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    contenido_texto TEXT NOT NULL,
    likes INT DEFAULT 0,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etiqueta TEXT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

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

CREATE TABLE "pregunta_foro" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "respuesta_foro" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pregunta_id INT NOT NULL,
    usuario_id INT NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (pregunta_id) REFERENCES "pregunta_foro"(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "comentario_foro" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    respuesta_id INT NOT NULL,
    usuario_id INT NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (respuesta_id) REFERENCES "respuesta_foro"(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "like_foro" (
    id INT AUTO_INCREMENT,
    respuesta_id INT NOT NULL,
    usuario_id INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (respuesta_id, usuario_id),
    FOREIGN KEY (respuesta_id) REFERENCES "respuesta_foro"(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);

CREATE TABLE "dislike_foro" (
    id INT AUTO_INCREMENT,
    respuesta_id INT NOT NULL,
    usuario_id INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (respuesta_id, usuario_id),
    FOREIGN KEY (respuesta_id) REFERENCES "respuesta_foro"(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE
);


-- ============================================
-- TABLAS DE TIENDA/ITEMS
-- ============================================

CREATE TABLE "item" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    precio INT NOT NULL,
    image_path VARCHAR(500)
);

CREATE TABLE "stella_item" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    image_path VARCHAR(500),
    FOREIGN KEY (item_id) REFERENCES "item"(id) ON DELETE CASCADE
);

-- ESTA ES LA DEFINICIÓN COMPLETA Y CORRECTA
CREATE TABLE "usuario_item" (
    usuario_id INT NOT NULL,
    item_id INT NOT NULL,
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    es_activo BOOLEAN DEFAULT TRUE NOT NULL,
    PRIMARY KEY (usuario_id, item_id),
    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES "item"(id) ON DELETE CASCADE
);