package Infrastructure.test_temporal;

import Application.dtos.Listado_Cursos.CursoResponse;
import Application.dtos.Listado_Cursos.InscripcionRequest;
import Application.services.ListarCursosService;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioCursoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.CursoRepository;
import Infrastructure.repositories.UsuarioCursoRepository;
import Application.dtos.Listado_Cursos.CursosResponse;

import java.util.List;

public class ListarCursosServiceTest {
    public static void main(String[] args) {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();
        System.out.println("Inicialización completada, iniciando prueba...");

        InterfazCursoRepository cursoRepository = new CursoRepository(connMgr);
        InterfazUsuarioCursoRepository usuarioCursoRepository = new UsuarioCursoRepository(connMgr);

        ListarCursosService service = new ListarCursosService(cursoRepository, usuarioCursoRepository);
        System.out.println("Prueba para listar todos los cursos disponibles y los cursos del usuario");
        System.out.println("Primero la idea es que el usuario no tenga cursos");

        Integer usuarioId = 1;
        try {
            CursosResponse respuestaInicial = service.obtenerCursosCompletos(usuarioId);
            System.out.println("Cursos del usuario:");
            if (respuestaInicial.cursosUsuario().isEmpty()) {
                System.out.println("El usuario no ha agregado ningún curso.");
            } else {
                List<CursoResponse> cursos_del_usuario = respuestaInicial.cursosUsuario();
                for (CursoResponse c : cursos_del_usuario) {
                    System.out.println(c.titulo());
                }
            }
            System.out.println("Todos los cursos disponibles:");
            List<CursoResponse> cursos_del_sistema = respuestaInicial.cursosDisponibles();
            for (CursoResponse c : cursos_del_sistema) {
                System.out.println(c.titulo());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Ahora se prueba que se actualice la lista correctamente al inscribir un curso");
        InscripcionRequest inscripcion = new InscripcionRequest(usuarioId, 1);
        try {
            CursosResponse respuestaPostInscripcion = service.inscribirCurso(inscripcion);
            System.out.println("\nPrueba 2 - Cursos del usuario después de inscribirse:");
            if (respuestaPostInscripcion.cursosUsuario().isEmpty()) {
                System.out.println("El usuario no ha agregado ningún curso.");
            } else {
                List<CursoResponse> cursos_del_usuario = respuestaPostInscripcion.cursosUsuario();
                for (CursoResponse c : cursos_del_usuario) {
                    System.out.println(c.titulo());
                }
            }
            System.out.println("Todos los cursos disponibles después de inscribirse:");
            List<CursoResponse> cursos_del_sistema = respuestaPostInscripcion.cursosDisponibles();
            for (CursoResponse c : cursos_del_sistema) {
                System.out.println(c.titulo());
            }
        } catch (Exception e) {
            System.out.println("Error durante la inscripción: " + e.getMessage());
        }




    }
}
