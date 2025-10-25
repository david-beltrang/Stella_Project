package Application.services;

import Application.dtos.curso.EstructuraCursoResponse;
import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.Seccion;

import Domain.models.Curso;
import Domain.models.LeccionValueObjects.EstadoProgreso;
import Domain.models.Leccion;
import Domain.models.ProgresoLeccion;

import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CursoService {

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

    // Obtener todo el curso,
    public EstructuraCursoResponse obtenerEstructuraCurso(Integer usuarioId, Integer cursoId) {
        // 1) Obtener la Entidad principal del Curso
        Optional<Curso> cursoOpt = interfazCursoRepository.buscarPorId(cursoId);
        if (cursoOpt.isEmpty()) {
            throw new IllegalArgumentException("El curso con ID " + cursoId + " no fue encontrado.");
        }
        Curso curso = cursoOpt.get();

        // 2) Obtener TODAS las lecciones (convirtiendo Iterable a List para ordenar ascendentemente)
        List<Leccion> todasLasLecciones = StreamSupport
                .stream(interfazLeccionRepository.buscarPorCursoId(cursoId).spliterator(), false)
                .collect(Collectors.toList());

        // Ordenamos por sección y luego por orden para aplicar la lógica secuencial
        todasLasLecciones.sort(
                Comparator.comparingInt(Leccion::getNumeroSeccion)
                        .thenComparingInt(Leccion::getNumeroOrden)
        );

        // 3) Obtener el estado y aplicar la lógica de desbloqueo
        List<LeccionResponse> leccionesConEstado = new ArrayList<>();
        boolean leccionAnteriorCompletada = true; // La primera lección siempre se desbloquea

        for (Leccion leccion : todasLasLecciones) {

            // Usamos ProgresoRepository para obtener el estado actual
            ProgresoLeccion progreso = interfazProgresoRepository.buscarPorUsuarioYLeccion(usuarioId, leccion.getId());
            EstadoProgreso estado = (progreso != null) ? progreso.getEstado() : EstadoProgreso.PENDIENTE;

            // Lógica de desbloqueo: desbloqueada si la anterior fue completada o si es la primera.
            boolean desbloqueada = leccionAnteriorCompletada;

            LeccionResponse dto = new LeccionResponse(
                    leccion.getId(),
                    leccion.getTitulo(),
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
        // Crear un Map donde la clave es un Integer (el número de sección) y el valor es una lista de objetos LeccionLista que pertenecen a esa sección.
        Map<Integer, List<LeccionResponse>> leccionesPorSeccion =
                // Convertir la lista leccionesConEstado en un Stream para usar map, filter y collect.
                leccionesConEstado.stream()
                        // Usar collect para transformar el Stream en un Map y groupingBy agrupa los elementos del Stream según la clave indicada
                        .collect(Collectors.groupingBy(
                                // Indicamos cómo obtener la clave de agrupamiento.
                                // Haciendo referencia a método (equivalente a leccion -> leccion.numeroSeccion()).
                                // Para agrupar todas las lecciones de una seccion en una misma lista
                                LeccionResponse::numeroSeccion
                        ));

        // 5) Mapear los grupos a los DTOs de Sección (ordenados por número de sección)
        List<Seccion> secciones =

                // Obtenemos un Stream de las entradas del Map cada una tiene
                // - getKey(): el número de sección (Integer)
                // - getValue(): la lista de lecciones asociadas a esa sección (List<LeccionLista>)
                leccionesPorSeccion.entrySet().stream()
                        // Ordenar el Map por la clave (número de sección) de menor a mayor.
                        .sorted(Map.Entry.comparingByKey())
                        // Transformamos cada entrada del Map en un objeto Seccion
                        .map(entry -> {
                            // Guardamos el número de sección en una variable para mayor claridad
                            int numero = entry.getKey();
                            // Creamos un título para la sección, personalizado con el número
                            String tituloSec = "Sección " + numero + ": Contenido Temático";
                            // Creamos un objeto Seccion
                            return new Seccion(numero, tituloSec, entry.getValue());
                        })

                        // Colectamos todos los objetos Seccion generados en una lista
                        .collect(Collectors.toList());


        // 6) Devolver la Estructura Final
        return new EstructuraCursoResponse(
                curso.getId(),
                curso.getTitulo(),
                secciones
        );
    }
}
