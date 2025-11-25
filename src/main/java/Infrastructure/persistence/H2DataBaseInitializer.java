package Infrastructure.persistence;

import org.h2.tools.RunScript;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2DataBaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(H2DataBaseInitializer.class);
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final IConexionBD connMgr;

    public H2DataBaseInitializer(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    public void initialize() {
        if (isInitialized.compareAndSet(false, true)) {
            logger.info("Inicializando base de datos H2...");
            try (Connection conn = connMgr.getConnection()) {

                runScript(conn, "sql/schema.sql");
                runScript(conn, "sql/data.sql");

                logger.info("Base de datos H2 inicializada correctamente");
            } catch (Exception e) {
                isInitialized.set(false);
                logger.error("Error cargando scripts H2", e);
                throw new RuntimeException("Error al inicializar la base de datos", e);
            }
        }
    }

    private void runScript(Connection conn, String scriptPath) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath);

        if (is == null) {
            throw new RuntimeException("Archivo no encontrado: " + scriptPath);
        }

        logger.debug("Ejecutando script: {}", scriptPath);

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            RunScript.execute(conn, reader);
        }
    }
}
