package Domain.models;

/**
 * Modelo de dominio para ejercicios de programación en lecciones interactivas
 */
public class Ejercicio {
    private final Integer id;
    private final Integer leccionId;
    private final String titulo;
    private final String instrucciones;
    private final String codigoPlantilla;
    private final String solucionEsperada;
    private final Integer puntos;
    private final Integer numeroOrden;

    private Ejercicio(Integer id, Integer leccionId, String titulo, String instrucciones,
            String codigoPlantilla, String solucionEsperada, Integer puntos, Integer numeroOrden) {
        this.id = id;
        this.leccionId = leccionId;
        this.titulo = titulo;
        this.instrucciones = instrucciones;
        this.codigoPlantilla = codigoPlantilla;
        this.solucionEsperada = solucionEsperada;
        this.puntos = puntos;
        this.numeroOrden = numeroOrden;
    }

    public static Ejercicio crear(Integer leccionId, String titulo, String instrucciones,
            String codigoPlantilla, String solucionEsperada, Integer puntos, Integer numeroOrden) {
        if (leccionId == null || leccionId <= 0) {
            throw new IllegalArgumentException("El ID de la lección no puede ser nulo o menor a 1");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (instrucciones == null || instrucciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las instrucciones no pueden estar vacías");
        }
        if (solucionEsperada == null || solucionEsperada.trim().isEmpty()) {
            throw new IllegalArgumentException("La solución esperada no puede estar vacía");
        }
        if (puntos == null || puntos < 0) {
            throw new IllegalArgumentException("Los puntos deben ser mayores o iguales a 0");
        }
        if (numeroOrden == null || numeroOrden < 1) {
            throw new IllegalArgumentException("El número de orden debe ser mayor a 0");
        }

        return new Ejercicio(null, leccionId, titulo, instrucciones, codigoPlantilla,
                solucionEsperada, puntos, numeroOrden);
    }

    public static Ejercicio reconstruir(Integer id, Integer leccionId, String titulo, String instrucciones,
            String codigoPlantilla, String solucionEsperada, Integer puntos, Integer numeroOrden) {
        return new Ejercicio(id, leccionId, titulo, instrucciones, codigoPlantilla,
                solucionEsperada, puntos, numeroOrden);
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getLeccionId() {
        return leccionId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public String getCodigoPlantilla() {
        return codigoPlantilla;
    }

    public String getSolucionEsperada() {
        return solucionEsperada;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public Integer getNumeroOrden() {
        return numeroOrden;
    }
}
