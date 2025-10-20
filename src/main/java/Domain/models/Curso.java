package Domain.models;

import Domain.models.CursoValueObjects.CursoId;
import Domain.models.CursoValueObjects.Titulo;

import java.util.Objects;

public class Curso {
    private final CursoId id;
    private final Titulo titulo;
    private final int numeroSecciones;

    public Curso(CursoId id, Titulo titulo, int numeroSecciones) {
        Objects.requireNonNull(id, "El ID del curso no puede ser nulo.");
        Objects.requireNonNull(titulo, "El título del curso no puede ser nulo.");
        if (numeroSecciones <= 0) {
            throw new IllegalArgumentException("El número de secciones debe ser positivo.");
        }
        this.id = id;
        this.titulo = titulo;
        this.numeroSecciones = numeroSecciones;
    }

    // Getters públicos
    public CursoId getId() {
        return id;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public int getNumeroSecciones() {
        return numeroSecciones;
    }
}
