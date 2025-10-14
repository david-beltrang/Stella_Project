package Infrastructure.test;

import Application.dtos.sesionEstudio.Pomodoro.IniciarSesionEstudioRequest;
import Application.dtos.sesionEstudio.Pomodoro.SesionEstudioResponse;
import Application.services.SesionPomodoroService;
import Domain.repositoriesInterfaces.InterfazSesionEstudioRepository;
import Infrastructure.repositories.SesionEstudioRepository;


public class SesionEstudioTest {

    private SesionEstudioResponse sesionIniciada; // Variable de instancia para compartir entre pruebas

    public static void main(String[] args) {

        Infrastructure.persistence.H2DataBaseInitializer.initialize();

        // Instanciar la interfaz con la implementación concreta
        InterfazSesionEstudioRepository sesionRepository = new SesionEstudioRepository();
        SesionPomodoroService sesionService = new SesionPomodoroService(sesionRepository);

        SesionEstudioTest test = new SesionEstudioTest(); // Se intancia el test
        test.ejecutarPruebas(sesionService); // Pasar el servicio como parametro
    }

    // Método de instancia para ejecutar las pruebas
    public void ejecutarPruebas(SesionPomodoroService sesionService) {
        // Prueba 1: Iniciar sesión con tiempo válido
        System.out.println("=== Prueba 2: Iniciar sesión con tiempo válido ===");
        IniciarSesionEstudioRequest iniciarRequest = new IniciarSesionEstudioRequest(42, 25, 5);
        try {
            sesionIniciada = sesionService.iniciarSesion(iniciarRequest);
            System.out.println("Sesión iniciada con éxito: " + sesionIniciada);
            System.out.println("ID de sesión: " + sesionIniciada.id());
            System.out.println("Fecha inicio: " + sesionIniciada.fechaInicio());
        } catch (IllegalArgumentException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }

        // Prueba 2: Finalizar una sesión
        System.out.println("\n=== Prueba 3: Finalizar sesión ===");
        if (sesionIniciada != null) {
            try {
                SesionEstudioResponse sesionFinalizada = sesionService.finalizarSesion(sesionIniciada.id());
                System.out.println("Sesión finalizada con éxito: " + sesionFinalizada);
                System.out.println("Fecha fin: " + sesionFinalizada.fechaFin());
            } catch (IllegalArgumentException e) {
                System.out.println("Error al finalizar sesión: " + e.getMessage());
            }
        } else {
            System.out.println("No se pudo probar finalizar: sesión no iniciada.");
        }

        // Prueba 3: Iniciar otra sesión
        System.out.println("\n=== Prueba 1: Iniciar otra sesión ===");
        IniciarSesionEstudioRequest otraRequest = new IniciarSesionEstudioRequest(42, 30, 5);
        try {
            sesionIniciada = sesionService.iniciarSesion(otraRequest);
            System.out.println("Sesión iniciada con éxito: " + sesionIniciada);
            System.out.println("ID de sesión: " + sesionIniciada.id());
            System.out.println("Fecha inicio: " + sesionIniciada.fechaInicio());
        } catch (IllegalArgumentException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }

        // Prueba 4: Obtener estado de una sesión
        System.out.println("\n=== Prueba 5: Obtener estado de sesión ===");
        if (sesionIniciada != null) {
            try {
                SesionEstudioResponse sesionEstado = sesionService.obtenerEstadoSesion(sesionIniciada.id());
                System.out.println("Estado de sesión: " + sesionEstado);
            } catch (IllegalArgumentException e) {
                System.out.println("Error al obtener estado: " + e.getMessage());
            }
        } else {
            System.out.println("No se pudo probar obtener estado: sesión no iniciada.");
        }

        // Prueba 5: Finalizar sesión no existente
        System.out.println("\n=== Prueba 4: Finalizar sesión no existente ===");
        try {
            sesionService.finalizarSesion(999); // ID inexistente
            System.out.println("Error: Debería haber fallado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        // Prueba 6: Obtener estado de sesión no existente
        System.out.println("\n=== Prueba 6: Obtener estado de sesión no existente ===");
        try {
            sesionService.obtenerEstadoSesion(999); // ID inexistente
            System.out.println("Error: Debería haber fallado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }
    }
}