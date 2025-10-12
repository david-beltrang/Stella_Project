package Application.dtos.internal;

import Domain.models.LeccionValueObjects.TipoContenido;
import java.util.Objects;

/** DTO interno usado entre SesionEstudioService y LeccionRepository. */
public record ContenidoLeccionInternal(
        int id,
        String titulo,
        TipoContenido tipoContenido,
        String contenido,
        Integer pruebaId // <--- Es Integer (Objeto) para aceptar null
) {
    public ContenidoLeccionInternal {
        Objects.requireNonNull(tipoContenido);
        Objects.requireNonNull(titulo);
    }

    /** * Retorna el ID de la prueba como int primitivo, o 0 si el valor es null.
     * Esto evita el NullPointerException al usarlo en sentencias SQL o primitivos.
     */
    public int getPruebaIdPrimitivo() {
        return pruebaId != null ? pruebaId.intValue() : 0;
    }
}
