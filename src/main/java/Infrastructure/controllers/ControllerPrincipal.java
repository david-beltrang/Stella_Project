package Infrastructure.controllers;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.SesionEstudioService;

public class ControllerPrincipal {

    //Servicios
    private LoginService loginService;
    private RegistroService registroService;
    private SesionEstudioService sesionEstudioService;

    //Controladores
    private LoginController loginController;
    private RegistroController registroController;

    public ControllerPrincipal(LoginService loginService, RegistroService registroService) {
        this.loginService = loginService;
        this.registroService = registroService;
        //this.sesionEstudioService = sesionEstudioService;

    }

    public void ejecutar() {

        // PRUEBA TEMPORAL PARA PROBAR CONTROLLER PRINCIPAL

        this.loginController = new LoginController(this.loginService);
        this.registroController = new RegistroController(this.registroService);


        System.out.println("=== Prueba 1: Registro ===");
        RegistrarUsuarioRequest registroRequest = new RegistrarUsuarioRequest(
                "user2", "test2@example.com", "TestUserDos", "pass456", "ESTUDIANTE"
        );
        UsuarioResponse response = null;
        try {
            response = accesoService.registrar(registroRequest);
            System.out.println("Registro exitoso: " + response);
            System.out.println("ID generado: " + response.id());
            System.out.println("Username generado: " + response.username());
            System.out.println("Correo generado: " + response.correo());
            System.out.println("Nombre generado: " + response.nombre());
            System.out.println("Tipo generado: " + response.tipo());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en registro: " + e.getMessage());
        }

    }
}