// src/test/java/Application/services/SeccionesService_IntegrationTest.java
package Application.services;

import Application.dtos.seccion.SeccionResponse;
import Domain.repositoriesInterfaces.InterfazSeccionRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.SeccionRepository;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SeccionesService.ListarSeccionesConLecciones() - Complejidad ciclomática")
class SeccionesService_IntegrationTest {

    private SeccionesService service;
    private InterfazSeccionRepository seccionRepo;

    @BeforeEach
    void setUp() {
        // BD limpia + datos reales (incluye curso 1 con 3 secciones y 15 lecciones)
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        seccionRepo = new SeccionRepository(ConexionBD.getInstance());
        service = new SeccionesService(seccionRepo);
    }
    //==========================================================================================
    // 2. ListarSeccionesConLecciones(cursoId) → complejidad ciclomática = 1 pero verificación adicional
    //por business rules
    //=========================================================================================

    @Test
    @DisplayName("Camino único: cursoId=1 (C++) → devuelve 3 secciones con 15 lecciones completas")
    void listarSeccionesConLecciones_CursoCPlusPlus_RetornaEstructuraCorrecta() {
        List<SeccionResponse> secciones = service.ListarSeccionesConLecciones(1);

        // Debe haber exactamente 3 secciones
        assertEquals(3, secciones.size());

        // Verificar sección 1
        SeccionResponse sec1 = secciones.get(0);
        assertEquals("Introducción al lenguaje y entorno", sec1.titulo());
        assertEquals(1, sec1.numeroOrden());
        assertEquals(5, sec1.lecciones().size());
        assertEquals("Historia y características de C++", sec1.lecciones().get(0).titulo());

        // Verificar sección 2
        SeccionResponse sec2 = secciones.get(1);
        assertEquals("Variables, tipos de datos y operadores", sec2.titulo());
        assertEquals(2, sec2.numeroOrden());
        assertEquals(5, sec2.lecciones().size());
        assertEquals("Tipos de datos primitivos", sec2.lecciones().get(0).titulo());

        // Verificar sección 3
        SeccionResponse sec3 = secciones.get(2);
        assertEquals("Estructuras de control", sec3.titulo());
        assertEquals(3, sec3.numeroOrden());
        assertEquals(5, sec3.lecciones().size());
        assertEquals("Condicionales if, else if, else", sec3.lecciones().get(0).titulo());

    }

    @Test
    @DisplayName("Curso sin secciones (id=3 Python) → devuelve lista vacía")
    void listarSeccionesConLecciones_CursoSinSecciones_RetornaListaVacia() {
        List<SeccionResponse> secciones = service.ListarSeccionesConLecciones(3); // Python no tiene secciones
        assertTrue(secciones.isEmpty());
    }

    @Test
    @DisplayName("Curso inexistente (id=999) → devuelve lista vacía (no lanza excepción)")
    void listarSeccionesConLecciones_CursoInexistente_RetornaListaVacia() {
        List<SeccionResponse> secciones = service.ListarSeccionesConLecciones(999);
        assertTrue(secciones.isEmpty());
    }
}