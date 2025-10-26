package Infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD implements IConexionBD {

    private static final String JDBC_URL = "jdbc:h2:./stella"; // Base en disco
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("H2 Driver no encontrado.", e);
            }
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        }
        return connection;
    }
}