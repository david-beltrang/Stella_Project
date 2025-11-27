package Application.services;

import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Servicio para ejecutar código Java dinámicamente usando JShell (Java 9+)
 */
public class CodeExecutionService {

    private final JShell jshell;
    private final ByteArrayOutputStream outputStream;
    private final ByteArrayOutputStream errorStream;

    public CodeExecutionService() {
        this.outputStream = new ByteArrayOutputStream();
        this.errorStream = new ByteArrayOutputStream();

        this.jshell = JShell.builder()
                .out(new PrintStream(outputStream))
                .err(new PrintStream(errorStream))
                .build();
    }

    /**
     * Ejecuta un fragmento de código Java y devuelve el resultado
     */
    public CodeExecutionResult ejecutar(String codigo) {
        try {
            // Limpiar streams anteriores
            outputStream.reset();
            errorStream.reset();

            // Evaluar el código
            List<SnippetEvent> events = jshell.eval(codigo);

            // Construir resultado
            StringBuilder resultado = new StringBuilder();
            boolean exitoso = true;

            for (SnippetEvent event : events) {
                Snippet snippet = event.snippet();

                // Si hubo error
                if (event.exception() != null) {
                    exitoso = false;
                    resultado.append("Error: ").append(event.exception().getMessage()).append("\n");
                } else if (event.status() == Snippet.Status.REJECTED) {
                    exitoso = false;
                    jshell.diagnostics(snippet)
                            .forEach(diag -> resultado.append("Error: ").append(diag.getMessage(null)).append("\n"));
                } else {
                    // Agregar valor devuelto si existe
                    if (event.value() != null && !event.value().isEmpty()) {
                        resultado.append(event.value()).append("\n");
                    }
                }
            }

            // Agregar salida estándar
            String output = outputStream.toString();
            if (!output.isEmpty()) {
                resultado.append(output);
            }

            // Agregar errores
            String errors = errorStream.toString();
            if (!errors.isEmpty()) {
                exitoso = false;
                resultado.append(errors);
            }

            return new CodeExecutionResult(
                    exitoso,
                    resultado.toString().trim(),
                    exitoso ? null : resultado.toString());

        } catch (Exception e) {
            return new CodeExecutionResult(
                    false,
                    "",
                    "Error de ejecución: " + e.getMessage());
        }
    }

    /**
     * Valida si el código ejecutado produce el output esperado
     */
    public boolean validarSolucion(String codigo, String solucionEsperada) {
        CodeExecutionResult result = ejecutar(codigo);

        if (!result.exitoso()) {
            return false;
        }

        // Normalizar y comparar
        String outputNormalizado = result.output().trim().replaceAll("\\s+", " ");
        String esperadoNormalizado = solucionEsperada.trim().replaceAll("\\s+", " ");

        return outputNormalizado.equals(esperadoNormalizado);
    }

    /**
     * Cierra el JShell y libera recursos
     */
    public void cerrar() {
        if (jshell != null) {
            jshell.close();
        }
    }

    /**
     * Resultado de la ejecución de código
     */
    public record CodeExecutionResult(
            boolean exitoso,
            String output,
            String error) {
    }
}
