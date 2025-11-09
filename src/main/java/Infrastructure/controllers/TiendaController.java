package Infrastructure.controllers;

import Application.services.TiendaService;
import Application.services.ForoService;
import Application.dtos.tienda.ItemTiendaResponse;
import Application.dtos.tienda.ItemDetalleRequest;
import Application.dtos.tienda.ItemDetalleResponse;
import Application.dtos.tienda.ItemCompraRequest;
import Application.dtos.foro.PostResponse;
import Application.dtos.foro.ComentarioResponse;
import Application.dtos.foro.CrearPostRequest;
import Application.dtos.foro.CrearComentarioRequest;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Function;

public class TiendaController {

    // ========= Dependencias =========
    private final TiendaService tiendaService;
    private final ForoService foroService;
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // Usuario logueado (para comprar / postear / comentar)
    private int usuarioId;

    // ========= FXML =========
    @FXML private Pane root;

    // Tienda
    @FXML private ListView<ItemTiendaResponse> listaItems;
    @FXML private Label lblNombreItem;
    @FXML private Label lblPrecioItem;
    @FXML private TextArea txtDescripcionItem;
    @FXML private Button btnComprar;

    // Foro sencillo dentro de la tienda
    @FXML private ListView<PostResponse> listaPosts;
    @FXML private TextArea txtNuevoPost;
    @FXML private TextField txtEtiquetaPost;
    @FXML private Button btnCrearPost;

    @FXML private ListView<ComentarioResponse> listaComentarios;
    @FXML private TextArea txtNuevoComentario;
    @FXML private Button btnComentar;
    @FXML private Button btnLikePost;
    @FXML private Button btnLikeComentario;

    // ========= Estado interno =========
    private ItemTiendaResponse itemSeleccionado;
    private PostResponse postSeleccionado;
    private ComentarioResponse comentarioSeleccionado;

    // ========= Constructor =========
    public TiendaController(TiendaService tiendaService, ForoService foroService) {
        this.tiendaService = tiendaService;
        this.foroService = foroService;
    }

    // Lo llamará ControllerControladores después de crear el controlador
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    // ========= Inicialización =========
    @FXML
    private void initialize() {
        configurarListas();
        cargarItemsTienda();
        cargarPostsForo();
    }

    private void configurarListas() {
        // Cómo mostrar los items en la lista
        listaItems.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ItemTiendaResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.nombre() + " - " + item.precio() + " pescaditos");
                }
            }
        });

        listaItems.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            itemSeleccionado = newVal;
            if (newVal != null) {
                cargarDetalleItem(newVal.id());
            }
        });

        // Posts
        listaPosts.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(PostResponse post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setText(null);
                } else {
                    setText("[" + post.etiqueta() + "] " + post.contenido() + " (likes: " + post.likes() + ")");
                }
            }
        });

        listaPosts.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            postSeleccionado = n;
            if (n != null) {
                listaComentarios.getItems().setAll(n.comentarios());
            } else {
                listaComentarios.getItems().clear();
            }
        });

        // Comentarios
        listaComentarios.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ComentarioResponse c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.usuarioNombre() + ": " + c.contenido() + " (likes: " + c.likes() + ")");
                }
            }
        });

        listaComentarios.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            comentarioSeleccionado = n;
        });
    }

    // ========= Tienda =========
    private void cargarItemsTienda() {
        try {
            List<ItemTiendaResponse> items = tiendaService.obtenerItemsTienda();
            listaItems.getItems().setAll(items);
        } catch (Exception e) {
            uiHelper.mostrarError("Error cargando la tienda", e.getMessage());
        }
    }

    private void cargarDetalleItem(int itemId) {
        try {
            ItemDetalleResponse detalle = tiendaService.obtenerDetalleItem(
                    new ItemDetalleRequest(itemId)
            );
            lblNombreItem.setText(detalle.nombre());
            lblPrecioItem.setText(detalle.precio() + " pescaditos");
            txtDescripcionItem.setText(detalle.descripcion());
        } catch (Exception e) {
            uiHelper.mostrarError("Error cargando detalle", e.getMessage());
        }
    }

    @FXML
    private void onComprarItem() {
        if (itemSeleccionado == null) {
            uiHelper.mostrarInfo("Selecciona un item", "Primero elige un producto de la lista.");
            return;
        }
        try {
            tiendaService.comprarItem(
                    usuarioId,
                    new ItemCompraRequest(itemSeleccionado.id())
            );
            uiHelper.mostrarInfo("Compra exitosa", "¡Has comprado " + itemSeleccionado.nombre() + "!");
            // Aquí podrías actualizar saldo del usuario, inventario, etc.
        } catch (Exception e) {
            uiHelper.mostrarError("No se pudo completar la compra", e.getMessage());
        }
    }

    // ========= Foro =========
    private void cargarPostsForo() {
        try {
            List<PostResponse> posts = foroService.obtenerTodosLosPosts();
            listaPosts.getItems().setAll(posts);
        } catch (Exception e) {
            uiHelper.mostrarError("Error cargando el foro", e.getMessage());
        }
    }

    @FXML
    private void onCrearPost() {
        String contenido = txtNuevoPost.getText();
        String etiqueta = txtEtiquetaPost.getText();

        if (contenido == null || contenido.isBlank()) {
            uiHelper.mostrarInfo("Contenido vacío", "Escribe algo para el post.");
            return;
        }

        try {
            CrearPostRequest req = new CrearPostRequest(usuarioId, contenido, etiqueta);
            foroService.crearPost(req);
            txtNuevoPost.clear();
            txtEtiquetaPost.clear();
            cargarPostsForo();
        } catch (Exception e) {
            uiHelper.mostrarError("Error creando post", e.getMessage());
        }
    }

    @FXML
    private void onComentar() {
        if (postSeleccionado == null) {
            uiHelper.mostrarInfo("Selecciona un post", "Elige un post para comentar.");
            return;
        }
        String contenido = txtNuevoComentario.getText();
        if (contenido == null || contenido.isBlank()) {
            uiHelper.mostrarInfo("Comentario vacío", "Escribe tu comentario.");
            return;
        }

        try {
            CrearComentarioRequest req = new CrearComentarioRequest(
                    postSeleccionado.id(),
                    usuarioId,
                    contenido
            );
            foroService.crearComentario(req);
            txtNuevoComentario.clear();
            cargarPostsForo(); // recarga para actualizar comentarios/likes
        } catch (Exception e) {
            uiHelper.mostrarError("Error creando comentario", e.getMessage());
        }
    }

    @FXML
    private void onLikePost() {
        if (postSeleccionado == null) {
            uiHelper.mostrarInfo("Selecciona un post", "Elige un post para dar like.");
            return;
        }
        try {
            foroService.darLikeAPost(postSeleccionado.id());
            cargarPostsForo();
        } catch (Exception e) {
            uiHelper.mostrarError("Error al dar like", e.getMessage());
        }
    }

    @FXML
    private void onLikeComentario() {
        if (comentarioSeleccionado == null) {
            uiHelper.mostrarInfo("Selecciona un comentario", "Elige un comentario para dar like.");
            return;
        }
        try {
            foroService.darLikeAComentario(comentarioSeleccionado.id());
            cargarPostsForo();
        } catch (Exception e) {
            uiHelper.mostrarError("Error al dar like", e.getMessage());
        }
    }

    // ========= Navegación opcional =========
    @FXML
    private void onVolverAPrincipal() {
        // Si quieres un botón "Volver"
        navigator.irAPrincipal(root, controllerFactory);
    }
}
