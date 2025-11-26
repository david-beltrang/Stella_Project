package Application.config;

import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import Application.services.ListarCursosService;
import Application.services.SeccionesService;

// === Pomodoro ===
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;
import Application.services.ProgresoGamificacionService;
import Application.services.PreguntasRespuestasForoService;
import Application.services.TiendaService;
import Application.services.UsuarioStatsService;
import Application.services.UsuarioStellaService;

public final class AppServices {
    private static LoginService service;
    private static RegistroService registroService;
    private static UsuarioResponse usuarioActual;

    // ===== Cursos / Secciones =====
    private static ListarCursosService listarCursosService;
    private static SeccionesService seccionesService;

    // ===== Pomodoro global =====
    private static SesionPomodoroService sesionPomodoroService;
    private static PomodoroTimer pomodoroTimer;
    private static Integer pomodoroSesionId; // id de la sesión activa o null
    private static boolean pomodoroFinishListenerRegistrado = false;

    // ===== Gamificación =====
    private static ProgresoGamificacionService progresoGamificacionService;

    // ===== Foro =====
    private static int preguntaIdActual = 0;
    private static PreguntasRespuestasForoService foroService;

    private AppServices() {
    }

    // ===== Acceso / Registro =====
    public static void init(LoginService s) {
        service = s;
    }

    public static LoginService service() {
        return service;
    }

    public static void initRegistro(RegistroService s) {
        registroService = s;
    }

    public static RegistroService registroService() {
        return registroService;
    }

    // ===== Usuario actual =====
    public static UsuarioResponse getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(UsuarioResponse usuario) {
        AppServices.usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        if (pomodoroTimer != null) {
            pomodoroTimer.reset();
        }
    }

    // ===== Pomodoro: init y getters =====
    public static void initPomodoro(SesionPomodoroService sesionService, PomodoroTimer timer) {
        sesionPomodoroService = sesionService;
        pomodoroTimer = timer;
    }

    public static SesionPomodoroService getSesionPomodoroService() {
        return sesionPomodoroService;
    }

    public static PomodoroTimer getPomodoroTimer() {
        return pomodoroTimer;
    }

    public static Integer getPomodoroSesionId() {
        return pomodoroSesionId;
    }

    public static void setPomodoroSesionId(Integer id) {
        pomodoroSesionId = id;
    }

    public static boolean isPomodoroFinishListenerRegistrado() {
        return pomodoroFinishListenerRegistrado;
    }

    public static void setPomodoroFinishListenerRegistrado(boolean v) {
        pomodoroFinishListenerRegistrado = v;
    }

    // ===== Cursos / Secciones =====
    public static void initCursos(ListarCursosService listar, SeccionesService secciones) {
        listarCursosService = listar;
        seccionesService = secciones;
    }

    // ===== Gamificación =====
    public static void initGamificacion(ProgresoGamificacionService service) {
        progresoGamificacionService = service;
    }

    public static ProgresoGamificacionService getProgresoGamificacionService() {
        return progresoGamificacionService;
    }

    // Métodos para pregunta ID actual
    public static void setPreguntaIdActual(int id) {
        preguntaIdActual = id;
    }

    public static int getPreguntaIdActual() {
        return preguntaIdActual;
    }

    public static ListarCursosService getListarCursosService() {
        return listarCursosService;
    }

    public static SeccionesService getSeccionesService() {
        return seccionesService;
    }

    // ===== Tienda / Stats / Stella =====
    private static UsuarioStatsService usuarioStatsService;
    private static TiendaService tiendaService;
    private static UsuarioStellaService usuarioStellaService;

    public static void initTienda(TiendaService tienda, UsuarioStatsService stats, UsuarioStellaService stella) {
        tiendaService = tienda;
        usuarioStatsService = stats;
        usuarioStellaService = stella;
    }

    public static TiendaService getTiendaService() {
        return tiendaService;
    }

    public static UsuarioStatsService getUsuarioStatsService() {
        return usuarioStatsService;
    }

    public static UsuarioStellaService getUsuarioStellaService() {
        return usuarioStellaService;
    }

    // ===== Foro =====
    public static void initForo(PreguntasRespuestasForoService service) {
        foroService = service;
    }

    public static PreguntasRespuestasForoService foroService() {
        return foroService;
    }
}
