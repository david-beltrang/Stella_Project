package Domain.repositoriesInterfaces;

// UsuarioStats es una entidad que se carga/actualiza en el servicio.
public interface InterfazUsuarioStatsRepository {

    // Actualiza la cantidad de "pescaditos" del usuario.
    void actualizarPescaditos(int usuarioId, int cantidad); // Puede ser suma o resta

    // Actualiza la racha de días de estudio del usuario.
    void actualizarRacha(int usuarioId, int diasRacha);

    // Crea el registro inicial de estadísticas para un nuevo usuario
    void crearStatsIniciales(int usuarioId);
}