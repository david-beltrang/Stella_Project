package Domain.repositoriesInterfaces;

import Application.dtos.Listado_Cursos.DetallesResponse;
import Domain.models.Curso;

import java.util.List;
import java.util.Optional;

public interface InterfazCursoRepository {
    public Optional<Curso> buscarPorId(Integer id);
    public List<Curso> encontrarCursosNoCursadosPorUsuarioId(Integer usuarioId);
    public DetallesResponse verDetallesCurso(Integer cursoId);
}
