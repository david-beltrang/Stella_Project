package Infrastructure.repositories;

// Imports necesarios
import Domain.models.Usuario;
import Domain.models.UsuarioValueObjects.Tipo;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.exceptions.usuario.UsuarioYaExisteException;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Clase para interactuar con la tabla de usuarios en la base de datos
public class UsuarioRepository implements InterfazUsuarioRepository {

    private IConexionBD connMgr;

    public UsuarioRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        // Usar comillas dobles para asegurar que H2 reconozca el nombre 'usuario' en
        // minúsculas.
        String sql = "INSERT INTO \"usuario\" (username, nombre, correo, contrasena, tipo_usuario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connMgr.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getCorreo());
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
                            usuario.getTipo(),
                            usuario.getFechaCreacion());
                }
                throw new SQLException("No se generó ID para el usuario.");
            }
        } catch (SQLException e) {
            // Código de error para violación de unicidad en H2: 23505.
            if (e.getErrorCode() == 23505) {
                throw new UsuarioYaExisteException(usuario.getCorreo());
            }
            throw new RuntimeException("Error guardando usuario: " + e.getMessage(), e);
        }
    }

    // Método para obtener un usuario por su ID.
    @Override
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT * FROM \"usuario\" WHERE id = ?"; // <<-- CAMBIO AQUÍ
        try (Connection conn = connMgr.getConnection();
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

    // Método para obtener un usuario por su correo.
    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM \"usuario\" WHERE correo = ?"; // <<-- CAMBIO AQUÍ
        try (Connection conn = connMgr.getConnection();
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

    // Método para obtener todos los usuarios.
    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM \"usuario\""; // <<-- CAMBIO AQUÍ
        try (Connection conn = connMgr.getConnection();
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

    // Método para eliminar un usuario.
    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM \"usuario\" WHERE id = ?"; // <<-- CAMBIO AQUÍ
        try (Connection conn = connMgr.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando usuario: " + e.getMessage(), e);
        }
    }

    // Método para actualizar el username de un usuario.
    @Override
    public void actualizarUsername(int id, String nuevoUsername) {
        String sql = "UPDATE \"usuario\" SET username = ? WHERE id = ?"; // <<-- CAMBIO AQUÍ
        try (Connection conn = connMgr.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoUsername);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando username: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para mapear un ResultSet a un Usuario.
    private Usuario obtenerUsuario(ResultSet rs) throws SQLException {
        return Usuario.reconstruir(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("correo"),
                rs.getString("nombre"),
                rs.getString("contrasena"),
                new Tipo(rs.getString("tipo_usuario")),
                rs.getTimestamp("fecha_creacion") != null ? rs.getTimestamp("fecha_creacion").toLocalDateTime() : null);
    }
}
