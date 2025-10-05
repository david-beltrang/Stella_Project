package Infrastructure.repositories;

// Imports necesarios
import Domain.models.Usuario;
import Domain.models.UsuarioValueObjects.Correo;
import Domain.models.UsuarioValueObjects.Nombre;
import Domain.models.UsuarioValueObjects.Tipo;
import Domain.models.UsuarioValueObjects.Username;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.exceptions.UsuarioYaExisteException;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Esta clase UsuarioRepository implementa la interfaz del repositorio
public class UsuarioRepository implements InterfazUsuarioRepository {

    public UsuarioRepository() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(100) NOT NULL UNIQUE,
                nombre VARCHAR(100) NOT NULL,
                correo VARCHAR(200) NOT NULL UNIQUE,
                contrasena VARCHAR(100) NOT NULL,
                tipo VARCHAR(20) NOT NULL DEFAULT 'ESTUDIANTE'
            )
        """;
        //Aquí se usa el metodo static de ConexionBD para poder obtener una conexión con la base de datos y no escribir las credenciales de nuevo
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement()) {
            //Se crea la tabla con el sql statement
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando tabla: " + e.getMessage(), e);
        }
    }

    @Override
    //Este metodo devuelve un usuario para poder usarlo en el caso de uso de registrar y que se sepa cual usuario tiene sesion activa
    public Usuario guardar(Usuario usuario) {
        //El metodo isPresent permite saber si el metodo de buscarPorCoreo devolvio o no un usuario
        if (buscarPorCorreo(usuario.getCorreo().valor()).isPresent()) {
            //Si se devuekve un usuario esto quiere decir que ya existe en la BD
            throw new UsuarioYaExisteException(usuario.getCorreo().valor());
        }
        String sql = "INSERT INTO usuarios (username, nombre, correo, contrasena, tipo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             //Se coloca el RETURN_GENERATED_KEYS para poder retornar el usuario posteriormente
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            //Se usa prepreStatement para poder ejecutar la sentencia SQL incluyendo los parametros y ubicandolos en donde se encuentran los ?
            pstmt.setString(1, usuario.getUsername().valor());
            pstmt.setString(2, usuario.getNombre().valor());
            pstmt.setString(3, usuario.getCorreo().valor());
            pstmt.setString(4, usuario.getContrasena());
            pstmt.setString(5, usuario.getTipo().valor());
            pstmt.executeUpdate();
            //Si se genero un registro entonces hubo exito registrando al usuario, usamos los datos del registro para poder retornar el objeto dle usuario
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Usuario.reconstruir(
                            generatedKeys.getInt(1),
                            //Aqui se usa unicamente un get porque si se genero un ID es porque se genero un registro, por ende se puede usar el objeto que llegó por parametro
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
            if (e.getErrorCode() == 23505) { // 23505 es una violación de unicidad en H2
                throw new UsuarioYaExisteException(usuario.getCorreo().valor());
            }
            throw new RuntimeException("Error guardando usuario: " + e.getMessage(), e);
        }
    }

    @Override
    //Se declara con optional porque si no se encuentra el usuario es posible que se retorne null, esto lo cntrola optional
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            //Si sí se obtuvo un registro entonces se devuelve el Optional de obtenerUsuario
            if (rs.next()) {
                return Optional.of(obtenerUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando por ID: " + e.getMessage(), e);
        }
        //Si no se encontró al usuario se retorna Optional.empty() que no genera un nullPointerException
        return Optional.empty();
    }

    @Override
    //Al igual que en buscarPorId se retorna un Optional de usuario
    public Optional<Usuario> buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(obtenerUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> listarTodos() {
        //Se crea una lista para almacenar los registros del usuario en memoria
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            //Un ciclo while para ir registro por registro
            while (rs.next()) {
                //Se añade a la lista el usuario del registro
                usuarios.add(obtenerUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando usuarios: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
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
        String sql = "UPDATE usuarios SET username = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoUsername);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando username: " + e.getMessage(), e);
        }
    }

    // Metodo auxiliar para reconstruir Usuario desde ResultSet y que se pueda usar en los otros metodos
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