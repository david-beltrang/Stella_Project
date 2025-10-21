package Domain.models;

import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.CursoValueObjects.TipoContenido;
import Domain.models.CursoValueObjects.Titulo;

/**
 * Representa una unidad de contenido dentro de un curso (teoría, práctica, quiz, etc.).
 * La entidad es responsable de gestionar sus datos estructurales.
 */
public class Leccion {
    private final LeccionId id; // Usamos VO
    private final Integer cursoId;
    private final Titulo titulo; // Usamos VO
    private final int numeroSeccion;
    private final int numeroOrden;
    private final TipoContenido tipoContenido;
    private final String contenidoHtml;

    public Leccion(LeccionId id, Integer cursoId, Titulo titulo, int numeroSeccion, int numeroOrden,
                   TipoContenido tipoContenido, String contenidoHtml) {
        this.id = id;
        this.cursoId = cursoId;
        this.titulo = titulo;
        this.numeroSeccion = numeroSeccion;
        this.numeroOrden = numeroOrden;
        this.tipoContenido = tipoContenido;
        this.contenidoHtml = contenidoHtml;
    }

    public String getTituloValor() {
        return titulo.valor();
    }

    // Getters actualizados para devolver el VO o el valor primitivo si no tiene VO
    public LeccionId getId() { return id; }
    public Integer getCursoId() { return cursoId; }
    public String getTitulo() { return titulo.getValor(); } // Se expone el valor a la Capa de Aplicación/DTOs
    public int getNumeroSeccion() { return numeroSeccion; }
    public int getNumeroOrden() { return numeroOrden; }
    public TipoContenido getTipoContenido() { return tipoContenido; }
    public String getContenidoHtml() { return contenidoHtml; }
}
