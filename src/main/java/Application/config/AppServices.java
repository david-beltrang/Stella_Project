package Application.config;

import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.DarAccesoService;
import Application.services.DarAcceso.LoginService;

public final class AppServices {
    private static LoginService service;
    private static UsuarioResponse usuarioActual;
    private AppServices() {}

    // Esta función es para inicializar el servicio de acceso a la base de datos
    public static void init(LoginService s) {
        service = s;
    }
    public static LoginService service() {
        return service;
    }

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