package Infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {


    private static final String JDBC_URL = "jdbc:h2:mem:AppDB;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver no encontrado.", e);
        }
        // H2 crea la BD "AppDB" aquí si no existe.
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}