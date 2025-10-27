package Infrastructure.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConexionBD {
    Connection getConnection() throws SQLException;
}