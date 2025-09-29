package Infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:h2:~/testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    //Como ConexionBD solo tiene un metodo estatico no necesita instanciarse con new
    private ConexionBD() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}