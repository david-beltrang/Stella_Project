package Domain.models;

import Domain.models.CursoValueObjects.NivelCurso;
import Domain.models.CursoValueObjects.Titulo;

import java.time.LocalDateTime;
import java.util.Objects;

public class Seccion {
    private Integer id;
    private Integer curso_id;
    private Titulo titulo;
    private Integer numero_orden;


    // Constructor, getters y setters
    private Seccion(Integer id, Integer cursoId, Titulo titulo, Integer numero_orden) {
        this.id = id;
        this.curso_id = Objects.requireNonNull(cursoId, "cursoId no puede ser nulo");
        this.titulo = Objects.requireNonNull(titulo, "titulo no puede ser nulo");
        this.numero_orden = Objects.requireNonNull(numero_orden, "titulo no puede ser nulo");
    }

    //FACTORY METHODS: Un metodo para construir un usuario desde el front y otro metodo para
    //reconstruirlo desde la BD

    //Construir desde front
    public static Seccion crearSeccion(Integer id, Integer curso_id, String titulo, Integer numero_orden) {
        Titulo tituloVO = new Titulo(titulo); // Lanza excepción si inválido
        return new Seccion(null,curso_id, tituloVO, numero_orden);
    }

    //Reconstruir desde la BD
    public static Seccion reconstruir(Integer id, Integer curso_id, Titulo titulo, Integer numero_orden) {
        return new Seccion(id, curso_id, titulo, numero_orden);
    }



    //getters
    public Integer getId() {
        return this.id;
    }

    public Integer getCurso_id() {
        return this.curso_id;
    }

    public Titulo getTitulo() {
        return this.titulo;
    }

    public Integer getNumero_orden(){
        return this.numero_orden;
    }

}
