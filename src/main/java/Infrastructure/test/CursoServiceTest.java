package Infrastructure.test;

import Application.dtos.curso.EstructuraCursoResponse;
import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.SeccionResponse;
import Application.services.CursoService;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.CursoRepository;
import Infrastructure.repositories.LeccionRepository;
import Infrastructure.repositories.ProgresoRepository;

public class CursoServiceTest {

    public static void main(String[] args) {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        // Instancia tus repositorios reales (no fakes)
        InterfazCursoRepository cursoRepo = new CursoRepository(connMgr);
        InterfazLeccionRepository leccionRepo = new LeccionRepository(connMgr);
        InterfazProgresoRepository progresoRepo = new ProgresoRepository(connMgr);

        // Crea el servicio con los repos reales
        CursoService service = new CursoService(cursoRepo, leccionRepo, progresoRepo);

        // Datos de prueba (IDs que existan en tu base de datos)
        Integer usuarioId = 1;
        Integer cursoId = 2;

        try {
            EstructuraCursoResponse resp = service.obtenerEstructuraCurso(usuarioId, cursoId);

            System.out.println("Curso: " + resp.tituloCurso());
            for (SeccionResponse s : resp.secciones()) {
                System.out.println(" - " + s.tituloSeccion());
                for (LeccionResponse l : s.lecciones()) {
                    System.out.printf("   [%s] %s | Estado: %s | Desbloqueada: %s%n",
                            l.tipoContenido(), l.titulo(), l.estado(), l.desbloqueada());
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener el curso: " + e.getMessage());
        }
    }
}
