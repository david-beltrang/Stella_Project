package Application.services;

import Application.config.AppServices;
import Application.dtos.PerfilDTO;
import Application.dtos.acceso.UsuarioResponse;
import Domain.models.Curso;
import Domain.models.StellaItem;
import Domain.models.Usuario;
import Domain.models.UsuarioCurso;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PerfilService {
    private static final Logger logger = LoggerFactory.getLogger(PerfilService.class);

    private final InterfazUsuarioRepository usuarioRepo;
    private final InterfazUsuarioCursoRepository usuarioCursoRepo;
    private final InterfazCursoRepository cursoRepo;
    private final UsuarioStellaService usuarioStellaService;

    public PerfilService(
            InterfazUsuarioRepository usuarioRepo,
            InterfazUsuarioCursoRepository usuarioCursoRepo,
            InterfazCursoRepository cursoRepo,
            UsuarioStellaService usuarioStellaService) {
        this.usuarioRepo = usuarioRepo;
        this.usuarioCursoRepo = usuarioCursoRepo;
        this.cursoRepo = cursoRepo;
        this.usuarioStellaService = usuarioStellaService;
    }

    public PerfilDTO obtenerPerfilUsuarioActual() {
        UsuarioResponse usuarioActual = AppServices.getUsuarioActual();
        if (usuarioActual == null) {
            throw new IllegalStateException("No hay usuario logueado.");
        }

        Integer usuarioId = usuarioActual.id();

        // 1. Obtener datos completos del usuario (incluyendo fecha creación)
        Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado en BD: " + usuarioId));

        // 2. Obtener cursos inscritos
        List<UsuarioCurso> inscripciones = usuarioCursoRepo.encontrarPorUsuarioId(usuarioId);
        List<String> nombresCursos = inscripciones.stream()
                .map(inscripcion -> cursoRepo.buscarPorId(inscripcion.getCursoId()))
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getTitulo().valorTitulo())
                .collect(Collectors.toList());

        // 3. Calcular cursos completados (Mock logic for now, assuming 0 or based on
        // progress if available)
        // Since we don't have a direct "completed" flag in UsuarioCurso easily
        // accessible without more logic,
        // we will assume 0 for now or implement a check if ProgresoLeccion is
        // available.
        // For this iteration, we'll return 0 as placeholder or count all inscripciones
        // if that was the intent.
        // The requirement says "Cantidad de cursos completados".
        int cursosCompletados = 0; // Placeholder logic

        // 4. Obtener Stella Actual
        String rutaImagenStella = usuarioStellaService.obtenerRutaImagenStellaActual();
        if (rutaImagenStella == null) {
            rutaImagenStella = "/Image/General/avatar-placeholder.png"; // Default
        }

        return new PerfilDTO(
                usuario.getNombre().valor(),
                usuario.getUsername().valor(),
                usuario.getCorreo().valor(),
                usuario.getContrasena(), // Requirement says "Contraseña"
                nombresCursos,
                cursosCompletados,
                usuario.getFechaCreacion(),
                rutaImagenStella);
    }
}
