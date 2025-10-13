package Domain.models;

import Domain.models.LeccionValueObjects.TipoContenido; // <-- Importación necesaria
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una unidad de contenido dentro de un curso (teoría, práctica, quiz, etc.).
 * La entidad es responsable de gestionar sus datos estructurales.
 */
public class Leccion {
    private final int id;
    private final int cursoId;
    private final int numeroSeccion;
    private final int numeroOrden;
    private final String titulo;
    private final TipoContenido tipoContenido;
    private final String contenidoHtml;

    private final Integer pruebaId;

    private List<Pregunta> preguntas;

    // Constructor para reconstruir desde el repositorio
    public Leccion(int id, int cursoId, int numeroSeccion, int numeroOrden, String titulo,
                   String tipoContenido, String contenidoHtml, Integer pruebaId) { // <-- pruebaId es Integer
        this.id = id;
        this.cursoId = cursoId;
        this.numeroSeccion = numeroSeccion;
        this.numeroOrden = numeroOrden;
        this.titulo = Objects.requireNonNull(titulo);
        // Conversión del String a Value Object
        this.tipoContenido = TipoContenido.fromString(Objects.requireNonNull(tipoContenido));
        this.contenidoHtml = contenidoHtml;
        this.pruebaId = pruebaId;
        this.preguntas = Collections.emptyList(); // Inicialmente vacío
    }

    // --- Métodos para el Test Funcional ---
    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas != null ? preguntas : Collections.emptyList();
    }
    // ------------------------------------

    // --- Getters de Dominio ---
    public int getId() { return id; }
    public int getCursoId() { return cursoId; }
    public int getNumeroSeccion() { return numeroSeccion; }
    public int getNumeroOrden() { return numeroOrden; }
    public String getTitulo() { return titulo; }

    // Devolvemos la representación en String del Value Object para la capa de Servicio
    public String getTipoContenido() {
        return tipoContenido.valor();
    }

    public String getContenidoHtml() { return contenidoHtml; }
    public Integer getPruebaId() { return pruebaId; }
    public List<Pregunta> getPreguntas() { return preguntas; }
}
