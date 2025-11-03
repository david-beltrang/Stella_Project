package Application.config;

import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.SeccionesService;

public final class AppServices {
    private static LoginService service;
    private static RegistroService registroService;
    private static UsuarioResponse usuarioActual;
    private AppServices() {}
    private static SeccionesService seccionesService;
    // Esta función es para inicializar el servicio de acceso a la base de datos
    public static void init(LoginService s) {
        service = s;
    }
    public static LoginService service() {
        return service;
    }
    public static RegistroService registroService() { return registroService;}
    public static UsuarioResponse getUsuarioActual() {
        return usuarioActual;
    }

    //Gets y sets para acceder a la sesion del usuario
    public static void setUsuarioActual(UsuarioResponse usuario) {
        AppServices.usuarioActual = usuario;
    }
    public static void cerrarSesion(){
        usuarioActual = null;
    }

}