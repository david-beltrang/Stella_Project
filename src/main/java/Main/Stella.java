package Main;

import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.LeccionService;
import Application.services.ListarCursosService;
import Application.services.PomodoroTimer;
import Application.services.SesionEstudioService;
import Application.services.SesionPomodoroService;

import Domain.repositoriesInterfaces.*;
import Infrastructure.controllers.ControllerControladores;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Stella extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // =====================================================
        // ===== 1️⃣ Inicializar conexión e infraestructura BD =====
        var connMgr = new ConexionBD();
        var initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        // ===== 2️⃣ Instanciar repositorios =====
        InterfazUsuarioRepository usuarioRepository = new UsuarioRepository(connMgr);
        InterfazLeccionRepository leccionRepository = new LeccionRepository(connMgr);
        InterfazCursoRepository cursoRepository = new CursoRepository(connMgr);
        InterfazProgresoRepository progresoRepository = new ProgresoRepository(connMgr);
        InterfazIntentoRepository intentoRepository = new IntentoRepository(connMgr);
        InterfazPruebaRepository pruebaRepository = new PruebaRepository(connMgr);
        InterfazUsuarioStatsRepository usuarioStatsRepository = new UsuarioStatsRepository(connMgr);
        InterfazSesionEstudioRepository sesionEstudioRepository = new SesionEstudioRepository(connMgr);
        InterfazUsuarioCursoRepository usuarioCursoRepository = new UsuarioCursoRepository(connMgr);

        // =====================================================
        // 2️⃣ Instanciar servicios de aplicación
        // =====================================================
        LeccionService leccionService = new LeccionService(
                leccionRepository, progresoRepository, intentoRepository, pruebaRepository
        );

        ListarCursosService listarCursosService = new ListarCursosService(cursoRepository, usuarioCursoRepository);
        PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
        SesionEstudioService sesionEstudioService = new SesionEstudioService(leccionRepository, progresoRepository);
        SesionPomodoroService sesionPomodoroService = new SesionPomodoroService(sesionEstudioRepository);
        LoginService loginService = new LoginService(usuarioRepository);
        RegistroService registroService = new RegistroService(usuarioRepository);


        // =====================================================
        // 3️⃣ Crear el orquestador de controladores
        // =====================================================
        ControllerControladores controllerControladores = new ControllerControladores(
                leccionService,
                listarCursosService,
                pomodoroTimer,
                sesionEstudioService,
                sesionPomodoroService,
                loginService,
                registroService
        );

        // =====================================================
        // 4️⃣ Cargar la vista inicial (Hello View)
        // =====================================================
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));

        // Inyectamos la factory global del orquestador
        loader.setControllerFactory((Callback<Class<?>, Object>) controllerControladores.controllerFactory());

        // Cargar escena principal
        Scene scene = new Scene(loader.load());
        stage.setTitle("STELLA - Inicio");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.centerOnScreen();
        stage.show();

        // 💡 Si más adelante quieres que el orquestador maneje la carga:
        // controllerControladores.mostrarVistaInicial(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}

