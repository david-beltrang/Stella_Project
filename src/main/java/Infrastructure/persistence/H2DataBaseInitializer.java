package Infrastructure.persistence;

import org.h2.tools.RunScript; // Necesitas el driver H2 en tu classpath

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

public class H2DataBaseInitializer {

    // Bandera para asegurar que la inicialización solo corra una vez
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);

    public static void initialize() {
        if (isInitialized.compareAndSet(false, true)) {
            System.out.println(">>> [H2 Setup] Inicializando esquema de la base de datos...");
            try (Connection conn = ConexionBD.getConnection()) {

                // Ejecuta el script SQL manualmente
                InputStream is = H2DataBaseInitializer.class.getClassLoader().getResourceAsStream("database_setup.sql");
                if (is == null) {
                    throw new RuntimeException("No se encontró el archivo database_setup.sql en el classpath.");
                }

                try (InputStreamReader isr = new InputStreamReader(is)) {
                    RunScript.execute(conn, isr);
                }
                System.out.println(">>> [H2 Setup] Esquema y datos iniciales cargados con éxito.");

            } catch (Exception e) {
                isInitialized.set(false); // Si falla, resetear el estado
                System.err.println("!!! [H2 Setup] FALLO FATAL al ejecutar el script de inicialización.");
                throw new RuntimeException("Fallo al inicializar H2: " + e.getMessage(), e);
            }
        }
    }
}