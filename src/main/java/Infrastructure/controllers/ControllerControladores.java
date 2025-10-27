package Infrastructure.controllers;

import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.LeccionService;
import Application.services.ListarCursosService;
import Application.services.PomodoroTimer;
import Application.services.SesionEstudioService;
import Application.services.SesionPomodoroService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Function;

/**
 * Controlador principal (orquestador de controladores).
 * Se encarga de crear los controladores de presentación,
 * inyectarles sus servicios y enlazarlos con la factory global
 * que usa el FXMLLoader para mantener la misma instancia por pantalla.
 */
public class ControllerControladores {

    // ======== Servicios de aplicación ========
    private final LeccionService leccionService;
    private final ListarCursosService listarCursosService;
    private final PomodoroTimer pomodoroTimer;
    private final SesionEstudioService sesionEstudioService;
    private final SesionPomodoroService sesionPomodoroService;
    private final LoginService loginService;
    private final RegistroService registroService;

    // ======== Controladores ========
    private HelloController helloController;
    private PrincipalController principalController;
    private PomodoroController pomodoroController;
    private RegistroController registroController;
    private LoginController loginController;

    // ======== Factory central (para FXMLLoader) ========
    private Function<Class<?>, Object> factory;

    // ======== Constructor (inyecta todos los servicios) ========
    public ControllerControladores(LeccionService leccionService,
                                   ListarCursosService listarCursosService,
                                   PomodoroTimer pomodoroTimer,
                                   SesionEstudioService sesionEstudioService,
                                   SesionPomodoroService sesionPomodoroService,
                                   LoginService loginService,
                                   RegistroService registroService) {
        this.leccionService        = Objects.requireNonNull(leccionService, "leccionService requerido");
        this.listarCursosService   = Objects.requireNonNull(listarCursosService, "listarCursosService requerido");
        this.pomodoroTimer         = Objects.requireNonNull(pomodoroTimer, "pomodoroTimer requerido");
        this.sesionEstudioService  = Objects.requireNonNull(sesionEstudioService, "sesionEstudioService requerido");
        this.sesionPomodoroService = Objects.requireNonNull(sesionPomodoroService, "sesionPomodoroService requerido");
        this.loginService          = Objects.requireNonNull(loginService, "loginService requerido");
        this.registroService       = Objects.requireNonNull(registroService, "registroService requerido");

        inicializar();
    }



    // ======== Inicialización de controladores ========
    private void inicializar() {
        // ---- Controladores sin dependencias
        this.helloController  = new HelloController();

        // ---- Controladores con dependencias
        this.principalController = new PrincipalController(listarCursosService, leccionService);
        this.pomodoroController  = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        this.loginController     = new LoginController(loginService);
        this.registroController  = new RegistroController(registroService);

        // ---- Construcción de la factory global ----
        this.factory = (Class<?> clazz) -> {
            try {
                if (clazz == HelloController.class)      return helloController;
                if (clazz == PrincipalController.class)  return principalController;
                if (clazz == PomodoroController.class)   return pomodoroController;
                if (clazz == RegistroController.class)   return registroController;
                if (clazz == LoginController.class)      return loginController;

                // fallback por si se carga un controlador no registrado aquí
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo crear controlador: " + clazz.getName(), e);
            }
        };

        // ---- Inyectar la factory en los controladores que navegan ----
        helloController.setControllerFactory(factory);
        loginController.setControllerFactory(factory);
        registroController.setControllerFactory(factory);
        pomodoroController.setControllerFactory(factory);
        // principalController y stellaController podrían necesitarla si también navegan
    }

    // ======== Getters para acceso externo ========
    public HelloController getHelloController()         { return helloController; }
    public PrincipalController getPrincipalController() { return principalController; }
    public PomodoroController getPomodoroController()   { return pomodoroController; }
    public RegistroController getRegistroController()   { return registroController; }
    public LoginController getLoginController()         { return loginController; }

    // ======== Factory pública (para FXMLLoader) ========
    public Function<Class<?>, Object> controllerFactory() {
        return this.factory;
    }

    // ======== Reinicializar controladores (opcional) ========
    public void reinicializar() {
        inicializar();
    }

    public void mostrarVistaInicial(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));

            // Envolvemos la Function en un Callback, que es lo que FXMLLoader requiere
            loader.setControllerFactory(clazz -> this.controllerFactory().apply(clazz));

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println("❌ Error al cargar la vista inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
