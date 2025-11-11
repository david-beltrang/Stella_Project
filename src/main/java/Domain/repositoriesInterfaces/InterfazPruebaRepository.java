package Domain.repositoriesInterfaces;

import Application.dtos.Prueba.PruebaResponse;
import java.util.Optional;

public interface InterfazPruebaRepository {
    Optional<PruebaResponse> encontrarPorSeccionId(Integer seccionId);
}
