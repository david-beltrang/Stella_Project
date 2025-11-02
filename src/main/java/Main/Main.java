package Main;

import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.ListarCursosService;
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;
import Application.services.SeccionesService;

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
        // ======== Base de datos ========
        var connMgr = new ConexionBD();
        var initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        // ======== Repositorios ========
        InterfazUsuarioRepository usuarioRepository = new UsuarioRepository(connMgr);
        InterfazCursoRepository cursoRepository = new CursoRepository(connMgr);
        InterfazSeccionRepository seccionRepository = new SeccionRepository(connMgr);
        InterfazUsuarioCursoRepository usuarioCursoRepository = new UsuarioCursoRepository(connMgr);
        InterfazUsuarioStatsRepository usuarioStatsRepository = new UsuarioStatsRepository(connMgr);
        InterfazSesionEstudioRepository sesionEstudioRepository = new SesionEstudioRepository(connMgr);

        // ======== Servicios ========
        SeccionesService seccionesService = new SeccionesService(seccionRepository);
        ListarCursosService listarCursosService = new ListarCursosService(cursoRepository, usuarioCursoRepository);
        PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
        SesionPomodoroService sesionPomodoroService = new SesionPomodoroService(sesionEstudioRepository);
        LoginService loginService = new LoginService(usuarioRepository);
        RegistroService registroService = new RegistroService(usuarioRepository);

        // ======== Front Controller ========
        ControllerControladores frontController = new ControllerControladores(
                seccionesService,
                listarCursosService,
                pomodoroTimer,
                sesionPomodoroService,
                loginService,
                registroService
        );

        // ======== Pantalla inicial ========
        frontController.mostrarVistaInicial(stage);
        stage.setTitle("STELLA - Inicio");
        stage.setResizable(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}



