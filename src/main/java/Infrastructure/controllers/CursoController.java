package Infrastructure.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Application.dtos.curso.CursoEstructura;
import Application.dtos.leccion.LeccionLista;
import Application.dtos.seccion.Seccion;
import Application.services.CursoService;

import Domain.models.UsuarioValueObjects.UsuarioId;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * CursoController sencillo:
 *  - Muestra el título del curso.
 *  - Lista las secciones (como texto).
 *  - Al seleccionar una sección, lista sus lecciones (como texto).
 *  - Botón para abrir la lección seleccionada (usa LeccionController).
 *  - Botón para refrescar y botón para volver.
 *
 * Notas de diseño (bajo acoplamiento):
 *  - No usamos cell factories ni clases anónimas.
 *  - Mostramos Strings en las ListView y guardamos las listas "reales" en memoria
 *    para mapear índice -> DTO cuando hace falta.
 */
public class CursoController {

    // ---------- UI: estos fx:id deben existir en tu Curso.fxml ----------
    @FXML private Label lblTituloCurso;
    @FXML private ListView<String> lstSecciones;   // mostramos texto simple
    @FXML private ListView<String> lstLecciones;   // mostramos texto simple
    @FXML private Button btnAbrirLeccion;
    @FXML private Button btnRefrescar;
    @FXML private Button btnVolver;

    // ---------- Dependencias / Contexto ----------
    private CursoService cursoService;  // se inyecta desde fuera
    private UsuarioId usuarioId;        // usuario actual
    private Integer cursoId;            // curso que estamos viendo

    // ---------- Estado interno (para mapear selección -> DTO real) ----------
    private CursoEstructura estructura;           // estructura completa del curso
    private List<Seccion> seccionesActuales;      // secciones mostradas
    private List<LeccionLista> leccionesActuales; // lecciones de la sección seleccionada
    private Seccion seccionSeleccionada;          // sección actualmente elegida

    // =====================================================================
    // =============== MÉTODOS PÚBLICOS QUE LLAMA OTRA CLASE ===============
    // =====================================================================

    /** Inyecta el servicio (evita acoplar a repos y a otras capas). */
    public void setCursoService(CursoService cursoService) {
        this.cursoService = Objects.requireNonNull(cursoService, "cursoService");
    }

    /** Punto de entrada a esta pantalla. Debes llamarlo al cargar el FXML. */
    public void init(UsuarioId usuarioId, Integer cursoId) {
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
        this.cursoId   = Objects.requireNonNull(cursoId,   "cursoId");
        cargarEstructuraYMostrar();   // carga datos y pinta listas
    }

    // =====================================================================
    // ======================== CARGA Y PINTADO =============================
    // =====================================================================

    /** Trae la estructura desde el servicio y pinta secciones y lecciones. */
    private void cargarEstructuraYMostrar() {
        try {
            // 1) Pido la estructura del curso al servicio (única dependencia)
            estructura = cursoService.obtenerEstructuraCurso(usuarioId, cursoId);

            // 2) Pinto el título del curso
            lblTituloCurso.setText(estructura.tituloCurso());

            // 3) Preparo las secciones para la lista visual
            if (estructura.secciones() == null) {
                seccionesActuales = Collections.emptyList();
            } else {
                seccionesActuales = new ArrayList<>(estructura.secciones());
            }

            // 4) Creo una lista de Strings para mostrar (fácil de leer)
            List<String> textosSecciones = new ArrayList<>();
            for (Seccion s : seccionesActuales) {
                int cantidad = (s.lecciones() == null) ? 0 : s.lecciones().size();
                String texto = "Sección " + s.numeroSeccion() + ": " + s.tituloSeccion() + " (" + cantidad + " lecciones)";
                textosSecciones.add(texto);
            }
            lstSecciones.setItems(FXCollections.observableArrayList(textosSecciones));

            // 5) Si hay secciones, selecciono la primera y cargo sus lecciones
            if (!seccionesActuales.isEmpty()) {
                lstSecciones.getSelectionModel().select(0);
                onSeleccionSeccion(); // carga las lecciones de esa sección
            } else {
                // no hay secciones -> limpio lista de lecciones
                leccionesActuales = Collections.emptyList();
                lstLecciones.setItems(FXCollections.observableArrayList());
                btnAbrirLeccion.setDisable(true);
            }

        } catch (Exception e) {
            mostrarError("No se pudo cargar el curso: " + e.getMessage());
        }
    }

    /** Llama este método cuando el usuario cambia de sección (desde FXML). */
    @FXML
    private void onSeleccionSeccion() {
        int indice = lstSecciones.getSelectionModel().getSelectedIndex();
        if (indice < 0 || indice >= seccionesActuales.size()) {
            // nada seleccionado o índice inválido
            seccionSeleccionada = null;
            leccionesActuales = Collections.emptyList();
            lstLecciones.setItems(FXCollections.observableArrayList());
            btnAbrirLeccion.setDisable(true);
            return;
        }

        // 1) Obtengo la sección real usando el índice
        seccionSeleccionada = seccionesActuales.get(indice);

        // 2) Obtengo sus lecciones (o lista vacía si es null)
        if (seccionSeleccionada.lecciones() == null) {
            leccionesActuales = Collections.emptyList();
        } else {
            leccionesActuales = new ArrayList<>(seccionSeleccionada.lecciones());
        }

        // 3) Convierto las lecciones a texto simple para mostrar
        List<String> textosLecciones = new ArrayList<>();
        for (LeccionLista l : leccionesActuales) {
            String candado = l.desbloqueada() ? "" : "[BLOQUEADA] ";
            String texto = candado + l.titulo() + " — " + l.tipoContenido() + " — " + l.estado().name();
            textosLecciones.add(texto);
        }

        // 4) Pinto lista y deshabilito el botón hasta que elijan una lección válida
        lstLecciones.setItems(FXCollections.observableArrayList(textosLecciones));
        btnAbrirLeccion.setDisable(true);
    }

    /** Llama este método cuando el usuario selecciona una lección (desde FXML). */
    @FXML
    private void onSeleccionLeccion() {
        int indice = lstLecciones.getSelectionModel().getSelectedIndex();
        if (indice < 0 || indice >= leccionesActuales.size()) {
            btnAbrirLeccion.setDisable(true);
            return;
        }
        LeccionLista l = leccionesActuales.get(indice);
        // Solo habilito si está desbloqueada
        btnAbrirLeccion.setDisable(!l.desbloqueada());
    }

    // =====================================================================
    // ======================= ACCIONES DE BOTONES ==========================
    // =====================================================================

    /** Abre la lección seleccionada en la pantalla de Lección. */
    @FXML
    private void abrirLeccionSeleccionada() {
        int indiceLeccion = lstLecciones.getSelectionModel().getSelectedIndex();
        if (indiceLeccion < 0 || indiceLeccion >= leccionesActuales.size()) {
            mostrarInfo("Selecciona una lección.");
            return;
        }
        LeccionLista l = leccionesActuales.get(indiceLeccion);
        if (!l.desbloqueada()) {
            mostrarInfo("La lección está bloqueada.");
            return;
        }
        if (seccionSeleccionada == null) {
            mostrarError("No hay sección seleccionada.");
            return;
        }

        // Navego a Leccion.fxml y paso el contexto mínimo
        navegarA("/views/Leccion.fxml", new Navegacion() {
            @Override
            public void despuesDeCargar(Object controller) {
                if (controller instanceof LeccionController) {
                    LeccionController lc = (LeccionController) controller;
                    lc.setCursoService(cursoService);
                    // Paso el índice de la lección dentro de la sección para habilitar anterior/siguiente
                    lc.init(usuarioId, cursoId, seccionSeleccionada.numeroSeccion(), indiceLeccion);
                }
            }
        });
    }

    /** Vuelve a cargar la estructura desde el servicio y repinta. */
    @FXML
    private void refrescar() {
        cargarEstructuraYMostrar();
        mostrarInfo("Contenido actualizado.");
    }

    /** Cierra la ventana actual (o reemplázalo por navegación si tienes otra pantalla). */
    @FXML
    private void volver() {
        Stage stage = (Stage) lblTituloCurso.getScene().getWindow();
        stage.close();
    }

    // =====================================================================
    // ====================== UTILIDADES SIMPLES ============================
    // =====================================================================

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    /** Interfaz simple para evitar lambdas y mantener el código claro. */
    private interface Navegacion {
        void despuesDeCargar(Object controller);
    }

    /** Carga un FXML, obtiene su controller y cambia la escena. */
    private void navegarA(String fxmlPath, Navegacion callback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Object nextCtrl = loader.getController();
            if (callback != null) {
                callback.despuesDeCargar(nextCtrl);
            }
            Stage stage = (Stage) lblTituloCurso.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            mostrarError("No se pudo navegar: " + e.getMessage());
        }
    }
}

