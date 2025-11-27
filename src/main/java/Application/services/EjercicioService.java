package Application.services;

import Application.services.CodeExecutionService.CodeExecutionResult;
import Domain.models.Ejercicio;
import Domain.repositoriesInterfaces.InterfazEjercicioRepository;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar ejercicios de programación
 */
public class EjercicioService {

    private final InterfazEjercicioRepository ejercicioRepository;
    private final CodeExecutionService codeExecutionService;

    public EjercicioService(InterfazEjercicioRepository ejercicioRepository,
            CodeExecutionService codeExecutionService) {
        this.ejercicioRepository = ejercicioRepository;
        this.codeExecutionService = codeExecutionService;
    }

    /**
     * Obtiene todos los ejercicios de una lección
     */
    public List<Ejercicio> listarEjerciciosPorLeccion(int leccionId) {
        return ejercicioRepository.listarPorLeccion(leccionId);
    }

    /**
     * Obtiene un ejercicio por ID
     */
    public Optional<Ejercicio> obtenerEjercicio(int ejercicioId) {
        return ejercicioRepository.obtenerPorId(ejercicioId);
    }

    /**
     * Valida la solución de un usuario contra el ejercicio
     * 
     * @return Resultado con información de validación
     */
    public ResultadoValidacion validarSolucion(int ejercicioId, String codigoUsuario) {
        Optional<Ejercicio> ejercicioOpt = ejercicioRepository.obtenerPorId(ejercicioId);

        if (ejercicioOpt.isEmpty()) {
            return new ResultadoValidacion(false, "Ejercicio no encontrado", null, 0);
        }

        Ejercicio ejercicio = ejercicioOpt.get();

        // Ejecutar código
        CodeExecutionResult resultado = codeExecutionService.ejecutar(codigoUsuario);

        if (!resultado.exitoso()) {
            return new ResultadoValidacion(
                    false,
                    "Error al ejecutar el código",
                    resultado.error(),
                    0);
        }

        // Validar contra solución esperada
        boolean esCorrecto = codeExecutionService.validarSolucion(
                codigoUsuario,
                ejercicio.getSolucionEsperada());

        int puntosObtenidos = esCorrecto ? ejercicio.getPuntos() : 0;

        return new ResultadoValidacion(
                esCorrecto,
                esCorrecto ? "¡Correcto!" : "La salida no coincide con la esperada",
                resultado.output(),
                puntosObtenidos);
    }

    /**
     * Ejecuta código sin validarlo (para práctica libre)
     */
    public ResultadoEjecucion ejecutarCodigo(String codigo) {
        CodeExecutionResult resultado = codeExecutionService.ejecutar(codigo);

        return new ResultadoEjecucion(
                resultado.exitoso(),
                resultado.output(),
                resultado.error());
    }

    /**
     * Resultado de la validación de un ejercicio
     */
    public record ResultadoValidacion(
            boolean esCorrecto,
            String mensaje,
            String output,
            int puntosObtenidos) {
    }

    /**
     * Resultado de la ejecución de código
     */
    public record ResultadoEjecucion(
            boolean exitoso,
            String output,
            String error) {
    }
}
