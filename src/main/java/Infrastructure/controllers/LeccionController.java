package Infrastructure.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Application.dtos.curso.CursoEstructura;
import Application.dtos.leccion.LeccionLista;
import Application.dtos.seccion.Seccion;
import Application.services.CursoService;

import Domain.models.CursoValueObjects.EstadoProgreso;
import Domain.models.UsuarioValueObjects.UsuarioId;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * LeccionController sencillo:
 *  - Carga la lista de lecciones de una sección usando solo CursoService.obtenerEstructuraCurso(...)
 *  - Permite navegar Anterior/Siguiente dentro de la sección.
 *  - "Marcar completada" actualiza SOLO la UI (no persiste en BD).
 *  - "Volver a Curso" y "Volver a la Sección" cambian de escena a Curso.fxml.
 */
public class LeccionController {

    // ------------------- UI (deben existir en Leccion.fxml) -------------------
    @FXML private Label lblTitulo;
    @FXML private Label lblTipo;
    @FXML private Label lblEstado;
    @FXML private Label lblPosicion;           // ejemplo: "2 / 8"
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnMarcarCompletada;
    @FXML private Button btnVolverCurso;
    @FXML private Button btnVolverSeccion;

    // ------------------- Dependencias / Contexto -------------------
    private CursoService cursoService;     // se inyecta desde fuera
    private UsuarioId usuarioId;           // usuario actual
    private Integer cursoId;               // curso actual
    private Integer numeroSeccion;         // sección actual (1, 2, 3, ...)

    // ------------------- Estado local de navegación -------------------
    private List<LeccionLista> lecciones;  // lecciones de la sección actual
    private int indexActual;               // 0..(n-1)
    private LeccionLista leccion;          // lección mostrada

    // =================================================================
    // =============== MÉTODOS PÚBLICOS QUE LLAMA OTRA CLASE ===========
    // =================================================================

    /** Inyecta el servicio (bajo acoplamiento: no tocamos repos ni otras capas). */
    public void setCursoService(CursoService cursoService) {
        this.cursoService = Objects.requireNonNull(cursoService, "cursoService");
    }

    /**
     * Inicializa la pantalla de Lección.
     * @param usuarioId        usuario actual (VO)
     * @param cursoId          id del curso (Integer)
     * @param numeroSeccion    número de sección a mostrar
     * @param indexSeleccionado índice de la lección dentro de esa sección (0-based)
     */
    public void init(UsuarioId usuarioId, Integer cursoId, Integer numeroSeccion, int indexSeleccionado) {
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
        this.cursoId = Objects.requireNonNull(cursoId, "cursoId");
        this.numeroSeccion = Objects.requireNonNull(numeroSeccion, "numeroSeccion");

        // 1) Cargar estructura y extraer la sección pedida
        cargarLeccionesDeSeccion();

        // 2) Ajustar índice y pintar
        if (lecciones.isEmpty()) {
            mostrarError("No hay lecciones en la sección " + numeroSeccion);
            limpiarUI();
            return;
        }
        if (indexSeleccionado < 0) indexSeleccionado = 0;
        if (indexSeleccionado >= lecciones.size()) indexSeleccionado = lecciones.size() - 1;

        indexActual = indexSeleccionado;
        leccion = lecciones.get(indexActual);

        pintar();
        actualizarNavegacion();
    }

    // =================================================================
    // ========================= CARGA / PINTADO =======================
    // =================================================================

    /** Carga desde el servicio solo lo necesario para esta pantalla. */
    private void cargarLeccionesDeSeccion() {
        lecciones = Collections.emptyList();

        CursoEstructura estructura = cursoService.obtenerEstructuraCurso(usuarioId, cursoId);

        // Buscar la sección con un for simple (sin streams)
        Seccion encontrada = null;
        if (estructura.secciones() != null) {
            for (Seccion s : estructura.secciones()) {
                if (s.numeroSeccion() == this.numeroSeccion) {
                    encontrada = s;
                    break;
                }
            }
        }

        if (encontrada != null && encontrada.lecciones() != null) {
            // Copia a una lista mutable para poder reemplazar elementos al marcar completada
            lecciones = new ArrayList<>(encontrada.lecciones());
        } else {
            lecciones = Collections.emptyList();
        }
    }

    /** Copia datos de la lección a la vista. */
    private void pintar() {
        if (leccion == null) {
            limpiarUI();
            return;
        }
        lblTitulo.setText(leccion.titulo());
        lblTipo.setText("Tipo: " + leccion.tipoContenido());
        lblEstado.setText("Estado: " + leccion.estado().name());
        lblPosicion.setText((indexActual + 1) + " / " + lecciones.size());
    }

    /** Habilita/deshabilita botones según el índice actual. */
    private void actualizarNavegacion() {
        btnAnterior.setDisable(indexActual <= 0);
        btnSiguiente.setDisable(indexActual >= lecciones.size() - 1);
    }

    /** Limpia los labels y desactiva navegación. */
    private void limpiarUI() {
        lblTitulo.setText("");
        lblTipo.setText("");
        lblEstado.setText("");
        lblPosicion.setText("--/--");
        btnAnterior.setDisable(true);
        btnSiguiente.setDisable(true);
    }

    // =================================================================
    // ======================== HANDLERS DE BOTONES ====================
    // =================================================================

    /** Pasa a la siguiente lección de la sección (si existe). */
    @FXML
    private void siguienteLeccion() {
        if (indexActual < lecciones.size() - 1) {
            indexActual = indexActual + 1;
            leccion = lecciones.get(indexActual);
            pintar();
            actualizarNavegacion();
        } else {
            mostrarInfo("Ya estás en la última lección.");
        }
    }

    /** Regresa a la lección anterior de la sección (si existe). */
    @FXML
    private void anteriorLeccion() {
        if (indexActual > 0) {
            indexActual = indexActual - 1;
            leccion = lecciones.get(indexActual);
            pintar();
            actualizarNavegacion();
        } else {
            mostrarInfo("Ya estás en la primera lección.");
        }
    }

    /**
     * Marca la lección como COMPLETADA en la UI (no persiste en BD) y
     * desbloquea visualmente la siguiente si existe.
     */
    @FXML
    private void marcarCompletada() {
        if (leccion == null) {
            mostrarError("Lección no disponible.");
            return;
        }

        // 1) Reemplazar el DTO actual por uno con estado COMPLETADA.
        //    (Ajusta los nombres si tu LeccionLista tiene otros componentes.)
        LeccionLista actualCompletada = new LeccionLista(
                leccion.id(),
                leccion.titulo(),
                leccion.numeroSeccion(),
                leccion.tipoContenido(),
                EstadoProgreso.COMPLETADA,
                leccion.desbloqueada()
        );
        lecciones.set(indexActual, actualCompletada);
        leccion = actualCompletada;

        // 2) Desbloquear visualmente la siguiente (si existe)
        if (indexActual + 1 < lecciones.size()) {
            LeccionLista siguiente = lecciones.get(indexActual + 1);
            if (!siguiente.desbloqueada()) {
                LeccionLista siguienteDesbloqueada = new LeccionLista(
                        siguiente.id(),
                        siguiente.titulo(),
                        siguiente.numeroSeccion(),
                        siguiente.tipoContenido(),
                        siguiente.estado(),
                        true
                );
                lecciones.set(indexActual + 1, siguienteDesbloqueada);
            }
        }

        // 3) Refrescar labels
        lblEstado.setText("Estado: " + EstadoProgreso.COMPLETADA.name());
        mostrarInfo("Lección marcada como COMPLETADA (solo UI).");
        actualizarNavegacion();
    }

    /** Vuelve a la pantalla del curso (lista de secciones/lecciones). */
    @FXML
    private void volverAlCurso() {
        navegarACurso(false);
    }

    /**
     * Volver a la sección (en tu flujo actual equivale a volver a Curso.fxml;
     * si quieres preseleccionar la misma sección, ver comentario dentro).
     */
    @FXML
    private void volverALaSeccion() {
        navegarACurso(true);
    }

    // =================================================================
    // ======================== NAVEGACIÓN SIMPLE ======================
    // =================================================================

    /** Carga Curso.fxml y pasa el contexto. */
    private void navegarACurso(boolean intentarPreseleccionarSeccion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Curso.fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof CursoController) {
                CursoController ctrl = (CursoController) controller;
                ctrl.setCursoService(cursoService);
                ctrl.init(usuarioId, cursoId);

                // (Opcional) Si agregas en tu CursoController un método público:
                //   void preseleccionarSeccionPorNumero(int numero);
                // aquí podrías llamar:
                // if (intentarPreseleccionarSeccion) {
                //     ctrl.preseleccionarSeccionPorNumero(this.numeroSeccion);
                // }
            }

            Stage stage = (Stage) lblTitulo.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            mostrarError("No se pudo volver: " + e.getMessage());
        }
    }

    // =================================================================
    // =========================== ALERTAS =============================
    // =================================================================

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
