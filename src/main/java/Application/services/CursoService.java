package Application.services;

import Application.dtos.curso.CursoEstructura;
import Application.dtos.leccion.LeccionLista;
import Application.dtos.seccion.Seccion;

import Domain.models.Curso;
import Domain.models.CursoValueObjects.EstadoProgreso;
import Domain.models.Leccion;
import Domain.models.ProgresoLeccion;
import Domain.models.UsuarioValueObjects.UsuarioId;

import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CursoService {

    // Dependencias inyectadas: solo Interfaces de Repositorio (Contratos de Dominio)
    private final InterfazCursoRepository interfazCursoRepository;
    private final InterfazLeccionRepository interfazLeccionRepository;
    private final InterfazProgresoRepository interfazProgresoRepository;

    public CursoService(InterfazCursoRepository interfazCursoRepository,
                        InterfazLeccionRepository interfazLeccionRepository,
                        InterfazProgresoRepository interfazProgresoRepository) {
        this.interfazCursoRepository = interfazCursoRepository;
        this.interfazLeccionRepository = interfazLeccionRepository;
        this.interfazProgresoRepository = interfazProgresoRepository;
    }

    /**
     * Obtiene la estructura completa del curso con el progreso y estado de desbloqueo
     * de cada lección para el usuario dado.
     *
     * @param usuarioId El ID del usuario actual.
     * @param cursoId   El ID del curso a visualizar.
     * @return CursoEstructura con las secciones y lecciones listas para la UI.
     */
    public CursoEstructura obtenerEstructuraCurso(UsuarioId usuarioId, Integer cursoId) {
        // 1) Obtener la Entidad principal del Curso
        Optional<Curso> cursoOpt = interfazCursoRepository.buscarPorId(cursoId);
        if (cursoOpt.isEmpty()) {
            throw new IllegalArgumentException("El curso con ID " + cursoId + " no fue encontrado.");
        }
        Curso curso = cursoOpt.get();

        // 2) Obtener TODAS las lecciones (convirtiendo Iterable a List para ordenar)
        List<Leccion> todasLasLecciones = StreamSupport
                .stream(interfazLeccionRepository.buscarPorCursoId(cursoId).spliterator(), false)
                .collect(Collectors.toList());

        // Ordenamos por sección y luego por orden para aplicar la lógica secuencial
        todasLasLecciones.sort(
                Comparator.comparingInt(Leccion::getNumeroSeccion)
                        .thenComparingInt(Leccion::getNumeroOrden)
        );

        // 3) Obtener el estado y aplicar la lógica de desbloqueo
        List<LeccionLista> leccionesConEstado = new ArrayList<>();
        boolean leccionAnteriorCompletada = true; // La primera lección siempre se desbloquea

        for (Leccion leccion : todasLasLecciones) {

            // Usamos ProgresoRepository para obtener el estado actual
            ProgresoLeccion progreso = interfazProgresoRepository
                    .buscarPorUsuarioYLeccion(usuarioId, leccion.getId());
            EstadoProgreso estado = (progreso != null)
                    ? progreso.getEstado()
                    : EstadoProgreso.PENDIENTE;

            // Lógica de desbloqueo: desbloqueada si la anterior fue completada O si es la primera.
            boolean desbloqueada = leccionAnteriorCompletada;

            LeccionLista dto = new LeccionLista(
                    leccion.getId(),
                    leccion.getTituloValor(),     // Accesor del record Titulo
                    leccion.getNumeroSeccion(),
                    leccion.getTipoContenido(),
                    estado,
                    desbloqueada
            );
            leccionesConEstado.add(dto);

            // Actualizar el estado para la próxima lección en el ciclo
            leccionAnteriorCompletada = (estado == EstadoProgreso.COMPLETADA);
        }

        // 4) Agrupar la lista plana de DTOs por número de sección
        Map<Integer, List<LeccionLista>> leccionesPorSeccion = leccionesConEstado.stream()
                .collect(Collectors.groupingBy(LeccionLista::numeroSeccion));

        // 5) Mapear los grupos a los DTOs de Sección (ordenados por número de sección)
        List<Seccion> secciones = leccionesPorSeccion.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int numero = entry.getKey();
                    String tituloSec = "Sección " + numero + ": Contenido Temático";
                    return new Seccion(numero, tituloSec, entry.getValue());
                })
                .collect(Collectors.toList());

        // 6) Devolver la Estructura Final
        return new CursoEstructura(
                curso.getId(),
                curso.getTitulo().valorTitulo(),
                secciones
        );
    }
}
