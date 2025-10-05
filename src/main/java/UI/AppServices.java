package UI;

import Application.services.DarAccesoService;

public final class AppServices {
    private static DarAccesoService service;

    private AppServices() {}

    public static void init(DarAccesoService s) { service = s; }
    public static DarAccesoService service() { return service; }
}
