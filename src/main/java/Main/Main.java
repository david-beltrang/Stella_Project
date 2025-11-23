package Main;

import Application.services.*;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;

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
        // Base de datos - Usando Singleton pattern
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

        // ======== Servicios ========
        SeccionesService seccionesService = new SeccionesService(seccionRepository);
        ListarCursosService listarCursosService = new ListarCursosService(cursoRepository, usuarioCursoRepository);
        PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
        SesionPomodoroService sesionPomodoroService = new SesionPomodoroService(sesionEstudioRepository);
        LoginService loginService = new LoginService(usuarioRepository);
        RegistroService registroService = new RegistroService(usuarioRepository);
        LeccionService leccionService = new LeccionService(leccionRepository);
        UsuarioStellaService usuarioStellaService = new UsuarioStellaService(usuarioItemRepository,
                stellaItemRepository);
        TiendaService tiendaService = new TiendaService(itemRepository, stellaItemRepository, usuarioItemRepository,
                usuarioStellaService);
        ChatbotService chatbotService = new ChatbotService();
        PerfilService perfilService = new PerfilService(usuarioRepository, usuarioCursoRepository, cursoRepository,
                usuarioStellaService);

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
                perfilService);

        // ======== Configurar Stage ========
        stage.setResizable(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.setTitle("Stella");

        // ======== Mostrar vista inicial ========
        controllerControladores.mostrarVistaInicial(stage);

        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}