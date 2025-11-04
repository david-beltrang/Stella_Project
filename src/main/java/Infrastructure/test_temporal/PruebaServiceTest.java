package Infrastructure.test_temporal;

import Application.dtos.Prueba.*;
import Application.services.PruebaService;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.*;
import Domain.repositoriesInterfaces.*;

import java.util.List;

public class PruebaServiceTest {
    public static void main(String[] args) {

        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();
        System.out.println("Inicialización completada, iniciando prueba...");

        InterfazPruebaRepository pruebaRepo = new PruebaRepository(connMgr);
        InterfazIntentoRepository intentoRepo = new IntentoRepository(connMgr);
        InterfazOpcionRepository opcionRepo = new OpcionRepository(connMgr);

        System.out.println("La idea es crear una prueba con los insert de prueba de un quiz ");
        PruebaService service = new PruebaService(pruebaRepo, intentoRepo, opcionRepo);

        try {

            System.out.println("OBTENIENDO EL QUIZ DE LA SECCIÓN 1...");
            PruebaResponse quiz = service.obtenerQuizPorSeccion(1);
            mostrarQuiz(quiz);

            System.out.println("Se simula una respuesta del usuario");
            IntentoRequest request = new IntentoRequest(
                    1,  // usuario_id
                    1,  // prueba_id
                    List.of(
                            new RespuestaRequest(1, 1),  // Pregunta 1 → opción 1 (correcta)
                            new RespuestaRequest(2, 4)   // Pregunta 2 → opción 6 (incorrecta: string es 4)
                    )
            );

            System.out.println("\nENVIANDO RESPUESTAS...");
            IntentoResponse resultado = service.crearIntento(request);

            //Se muestra el resultado
            mostrarResultado(resultado);

        } catch (Exception e) {
            System.err.println("Error en la simulacion: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void mostrarQuiz(PruebaResponse quiz) {
        System.out.println("QUIZ: " + quiz.titulo());
        System.out.println("Tipo: " + quiz.tipo());
        System.out.println("Preguntas: " + quiz.preguntas().size());
        for (int i = 0; i < quiz.preguntas().size(); i++) {
            PreguntaResponse p = quiz.preguntas().get(i);
            System.out.println((i+1) + ". " + p.enunciado());
            for (OpcionResponse o : p.opciones()) {
                System.out.println("   [ID: " + o.id() + "] " + o.texto());
            }
        }
    }

    private static void mostrarResultado(IntentoResponse r) {
        System.out.println("\nRESULTADO DEL INTENTO");
        System.out.println("ID del intento: " + r.id());
        System.out.println("Puntaje: " + String.format("%.2f", r.puntaje()) + "%");
        System.out.println("Aciertos: " + r.aciertos() + " de " + r.totalPreguntas());
        System.out.println("Mensaje: " + r.mensaje());
    }
}

