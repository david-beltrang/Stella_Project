package Infrastructure.test_temporal;

import Application.dtos.leccion.LeccionResponse;
import Application.services.LeccionService;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.LeccionRepository;

public class LeccionServiceTest {


    public static void main(String[] args) {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        System.out.println("\nPruebas con cursoId + numeroOrdenSeccion + numeroOrdenLeccion\n");

        InterfazLeccionRepository repo = new LeccionRepository(connMgr);
        LeccionService service = new LeccionService(repo);

        int cursoId = 1; // Curso de C++ básico

        // Sección 1: orden 1 → Tipos de datos primitivos
        probar(service, cursoId, 1, 3);
        probar(service, cursoId, 1, 2);

        // Sección 2: orden 2 → Imprimir en consola
        probar(service, cursoId, 2, 1);
        probar(service, cursoId, 2, 2);

        // Caso inexistente
        System.out.println("PRUEBA: cursoId=1, seccion=99, leccion=1");
        try {
            service.obtenerLeccionPorCursoYOrden(1, 99, 1);
            System.out.println("ERROR: Debería fallar");
        } catch (RuntimeException e) {
            System.out.println("ÉXITO: " + e.getMessage() + "\n");
        }
    }

    private static void probar(LeccionService s, int cursoId, int ordenSeccion, int ordenLeccion) {
        System.out.printf("PRUEBA: curso=%d, sección=%d, lección=%d%n", cursoId, ordenSeccion, ordenLeccion);
        try {
            LeccionResponse r = s.obtenerLeccionPorCursoYOrden(cursoId, ordenSeccion, ordenLeccion);
            System.out.println("  ÉXITO");
            System.out.println("    Título: " + r.titulo());
            System.out.println("    Tipo: " + r.tipoContenido());

            if ("VIDEO".equals(r.tipoContenido())) {
                System.out.println("    URL: " + r.url_video());
                System.out.println("    [Reproducir] → https://www.youtube.com/watch?v=" +
                        r.url_video().substring(r.url_video().indexOf("v=") + 2));
            } else {
                String contenido = r.contenido();
                if (contenido != null && !contenido.isEmpty()) {
                    String preview = contenido.length() > 100 ? contenido.substring(0, 100) + "..." : contenido;
                    System.out.println("    Contenido: " + preview);
                } else {
                    System.out.println("    Contenido: <vacío>");
                }
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage() + "\n");
        }
    }
}