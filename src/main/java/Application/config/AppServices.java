package Application.config;

import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.SeccionesService;

// === Pomodoro ===
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;

public final class AppServices {
    private static LoginService service;
    private static RegistroService registroService;
    private static UsuarioResponse usuarioActual;
    private static SeccionesService seccionesService;

    // ===== Pomodoro global =====
    private static SesionPomodoroService sesionPomodoroService;
    private static PomodoroTimer pomodoroTimer;
    private static Integer pomodoroSesionId; // id de la sesión activa o null
    private static boolean pomodoroFinishListenerRegistrado = false;

    private AppServices() {}

    // ===== Acceso / Registro =====
    public static void init(LoginService s) { service = s; }
    public static LoginService service() { return service; }
    public static RegistroService registroService() { return registroService; }

    // ===== Usuario actual =====
    public static UsuarioResponse getUsuarioActual() { return usuarioActual; }
    public static void setUsuarioActual(UsuarioResponse usuario) { AppServices.usuarioActual = usuario; }
    public static void cerrarSesion() { usuarioActual = null; }

    // ===== Pomodoro: init y getters =====
    public static void initPomodoro(SesionPomodoroService sesionService, PomodoroTimer timer) {
        sesionPomodoroService = sesionService;
        pomodoroTimer = timer;
    }

    public static SesionPomodoroService getSesionPomodoroService() { return sesionPomodoroService; }
    public static PomodoroTimer getPomodoroTimer() { return pomodoroTimer; }

    public static Integer getPomodoroSesionId() { return pomodoroSesionId; }
    public static void setPomodoroSesionId(Integer id) { pomodoroSesionId = id; }

    public static boolean isPomodoroFinishListenerRegistrado() { return pomodoroFinishListenerRegistrado; }
    public static void setPomodoroFinishListenerRegistrado(boolean v) { pomodoroFinishListenerRegistrado = v; }
}
