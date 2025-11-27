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

-- TABLA: "usuario"
CREATE TABLE "usuario" (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           username VARCHAR(255) NOT NULL UNIQUE,
                           correo VARCHAR(255) NOT NULL UNIQUE,
                           nombre VARCHAR(255) NOT NULL,
                           contrasena VARCHAR(255) NOT NULL,
                           tipo_usuario VARCHAR(50) NOT NULL,
                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABLA: "usuario_stats"
CREATE TABLE "usuario_stats" (
                                 usuario_id INT NOT NULL PRIMARY KEY,
                                 pescaditos INT NOT NULL DEFAULT 0,
                                 objetivo_sesiones INT NOT NULL DEFAULT 1,
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
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          curso_id INT NOT NULL,
                          seccion_id INT NOT NULL,
                          titulo VARCHAR(255) NOT NULL,
                          tipo VARCHAR(20) NOT NULL,
                          FOREIGN KEY (curso_id) REFERENCES "curso"(id) ON DELETE CASCADE,
                          FOREIGN KEY (seccion_id) REFERENCES "seccion"(id) ON DELETE CASCADE
);

-- TABLA: "pregunta"
CREATE TABLE "pregunta" (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            enunciado VARCHAR(500) NOT NULL,
                            leccion_id INT,
                            prueba_id INT,
                            FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
                            FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE,
                            CHECK (leccion_id IS NOT NULL OR prueba_id IS NOT NULL)
);

-- TABLA: "opcion"
CREATE TABLE "opcion" (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          pregunta_id INT NOT NULL,
                          texto VARCHAR(500) NOT NULL,
                          es_correcta BOOLEAN NOT NULL,
                          FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE
);

-- TABLA: "progreso_leccion"
CREATE TABLE "progreso_leccion" (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    usuario_id INT NOT NULL,
                                    leccion_id INT NOT NULL,
                                    estado VARCHAR(20) NOT NULL,
                                    FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
                                    FOREIGN KEY (leccion_id) REFERENCES "leccion"(id) ON DELETE CASCADE,
                                    UNIQUE (usuario_id, leccion_id)
);

-- TABLA: "intento" (Registro de una prueba realizada)
CREATE TABLE "intento" (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           usuario_id INT NOT NULL,
                           prueba_id INT NOT NULL,
                           puntaje DOUBLE NOT NULL,
                           fecha_intento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (usuario_id) REFERENCES "usuario"(id) ON DELETE CASCADE,
                           FOREIGN KEY (prueba_id) REFERENCES "prueba"(id) ON DELETE CASCADE
);

-- TABLA: "respuesta" (Respuestas específicas dentro de un Intento)
CREATE TABLE "respuesta" (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             intento_id INT NOT NULL,
                             pregunta_id INT NOT NULL,
                             opcion_seleccionada_id INT NOT NULL,
                             FOREIGN KEY (intento_id) REFERENCES "intento"(id) ON DELETE CASCADE,
                             FOREIGN KEY (pregunta_id) REFERENCES "pregunta"(id) ON DELETE CASCADE,
                             FOREIGN KEY (opcion_seleccionada_id) REFERENCES "opcion"(id) ON DELETE CASCADE
);

-- TABLA: "sesion_estudio" (Guardar todas las sesiones de estudio)
CREATE TABLE "sesion_estudio" (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
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