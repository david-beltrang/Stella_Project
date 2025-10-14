package Application.services;

import Application.dtos.sesionEstudio.Pomodoro.IniciarSesionEstudioRequest;
import Application.dtos.sesionEstudio.Pomodoro.SesionEstudioResponse;
import Domain.models.SesionEstudio;
import Domain.repositoriesInterfaces.InterfazSesionEstudioRepository;

public class SesionPomodoroService {
    private final InterfazSesionEstudioRepository repository;

    //Inyectamos la dependencia a la interfaz del repositorio
    public SesionPomodoroService(InterfazSesionEstudioRepository repository) {
        this.repository = repository; // Inyección por constructor
    }

    //Apenas empiece una sesión de estudio se debe llamar este metodo para guardar la sesion de estudio y su inicio
    public SesionEstudioResponse iniciarSesion(IniciarSesionEstudioRequest request) {
        SesionEstudio sesion = SesionEstudio.crearNueva(request.usuarioId(), request.tiempoEstudio(), request.tiempoDescanso());
        //Se guarda la sesión en la base de datos
        sesion = repository.guardar(sesion);
        //Se retornan los datos de la sesion en curso
        return mapToResponse(sesion);
    }

    public SesionEstudioResponse finalizarSesion(int id) {
        //Primero se busca la sesión en la BD
        SesionEstudio sesion = repository.encontrarPorId(id);
        if (sesion == null) {
            throw new IllegalArgumentException("Sesión no encontrada.");
        }
        //Cuando el tiempo agote la idea es llamar este método de finalizarSesion para que se pueda llamar el metodo de la clase sesionEstudio
        //de finalizar y obtener la fecha fin.
        sesion.finalizar();
        //Guardamos la sesion modificada con su fecha de inicio y fin
        sesion = repository.guardar(sesion);
        return mapToResponse(sesion);
    }

    //Metodo auxiliar para poder saber si se está guardando la sesion en la BD
    public SesionEstudioResponse obtenerEstadoSesion(int id) {
        SesionEstudio sesion = repository.encontrarPorId(id);
        if (sesion == null) {
            throw new IllegalArgumentException("Sesión no encontrada.");
        }
        return mapToResponse(sesion);
    }

    //Metodo auxiliar para obtener los datos de la sesion y subirlos al frontend
    private SesionEstudioResponse mapToResponse(SesionEstudio sesion) {
        return new SesionEstudioResponse(
                sesion.getId(),
                sesion.getUsuarioId(),
                sesion.getTiempoEstudio().minutos(),
                sesion.getTiempoDescanso().minutos(),
                sesion.getFechaInicio(),
                sesion.getFechaFinal()
        );
    }
}
