package Infrastructure.persistence;

import org.h2.tools.Server;

public class h2webconsole {
    public static void main(String[] args) throws Exception {
        Server.createWebServer().start();
    }
}