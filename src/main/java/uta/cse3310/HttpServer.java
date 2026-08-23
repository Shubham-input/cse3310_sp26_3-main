package uta.cse3310;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.FileContextHandler;
import net.freeutils.httpserver.HTTPServer.VirtualHost;

import java.io.File;
import java.io.FileNotFoundException;

// http server include is a GPL licensed package from
//            http://www.freeutils.net/source/jlhttp/

public class HttpServer {

    int port;
    String dirname;

    public HttpServer(int portNum, String dirName) {
        port = portNum;
        dirname = dirName;
    }

    public void start() {
        try {
            File dir = new File(dirname);
            System.out.println("Serving " + dir.getAbsolutePath());

            if (!dir.canRead()) {
                throw new FileNotFoundException(dir.getAbsolutePath());
            }

            // set up a server
            HTTPServer server = new HTTPServer(port);
            VirtualHost host = server.getVirtualHost(null); // default host

            host.addContext("/{*}", new FileContextHandler(dir));
            host.addContext("/api/time", (req, resp) -> {
                long now = System.currentTimeMillis();
                resp.getHeaders().add("Content-Type", "text/plain");
                resp.send(200, String.format("%tF %<tT", now));
                return 0;
            });

            server.start();
        } catch (Exception e) {
            System.err.println("error: " + e);
        }
    }
}