package Infrastructure.controllers;

import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.SeccionesService;
import Application.services.ListarCursosService;
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;
import Application.services.LeccionService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Function;

public class ControllerControladores {

    // ======== Servicios ========
    private final SeccionesService seccionesService;
    private final ListarCursosService listarCursosService;
    private final PomodoroTimer pomodoroTimer;
    private final SesionPomodoroService sesionPomodoroService;
    private final LoginService loginService;
    private final RegistroService registroService;
    private final LeccionService leccionService;

    // ======== Controladores ========
    private HelloController helloController;
    private PrincipalController principalController;
    private PomodoroController pomodoroController;
    private RegistroController registroController;
    private LoginController loginController;
    private CursoController cursoController;
    private LeccionController leccionController;
    private QuizController quizController; // ✅ Nuevo

    // ======== Factory global ========
    private Function<Class<?>, Object> factory;

    public ControllerControladores(
            SeccionesService seccionesService,
            ListarCursosService listarCursosService,
            PomodoroTimer pomodoroTimer,
            SesionPomodoroService sesionPomodoroService,
            LoginService loginService,
            RegistroService registroService,
            LeccionService leccionService
    ) {
        this.seccionesService = Objects.requireNonNull(seccionesService);
        this.listarCursosService = Objects.requireNonNull(listarCursosService);
        this.pomodoroTimer = Objects.requireNonNull(pomodoroTimer);
        this.sesionPomodoroService = Objects.requireNonNull(sesionPomodoroService);
        this.loginService = Objects.requireNonNull(loginService);
        this.registroService = Objects.requireNonNull(registroService);
        this.leccionService = Objects.requireNonNull(leccionService);
        inicializar();
    }

    private void inicializar() {
        // === Instanciación con dependencias ===
        this.helloController = new HelloController();
        this.principalController = new PrincipalController(listarCursosService, seccionesService);
        this.pomodoroController = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        this.loginController = new LoginController(loginService);
        this.registroController = new RegistroController(registroService);
        this.cursoController = new CursoController(seccionesService, leccionService);
        this.leccionController = new LeccionController(leccionService);
        this.quizController = new QuizController(); // ✅ agregado

        // === Factory global ===
        this.factory = (Class<?> clazz) -> {
            if (clazz == HelloController.class) return helloController;
            if (clazz == PrincipalController.class) return principalController;
            if (clazz == PomodoroController.class) return pomodoroController;
            if (clazz == RegistroController.class) return registroController;
            if (clazz == LoginController.class) return loginController;
            if (clazz == CursoController.class) return cursoController;
            if (clazz == LeccionController.class) return leccionController;
            if (clazz == QuizController.class) return quizController; // ✅ agregado
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
        quizController.setControllerFactory(factory); // ✅ agregado
    }

    // ======== Getters ========
    public PrincipalController getPrincipalController() { return principalController; }
    public CursoController getCursoController() { return cursoController; }
    public Function<Class<?>, Object> controllerFactory() { return factory; }

    // ======== Vista inicial ========
    public void mostrarVistaInicial(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));
            loader.setControllerFactory(clazz -> this.controllerFactory().apply(clazz));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("❌ Error al cargar la vista inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }
}