package Infrastructure.controllers;

import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.SeccionesService;
import Application.services.ListarCursosService;
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Function;

/**
 * Controlador principal (Front Controller).
 * Centraliza la creación de controladores, inyección de dependencias
 * y manejo de la factory global para mantener instancias consistentes.
 */
public class ControllerControladores {

    // ======== Servicios ========
    private final SeccionesService seccionesService;
    private final ListarCursosService listarCursosService;
    private final PomodoroTimer pomodoroTimer;
    private final SesionPomodoroService sesionPomodoroService;
    private final LoginService loginService;
    private final RegistroService registroService;

    // ======== Controladores ========
    private HelloController helloController;
    private PrincipalController principalController;
    private PomodoroController pomodoroController;
    private RegistroController registroController;
    private LoginController loginController;
    private CursoController cursoController;
    private LeccionController leccionController;

    // Factory global (para FXMLLoader)
    private Function<Class<?>, Object> factory;

    public ControllerControladores(
            SeccionesService seccionesService,
            ListarCursosService listarCursosService,
            PomodoroTimer pomodoroTimer,
            SesionPomodoroService sesionPomodoroService,
            LoginService loginService,
            RegistroService registroService
    ) {
        this.seccionesService = Objects.requireNonNull(seccionesService);
        this.listarCursosService = Objects.requireNonNull(listarCursosService);
        this.pomodoroTimer = Objects.requireNonNull(pomodoroTimer);
        this.sesionPomodoroService = Objects.requireNonNull(sesionPomodoroService);
        this.loginService = Objects.requireNonNull(loginService);
        this.registroService = Objects.requireNonNull(registroService);

        inicializar();
    }

    private void inicializar() {
        // Controladores sin dependencias
        this.helloController = new HelloController();

        // Controladores con dependencias
        this.principalController = new PrincipalController(listarCursosService, seccionesService);
        principalController.inicializarUsuario();
        this.pomodoroController = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        this.loginController = new LoginController(loginService);
        this.registroController = new RegistroController(registroService);
        this.cursoController = new CursoController(seccionesService);
        this.leccionController = new LeccionController(); // ProgresoService aún no se inyecta

        // Factory principal
        this.factory = (Class<?> clazz) -> {
            try {
                if (clazz == HelloController.class) return helloController;
                if (clazz == PrincipalController.class) return principalController;
                if (clazz == PomodoroController.class) return pomodoroController;
                if (clazz == RegistroController.class) return registroController;
                if (clazz == LoginController.class) return loginController;
                if (clazz == CursoController.class) return cursoController;
                if (clazz == LeccionController.class) return leccionController;

                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo crear controlador: " + clazz.getName(), e);
            }
        };

        // Asignar factory a controladores que la necesiten
        helloController.setControllerFactory(factory);
        loginController.setControllerFactory(factory);
        registroController.setControllerFactory(factory);
        pomodoroController.setControllerFactory(factory);
        cursoController.setControllerFactory(factory);
        leccionController.setControllerFactory(factory);
    }

    // ======== Getters ========
    public HelloController getHelloController() { return helloController; }
    public PrincipalController getPrincipalController() { return principalController; }
    public PomodoroController getPomodoroController() { return pomodoroController; }
    public RegistroController getRegistroController() { return registroController; }
    public LoginController getLoginController() { return loginController; }
    public CursoController getCursoController() { return cursoController; }
    public LeccionController getLeccionController() { return leccionController; }

    // Factory para FXMLLoader
    public Function<Class<?>, Object> controllerFactory() { return this.factory; }

    public void reinicializar() { inicializar(); }

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
