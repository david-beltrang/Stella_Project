// src/test/java/Application/services/UsuarioStatsService_Test.java
package Application.services;

import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@DisplayName("UsuarioStatsService - Tests de unidad")
class UsuarioStatsService_Test {

    private UsuarioStatsService service;
    private InterfazUsuarioItemRepository mockRepo;
    private UsuarioStellaService mockStellaService;

    @BeforeEach
    void setUp() {
        mockRepo = mock(InterfazUsuarioItemRepository.class);
        mockStellaService = mock(UsuarioStellaService.class);
        service = new UsuarioStatsService(mockRepo, mockStellaService);
    }

    @Test
    @DisplayName("obtenerPescaditos(id) - Error en repositorio → Retorna 0 y loguea error")
    void obtenerPescaditos_IdEspecifico_ErrorBD_RetornaCero() {
        when(mockRepo.obtenerPescaditos(anyInt())).thenThrow(new RuntimeException("Error BD"));

        int resultado = service.obtenerPescaditos(1);

        assertEquals(0, resultado);
        verify(mockRepo).obtenerPescaditos(1);
    }

    @Test
    @DisplayName("obtenerRachaDias - Error en repositorio → Retorna 0 y loguea error")
    void obtenerRachaDias_ErrorBD_RetornaCero() {
        // Necesitamos simular un usuario logueado.
        // Como obtenerUsuarioIdActual es estático/dependiente de AppServices,
        // y no podemos mockear estáticos fácilmente sin PowerMock,
        // este test es difícil de aislar completamente si AppServices no tiene usuario.
        // Sin embargo, si AppServices.getUsuarioActual() retorna null, el método
        // retorna 0 antes del try-catch.

        // Para probar el catch, necesitamos que obtenerUsuarioIdActual() retorne un ID
        // válido.
        // Si no podemos controlar AppServices, podemos crear una subclase de
        // UsuarioStatsService para el test
        // que sobrescriba obtenerUsuarioIdActual().

        UsuarioStatsService serviceTest = new UsuarioStatsService(mockRepo, mockStellaService) {
            @Override
            public Integer obtenerUsuarioIdActual() {
                return 1;
            }
        };

        when(mockRepo.obtenerRacha(anyInt())).thenThrow(new RuntimeException("Error BD"));

        int resultado = serviceTest.obtenerRachaDias();

        assertEquals(0, resultado);
    }

    @Test
    @DisplayName("agregarPescaditos - Error en repositorio → Lanza excepción y loguea error")
    void agregarPescaditos_ErrorBD_LanzaExcepcion() {
        doThrow(new RuntimeException("Error BD")).when(mockRepo).agregarPescaditos(anyInt(), anyInt());

        assertThrows(RuntimeException.class, () -> service.agregarPescaditos(1, 100));
    }

    @Test
    @DisplayName("obtenerPescaditos() - Error en repositorio (con usuario logueado) → Retorna 0")
    void obtenerPescaditos_UsuarioActual_ErrorBD_RetornaCero() {
        UsuarioStatsService serviceTest = new UsuarioStatsService(mockRepo, mockStellaService) {
            @Override
            public Integer obtenerUsuarioIdActual() {
                return 1;
            }
        };

        when(mockRepo.obtenerPescaditos(anyInt())).thenThrow(new RuntimeException("Error BD"));

        int resultado = serviceTest.obtenerPescaditos();

        assertEquals(0, resultado);
    }
}
