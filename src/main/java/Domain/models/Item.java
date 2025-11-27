// Domain/models/Item.java
package Domain.models;

import Domain.models.TiendaValueObjects.PrecioPescaditos;

public class Item {
    private final Integer id;
    private final String nombre;
    private final String descripcion;
    private final PrecioPescaditos precio;
    private final String imagePath;

    private Item(Integer id, String nombre, String descripcion, PrecioPescaditos precio, String imagePath) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagePath = imagePath;
    }

    public static Item crear(String nombre, String descripcion, int precio, String imagePath) {
        return new Item(null, nombre, descripcion, new PrecioPescaditos(precio), imagePath);
    }

    public static Item reconstruir(Integer id, String nombre, String descripcion, int precio, String imagePath) {
        return new Item(id, nombre, descripcion, new PrecioPescaditos(precio), imagePath);
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public PrecioPescaditos getPrecio() { return precio; }
    public String getImagePath() { return imagePath; }
}