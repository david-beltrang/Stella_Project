package Infrastructure.test;

import Application.dtos.sesionEstudio.LeccionDetalleResponse;
import Application.dtos.sesionEstudio.*;
import Application.services.LeccionService;
import Application.services.SesionEstudioService;
import Domain.models.ProgresoLeccion;
import Domain.repositoriesInterfaces.*;
import Infrastructure.repositories.*;

import java.util.Optional;
import java.util.List;

/**
 * Clase para pruebas manuales rápidas del SesionEstudioService.
 * Simula el flujo completo (Iniciar, Evaluar, Finalizar) y verifica los resultados en consola.
 * * NOTA: Se asume que los errores de persistencia (racha_dias, logica de Intento, NullPointer)
 * han sido corregidos en las clases UsuarioStatsRepository, SesionEstudioService y ContenidoResponse/ProgresoLeccionRepository.
 */
public class TestSesionEstudioService {

    public static void main(String[] args) {

        // Aseguramos que la BD esté inicializada antes de instanciar cualquier repositorio.
        Infrastructure.persistence.H2DataBaseInitializer.initialize();

        System.out.println("=================================================");
        System.out.println("=== TEST FUNCIONAL: SesionEstudioService (v.1) ==");
        System.out.println("=================================================");

        // 1. Instanciar Repositorios
        InterfazProgresoLeccionRepository progresoRepo = new ProgresoLeccionRepository();
        InterfazLeccionRepository leccionRepo = new LeccionRepository();
        InterfazPruebaRepository pruebaRepo = new PruebaRepository();
        InterfazIntentoRepository intentoRepo = new IntentoRepository();
        InterfazUsuarioStatsRepository statsRepo = new UsuarioStatsRepository();

        // 2. Instanciar el Servicio de Aplicación
        SesionEstudioService estudioService = new SesionEstudioService(
                progresoRepo, leccionRepo, pruebaRepo, intentoRepo, statsRepo
        );
        // Necesitamos un LeccionService para la parte final del requerimiento
        LeccionService leccionService = new LeccionService(leccionRepo, progresoRepo);

        final int USUARIO_ID = 42;

        // Ajuste de IDs para simular el flujo:
        final int LECCION_ID_INICIO = 1;      // Lección de tipo TEORIA
        final int LECCION_ID_PREGUNTA = 3;    // Lección de tipo PREGUNTA
        final int PREGUNTA_ID = 1;            // Pregunta de la Lección 3
        final int OPCION_CORRECTA = 11;       // ID de la Opción 'main()' (Pregunta 1)
        final int OPCION_INCORRECTA = 10;     // ID de la Opción 'start()' (Pregunta 1)

        final int CURSO_ID = 1;
        final int SECCION_ID = 1;

        // --- PRUEBA 1: Iniciar Sesión de TEORÍA (ID: 1) ---
        System.out.println("\n--- PRUEBA 1: Iniciar Sesión de TEORÍA (ID: " + LECCION_ID_INICIO + ") ---");
        LeccionRequest inicioRequest = new LeccionRequest(USUARIO_ID, LECCION_ID_INICIO);
        try {
            ContenidoResponse response = estudioService.iniciarSesion(inicioRequest);
            System.out.println("-> INICIO EXITOSO:");
            System.out.println("   Estado Progreso: " + response.progresoEstado()); // Esperado: EN_PROGRESO
            System.out.println("   Contenido Tipo: " + response.tipoContenido()); // Esperado: TEORIA
            // Asumiendo que se corrigió el NullPointer, debería devolver 0 o una lista vacía.
            System.out.println("   N° Preguntas cargadas: " + (response.preguntas() != null ? response.preguntas().size() : 0));
        } catch (Exception e) {
            System.err.println("-> ERROR INICIO: " + e.getMessage());
        }

        // --- Iniciar la Lección de Pregunta para las Pruebas 2-4 ---
        // Esto es necesario para que el progreso esté en estado EN_PROGRESO para LECCION_ID_PREGUNTA
        try {
            estudioService.iniciarSesion(new LeccionRequest(USUARIO_ID, LECCION_ID_PREGUNTA));
            // System.out.println("   -> Iniciada Lección Pregunta (ID " + LECCION_ID_PREGUNTA + ") para evaluación.");
        } catch (Exception e) {
            System.err.println("   -> Error al pre-iniciar Lección Pregunta: " + e.getMessage());
        }


        // --- PRUEBA 2: Evaluar Respuesta Correcta (Ganar Pescaditos) ---
        System.out.println("\n--- PRUEBA 2: Evaluar Respuesta CORRECTA (Ganar Pescaditos) ---");
        EvaluarRespuestaRequest correctaRequest = new EvaluarRespuestaRequest(
                USUARIO_ID, LECCION_ID_PREGUNTA, PREGUNTA_ID, OPCION_CORRECTA
        );
        try {
            EvaluacionResponse response = estudioService.evaluarRespuesta(correctaRequest);
            System.out.println("-> EVALUACIÓN CORRECTA:");
            System.out.println("   Resultado: " + (response.esCorrecta() ? "CORRECTO" : "INCORRECTO"));
            System.out.println("   Pescaditos Ganados: " + response.puntosObtenidos()); // Esperado: 5 (por ser correcta)
            // Se asume que el servicio NO INTENTA insertar en 'intento' para una lección de tipo PREGUNTA (ID 3).
        } catch (Exception e) {
            System.err.println("-> ERROR EVALUACIÓN: " + e.getMessage());
        }

        // --- PRUEBA 3: Evaluar Respuesta Incorrecta ---
        System.out.println("\n--- PRUEBA 3: Evaluar Respuesta INCORRECTA ---");
        EvaluarRespuestaRequest incorrectaRequest = new EvaluarRespuestaRequest(
                USUARIO_ID, LECCION_ID_PREGUNTA, PREGUNTA_ID, OPCION_INCORRECTA
        );
        try {
            EvaluacionResponse response = estudioService.evaluarRespuesta(incorrectaRequest);
            System.out.println("-> EVALUACIÓN INCORRECTA:");
            System.out.println("   Resultado: " + (response.esCorrecta() ? "CORRECTO" : "INCORRECTO"));
            System.out.println("   Pescaditos Ganados: " + response.puntosObtenidos()); // Esperado: 0
        } catch (Exception e) {
            System.err.println("-> ERROR EVALUACIÓN: " + e.getMessage());
        }

        // --- PRUEBA 4: Finalizar Sesión (Marcar COMPLETADA y Actualizar Racha) ---
        System.out.println("\n--- PRUEBA 4: Finalizar Sesión y Stats ---");
        FinalizarSesionRequest finalizarRequest = new FinalizarSesionRequest(
                USUARIO_ID, LECCION_ID_PREGUNTA, 600, 30 // Usamos la Leccion Pregunta
        );
        try {
            estudioService.finalizarSesion(finalizarRequest);
            System.out.println("-> FINALIZACIÓN EXITOSA: Se ejecutaron los updates de progreso y stats.");

            // Verificación manual del Progreso
            Optional<ProgresoLeccion> progresoFinal = progresoRepo.buscarPorUsuarioYLeccion(USUARIO_ID, LECCION_ID_PREGUNTA);
            System.out.println("   Estado final del progreso (Repo): " + progresoFinal.map(p -> p.getEstado().valor()).orElse("NO ENCONTRADO")); // Esperado: COMPLETADA

        } catch (Exception e) {
            System.err.println("-> ERROR FINALIZAR: " + e.getMessage());
        }

        // --- PRUEBA 5: Intentar iniciar sesión YA COMPLETADA ---
        System.out.println("\n--- PRUEBA 5: Intentar iniciar sesión YA COMPLETADA ---");
        try {
            estudioService.iniciarSesion(new LeccionRequest(USUARIO_ID, LECCION_ID_PREGUNTA));
            System.err.println("-> ERROR: Esta prueba DEBIÓ fallar (no se puede iniciar COMPLETADA).");
        } catch (IllegalArgumentException e) {
            System.out.println("-> FALLO ESPERADO: " + e.getMessage()); // Esperado: IllegalStateException capturada
        }

        // =========================================================
        // === REQUERIMIENTO FINAL: Listar Contenido de la Sección ===
        // =========================================================
        System.out.println("\n=======================================================");
        System.out.println("=== CONTENIDO COMPLETO DE SECCIÓN " + SECCION_ID + " (CURSO " + CURSO_ID + ") ===");
        System.out.println("=======================================================");

        try {
            List<LeccionDetalleResponse> lecciones = leccionService.obtenerLeccionesPorSeccion(USUARIO_ID, CURSO_ID, SECCION_ID);

            if (lecciones.isEmpty()) {
                System.out.println("-> ERROR: No se encontraron lecciones para la sección.");
            } else {
                for (LeccionDetalleResponse leccion : lecciones) {
                    System.out.println("-----------------------------------------------------------------");
                    System.out.println("ID: " + leccion.id() +
                            " | Orden: " + leccion.orden() +
                            " | Tipo: " + leccion.tipoContenido() +
                            " | Progreso: " + leccion.estadoProgreso());
                    System.out.println("   Título: " + leccion.titulo());
                    System.out.println("   Contenido/HTML (Snippet): " + (leccion.contenidoHtml() != null ? leccion.contenidoHtml().substring(0, Math.min(leccion.contenidoHtml().length(), 50)) + "..." : "N/A"));

                    // Solo si hay preguntas
                    if (leccion.preguntas() != null && !leccion.preguntas().isEmpty()) {
                        System.out.println("   [!] Contiene " + leccion.preguntas().size() + " preguntas.");
                    }
                    // Mostrar progreso específico
                    if (leccion.id() == LECCION_ID_PREGUNTA) {
                        System.out.println("   [!] Esta lección fue COMPLETADA en las pruebas.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("-> ERROR AL OBTENER SECCIÓN: " + e.getMessage());
        }
    }
}