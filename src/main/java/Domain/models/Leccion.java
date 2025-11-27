package Domain.models;

import Domain.models.LeccionValueObjects.TipoContenido;
import Domain.models.CursoValueObjects.Titulo;

import java.util.Objects;

/**
 * Representa una unidad de contenido dentro de un curso (teoría, práctica,
 * quiz, etc.).
 * La entidad es responsable de gestionar sus datos estructurales.
 */
public class Leccion {
    private Integer id;
    private Integer seccion_id;
    private Titulo titulo;
    private Integer numeroOrden;
    private TipoContenido tipoContenido;
    private String url_video;
    private String contenido;
    private String contenidoHtml;
    private String pdfUrl;

    public Leccion(Integer id, Integer seccion_id, Titulo titulo, Integer numeroOrden,
            TipoContenido tipoContenido, String url_video, String contenido,
            String contenidoHtml, String pdfUrl) {
        this.id = id;
        this.seccion_id = Objects.requireNonNull(seccion_id, "seccion_id no puede ser nulo");
        this.titulo = Objects.requireNonNull(titulo, "titulo no puede ser nulo");
        this.numeroOrden = Objects.requireNonNull(numeroOrden, "numero_orden no puede ser nulo");
        this.tipoContenido = Objects.requireNonNull(tipoContenido, "tipoContenido no puede ser nulo");
        this.url_video = url_video;
        this.contenido = contenido;
        this.contenidoHtml = contenidoHtml;
        this.pdfUrl = pdfUrl;
    }

    public static Leccion crearLeccion(Integer id, Integer seccion_id, Titulo titulo, Integer numeroOrden,
            TipoContenido tipoContenido, String url_video, String contenido,
            String contenidoHtml, String pdfUrl) {
        return new Leccion(null, seccion_id, titulo, numeroOrden, tipoContenido, url_video, contenido, contenidoHtml,
                pdfUrl);
    }

    public static Leccion reconstruir(Integer id, Integer seccion_id, Titulo titulo, Integer numeroOrden,
            TipoContenido tipoContenido, String url_video, String contenido,
            String contenidoHtml, String pdfUrl) {
        return new Leccion(id, seccion_id, titulo, numeroOrden, tipoContenido, url_video, contenido, contenidoHtml,
                pdfUrl);
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getSeccion_id() {
        return seccion_id;
    }

    public Titulo getTitulo() {
        return this.titulo;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public TipoContenido getTipoContenido() {
        return tipoContenido;
    }

    public String getUrl_video() {
        return url_video;
    }

    public String getContenido() {
        return contenido;
    }

    public String getContenidoHtml() {
        return contenidoHtml;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }
}