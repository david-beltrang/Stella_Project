package Infrastructure.repositories;

// Imports necesarios
import Domain.models.Usuario;
import Domain.models.UsuarioValueObjects.Correo;
import Domain.models.UsuarioValueObjects.Nombre;
import Domain.models.UsuarioValueObjects.Tipo;
import Domain.models.UsuarioValueObjects.Username;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.exceptions.usuario.UsuarioYaExisteException;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Esta clase UsuarioRepository implementa la interfaz del repositorio
public class UsuarioRepository implements InterfazUsuarioRepository {

    // **IMPORTANTE:** La tabla en el script SQL es 'usuario', no 'usuarios'

    public UsuarioRepository() {
        // La creación de la tabla se maneja ahora en el script SQL al iniciar H2.
        // Se puede dejar vacío o usar para inicializar otros recursos.
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // Se asume que el script de inicialización creó la tabla 'usuario'
        String sql = "INSERT INTO usuario (username, nombre, correo, contrasena, tipo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getUsername().valor());
            pstmt.setString(2, usuario.getNombre().valor());
            pstmt.setString(3, usuario.getCorreo().valor());
            pstmt.setString(4, usuario.getContrasena());
            pstmt.setString(5, usuario.getTipo().valor());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Reconstruir con el ID generado (patrón de Dominio)
                    return Usuario.reconstruir(
                            generatedKeys.getInt(1),
                            usuario.getUsername(),
                            usuario.getCorreo(),
                            usuario.getNombre(),
                            usuario.getContrasena(),
                            usuario.getTipo()
                    );
                }
                throw new SQLException("No se generó ID para el usuario.");
            }
        } catch (SQLException e) {
            // Código de error para violación de unicidad en H2: 23505.
            if (e.getErrorCode() == 23505) {
                throw new UsuarioYaExisteException(usuario.getCorreo().valor());
            }
            throw new RuntimeException("Error guardando usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(obtenerUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(obtenerUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(obtenerUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarUsername(int id, String nuevoUsername) {
        String sql = "UPDATE usuario SET username = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoUsername);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando username: " + e.getMessage(), e);
        }
    }

    private Usuario obtenerUsuario(ResultSet rs) throws SQLException {
        return Usuario.reconstruir(
                rs.getInt("id"),
                new Username(rs.getString("username")),
                new Correo(rs.getString("correo")),
                new Nombre(rs.getString("nombre")),
                rs.getString("contrasena"),
                new Tipo(rs.getString("tipo"))
        );
    }
}