package Domain.models;

import java.time.LocalDateTime;

public class UsuarioItem {
    private final Integer usuarioId;
    private final Integer itemId;
    private final LocalDateTime fechaCompra;
    private final boolean esActivo;

    private UsuarioItem(Integer usuarioId, Integer itemId, LocalDateTime fechaCompra, boolean esActivo) {
        this.usuarioId = usuarioId;
        this.itemId = itemId;
        this.fechaCompra = fechaCompra;
        this.esActivo = esActivo;
    }

    public static UsuarioItem crear(Integer usuarioId, Integer itemId) {
        return new UsuarioItem(usuarioId, itemId, LocalDateTime.now(), true);
    }

    public static UsuarioItem reconstruir(Integer usuarioId, Integer itemId, LocalDateTime fechaCompra, boolean esActivo) {
        return new UsuarioItem(usuarioId, itemId, fechaCompra, esActivo);
    }

    public Integer getUsuarioId() { return usuarioId; }
    public Integer getItemId() { return itemId; }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public boolean isEsActivo() { return esActivo; }
}