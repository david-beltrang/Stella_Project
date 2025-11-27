package Domain.models;

import Domain.models.CursoValueObjects.Titulo;
import Domain.models.PruebaValueObjects.TipoValueObject;

import java.util.Objects;


public class Prueba {

    private final Integer id;
    private final Integer curso_id;
    private final Integer seccion_id;
    private final Titulo titulo;
    private final TipoValueObject tipo;


    private Prueba(Integer id, Integer curso_id, Integer seccion_id, Titulo titulo, TipoValueObject tipo) {
        this.id = id;
        this.curso_id = Objects.requireNonNull(curso_id, "El id del curso no puede ser nulo");
        this.seccion_id = Objects.requireNonNull(seccion_id, "El id de la sección no puede ser nulo");;
        this.titulo = titulo;
        this.tipo = Objects.requireNonNull(tipo, "El id de la sección no puede ser nulo");
    }

    // ------------------ FACTORY METHODS ------------------

    public static Prueba crear(Integer cursoId, Integer seccionId, String titulo, String tipo) {
        return new Prueba(null, cursoId, seccionId, new Titulo(titulo), new TipoValueObject(tipo));
    }

    public static Prueba reconstruir(Integer id, Integer cursoId, Integer seccionId, Titulo titulo, TipoValueObject tipo) {
        return new Prueba(id, cursoId, seccionId, titulo, tipo);
    }


    // ---------- GETTERS ---------

    public Integer getId() { return id; }
    public Integer getCursoId() { return curso_id; }
    public Integer getSeccionId() { return seccion_id; }
    public Titulo getTitulo() { return titulo; }
    public TipoValueObject getTipo() { return tipo; }
}