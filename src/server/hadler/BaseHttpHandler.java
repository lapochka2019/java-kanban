package server.hadler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager manager;

    public BaseHttpHandler(Gson gson, TaskManager manager) {
        this.gson = gson;
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }

    protected void sendSuccessfullyDefault(HttpExchange h, String text) throws IOException {
        sendSuccessfully(h, text, 200);
    }

    protected void sendSuccessfully(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendError(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendLengthRequired(HttpExchange h) throws IOException {
        sendError(h, "Заполните тело запроса!", 411);
    }

    protected void sendNotImplemented(HttpExchange h) throws IOException {
        sendError(h, "Некорректный метод запроса!", 501);
    }

    protected void sendTimeTaken(HttpExchange h) throws IOException {
        sendError(h, "Данное время выполнения уже занято!", 406);
    }

    protected void sendTaskNotFound(HttpExchange h) throws IOException {
        sendError(h, "По этому id ничего не найдено!", 404);
    }

    public Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = Arrays.stream(exchange.getRequestURI().getPath().split("/")).filter(Predicate.not(String::isEmpty)).toArray(String[]::new);
        try {
            return Optional.of(Integer.parseInt(pathParts[1]));
        } catch (ArrayIndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    public boolean isGet(String methodType) {
        return "GET".equals(methodType);
    }

    public boolean isPost(String methodType) {
        return "POST".equals(methodType);
    }

    public boolean isDelete(String methodType) {
        return "DELETE".equals(methodType);
    }
}
