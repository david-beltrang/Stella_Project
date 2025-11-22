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
            System.out.println(">>> [H2 Setup] Inicializando base de datos...");
            try (Connection conn = connMgr.getConnection()) {

                runScript(conn, "sql/schema.sql");
                runScript(conn, "sql/data.sql");

                System.out.println(">>> [H2 Setup] Base de datos lista.");
            } catch (Exception e) {
                isInitialized.set(false);
                System.err.println("!!! [H2 Setup] Error cargando scripts H2");
                e.printStackTrace();
            }
        }
    }

    private void runScript(Connection conn, String scriptPath) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath);

        if (is == null) {
            throw new RuntimeException("Archivo no encontrado: " + scriptPath);
        }

        System.out.println(">>> Ejecutando: " + scriptPath);

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            RunScript.execute(conn, reader);
        }
    }
}
