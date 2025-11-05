package Domain.repositoriesInterfaces;

import java.util.Map;

public interface InterfazOpcionRepository {
    public Map<Integer, Integer> encontrarOpcionesCorrectasPorPruebaId(int pruebaId);
}
