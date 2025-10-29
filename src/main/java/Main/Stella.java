package Main;


import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Domain.repositoriesInterfaces.*;
import Infrastructure.controllers.ControllerPrincipal;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.*;
import javafx.stage.Stage;
import Application.services.DarAcceso.DarAccesoService;

import javafx.application.Application;


public class Stella extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();

        //Instanciación de interfaces
        InterfazUsuarioRepository usuarioRepository = new UsuarioRepository(connMgr);
        //InterfazIntentoRepository intentoRepository = new IntentoRepository(connMgr);
        //InterfazLeccionRepository leccionRepository = new LeccionRepository(connMgr);
        //InterfazProgresoLeccionRepository progresoRepository = new ProgresoLeccionRepository(connMgr);
        //InterfazPruebaRepository pruebaRepository = new PruebaRepository(connMgr);
        InterfazUsuarioStatsRepository usuarioStatsRepository = new UsuarioStatsRepository(connMgr);

        //Instanciacion de servicios
        LoginService loginService = new LoginService(usuarioRepository);
        RegistroService registroService = new RegistroService(usuarioRepository);

        //Instanciacion de controlador principal
        ControllerPrincipal controllerPrincipal = new ControllerPrincipal(loginService, registroService);

        /*
        // **********************************************
        // 1. PASO CRÍTICO: INICIALIZAR LA BASE DE DATOS
        H2DataBaseInitializer.initialize();
        // **********************************************

        // Lo del back se llama una sola vez con repo y service
        InterfazUsuarioRepository repo = new UsuarioRepository();
        DarAccesoService service = new DarAccesoService(repo);
        AppServices.init(service);

        // S carga FXML e inyecta controladores cuando se necesite
        FXMLLoader loader = new FXMLLoader(Stella.class.getResource("/views/hello-view.fxml"));
        loader.setControllerFactory(clazz -> {
            if (clazz == LoginController.class)
                return new LoginController(service);
            try {
                return clazz.getDeclaredConstructor().newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(loader.load());
        stage.setTitle("Stella App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.centerOnScreen();
        stage.show();
    }
    */

    }
    public static void main (String[]args){
        launch();
    }
}