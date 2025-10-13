package Infrastructure.test;

import Infrastructure.persistence.ConexionBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConexionSetup {
    public static void main(String[] args) {
        System.out.println("--- Probando Inicialización de H2 en Memoria ---");

        try (Connection conn = ConexionBD.getConnection()) {
            System.out.println("✅ Conexión establecida y BD inicializada.");

            // Prueba de lectura: Verificar si la tabla 'usuario' existe y tiene datos
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT id, username FROM usuario WHERE id = 1");

                if (rs.next()) {
                    System.out.println("✅ Dato de prueba encontrado:");
                    System.out.println("   ID: " + rs.getInt("id") + ", Username: " + rs.getString("username"));
                } else {
                    System.err.println("❌ Error: No se encontraron los datos de prueba (ID 1).");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR FATAL al conectar o ejecutar SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}