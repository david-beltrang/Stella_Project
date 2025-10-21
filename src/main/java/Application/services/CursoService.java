package Application.services;

import Application.dtos.Listado_Cursos.CursoResponse;
import Application.dtos.Listado_Cursos.CursosResponse;
import Application.dtos.Listado_Cursos.InscripcionRequest;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioCursoRepository;
import Domain.models.Curso;
import Domain.models.UsuarioCurso;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursoService {

    private final InterfazCursoRepository cursoRepository;
    private final InterfazUsuarioCursoRepository usuarioCursoRepository;

    public CursoService(InterfazCursoRepository cursoRepository,
                                InterfazUsuarioCursoRepository usuarioCursoRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioCursoRepository = usuarioCursoRepository;
    }

    public CursosResponse obtenerCursosCompletos(Integer usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        //CURSOS DEL USUARIO
        List<UsuarioCurso> usuarioCursos = usuarioCursoRepository.encontrarPorUsuarioId(usuarioId);
        List<CursoResponse> cursosUsuario = new ArrayList<>();
        for (UsuarioCurso uc : usuarioCursos) {
            Optional<Curso> cursoOpt = cursoRepository.buscarPorId(uc.getCursoId());
            if (cursoOpt.isEmpty()){
                throw new IllegalArgumentException("Curso no encontrado");
            }
            Curso curso = cursoOpt.get();
            cursosUsuario.add(new CursoResponse(
                    curso.getId(), curso.getTitulo().valorTitulo(), curso.getDescripcion(),
                    curso.getNivel().valor(), curso.getCategoria(), curso.getDuracionMinutos()));
        }

        //TODOS LOS CURSOS
        List<Curso> cursosNoCursados = cursoRepository.encontrarCursosNoCursadosPorUsuarioId(usuarioId);
        List<CursoResponse> cursosDisponibles = new ArrayList<>();
        for (Curso curso : cursosNoCursados) {
            cursosDisponibles.add(new CursoResponse(
                    curso.getId(), curso.getTitulo().valorTitulo(), curso.getDescripcion(),
                    curso.getNivel().valor(), curso.getCategoria(), curso.getDuracionMinutos()));
        }

        return new CursosResponse(
                cursosUsuario,
                cursosDisponibles
        );
    }

    public CursosResponse inscribirCurso(InscripcionRequest request) {
        if (request == null || request.usuario_id() <= 0 || request.curso_id() <= 0) {
            throw new IllegalArgumentException("Datos de inscripción inválidos");
        }

        Integer usuario_id = request.usuario_id();
        Integer curso_id = request.curso_id();

        if (usuarioCursoRepository.existeInscripcion(usuario_id, curso_id)) {
            throw new RuntimeException("Inscripción ya existe");
        }

        UsuarioCurso usuarioCurso = UsuarioCurso.crearInscripcion(usuario_id, curso_id, null);
        usuarioCursoRepository.inscribir(usuarioCurso);
        return obtenerCursosCompletos(usuario_id);
    }
}