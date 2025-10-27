package Domain.repositoriesInterfaces;

import Domain.models.Curso;

import java.util.List;
import java.util.Optional;

public interface InterfazCursoRepository {
    public Optional<Curso> buscarPorId(Integer id);
    public List<Curso> encontrarCursosNoCursadosPorUsuarioId(Integer usuarioId);

}
