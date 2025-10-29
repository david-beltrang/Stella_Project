package Infrastructure.test_temporal;

import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.SeccionResponse;
import Application.services.SeccionesService;
import Domain.repositoriesInterfaces.InterfazSeccionRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.SeccionRepository;

import java.util.List;

public class SeccionesServiceTest {
    public static void main(String[] args) {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();
        System.out.println("Inicialización completada, iniciando prueba...");

        InterfazSeccionRepository seccionRepository = new SeccionRepository(connMgr);
        SeccionesService seccionesService = new SeccionesService(seccionRepository);
        System.out.println("Prueba para listar todas las secciones disponibles y las lecciones del curso seleccionado");
        System.out.println("Primero la idea es listar les secciones y las lecciones del curso con id 1");

        Integer cursoId = 1;
        try {
            List<SeccionResponse> respuestaSecciones = seccionesService.ListarSeccionesConLecciones(cursoId);
            System.out.println("Secciones y lecciones del curso con id " + cursoId + ":");
            for (SeccionResponse seccion : respuestaSecciones) {
                System.out.println("Sección: " + seccion.titulo() + " (número: " + seccion.numeroOrden() + ")");
                for (LeccionResponse leccion : seccion.lecciones()) {
                    System.out.println("  Lección: " + leccion.titulo() + " (numero: " + leccion.numeroOrden() + ")");
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
