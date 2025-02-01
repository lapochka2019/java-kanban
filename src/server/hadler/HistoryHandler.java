package server.hadler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(Gson gson, TaskManager manager) {
        super(gson, manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = Arrays.stream(exchange.getRequestURI().getPath().split("/")).filter(Predicate.not(String::isEmpty)).toArray(String[]::new)[0];
        if (!path.equals("history")) {
            sendError(exchange, "Некорректный запрос!", 400);
            return;
        }
        String methodType = exchange.getRequestMethod();
        if ("GET".equals(methodType)) {
            String responseString = gson.toJson(manager.getHistory());
            sendSuccessfullyDefault(exchange, responseString);
        } else {
            sendNotImplemented(exchange);
        }
    }
}
