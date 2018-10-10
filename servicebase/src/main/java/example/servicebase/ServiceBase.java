package example.servicebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public abstract class ServiceBase {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HttpServer server;

    public ServiceBase(int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    protected void start(String path, HttpHandler handler) {
        server.createContext(path, handler);
        server.setExecutor(null);
        System.out.printf("Listening on %s\n", server.getAddress());
        server.start();
    }

    protected static void write(byte[] bytes, HttpExchange ex) {
        try {
            ex.getResponseHeaders().add("X-Powered-By", "Plain Java");
            ex.sendResponseHeaders(200, bytes.length);

            OutputStream os = ex.getResponseBody();
            os.write(bytes);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    protected static void write(String message, HttpExchange ex) {
        write(message.getBytes(StandardCharsets.UTF_8), ex);
    }

    protected static void writeObject(Object o, HttpExchange ex) {
        ex.getResponseHeaders().add("Content-Type", "application/json");
        write(toJson(o), ex);
    }

    private static byte[] toJson(Object o) {
        try {
            return MAPPER.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
    }
}
