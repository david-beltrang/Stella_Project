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
    private static final Logger logger = LoggerFactory.getLogger(ControllerControladores.class);

    // ======== Servicios =======
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

    // ======== Controladores ========
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
            EjercicioService ejercicioService) {
        this.seccionesService = Objects.requireNonNull(seccionesService);
        this.listarCursosService = Objects.requireNonNull(listarCursosService);
        this.pomodoroTimer = Objects.requireNonNull(pomodoroTimer);
        this.sesionPomodoroService = Objects.requireNonNull(sesionPomodoroService);
        this.loginService = Objects.requireNonNull(loginService);
        this.registroService = Objects.requireNonNull(registroService);
        this.leccionService = Objects.requireNonNull(leccionService);
        this.tiendaService = Objects.requireNonNull(tiendaService);
        this.chatbotService = Objects.requireNonNull(chatbotService);
        this.perfilService = Objects.requireNonNull(perfilService);
        this.usuarioStatsService = Objects.requireNonNull(usuarioStatsService);
        this.ejercicioService = Objects.requireNonNull(ejercicioService);
        inicializar();
    }

    private void inicializar() {
        // === Instanciación con dependencias ===
        this.helloController = new HelloController();
        this.principalController = new PrincipalController(listarCursosService, seccionesService, usuarioStatsService);
        this.pomodoroController = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        this.loginController = new LoginController(loginService);
        this.registroController = new RegistroController(registroService);
        this.cursoController = new CursoController(seccionesService, leccionService);
        this.leccionController = new LeccionController(leccionService, ejercicioService);
        this.quizController = new QuizController();
        this.tiendaController = new TiendaController(tiendaService, usuarioStatsService);
        this.perfilController = new PerfilController(perfilService, usuarioStatsService);
        this.foroController = new ForoController(usuarioStatsService, AppServices.foroService());
        this.inventarioAvatarController = new InventarioAvatarController(tiendaService, usuarioStatsService);
        this.pomodoroDescansoController = new PomodoroDescansoController(pomodoroTimer);

        // === Factory global ===
        this.factory = (Class<?> clazz) -> {
            if (clazz == ChatbotController.class)
                return new ChatbotController(chatbotService);
            if (clazz == HelloController.class)
                return helloController;
            if (clazz == PrincipalController.class)
                return principalController;
            if (clazz == PomodoroController.class)
                return pomodoroController;
            if (clazz == RegistroController.class)
                return registroController;
            if (clazz == LoginController.class)
                return loginController;
            if (clazz == CursoController.class)
                return cursoController;
            if (clazz == LeccionController.class)
                return leccionController;
            if (clazz == QuizController.class)
                return quizController;
            if (clazz == TiendaController.class)
                return tiendaController;
            if (clazz == PerfilController.class)
                return perfilController;
            if (clazz == ForoController.class)
                return foroController;
            if (clazz == InventarioAvatarController.class)
                return inventarioAvatarController;
            if (clazz == PomodoroDescansoController.class)
                return pomodoroDescansoController;
            if (clazz == ResponderForoController.class)
                return new ResponderForoController(AppServices.foroService());
            if (clazz == ProductoTiendaController.class)
                return new ProductoTiendaController(tiendaService);
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo crear el controlador: " + clazz.getName(), e);
            }
        };

        // === Asignar factory a todos los controladores ===
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
        } catch (Exception e) {
            logger.error("Error al cargar la vista inicial", e);
            throw new RuntimeException("No se pudo cargar la vista inicial", e);
        }
    }
}