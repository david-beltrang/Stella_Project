package Infrastructure.controllers;

import Application.services.*;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.config.AppServices;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerControladores {

    private static final Logger logger =
            LoggerFactory.getLogger(ControllerControladores.class);

    // ======== Servicios ========
    private final SeccionesService seccionesService;
    private final ListarCursosService listarCursosService;
    private final PomodoroTimer pomodoroTimer;
    private final SesionPomodoroService sesionPomodoroService;
    private final LoginService loginService;
    private final RegistroService registroService;
    private final LeccionService leccionService;
    private final TiendaService tiendaService;
    private final PerfilService perfilService;
    private final ChatbotService chatbotService;
    private final UsuarioStatsService usuarioStatsService;
    private final EjercicioService ejercicioService;
    private final PruebaService pruebaService;

    // ======== Controladores principales ========
    private HelloController helloController;
    private PrincipalController principalController;
    private PomodoroController pomodoroController;
    private RegistroController registroController;
    private LoginController loginController;
    private CursoController cursoController;
    private LeccionController leccionController;
    private QuizController quizController;
    private TiendaController tiendaController;
    private PerfilController perfilController;
    private ForoController foroController;
    private InventarioAvatarController inventarioAvatarController;
    private PomodoroDescansoController pomodoroDescansoController;
    private IgluController igluController;

    // ======== Factory global ========
    private Function<Class<?>, Object> factory;


    public ControllerControladores(
            SeccionesService seccionesService,
            ListarCursosService listarCursosService,
            PomodoroTimer pomodoroTimer,
            SesionPomodoroService sesionPomodoroService,
            LoginService loginService,
            RegistroService registroService,
            LeccionService leccionService,
            TiendaService tiendaService,
            ChatbotService chatbotService,
            PerfilService perfilService,
            UsuarioStatsService usuarioStatsService,
            EjercicioService ejercicioService,
            PruebaService pruebaService
    ) {
        this.seccionesService = seccionesService;
        this.listarCursosService = listarCursosService;
        this.pomodoroTimer = pomodoroTimer;
        this.sesionPomodoroService = sesionPomodoroService;
        this.loginService = loginService;
        this.registroService = registroService;
        this.leccionService = leccionService;
        this.tiendaService = tiendaService;
        this.chatbotService = chatbotService;
        this.perfilService = perfilService;
        this.usuarioStatsService = usuarioStatsService;
        this.ejercicioService = ejercicioService;
        this.pruebaService = pruebaService;

        inicializar();
    }



    private void inicializar() {

        // ======== Crear instancias únicas ========
        helloController = new HelloController();
        principalController = new PrincipalController(listarCursosService, seccionesService, usuarioStatsService);
        pomodoroController = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        loginController = new LoginController(loginService);
        registroController = new RegistroController(registroService);
        cursoController = new CursoController(seccionesService, leccionService, pruebaService);
        leccionController = new LeccionController(leccionService, ejercicioService, usuarioStatsService);
        quizController = new QuizController(pruebaService, usuarioStatsService);
        tiendaController = new TiendaController(tiendaService, usuarioStatsService);
        perfilController = new PerfilController(perfilService, usuarioStatsService);
        foroController = new ForoController(usuarioStatsService, AppServices.foroService());
        inventarioAvatarController = new InventarioAvatarController(tiendaService, usuarioStatsService);
        pomodoroDescansoController = new PomodoroDescansoController(pomodoroTimer);
        igluController = new IgluController(sesionPomodoroService);


        // ======== Factory ========
        factory = clazz -> {

            // ----------- Controladores principales (singletons) -----------
            if (clazz == HelloController.class) return helloController;
            if (clazz == PrincipalController.class) return principalController;
            if (clazz == PomodoroController.class) return pomodoroController;
            if (clazz == RegistroController.class) return registroController;
            if (clazz == LoginController.class) return loginController;
            if (clazz == CursoController.class) return cursoController;
            if (clazz == LeccionController.class) return leccionController;
            if (clazz == QuizController.class) return quizController;
            if (clazz == TiendaController.class) return tiendaController;
            if (clazz == PerfilController.class) return perfilController;
            if (clazz == ForoController.class) return foroController;
            if (clazz == InventarioAvatarController.class) return inventarioAvatarController;
            if (clazz == PomodoroDescansoController.class) return pomodoroDescansoController;
            if (clazz == IgluController.class) return igluController;

            // ----------- Controladores con dependencias dinámicas -----------
            if (clazz == ChatbotController.class)
                return new ChatbotController(chatbotService);

            // ----------- Popups (se crean cada vez) -----------
            if (clazz == SemanaPopupController.class) {
                SemanaPopupController c = new SemanaPopupController();
                c.setSesionService(sesionPomodoroService);
                return c;
            }

            if (clazz == DiasSemanaPopupController.class) {
                DiasSemanaPopupController c = new DiasSemanaPopupController();
                c.setSesionService(sesionPomodoroService);
                return c;
            }

            if (clazz == ResponderForoController.class) {
                ResponderForoController c = new ResponderForoController(AppServices.foroService());
                c.setControllerFactory(factory);
                return c;
            }

            if (clazz == ProductoTiendaController.class) {
                ProductoTiendaController c = new ProductoTiendaController(tiendaService);
                c.setControllerFactory(factory);
                return c;
            }

            // ----------- Fallback -----------
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                        "No se pudo crear el controlador: " + clazz.getName(), e);
            }
        };


        // ======== Asignar factory ========
        helloController.setControllerFactory(factory);
        principalController.setControllerFactory(factory);
        pomodoroController.setControllerFactory(factory);
        registroController.setControllerFactory(factory);
        loginController.setControllerFactory(factory);
        cursoController.setControllerFactory(factory);
        leccionController.setControllerFactory(factory);
        quizController.setControllerFactory(factory);
        tiendaController.setControllerFactory(factory);
        perfilController.setControllerFactory(factory);
        foroController.setControllerFactory(factory);
        inventarioAvatarController.setControllerFactory(factory);
        pomodoroDescansoController.setControllerFactory(factory);
        igluController.setControllerFactory(factory);
    }



    // ======== Getters ========
    public PrincipalController getPrincipalController() {
        return principalController;
    }

    public CursoController getCursoController() {
        return cursoController;
    }

    public Function<Class<?>, Object> controllerFactory() {
        return factory;
    }

    // ======== Vista inicial ========
    public void mostrarVistaInicial(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
            loader.setControllerFactory(clazz -> this.controllerFactory().apply(clazz));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            logger.error("Error al cargar la vista inicial", e);
            throw new RuntimeException("No se pudo cargar la vista inicial", e);
        }
    }
}