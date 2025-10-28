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

    // Factory sirve para saber cual controlador devolver
    private Function<Class<?>, Object> factory;

    // Constructor (inyecta todos los servicios)
    // SE van a guardar en variables y después se llama a inicializar() para crear los controladores
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



    // Inicialización de controladores
    private void inicializar() {
        // Controladores sin dependencias
        this.helloController  = new HelloController();

        // Controladores con dependencias
        this.principalController = new PrincipalController(listarCursosService, leccionService);
        this.pomodoroController  = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        this.loginController     = new LoginController(loginService);
        this.registroController  = new RegistroController(registroService);

        // aquí se decide que controlador devolver según la clase que pida FXMLLoader
        this.factory = (Class<?> clazz) -> {
            try {
                if (clazz == HelloController.class)      return helloController;
                if (clazz == PrincipalController.class)  return principalController;
                if (clazz == PomodoroController.class)   return pomodoroController;
                if (clazz == RegistroController.class)   return registroController;
                if (clazz == LoginController.class)      return loginController;

                // si se pide un controlador no existe se crea de forma manual
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo crear controlador: " + clazz.getName(), e);
            }
        };

        // SE pasa la factory a los controladores que cambian de pantalla
        // Así los controladores pueden cargar otras vistas sin perder la misma instancia de factory
        helloController.setControllerFactory(factory);
        loginController.setControllerFactory(factory);
        registroController.setControllerFactory(factory);
        pomodoroController.setControllerFactory(factory);

    }

    //  Getters para acceso externo
    public HelloController getHelloController()         { return helloController; }
    public PrincipalController getPrincipalController() { return principalController; }
    public PomodoroController getPomodoroController()   { return pomodoroController; }
    public RegistroController getRegistroController()   { return registroController; }
    public LoginController getLoginController()         { return loginController; }

    // Se devuelve la función factory para que el FXMLLoader la use al cargar las vistas
    public Function<Class<?>, Object> controllerFactory() {
        return this.factory;
    }

    // Reinicializar controladores
    public void reinicializar() {
        inicializar();
    }

    public void mostrarVistaInicial(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));

            // FXMLLoader va a espera un "Callback", por eso se adapta esta forma
            loader.setControllerFactory(clazz -> this.controllerFactory().apply(clazz));

            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

        } catch (Exception e) {
            System.err.println(" Error al cargar la vista inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

}