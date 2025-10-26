package Domain.models;
import java.time.LocalDateTime;
import java.util.Objects;

public class UsuarioCurso {
    private Integer usuario_id;
    private Integer curso_id;
    private LocalDateTime fecha;

    private UsuarioCurso(Integer usuarioId, Integer cursoId, LocalDateTime fecha) {
        this.usuario_id = Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        this.curso_id = Objects.requireNonNull(cursoId, "cursoId no puede ser nulo");
        this.fecha = (fecha != null) ? fecha : LocalDateTime.now();
    }

    public static UsuarioCurso crearInscripcion(Integer usuarioId, Integer cursoId, LocalDateTime fecha) {
        return new UsuarioCurso(usuarioId, cursoId, fecha);
    }

    public static UsuarioCurso reconstruir(Integer usuario_id, Integer curso_id, LocalDateTime fecha){
        return new UsuarioCurso(usuario_id, curso_id, fecha);
    }

    // Getters
    public Integer getUsuarioId() { return usuario_id; }
    public Integer getCursoId() { return curso_id; }
    public LocalDateTime getFecha() { return fecha; }
}
