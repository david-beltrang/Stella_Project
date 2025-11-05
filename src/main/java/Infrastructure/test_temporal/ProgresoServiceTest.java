package Infrastructure.test_temporal;

import Application.dtos.progreso.ProgresoRequest;
import Application.dtos.progreso.ProgresoCursoResponse;
import Application.services.ProgresoService;
import Domain.models.LeccionValueObjects.Estado;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.ProgresoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProgresoServiceTest {

    public static void main(String[] args) {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        System.out.println("\nPRUEBAS DE ProgresoService (Estado: PENDIENTE, EN_PROGRESO, COMPLETADA)\n");

        InterfazProgresoRepository repo = new ProgresoRepository(connMgr);
        ProgresoService progresoService = new ProgresoService(repo);

        int usuarioId = 1;
        int leccionId1 = 1;   // Lección 1 del curso 1
        int leccionId2 = 3;   // Lección 3 del curso 1 (VIDEO)
        int cursoId = 1;

        // === PRUEBA 1: Marcar primera lección como COMPLETADA ===
        System.out.println("PRUEBA 1: Marcar lección 1 como COMPLETADA");
        ejecutarMarcarCompletada(progresoService, usuarioId, leccionId1);

        // === PRUEBA 2: Marcar misma lección otra vez (debe ser UPDATE, no error) ===
        System.out.println("PRUEBA 2: Volver a marcar lección 1 como COMPLETADA (idempotente)");
        ejecutarMarcarCompletada(progresoService, usuarioId, leccionId1);

        // === PRUEBA 3: Marcar segunda lección como COMPLETADA ===
        System.out.println("PRUEBA 3: Marcar lección 3 como COMPLETADA");
        ejecutarMarcarCompletada(progresoService, usuarioId, leccionId2);

        // === PRUEBA 4: Progreso del curso (2/15 = 13.33%) ===
        System.out.println("PRUEBA 4: Obtener progreso del curso");
        try {
            ProgresoCursoResponse response = progresoService.obtenerProgresoPorCurso(usuarioId, cursoId);
            System.out.println("  ÉXITO");
            System.out.println("    Curso ID: " + response.cursoId());
            System.out.println("    Porcentaje: " + String.format("%.2f", response.porcentaje()) + "%");
            System.out.println("    (2 de 15 lecciones completadas)");
            System.out.println();
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage() + "\n");
        }

        // === PRUEBA 5: Marcar una lección como EN_PROGRESO (opcional, si tienes el método) ===
        // Descomenta si implementaste marcarEnProgreso()
        /*
        System.out.println("PRUEBA 5: Marcar lección como EN_PROGRESO");
        try {
            repo.marcarEnProgreso(usuarioId, leccionId1);
            var prog = repo.findByUsuarioIdAndLeccionId(usuarioId, leccionId1);
            System.out.println("  ÉXITO: Estado actual = " + prog.get().getEstado());
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
        */

        System.out.println("TODAS LAS PRUEBAS COMPLETADAS.");
    }

    // === MÉTODO AUXILIAR: Marcar completada con salida ===
    private static void ejecutarMarcarCompletada(ProgresoService service, int usuarioId, int leccionId) {
        ProgresoRequest request = new ProgresoRequest(usuarioId, leccionId);
        try {
            var response = service.marcarCompletada(request);
            System.out.println("  ÉXITO");
            System.out.println("    ID: " + response.id());
            System.out.println("    Usuario: " + response.usuarioId());
            System.out.println("    Lección: " + response.leccionId());
            System.out.println("    Estado: " + response.estado());
            System.out.println();
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage() + "\n");
        }
    }
}