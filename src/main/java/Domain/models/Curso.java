package Domain.models;

import java.util.Objects;

import Domain.models.CursoValueObjects.NivelCurso;
import Domain.models.CursoValueObjects.Titulo;


public class Curso {
    private Integer id;
    private Titulo titulo;
    private String descripcion;
    private NivelCurso nivel;
    private String categoria;
    private Integer duracionMinutos;
    private Integer numeroSecciones;

    // Constructor, getters y setters
    private Curso(Integer id, Titulo titulo, String descripcion, NivelCurso nivel, String categoria, Integer duracionMinutos, Integer numeroSecciones) {
        this.id = id;
        this.titulo = Objects.requireNonNull(titulo, "El título no puede ser nulo");
        this.descripcion = descripcion;
        this.nivel = Objects.requireNonNull(nivel, "El nivel no puede ser nulo");
        this.categoria = categoria;
        this.duracionMinutos = duracionMinutos;
        this.numeroSecciones = numeroSecciones;
    }

    //FACTORY METHODS: Un metodo para construir un usuario desde el front y otro metodo para
    //reconstruirlo desde la BD

    //Construir desde front
    public static Curso crearNuevo(String tituloStr, String descripcionStr, String nivelStr, String categoriaStr, Integer duracionMinutos) {
        Titulo titulo = new Titulo(tituloStr); // Lanza excepción si inválido
        NivelCurso nivel = new NivelCurso(nivelStr); // Lanza excepción si inválido
        return new Curso(null, titulo, descripcionStr, nivel, categoriaStr, duracionMinutos, null);
    }

    //Reconstruir desde la BD
    public static Curso reconstruir(Integer id,
                                      Titulo titulo,
                                      String descripcion,
                                      NivelCurso nivel,
                                      String categoria,
                                      Integer duracionMinutos,
                                      Integer numeroSecciones) {
        return new Curso(id, titulo, descripcion, nivel, categoria, duracionMinutos, numeroSecciones);
    }

    

    //getters
    public Integer getId() {
        return this.id;
    }

    public Titulo getTitulo() {
        return this.titulo;
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public NivelCurso getNivel() {
        return this.nivel;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public Integer getDuracionMinutos() {
        return this.duracionMinutos;
    }
    public Integer getNumeroSecciones() {
        return this.numeroSecciones;
    }




}
