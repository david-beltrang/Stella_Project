package main.java.Main;


import javafx.stage.Stage;
import Application.config.AppServices;
import Application.services.DarAccesoService;
import Infrastructure.repositories.UsuarioRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Infrastructure.controllers.LoginController;
import Infrastructure.persistence.H2DataBaseInitializer; // <--- Importación necesaria

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Stella extends Application {
    @Override
    public void start(Stage stage) throws Exception {

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

    public static void main(String[] args) { launch();}
}
