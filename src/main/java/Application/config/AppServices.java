package Application.config;

import Application.services.DarAcceso.DarAccesoService;
import Application.services.DarAcceso.LoginService;

public final class AppServices {
    private static LoginService service;

    private AppServices() {}

    // Esta función es para inicializar el servicio de acceso a la base de datos
    public static void init(LoginService s) {
        service = s;
    }
    public static LoginService service() {
        return service;
    }
}