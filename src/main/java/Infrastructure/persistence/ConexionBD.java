package Infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementación Singleton thread-safe del patrón DAO para gestión de
 * conexiones a base de datos.
 * Aplica el principio de Indirection y Low Coupling al abstraer la creación de
 * conexiones.
 */
public class ConexionBD implements IConexionBD {

    private static final String JDBC_URL = "jdbc:h2:./stella"; // Base en disco
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // Singleton thread-safe usando volatile y double-checked locking
    private static volatile ConexionBD instance;
    private static volatile Connection connection = null;
    private static final Object lock = new Object();

    // Constructor privado para Singleton
    private ConexionBD() {
        // Prevenir instanciación directa
    }

    /**
     * Obtiene la instancia única del Singleton (thread-safe).
     * Aplica el patrón Singleton correctamente.
     */
    public static ConexionBD getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConexionBD();
                }
            }
        }
        return instance;
    }

    /**
     * Obtiene una conexión a la base de datos (thread-safe).
     * Aplica el patrón Singleton para la conexión compartida.
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || isConnectionClosed(connection)) {
            synchronized (lock) {
                if (connection == null || isConnectionClosed(connection)) {
                    try {
                        Class.forName("org.h2.Driver");
                    } catch (ClassNotFoundException e) {
                        throw new SQLException("H2 Driver no encontrado.", e);
                    }
                    connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                }
            }
        }
        return connection;
    }

    /**
     * Verifica si una conexión está cerrada de forma segura.
     */
    private boolean isConnectionClosed(Connection conn) {
        try {
            return conn == null || conn.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
}