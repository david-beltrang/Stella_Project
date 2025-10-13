package Infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Configuración de la URL de H2 en modo en memoria.
    // DB_CLOSE_DELAY=-1 es crucial para mantener la BD viva.
    private static final String JDBC_URL = "jdbc:h2:mem:AppDB;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // 1. Declaración de la conexión única y estática (Singleton).
    private static Connection connection = null;

    /**
     * Devuelve una única instancia de la conexión a la base de datos H2 en memoria.
     * Si la conexión aún no existe, la crea; de lo contrario, devuelve la existente.
     * Esto garantiza que todas las llamadas compartan la misma instancia de AppDB.
     */
    public static Connection getConnection() throws SQLException {
        // 2. Lógica para crear la conexión solo la primera vez.
        if (connection == null || connection.isClosed()) {
            try {
                // Carga del driver (aunque no siempre es necesario con versiones modernas de JDBC)
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("H2 Driver no encontrado.", e);
            }

            // 3. Crear y almacenar la conexión única.
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        }

        // Devolver la misma conexión para todos los usuarios/repositorios.
        return connection;
    }
}
