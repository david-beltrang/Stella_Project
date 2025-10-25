package Infrastructure.controllers;

import Application.services.DarAcceso.LoginService;
import Application.services.LeccionService;
import Application.services.ListarCursosService;
import Application.services.PomodoroTimer;
import Application.services.SesionEstudioService;
import Application.services.SesionPomodoroService;
import java.util.Objects;
import java.util.function.Function;

public class ControllerControladores {

    // ===== Servicios disponibles (DI) =====
    private final LeccionService leccionService;
    private final ListarCursosService listarCursosService;
    private final PomodoroTimer pomodoroTimer;
    private final SesionEstudioService sesionEstudioService;
    private final SesionPomodoroService sesionPomodoroService;
    private final LoginService loginService;

    // ===== Controladores =====
    private HelloController helloController;
    private PrincipalController principalController;
    private StellaController stellaController;
    private PomodoroController pomodoroController;
    private RegistroController registroController;
    private LoginController loginController;

    // ===== Factory central para FXMLLoader =====
    private Function<Class<?>, Object> factory;

    // ===== Ctor inyectando servicios =====
    public ControllerControladores(LeccionService leccionService,
                                   ListarCursosService listarCursosService,
                                   PomodoroTimer pomodoroTimer,
                                   SesionEstudioService sesionEstudioService,
                                   SesionPomodoroService sesionPomodoroService,
                                   LoginService loginService) {
        this.leccionService        = Objects.requireNonNull(leccionService, "leccionService requerido");
        this.listarCursosService   = Objects.requireNonNull(listarCursosService, "listarCursosService requerido");
        this.pomodoroTimer         = Objects.requireNonNull(pomodoroTimer, "pomodoroTimer requerido");
        this.sesionEstudioService  = Objects.requireNonNull(sesionEstudioService, "sesionEstudioService requerido");
        this.sesionPomodoroService = Objects.requireNonNull(sesionPomodoroService, "sesionPomodoroService requerido");
        this.loginService          = Objects.requireNonNull(loginService, "loginService requerido");
        inicializar();
    }

    private void inicializar() {
        // ---- Controladores sin deps
        this.helloController   = new HelloController();
        this.stellaController  = new StellaController();

        // ---- Controladores con deps
        this.principalController = new PrincipalController(listarCursosService, leccionService);
        this.pomodoroController  = new PomodoroController(sesionPomodoroService, pomodoroTimer);
        this.registroController  = new RegistroController();
        this.loginController     = new LoginController(loginService);

        // ---- Construimos la factory central
        this.factory = (Class<?> clazz) -> {
            try {
                if (clazz == HelloController.class)      return getHelloController();
                if (clazz == PrincipalController.class)  return getPrincipalController();
                if (clazz == StellaController.class)     return getStellaController();
                if (clazz == PomodoroController.class)   return getPomodoroController();
                if (clazz == RegistroController.class)   return getRegistroController();
                if (clazz == LoginController.class)      return getLoginController();

                // Fallback a ctor vacío si aparece un controlador no registrado aquí
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("No se pudo crear controlador: " + clazz.getName(), e);
            }
        };

        // ---- Inyectamos la factory en controladores que navegan
        // (estos setters deben existir en los controladores;)
        try {
            this.helloController.setControllerFactory(this.factory);
        } catch (NoSuchMethodError | NoSuchMethodException | RuntimeException ignored) {}
        try {
            this.loginController.setControllerFactory(this.factory);
        } catch (NoSuchMethodError | NoSuchMethodException | RuntimeException ignored) {}
    }

    // Getters
    public HelloController getHelloController()           { return helloController; }
    public PrincipalController getPrincipalController()   { return principalController; }
    public StellaController getStellaController()         { return stellaController; }
    public PomodoroController getPomodoroController()     { return pomodoroController; }
    public RegistroController getRegistroController()     { return registroController; }
    public LoginController getLoginController()           { return loginController; }

    // ===== Exponer la factory para el FXMLLoader =====
    public Function<Class<?>, Object> controllerFactory() {
        return this.factory;
    }

    // Por si se necesita resetear| instancias (cambio de sesión, etc.)
    public void reinicializar() {
        inicializar();
    }
}
