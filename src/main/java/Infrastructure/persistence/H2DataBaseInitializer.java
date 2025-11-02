package Infrastructure.persistence;

import org.h2.tools.RunScript;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

public class H2DataBaseInitializer {
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final IConexionBD connMgr;

    public H2DataBaseInitializer(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    public void initialize() {
        if (isInitialized.compareAndSet(false, true)) {
            System.out.println(">>> [H2 Setup] Inicializando esquema de la base de datos...");
            try (Connection conn = connMgr.getConnection()) {
                InputStream is = H2DataBaseInitializer.class.getClassLoader()
                        .getResourceAsStream("database_setup.sql");

                if (is == null) {
                    throw new RuntimeException("No se encontró el archivo database_setup.sql en el classpath.");
                }

                // 🔥 Aquí está el cambio clave:
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    RunScript.execute(conn, reader);
                }

                System.out.println(">>> [H2 Setup] Esquema y datos iniciales cargados con éxito.");
            } catch (Exception e) {
                isInitialized.set(false);
                System.err.println("!!! [H2 Setup] FALLO FATAL al ejecutar el script de inicialización.");
                e.printStackTrace();
                throw new RuntimeException("Fallo al inicializar H2: " + e.getMessage(), e);
            }
        }
    }
}

