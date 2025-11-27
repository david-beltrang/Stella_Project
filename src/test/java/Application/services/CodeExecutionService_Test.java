// src/test/java/Application/services/CodeExecutionService_Test.java
package Application.services;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CodeExecutionService - Tests de unidad")
class CodeExecutionService_Test {

    private CodeExecutionService service;

    @BeforeEach
    void setUp() {
        service = new CodeExecutionService();
    }

    @AfterEach
    void tearDown() {
        service.cerrar();
    }

    @Test
    @DisplayName("ejecutar - Código válido (System.out.println) → Éxito y salida correcta")
    void ejecutar_CodigoValido_Exito() {
        CodeExecutionService.CodeExecutionResult result = service.ejecutar("System.out.println(\"Hola Mundo\");");

        assertTrue(result.exitoso());
        assertEquals("Hola Mundo", result.output());
        assertNull(result.error());
    }

    @Test
    @DisplayName("ejecutar - Retorno de valor → Éxito y valor en output")
    void ejecutar_RetornoValor_Exito() {
        CodeExecutionService.CodeExecutionResult result = service.ejecutar("2 + 2");

        assertTrue(result.exitoso());
        assertEquals("4", result.output());
    }

    @Test
    @DisplayName("ejecutar - Error de compilación → Fallo y mensaje de error")
    void ejecutar_ErrorCompilacion_Fallo() {
        CodeExecutionService.CodeExecutionResult result = service.ejecutar("int a = \"texto\";");

        assertFalse(result.exitoso());
        assertTrue(result.error().contains("incompatible types"));
    }

    @Test
    @DisplayName("ejecutar - Excepción en tiempo de ejecución → Fallo y mensaje de excepción")
    void ejecutar_RuntimeException_Fallo() {
        CodeExecutionService.CodeExecutionResult result = service.ejecutar("int a = 1 / 0;");

        assertFalse(result.exitoso());
        assertTrue(result.error().contains("/ by zero"));
    }

    @Test
    @DisplayName("validarSolucion - Solución correcta → true")
    void validarSolucion_Correcta_True() {
        boolean valido = service.validarSolucion("System.out.println(\"Test\");", "Test");
        assertTrue(valido);
    }

    @Test
    @DisplayName("validarSolucion - Solución incorrecta → false")
    void validarSolucion_Incorrecta_False() {
        boolean valido = service.validarSolucion("System.out.println(\"Otro\");", "Test");
        assertFalse(valido);
    }

    @Test
    @DisplayName("validarSolucion - Error en código → false")
    void validarSolucion_ErrorCodigo_False() {
        boolean valido = service.validarSolucion("int a = 1/0;", "Test");
        assertFalse(valido);
    }

    @Test
    @DisplayName("ejecutar - JShell cerrado → Lanza excepción controlada")
    void ejecutar_JShellCerrado_CapturaExcepcion() {
        service.cerrar();
        CodeExecutionService.CodeExecutionResult result = service.ejecutar("System.out.println(\"Fail\");");

        // Al estar cerrado, eval() lanzará IllegalStateException o similar,
        // que debe ser capturada por el bloque catch(Exception e)
        assertFalse(result.exitoso());
        assertTrue(result.error().contains("Error de ejecución"));
    }
}
