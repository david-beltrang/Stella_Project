package Domain.repositoriesInterfaces;

import Domain.models.UsuarioCurso;

import java.util.List;

public interface InterfazUsuarioCursoRepository {
    public void inscribir(UsuarioCurso usuarioCurso);
    public boolean existeInscripcion(Integer usuarioId, Integer cursoId);
    public List<UsuarioCurso> encontrarPorUsuarioId(Integer usuario_id);

}

