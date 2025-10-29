package Infrastructure.controllers;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
//import Application.services.SesionEstudioService;

public class ControllerPrincipal {

    //Servicios
    private LoginService loginService;
    private RegistroService registroService;



    //Controladores
    private LoginController loginController;
    private RegistroController registroController;


    public ControllerPrincipal(LoginService loginService, RegistroService registroService) {
        this.loginService = loginService;
        this.registroService = registroService;

    }

}