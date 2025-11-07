package Domain.models;

import Domain.models.LeccionValueObjects.TipoContenido;
import Domain.models.CursoValueObjects.Titulo;

import java.util.Objects;

/**
 * Representa una unidad de contenido dentro de un curso (teoría, práctica, quiz, etc.).
 * La entidad es responsable de gestionar sus datos estructurales.
 */
public class Leccion {
    private Integer id; // Usamos VO
    private Integer seccion_id;
    private Titulo titulo; // Usamos VO
    private Integer numeroOrden;
    private TipoContenido tipoContenido;
    private String url_video;
    private String contenido;

    public Leccion(Integer id, Integer seccion_id, Titulo titulo, Integer numeroOrden,
                   TipoContenido tipoContenido, String url_video, String contenido) {
        this.id = id;
        this.seccion_id = Objects.requireNonNull(seccion_id, "seccion_id no puede ser nulo");
        this.titulo = Objects.requireNonNull(titulo, "titulo no puede ser nulo");
        this.numeroOrden = Objects.requireNonNull(numeroOrden, "numero_orden no puede ser nulo");
        this.tipoContenido = Objects.requireNonNull(tipoContenido, "tipoContenido no puede ser nulo");
        this.url_video = url_video;
        this.contenido = contenido;
    }

    public static Leccion crearLeccion(Integer id, Integer seccion_id, Titulo titulo, Integer numeroOrden, TipoContenido tipoContenido, String url_video, String contenido ) {
        return new Leccion(null, seccion_id, titulo, numeroOrden, tipoContenido, url_video, contenido);
    }

    public static Leccion reconstruir(Integer id, Integer seccion_id, Titulo titulo, Integer numeroOrden, TipoContenido tipoContenido, String url_video, String contenido ){
        return new Leccion(id, seccion_id, titulo, numeroOrden, tipoContenido, url_video, contenido);
    }

    // Getters actualizados para devolver el VO o el valor primitivo si no tiene VO
    public Integer getId() { return id; }
    public Integer getSeccion_id() { return seccion_id; }
    public Titulo getTitulo() { return this.titulo; } // Se expone el valor a la Capa de Aplicación/DTOs
    public int getNumeroOrden() { return numeroOrden; }
    public TipoContenido getTipoContenido() { return tipoContenido; }
    public String getUrl_video() {return url_video;}
    public String getContenido() { return contenido; }
}