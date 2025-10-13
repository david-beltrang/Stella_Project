package Application.config;

import Application.services.DarAccesoService;

public final class AppServices {
    private static DarAccesoService service;

    private AppServices() {}

    // Esta función es para inicializar el servicio de acceso a la base de datos
    public static void init(DarAccesoService s) {
        service = s;
    }
    public static DarAccesoService service() {
        return service;
    }
}