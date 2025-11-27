package Main;

import Application.services.*;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.config.AppServices;

import Domain.repositoriesInterfaces.*;
import Infrastructure.controllers.ControllerControladores;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.*;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Base de datos
        var connMgr = ConexionBD.getInstance();
        var initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        // Repositorios
        InterfazUsuarioRepository usuarioRepository = new UsuarioRepository(connMgr);
        InterfazCursoRepository cursoRepository = new CursoRepository(connMgr);
        InterfazSeccionRepository seccionRepository = new SeccionRepository(connMgr);
        InterfazLeccionRepository leccionRepository = new LeccionRepository(connMgr);
        InterfazUsuarioCursoRepository usuarioCursoRepository = new UsuarioCursoRepository(connMgr);
        InterfazUsuarioStatsRepository usuarioStatsRepository = new UsuarioStatsRepository(connMgr);
        InterfazSesionEstudioRepository sesionEstudioRepository = new SesionEstudioRepository(connMgr);
        InterfazItemRepository itemRepository = new ItemRepository(connMgr);
        InterfazStellaItemRepository stellaItemRepository = new StellaItemRepository(connMgr);
        InterfazUsuarioItemRepository usuarioItemRepository = new UsuarioItemRepository(connMgr);
        InterfazProgresoEstudioRepository progresoEstudioRepository = new ProgresoEstudioRepository(connMgr);
        InterfazPreguntaForoRepository preguntaForoRepository = new PreguntaForoRepository(connMgr);
        InterfazRespuestaForoRepository respuestaForoRepository = new RespuestaForoRepository(connMgr);
        InterfazEjercicioRepository ejercicioRepository = new EjercicioRepository(connMgr);
        InterfazPruebaRepository pruebaRepository = new PruebaRepository(connMgr);
        InterfazIntentoRepository intentoRepository = new IntentoRepository(connMgr);
        InterfazOpcionRepository opcionRepository = new OpcionRepository(connMgr);

        // ======== Servicios ========
        SeccionesService seccionesService = new SeccionesService(seccionRepository);
        ListarCursosService listarCursosService = new ListarCursosService(cursoRepository,
                usuarioCursoRepository);
        PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
        SesionPomodoroService sesionPomodoroService = new SesionPomodoroService(sesionEstudioRepository);
        LoginService loginService = new LoginService(usuarioRepository);
        RegistroService registroService = new RegistroService(usuarioRepository, usuarioItemRepository, usuarioStatsRepository);
        LeccionService leccionService = new LeccionService(leccionRepository);
        UsuarioStellaService usuarioStellaService = new UsuarioStellaService(usuarioItemRepository,
                stellaItemRepository);
        UsuarioStatsService usuarioStatsService = new UsuarioStatsService(usuarioItemRepository,
                usuarioStellaService);
        TiendaService tiendaService = new TiendaService(itemRepository, stellaItemRepository,
                usuarioItemRepository,
                usuarioStellaService);
        ChatbotService chatbotService = new ChatbotService();
        PerfilService perfilService = new PerfilService(usuarioRepository, usuarioCursoRepository,
                cursoRepository,
                usuarioStellaService);
        PreguntasRespuestasForoService foroService = new PreguntasRespuestasForoService(
                preguntaForoRepository, respuestaForoRepository);
        CodeExecutionService codeExecutionService = new CodeExecutionService();
        EjercicioService ejercicioService = new EjercicioService(ejercicioRepository, codeExecutionService);
        PruebaService pruebaService = new PruebaService(pruebaRepository, intentoRepository, opcionRepository);

        // ======== Inicializar AppServices ========
        AppServices.initForo(foroService);
        AppServices.initCursos(listarCursosService, seccionesService);
        AppServices.initTienda(tiendaService, usuarioStatsService, usuarioStellaService);
        AppServices.initPomodoro(sesionPomodoroService, pomodoroTimer);
        AppServices.init(loginService);
        AppServices.initRegistro(registroService);

        // ======== Controladores ========
        ControllerControladores controllerControladores = new ControllerControladores(
                seccionesService,
                listarCursosService,
                pomodoroTimer,
                sesionPomodoroService,
                loginService,
                registroService,
                leccionService,
                tiendaService,
                chatbotService,
                perfilService,
                usuarioStatsService,
                ejercicioService,
                pruebaService);

        // ======== Configurar Stage ========
        stage.setResizable(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.setTitle("Stella");

        // ======== Mostrar vista inicial ========
        controllerControladores.mostrarVistaInicial(stage);

        stage.centerOnScreen();
        stage.show();

        // ======== Shutdown Hook: Eliminar BD al cerrar ========
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("stella.mv.db"));
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get("stella.trace.db"));
                System.out.println("Base de datos eliminada correctamente");
            } catch (Exception e) {
                System.err.println("Error eliminando base de datos: " + e.getMessage());
            }
        }));
    }

    public static void main(String[] args) {
        launch();
    }
}