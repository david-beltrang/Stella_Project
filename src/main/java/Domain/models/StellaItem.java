// Domain/models/StellaItem.java
package Domain.models;

public class StellaItem {
    private final Integer id;
    private final Integer itemId;
    private final String imagePath;

    private StellaItem(Integer id, Integer itemId, String imagePath) {
        this.id = id;
        this.itemId = itemId;
        this.imagePath = imagePath;
    }

    public static StellaItem crear(Integer itemId, String imagePath) {
        return new StellaItem(null, itemId, imagePath);
    }

    public static StellaItem reconstruir(Integer id, Integer itemId, String imagePath) {
        return new StellaItem(id, itemId, imagePath);
    }

    public Integer getId() { return id; }
    public Integer getItemId() { return itemId; }
    public String getImagePath() { return imagePath; }
}